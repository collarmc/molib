package com.collarmc.molib;

import com.collarmc.molib.authentication.AuthenticationService;
import com.collarmc.molib.profile.PlayerProfile;
import com.collarmc.molib.profile.PlayerProfileInfo;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.collarmc.molib.authentication.AuthenticationService.Agent.MINECRAFT;

@Ignore
public class MojangTest {

    @Test
    public void getProfileByName() throws IOException {
        Mojang mojang = new Mojang();
        Optional<PlayerProfileInfo> profile = mojang.api().getByName("Notch");
        Assert.assertTrue(profile.isPresent());
        Assert.assertEquals("Notch", profile.get().name);
    }

    @Test
    public void getProfileById() throws IOException {
        Mojang mojang = new Mojang();
        Optional<PlayerProfile> profile = mojang.sessions().getProfile(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"));
        Assert.assertTrue(profile.isPresent());
        Assert.assertEquals("Notch", profile.get().name);
    }

    @Test
    public void getProfileWithBadId() throws IOException {
        Mojang mojang = new Mojang();
        Optional<PlayerProfile> profile = mojang.sessions().getProfile(UUID.randomUUID());
        Assert.assertFalse(profile.isPresent());
    }

    @Test
    public void authenticate() {

        Mojang mojang = new Mojang();
        Optional<AuthenticationService.AuthenticateResponse> resp = mojang.auth().authenticate(new AuthenticationService.AuthenticateRequest(
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
