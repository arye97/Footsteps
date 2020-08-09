package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IsFollowingResponse {

    @JsonProperty("subscribed")
    private boolean isFollowing;

    public IsFollowingResponse(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public boolean isFollowing() {
        return isFollowing;
    }
}
