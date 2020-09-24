package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.payload.request.LocationRequest;

import javax.persistence.*;

/**
 * This class stores the longitude and latitude of a location, as well as the name of the location as defined by the user
 * This is intended for use with both the user and the activity
 */
@Entity
@Table(name = "location")
public class Location {
    final static private int NAME_LEN = 300;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", nullable = false)
    @JsonProperty("id")
    private Long locationId;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "location_name", length = NAME_LEN, nullable = false)
    @JsonProperty("name")
    private String locationName;


    public Location() {}

    /**
     * Creates a new location with a longitude, latitude, and name
     * @param longitude the longitude of the location
     * @param latitude the latitude of the location
     * @param name the name of the location
     */
    public Location(Double longitude, Double latitude, String name) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.locationName = name;
    }

    /**
     * Creates a new location by LocationPayload
     * @param locationRequest payload of location
     */
    public Location(LocationRequest locationRequest) {
        this.longitude = locationRequest.getLongitude();
        this.latitude = locationRequest.getLatitude();
        this.locationName = locationRequest.getLocationName();
    }

    public Long getLocationId() {
        return locationId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
