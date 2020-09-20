package com.springvuegradle.seng302team600.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.User;


/**
 * The payload to be returned to the front-end for the GET participants endpoint.
 * Json should be formatted as follows:
 * - lastname
 * - firstname
 * - id
 */
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
