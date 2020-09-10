package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationPayload {

    private Double longitude;

    private Double latitude;

    @JsonProperty("name")
    private String locationName;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
