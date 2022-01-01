package com.collarmc.molib.encryption;

import javax.crypto.SecretKey;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * Used for protocol encryption
 */
public final class Encryption {
    public static String encodeServerPublicKey(PublicKey publicKey) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        printWriter.println("-----BEGIN PUBLIC KEY-----");
        printWriter.println(Base32.encode(publicKey.getEncoded()));
        printWriter.println("-----END PUBLIC KEY-----");
        return writer.toString();
    }

    public static KeyPair createServerKeyPair() {
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

    public static String createSharedSecret(String base, PublicKey publicKey, SecretKey secretKey) {
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

    public Encryption() {}
}
