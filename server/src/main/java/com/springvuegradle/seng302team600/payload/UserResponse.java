package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class UserResponse {


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

    @JsonProperty("additional_email")
    private List<String> additionalEmails = new ArrayList<>();

    @JsonProperty("activities")
    private Set<ActivityType> activityTypes;

    //adding extra data for the user profile page redirection
    //may have to remove, but this will make it easier
    @JsonProperty("id")
    private Long id;

    @JsonProperty("passports")
    private List<String> passports;

    @JsonProperty("fitness")
    private int fitnessLevel;

    @JsonProperty("gender")
    private User.Gender gender;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("bio")
    private String bio;

    public UserResponse(String lastName, String firstName, String middleName,
                        String nickName, String primaryEmail, List<String> additionalEmails,
                        Set<ActivityType> activityTypes, Long id,
                        List<String> passports, int fitnessLevel,
                        User.Gender gender, Date dateOfBirth, String bio) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.nickName = nickName;
        this.primaryEmail = primaryEmail;
        this.additionalEmails = additionalEmails;
        this.activityTypes = activityTypes;
        this.id = id;
        this.passports = passports;
        this.fitnessLevel = fitnessLevel;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.bio = bio;
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

    public List<String> getAdditionalEmails() { return additionalEmails; }

    public Set<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public Long getId() {
        return id;
    }

    public List<String> getPassports() { return passports; }

    public User.Gender getGender() { return gender; }

    public Date getDateOfBirth() { return dateOfBirth; }

    public String getBio() { return bio; }
}
