package com.collarmc.molib.authentication;

import com.collarmc.http.Http;
import com.collarmc.http.Response;
import com.collarmc.molib.profile.PlayerProfile;
import com.collarmc.molib.profile.PlayerProfileInfo;
import com.collarmc.molib.session.SessionService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthenticationService {

    private static final Logger LOGGER = LogManager.getLogger(AuthenticationService.class);

    private final String authServerBaseUrl;
    private final Http http;

    public AuthenticationService(String authServerBaseUrl, Http http) {
        this.authServerBaseUrl = authServerBaseUrl;
        this.http = http;
    }

    /**
     * Validates the clients access token
     * @param request to send
     * @return accessToken is valid or not
     */
    public boolean validateToken(ValidateTokenRequest request) {
        try {
            http.post(URI.create(authServerBaseUrl + "/validate"), request, Response.noContent());
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Validates the clients access token
     * @param request to send
     * @return accessToken is valid or not
     */
    public Optional<RefreshTokenResponse> refreshToken(RefreshTokenRequest request) {
        return http.post(URI.create(authServerBaseUrl + "/refresh"), request, Response.json(RefreshTokenResponse.class));
    }

    /**
     * Authenticate with Mojang
     * @param request to send
     * @return response on success or empty on failure
     */
    public Optional<AuthenticateResponse> authenticate(AuthenticateRequest request) {
        try {
            return http.post(URI.create(authServerBaseUrl + "/authenticate"), request, Response.json(AuthenticateResponse.class));
        } catch (Throwable e) {
            LOGGER.error("auth failed " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static class Agent {

        public static final Agent MINECRAFT = new Agent("Minecraft", 1);

        @JsonProperty("name")
        public final String name;
        @JsonProperty("version")
        public final Integer version;

        @JsonCreator
        public Agent(@JsonProperty("name") String name,
                     @JsonProperty("version") Integer version) {
            this.name = name;
            this.version = version;
        }
    }

    public static final class AuthenticateRequest {
        @JsonProperty("agent")
        public final Agent agent;
        @JsonProperty("username")
        public final String username;
        @JsonProperty("password")
        public final String password;
        @JsonProperty("clientToken")
        public final UUID clientToken;
        @JsonProperty("requestUser")
        public final Boolean requestUser;

        @JsonCreator
        public AuthenticateRequest(@JsonProperty("agent") Agent agent,
                                   @JsonProperty("username") String username,
                                   @JsonProperty("password") String password,
                                   @JsonProperty("clientToken") UUID clientToken,
                                   @JsonProperty("requestUser") Boolean requestUser) {
            this.agent = agent;
            this.username = username;
            this.password = password;
            this.clientToken = clientToken;
            this.requestUser = requestUser;
        }
    }

    public static final class AuthenticateResponse {
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

        public SessionService.MinecraftSession toMinecraftSession() {
            return new SessionService.MinecraftSession(selectedProfile.toId(), selectedProfile.name, accessToken);
        }
    }

    public static final class RefreshTokenRequest {
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

    public static final class RefreshTokenResponse {
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

    public static final class ValidateTokenRequest {
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
}
