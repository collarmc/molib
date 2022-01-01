package com.collarmc.http;

import com.collarmc.molib.Mojang;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

/**
 * Http response
 * @param <T> response type
 */
public abstract class Response<T> {

    abstract T map(String contents);

    /**
     * No content response
     * @return void response
     */
    public static Response<Void> noContent() {
        return new Response<Void>() {
            @Override
            Void map(String contents) {
                return null;
            }
        };
    }

    /**
     * Map a response to a JSON object
     * @param tClass to map to
     * @param <T> type to map to
     * @return response
     */
    public static <T> Response<T> json(Class<T> tClass) {
        return new Response<T>() {
            @Override
            public T map(String contents) {
                try {
                    return Mojang.MAPPER.readValue(contents, tClass);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    /**
     * Map a response to a JSON object
     * @param typeReference to map to
     * @param <T> type to map to
     * @return response
     */
    public static <T> Response<T> json(TypeReference<T> typeReference) {
        return new Response<T>() {
            @Override
            public T map(String contents) {
                try {
                    return Mojang.MAPPER.readValue(contents, typeReference);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
