package com.collarmc.molib;

import com.collarmc.molib.profile.PlayerProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;
import io.mikael.urlbuilder.UrlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

public final class Mojang {

    private static final Logger LOGGER = LogManager.getLogger(Mojang.class.getName());

    public static final String DEFAULT_AUTH_SERVER = "https://authserver.mojang.com/";
    public static final String DEFAULT_SESSION_SERVER = "https://sessionserver.mojang.com/";

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private final Http http = new Http(MAPPER);
    private final String sessionServerBaseUrl;
    private final String authServerBaseUrl;

    public Mojang(String sessionServerBaseUrl, String authServerBaseUrl) {
        this.sessionServerBaseUrl = sessionServerBaseUrl;
        this.authServerBaseUrl = authServerBaseUrl;
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
        return http.httpGet(sessionServerBaseUrl + "session/minecraft/profile/" + profileId, PlayerProfile.class);
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
            http.post(sessionServerBaseUrl + "session/minecraft/join", joinReq, Void.class);
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
            UrlBuilder builder = UrlBuilder.fromString(sessionServerBaseUrl + "session/minecraft/hasJoined")
                    .addParameter("username", session.username)
                    .addParameter("serverId", serverId);
            HasJoinedResponse hasJoinedResponse = http.httpGet(builder.toString(), HasJoinedResponse.class);
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
            http.post(authServerBaseUrl + "/validate", request, Void.class);
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
            return Optional.of(http.post(authServerBaseUrl + "/refresh", request, RefreshTokenResponse.class));
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
            return Optional.of(http.post(authServerBaseUrl + "/authenticate", AuthenticateResponse.class, AuthenticateResponse.class));
        } catch (Throwable e) {
            LOGGER.error("auth failed " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static String serverPublicKey(KeyPair keyPair) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        printWriter.println("-----BEGIN PUBLIC KEY-----");
        printWriter.println(BaseEncoding.base32().encode(keyPair.getPublic().getEncoded()));
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
}
