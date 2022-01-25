package com.squidio.javassement.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Users implements Serializable {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "name")
    private String name;

    public Users() {
    }

    public Users(String id, String name) {
        this.id = id;
        this.name = name;
    }
   @JsonAnyGetter
    public String getId() {
        return id;
    }
   @JsonAnySetter
    public void setId(String id) {
        this.id = id;
    }
    @JsonAnyGetter
    public String getName() {
        return name;
    }
    @JsonAnySetter
    public void setName(String name) {
        this.name = name;
    }
}
