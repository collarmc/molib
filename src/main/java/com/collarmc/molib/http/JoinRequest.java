package com.collarmc.molib.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class JoinRequest {
    @JsonProperty("agent")
    public final Agent agent;
    @JsonProperty("accessToken")
    public final String accessToken;
    @JsonProperty("selectedProfile")
    public final String selectedProfile;
    @JsonProperty("serverId")
    public final String serverId;

    @JsonCreator
    public JoinRequest(@JsonProperty("agent") Agent agent,
                       @JsonProperty("accessToken") String accessToken,
                       @JsonProperty("selectedProfile") String selectedProfile,
                       @JsonProperty("serverId") String serverId) {
        this.agent = agent;
        this.accessToken = accessToken;
        this.selectedProfile = selectedProfile;
        this.serverId = serverId;
    }
}
