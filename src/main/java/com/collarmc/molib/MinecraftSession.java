package com.collarmc.molib;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * Represents a session of the Minecraft client
 */
public class MinecraftSession {
    @JsonProperty("id")
    public final UUID id;
    @JsonProperty("username")
    public final String username;
    @JsonIgnore
    public final String accessToken;

    @JsonCreator
    public MinecraftSession(
            @JsonProperty("id") UUID id,
            @JsonProperty("username") String username,
            @JsonProperty("accessToken") String accessToken) {
        this.id = id;
        this.username = username;
        this.accessToken = accessToken;
    }
}
