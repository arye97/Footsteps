package com.springvuegradle.seng302team600.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Coordinates {

    @JsonProperty("lng")
    private Double longitude;

    @JsonProperty("lat")
    private Double latitude;

    public Coordinates() {
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}
