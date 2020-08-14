package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.User;

import java.util.List;
import java.util.Set;

public class ParticipantResponse {

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("id")
    private Long id;

    public ParticipantResponse(User user) {
        this.lastName = user.getLastName();
        this.firstName = user.getFirstName();
        this.id = user.getUserId();
    }

    public ParticipantResponse() {}

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Resp: %s %s with id %s", firstName, lastName, id);
    }
}
