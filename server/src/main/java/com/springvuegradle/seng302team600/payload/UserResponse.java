package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse {
    @JsonProperty("Token")
    private String token;

    @JsonProperty("userId")
    private Long userId;

    public UserResponse(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }
}
