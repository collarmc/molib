package com.collarmc.molib.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Agent {

    public static final Agent MINECRAFT = new Agent("Minecraft", 1);

    @JsonProperty("name")
    public final String name;
    @JsonProperty("version")
    public final Integer version;

    @JsonCreator
    public Agent(@JsonProperty("name") String name,
                 @JsonProperty("version") Integer version) {
        this.name = name;
        this.version = version;
    }
}
