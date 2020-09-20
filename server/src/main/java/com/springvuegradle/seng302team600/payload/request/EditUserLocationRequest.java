package com.springvuegradle.seng302team600.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EditUserLocationRequest {

    @JsonProperty("public_location")
    private LocationRequest publicLocation;

    @JsonProperty("private_location")
    private LocationRequest privateLocation;
    
    public LocationRequest getPublicLocation() {
        return publicLocation;
    }

    public void setPublicLocation(LocationRequest publicLocation) {
        this.publicLocation = publicLocation;
    }

    public LocationRequest getPrivateLocation() {
        return privateLocation;
    }

    public void setPrivateLocation(LocationRequest privateLocation) {
        this.privateLocation = privateLocation;
    }
}
