package com.collarmc.molib;

import com.collarmc.http.Http;
import com.collarmc.molib.authentication.AuthenticationService;
import com.collarmc.molib.session.SessionService;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Authenticator;
import java.net.ProxySelector;
import java.util.UUID;

public final class Mojang {

    private static final Logger LOGGER = LogManager.getLogger(Mojang.class.getName());

    public static final String DEFAULT_AUTH_SERVER = "https://authserver.mojang.com";
    public static final String DEFAULT_SESSION_SERVER = "https://sessionserver.mojang.com";

    public static final ObjectMapper MAPPER = JsonMapper.builder()
            .configure(JsonReadFeature.ALLOW_TRAILING_COMMA, true)
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, false)
            .build();

    private final Http http;
    private final String sessionServerBaseUrl;
    private final String authServerBaseUrl;
    public final boolean isUsingProxy;

    public Mojang(String sessionServerBaseUrl, String authServerBaseUrl, ProxySelector proxySelector, Authenticator authenticator) {
        this.http = new Http(proxySelector, authenticator, MAPPER);
        this.sessionServerBaseUrl = sessionServerBaseUrl.endsWith("/") ? sessionServerBaseUrl : sessionServerBaseUrl + "/";
        this.authServerBaseUrl = authServerBaseUrl.endsWith("/") ? authServerBaseUrl : authServerBaseUrl + "/";
        this.isUsingProxy = proxySelector != null;
    }

    public Mojang() {
        this(Mojang.DEFAULT_SESSION_SERVER, Mojang.DEFAULT_AUTH_SERVER, null, null);
    }

    public AuthenticationService auth() {
        return new AuthenticationService(authServerBaseUrl, http);
    }

    public SessionService sessions() {
        return new SessionService(sessionServerBaseUrl, http);
    }
}
