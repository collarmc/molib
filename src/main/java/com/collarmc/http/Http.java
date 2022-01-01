package com.collarmc.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class Http {
    private final ObjectMapper mapper;
    private final HttpClient http = HttpClient.newBuilder().build();

    public Http(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> Optional<T> post(URI uri, Object req, Response<T> responseType) {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(req)))
                    .build();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return execute(request, responseType);
    }

    public <T> Optional<T> httpGet(URI uri, Response<T> responseType) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        return execute(request, responseType);
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
            case 500 -> throw new HttpException.ServerErrorException("server error");
            default -> throw new HttpException.UnmappedHttpException(response.statusCode(), "unmapped");
        };
    }
}
