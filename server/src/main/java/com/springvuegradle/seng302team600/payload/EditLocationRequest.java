package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.Location;

public class EditLocationRequest {

    @JsonProperty("public_location")
    private LocationPayload publicLocation;

    @JsonProperty("private_location")
    private LocationPayload privateLocation;
    
    public LocationPayload getPublicLocation() {
        return publicLocation;
    }

    public void setPublicLocation(LocationPayload publicLocation) {
        this.publicLocation = publicLocation;
    }

    public LocationPayload getPrivateLocation() {
        return privateLocation;
    }

    public void setPrivateLocation(LocationPayload privateLocation) {
        this.privateLocation = privateLocation;
    }
}
