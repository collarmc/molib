package com.collarmc.molib.session;

import com.collarmc.http.Http;
import com.collarmc.http.Response;
import com.collarmc.molib.authentication.AuthenticationService;
import com.collarmc.molib.profile.PlayerProfile;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static com.collarmc.molib.Mojang.toProfileId;

public class SessionService {

    private static final Logger LOGGER = LogManager.getLogger(SessionService.class);

    private final String sessionServerBaseUrl;
    private final Http http;

    public SessionService(String sessionServerBaseUrl, Http http) {
        this.sessionServerBaseUrl = sessionServerBaseUrl;
        this.http = http;
    }

    /**
     * Fetch player profile by id
     * @param id of player
     * @return player profile
     * @throws IOException if error occurs
     */
    public Optional<PlayerProfile> getProfile(UUID id) throws IOException {
        String profileId = id.toString().replace("-", "");
        return http.httpGet(URI.create(String.format(sessionServerBaseUrl + "session/minecraft/profile/%s", profileId)), Response.json(PlayerProfile.class));
    }

    /**
     * Join server
     * @param session to join with
     * @param serverId calculated secret
     * @return response
     */
    public Optional<JoinServerResponse> joinServer(MinecraftSession session, String serverId) {
        JoinServerRequest joinReq = new JoinServerRequest(AuthenticationService.Agent.MINECRAFT, session.accessToken, toProfileId(session.id), serverId);
        http.post(URI.create(sessionServerBaseUrl + "session/minecraft/join"), joinReq, Response.noContent());
        return Optional.of(new JoinServerResponse(serverId));
    }

    /**
     * Verify that the client can login to the server
     * @param session to check
     * @param serverId server id
     * @return client verified or not
     */
    public boolean hasJoined(MinecraftSession session, String serverId) {
        try {
            URI uri = URI.create(String.format(sessionServerBaseUrl + "session/minecraft/hasJoined?username=%s&serverId=%s", URLEncoder.encode(session.username, StandardCharsets.UTF_8), serverId));
            Optional<HasJoinedResponse> hasJoinedResponse = http.httpGet(uri, Response.json(HasJoinedResponse.class));
            return hasJoinedResponse.map(resp -> resp.id.equals(toProfileId(session.id))).orElse(false);
        } catch (Throwable e) {
            LOGGER.error("Couldn't verify " + session.username,e);
            return false;
        }
    }

    public static final class HasJoinedResponse {
        @JsonProperty("id")
        public final String id;
        @JsonProperty("name")
        public final String name;

        @JsonCreator
        public HasJoinedResponse(@JsonProperty("id") String id,
                                 @JsonProperty("name") String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static final class JoinServerRequest {
        @JsonProperty("agent")
        public final AuthenticationService.Agent agent;
        @JsonProperty("accessToken")
        public final String accessToken;
        @JsonProperty("selectedProfile")
        public final String selectedProfile;
        @JsonProperty("serverId")
        public final String serverId;

        @JsonCreator
        public JoinServerRequest(@JsonProperty("agent") AuthenticationService.Agent agent,
                                 @JsonProperty("accessToken") String accessToken,
                                 @JsonProperty("selectedProfile") String selectedProfile,
                                 @JsonProperty("serverId") String serverId) {
            this.agent = agent;
            this.accessToken = accessToken;
            this.selectedProfile = selectedProfile;
            this.serverId = serverId;
        }
    }

    public static class JoinServerResponse {
        @JsonProperty("serverId")
        public final String serverId;

        public JoinServerResponse(@JsonProperty("serverId") String serverId) {
            this.serverId = serverId;
        }
    }

    /**
     * Represents a session of the Minecraft client
     */
    public static class MinecraftSession {
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
}
