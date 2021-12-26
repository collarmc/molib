package com.collarmc.molib.http;

import com.collarmc.molib.MinecraftSession;
import com.collarmc.molib.profile.PlayerProfile;
import com.collarmc.molib.profile.PlayerProfileInfo;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class AuthenticateResponse {
    @JsonProperty("clientToken")
    public final String clientToken;
    @JsonProperty("accessToken")
    public final String accessToken;
    @JsonProperty("selectedProfile")
    public final PlayerProfileInfo selectedProfile;
    @JsonProperty("user")
    public final PlayerProfile user;
    @JsonProperty("availableProfiles")
    public final List<PlayerProfileInfo> availableProfiles;

    @JsonCreator
    public AuthenticateResponse(@JsonProperty("clientToken") String clientToken,
                                @JsonProperty("accessToken") String accessToken,
                                @JsonProperty("selectedProfile") PlayerProfileInfo selectedProfile,
                                @JsonProperty("user") PlayerProfile user,
                                @JsonProperty("availableProfiles") List<PlayerProfileInfo> availableProfiles) {
        this.clientToken = clientToken;
        this.accessToken = accessToken;
        this.selectedProfile = selectedProfile;
        this.user = user;
        this.availableProfiles = availableProfiles;
    }

    public MinecraftSession toMinecraftSession() {
        return new MinecraftSession(user.toId(), user.name, accessToken);
    }
}
