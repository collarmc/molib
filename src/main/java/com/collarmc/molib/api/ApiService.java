package com.collarmc.molib.api;

import com.collarmc.http.Http;
import com.collarmc.http.Response;
import com.collarmc.molib.profile.PlayerProfileInfo;
import com.collarmc.molib.session.SessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.Optional;

public final class ApiService {
    private static final Logger LOGGER = LogManager.getLogger(SessionService.class);

    private final String mojangApiBaseUrl;
    private final Http http;

    public ApiService(String mojangApiBaseUrl, Http http) {
        this.mojangApiBaseUrl = mojangApiBaseUrl;
        this.http = http;
    }

    /**
     * Fetch player profile by id
     * @param playerName of player
     * @return player profile
     */
    public Optional<PlayerProfileInfo> getByName(String playerName) {
        return http.httpGet(URI.create(String.format(mojangApiBaseUrl + "/users/profiles/minecraft/%s", playerName)), Response.json(PlayerProfileInfo.class));
    }
}
