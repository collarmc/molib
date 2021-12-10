package com.collarmc.molib.profile;

import com.collarmc.molib.Mojang;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public final class PlayerProfile extends PlayerProfileInfo {
    @JsonProperty("properties")
    public final List<PlayerProfileProperty> properties;

    public PlayerProfile(@JsonProperty("id") String id,
                         @JsonProperty("name") String name,
                         @JsonProperty("properies") List<PlayerProfileProperty> properties) {
        super(id, name);
        this.properties = properties;
    }

    public Optional<TexturesProperty> textures() {
        return properties.stream().filter(playerProfileProperty -> playerProfileProperty.name.equals("textures"))
                .map(playerProfileProperty -> playerProfileProperty.value)
                .map(value -> Base64.getUrlDecoder().decode(value))
                .map(bytes -> {
                    try {
                        return Mojang.MAPPER.readValue(bytes, TexturesProperty.class);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .findFirst();
    }
}
