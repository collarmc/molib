package com.collarmc.molib.http;

import com.collarmc.molib.profile.PlayerProfileInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public final class RefreshTokenRequest {
    @JsonProperty("accessToken")
    public final String accessToken;
    @JsonProperty("clientToken")
    public final UUID clientToken;
    @JsonProperty("selectedProfile")
    public final PlayerProfileInfo selectedProfile;
    @JsonProperty("requestUser")
    public final boolean requestUser;

    public RefreshTokenRequest(@JsonProperty("accessToken") String accessToken,
                               @JsonProperty("clientToken") UUID clientToken,
                               @JsonProperty("selectedProfile") PlayerProfileInfo selectedProfile,
                               @JsonProperty("requestUser") boolean requestUser) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.selectedProfile = selectedProfile;
        this.requestUser = requestUser;
    }
}
