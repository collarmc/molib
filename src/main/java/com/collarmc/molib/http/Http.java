package com.collarmc.molib.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class Http {
    private final ObjectMapper mapper;
    private final HttpClient http = HttpClient.newBuilder().build();

    public Http(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T post(URI uri, Object req, Class<T> responseType) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(req)))
                .build();
        return execute(request, responseType);
    }

    public  <T> T httpGet(URI uri, Class<T> responseType) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        return execute(request, responseType);
    }

    private <T> T execute(HttpRequest request, Class<T> responseType) throws IOException {
        HttpResponse<String> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new IOException("Request interrupted", e);
        }
        int code = response.statusCode();
        if (code >= 200 && code <= 299) {
            if (responseType == Void.class) {
                return null;
            }
            return mapper.readValue(response.body(), responseType);
        }
        throw new IOException("status: " + code + " body: " + response.body());
    }
}
