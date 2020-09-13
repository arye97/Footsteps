package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.Location;
import com.springvuegradle.seng302team600.model.User;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class LocationResponse {

    @JsonProperty("id")
    private Long locationId;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("name")
    private String locationName;

    public LocationResponse(Location location) {
        this.locationId = location.getLocationId();
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.locationName = location.getLocationName();
    }
}
