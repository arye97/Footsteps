package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.ActivityType;

import java.util.Set;

public class UserSearchResponse {


    /**
     *  Based from U12 API Spec
     */

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("middlename")
    private String middleName;

    @JsonProperty("nickname")
    private String nickName;

    @JsonProperty("primary_email")
    private String primaryEmail;

    @JsonProperty("activities")
    private Set<ActivityType> activityTypes;

    //adding userId for the user profile page redirection
    //may have to remove, but this will make it easier
    @JsonProperty("userId")
    private Long userId;

    public UserSearchResponse(String lastName, String firstName, String middleName,
                              String nickName, String primaryEmail,
                              Set<ActivityType> activityTypes, Long userId) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.nickName = nickName;
        this.primaryEmail = primaryEmail;
        this.activityTypes = activityTypes;
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public Set<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public Long getUserId() {
        return userId;
    }
}
