package com.collarmc.molib;

import com.collarmc.molib.http.*;
import com.collarmc.molib.profile.PlayerProfile;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Optional;
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

    private final Http http = new Http(MAPPER);
    private final String sessionServerBaseUrl;
    private final String authServerBaseUrl;

    public Mojang(String sessionServerBaseUrl, String authServerBaseUrl) {
        this.sessionServerBaseUrl = sessionServerBaseUrl.endsWith("/") ? sessionServerBaseUrl : sessionServerBaseUrl + "/";
        this.authServerBaseUrl = authServerBaseUrl.endsWith("/") ? authServerBaseUrl : authServerBaseUrl + "/";
    }

    public Mojang() {
        this(Mojang.DEFAULT_SESSION_SERVER, Mojang.DEFAULT_AUTH_SERVER);
    }

    /**
     * Fetch player profile by id
     * @param id of player
     * @return player profile
     * @throws IOException if error occurs
     */
    public PlayerProfile getProfile(UUID id) throws IOException {
        String profileId = id.toString().replace("-", "");
        return http.httpGet(URI.create(String.format(sessionServerBaseUrl + "session/minecraft/profile/%s", profileId)), PlayerProfile.class);
    }

    /**
     * Join
     * @param session
     * @param serverPublicKey
     * @param sharedSecret
     * @return
     */
    public Optional<JoinServerResponse> joinServer(MinecraftSession session, byte[] serverPublicKey, byte[] sharedSecret) {
        try {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
            md.update("".getBytes(StandardCharsets.ISO_8859_1));
            md.update(sharedSecret);
            md.update(serverPublicKey);
            byte[] digest = md.digest();
            String serverId = new BigInteger(digest).toString(16);
            JoinRequest joinReq = new JoinRequest(Agent.MINECRAFT, session.accessToken, toProfileId(session.id), serverId);
            http.post(URI.create(sessionServerBaseUrl + "session/minecraft/join"), joinReq, Void.class);
            return Optional.of(new JoinServerResponse(serverId));
        } catch (IOException e) {
            LOGGER.error("Could not start verification with Mojang", e);
            return Optional.empty();
        }
    }

    /**
     * Verify that the client can login to the server
     * @param session to check
     * @param serverId server id
     * @return client verified or not
     */
    public boolean hasJoined(MinecraftSession session, String serverId) {
        try {
            URI uri = URI.create(String.format(sessionServerBaseUrl + "session/minecraft/hasJoined?username=%s&serverId=%s", session.username, serverId));
            HasJoinedResponse hasJoinedResponse = http.httpGet(uri, HasJoinedResponse.class);
            return hasJoinedResponse.id.equals(toProfileId(session.id));
        } catch (Throwable e) {
            LOGGER.error("Couldn't verify " + session.username,e);
            return false;
        }
    }

    /**
     * Validates the clients access token
     * @param request to send
     * @return accessToken is valid or not
     */
    public boolean validateToken(ValidateTokenRequest request) {
        try {
            http.post(URI.create(authServerBaseUrl + "/validate"), request, Void.class);
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
        try {
            return Optional.of(http.post(URI.create(authServerBaseUrl + "/refresh"), request, RefreshTokenResponse.class));
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    /**
     * Authenticate with Mojang
     * @param request to send
     * @return response on success or empty on failure
     */
    public Optional<AuthenticateResponse> authenticate(AuthenticateRequest request) {
        try {
            return Optional.of(http.post(URI.create(authServerBaseUrl + "/authenticate"), request, AuthenticateResponse.class));
        } catch (Throwable e) {
            LOGGER.error("auth failed " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static String serverPublicKey(KeyPair keyPair) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        printWriter.println("-----BEGIN PUBLIC KEY-----");
        printWriter.println(Base32.encode(keyPair.getPublic().getEncoded()));
        printWriter.println("-----END PUBLIC KEY-----");
        return writer.toString();
    }

    public static String toProfileId(UUID id) {
        return id.toString().replace("-", "");
    }

    private static KeyPair createKeyPair() {
        KeyPair kp;
        try {
            KeyPairGenerator keyPairGene = KeyPairGenerator.getInstance("RSA");
            keyPairGene.initialize(1024);
            kp = keyPairGene.genKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("problem generating the key", e);
        }
        return kp;
    }

    public static String getServerId(String base, PublicKey publicKey, SecretKey secretKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(base.getBytes(StandardCharsets.ISO_8859_1));
            digest.update(secretKey.getEncoded());
            digest.update(publicKey.getEncoded());
            return (new BigInteger(digest.digest())).toString(16);
        } catch (NoSuchAlgorithmException var5) {
            throw new IllegalStateException("Server ID hash algorithm unavailable.", var5);
        }
    }
}
