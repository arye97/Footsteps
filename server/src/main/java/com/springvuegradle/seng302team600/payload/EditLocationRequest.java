package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.Location;

public class EditLocationRequest {

    @JsonProperty("public_location")
    private Location publicLocation;

    @JsonProperty("private_location")
    private Location privateLocation;
    
    public Location getPublicLocation() {
        return publicLocation;
    }

    public void setPublicLocation(Location publicLocation) {
        this.publicLocation = publicLocation;
    }

    public Location getPrivateLocation() {
        return privateLocation;
    }

    public void setPrivateLocation(Location privateLocation) {
        this.privateLocation = privateLocation;
    }
}
