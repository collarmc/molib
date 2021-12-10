package com.collarmc.molib;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class HasJoinedResponse {
    @JsonProperty("id")
    public final String id;
    @JsonProperty("name")
    public final String name;

    @JsonCreator
    public HasJoinedResponse(@JsonProperty("id") String id,
                             @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }
}
