package com.collarmc.molib;

import com.collarmc.molib.http.Agent;
import com.collarmc.molib.http.AuthenticateRequest;
import com.collarmc.molib.http.AuthenticateResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static com.collarmc.molib.http.Agent.*;

public class MojangTest {
    @Test
    public void authenticate() {

        Mojang mojang = new Mojang();
        Optional<AuthenticateResponse> resp = mojang.authenticate(new AuthenticateRequest(
                MINECRAFT,
                username(),
                password(),
                null,
                null)
        );
        Assert.assertTrue(resp.isPresent());
    }

    private static String username() {
        return System.getenv("MINECRAFT_USERNAME");
    }

    private static String password() {
        return System.getenv("MINECRAFT_PASSWORD");
    }
}
