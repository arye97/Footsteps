package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.Result;
import com.springvuegradle.seng302team600.model.User;

public class ResultResponse {


    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("value")
    private String value;

    @JsonProperty("did_not_finish")
    private boolean didNotFinish;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("user_name")
    private String userName;

    public ResultResponse(Result result, User user) {
        userId = result.getUserId();
        value = result.getValue();
        didNotFinish = result.isDidNotFinish();
        comment = result.getComment();
        userName = user.getFirstName() + " " + user.getLastName();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getValue() {
        return value;
    }

    public void setValues(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean getDidNotFinish() {
        return didNotFinish;
    }

    public void setDidNotFinish(boolean didNotFinish) {
        this.didNotFinish = didNotFinish;
    }

}