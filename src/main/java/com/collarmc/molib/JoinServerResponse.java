package com.collarmc.molib;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinServerResponse {
    @JsonProperty("serverId")
    public final String serverId;

    public JoinServerResponse(@JsonProperty("serverId") String serverId) {
        this.serverId = serverId;
    }
}
