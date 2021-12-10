package com.collarmc.molib;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public final class AuthenticateRequest {
    @JsonProperty("agent")
    public final Agent agent;
    @JsonProperty("username")
    public final String username;
    @JsonProperty("password")
    public final String password;
    @JsonProperty("clientToken")
    public final UUID clientToken;
    @JsonProperty("requestUser")
    public final boolean requestUser;

    public AuthenticateRequest(@JsonProperty("agent") Agent agent,
                               @JsonProperty("username") String username,
                               @JsonProperty("password") String password,
                               @JsonProperty("clientToken") UUID clientToken,
                               @JsonProperty("requestUser") boolean requestUser) {
        this.agent = agent;
        this.username = username;
        this.password = password;
        this.clientToken = clientToken;
        this.requestUser = requestUser;
    }
}
