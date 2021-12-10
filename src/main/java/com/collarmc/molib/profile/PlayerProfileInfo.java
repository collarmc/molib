package com.collarmc.molib.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerProfileInfo {
    @JsonProperty("id")
    public final String id;
    @JsonProperty("name")
    public final String name;

    public PlayerProfileInfo(@JsonProperty("id") String id,
                             @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public UUID toId() {
        return UUID.fromString(id.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }

    public static final class PlayerProfileProperty {
        @JsonProperty("name")
        public final String name;
        @JsonProperty("value")
        public final String value;

        public PlayerProfileProperty(@JsonProperty("name") String name,
                                     @JsonProperty("value") String value) {
            this.name = name;
            this.value = value;
        }
    }

    public static final class TexturesProperty {
        @JsonProperty("timestamp")
        public final long timestamp;
        @JsonProperty("profileId")
        public final String profileId;
        @JsonProperty("profileName")
        public final String profileName;
        public final Map<String, Texture> textures;

        public TexturesProperty(@JsonProperty("timestamp") long timestamp,
                                @JsonProperty("profileId") String profileId,
                                @JsonProperty("profileName") String profileName,
                                @JsonProperty("textures") Map<String, Texture> textures) {
            this.timestamp = timestamp;
            this.profileId = profileId;
            this.profileName = profileName;
            this.textures = textures;
        }

        public Optional<String> skin() {
            return getTexture("SKIN");
        }

        public Optional<String> cape() {
            return getTexture("CAPE");
        }

        private Optional<String> getTexture(String name) {
            Texture texture = textures.get(name);
            return texture == null ? Optional.empty() : Optional.of(texture.url);
        }
    }

    public static final class Texture {
        @JsonProperty("url")
        public final String url;

        public Texture(@JsonProperty("url") String url) {
            this.url = url;
        }
    }
}
