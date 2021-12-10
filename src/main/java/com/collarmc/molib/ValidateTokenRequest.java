package com.collarmc.molib;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public final class ValidateTokenRequest {
    @JsonProperty("accessToken")
    public final String accessToken;
    @JsonProperty("clientToken")
    public final UUID clientToken;

    public ValidateTokenRequest(@JsonProperty("accessToken") String accessToken,
                                @JsonProperty("clientToken") UUID clientToken) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }
}
