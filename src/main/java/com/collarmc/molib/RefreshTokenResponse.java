package com.collarmc.molib;

import com.collarmc.molib.profile.PlayerProfile;
import com.collarmc.molib.profile.PlayerProfileInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class RefreshTokenResponse {
    @JsonProperty("accessToken")
    public final String accessToken;
    @JsonProperty("clientToken")
    public final String clientToken;
    @JsonProperty("selectedProfile")
    public final PlayerProfileInfo selectedProfile;
    @JsonProperty("user")
    public final PlayerProfile user;

    public RefreshTokenResponse(@JsonProperty("accessToken") String accessToken,
                                @JsonProperty("clientToken") String clientToken,
                                @JsonProperty("selectedProfile") PlayerProfileInfo selectedProfile,
                                @JsonProperty("user") PlayerProfile user) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.selectedProfile = selectedProfile;
        this.user = user;
    }
}
