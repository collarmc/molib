package com.collarmc.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.Authenticator;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public final class Http {
    private final ObjectMapper mapper;
    private final HttpClient http;
    private final Map<String, String> headers;

    public Http(HttpClient http, ObjectMapper mapper, Map<String, String> headers) {
        this.mapper = mapper;
        this.headers = headers;
        this.http = http;
    }

    public <T> Optional<T> post(URI uri, Object req, Response<T> responseType) {
        HttpRequest request;
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(req)));
            headers.forEach(builder::header);
            request = builder.build();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return execute(request, responseType);
    }

    public <T> Optional<T> httpGet(URI uri, Response<T> responseType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(uri)
                .GET();
        headers.forEach(builder::header);
        return execute(builder.build(), responseType);
    }

    private <T> Optional<T> execute(HttpRequest request, Response<T> responseType) {
        HttpResponse<String> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            throw new IllegalStateException(e);
        }
        int code = response.statusCode();
        return switch (code) {
            case 200 -> Optional.of(responseType.map(response.body()));
            case 204 -> Optional.empty();
            case 400 -> throw new HttpException.BadRequestException("bad request");
            case 401 -> throw new HttpException.UnauthorisedException("unauthorised");
            case 403 -> throw new HttpException.ForbiddenException("forbidden");
            case 404 -> throw new HttpException.NotFoundException("not found");
            case 409 -> throw new HttpException.ConflictException("conflict");
            case 429 -> throw new HttpException.TooManyRequestsException("too many requests: " + response.body());
            case 500 -> throw new HttpException.ServerErrorException("server error");
            default -> throw new HttpException.UnmappedHttpException(response.statusCode(), "unmapped error " + code);
        };
    }
}
