package com.springvuegradle.seng302team600.payload.pins;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.User;

public class UserPin extends Pin {

    @JsonProperty("title")
    private String title;

    @JsonProperty("location_name")
    private String locationName;

    public UserPin() {
    }

    public UserPin(User user) {
        super(user);
        if (user.getPrivateLocation() != null) {
            this.title = "Private Location";
            this.locationName = user.getPrivateLocation().getLocationName();
        } else if (user.getPublicLocation() != null) {
            this.title = "Public Location";
            this.locationName = user.getPublicLocation().getLocationName();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
