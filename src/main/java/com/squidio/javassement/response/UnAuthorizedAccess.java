package com.squidio.javassement.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class UnAuthorizedAccess implements Serializable {
    @JsonProperty(value = "message")
    private String message;

    public UnAuthorizedAccess(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


}
