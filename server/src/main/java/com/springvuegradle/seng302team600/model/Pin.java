package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.enumeration.PinType;

public class Pin {


    private PinType pinType;
    private long userId;
    private long activityId;
    private String colour;
    private Double longitude;
    private boolean isFocus;
    private Double latitude;

    public Pin(String name) {
        this.longitude = 1.0;
        this.latitude = 1.0;
    }
    public Pin() {}

}
