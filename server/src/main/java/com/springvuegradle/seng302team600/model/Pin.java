package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.enumeration.PinType;


public class Pin {


    @JsonProperty("pin_type")
    private PinType pinType;

    @JsonProperty("id")
    private long id;

    @JsonProperty("colour")
    private String colour;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("is_focus")
    private boolean isFocus;

    @JsonProperty("latitude")
    private Double latitude;

    /**
     * Create a Pin with location specified
     * @param id The id given to the pin from the front-end, may be of User or Activity
     * @param pinType The type of pin - set by the PinType enum
     * @param colour The colour of the marker represented by a string
     * @param longitude The double value of the longitudinal coordinate
     * @param isFocus A boolean value representing the focus
     * @param latitude The double value of the latitudinal coordinate
     */
    public Pin(long id, PinType pinType, String colour, Double longitude, boolean isFocus, Double latitude) {
        this.id = id;
        this.pinType = pinType;
        this.colour = colour;
        this.longitude = longitude;
        this.isFocus = isFocus;
        this.latitude = latitude;
    }

    /**
     * Default constructor for pins
     */
    public Pin() {}

    public long getId() {
        return id;
    }

    public PinType getPinType() {
        return pinType;
    }

    public String getColour() { return colour; }

    public Double getLongitude() {
        return longitude;
    }

    public boolean getIsFocus() {
        return isFocus;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setPinType(PinType type) {
        pinType = type;
    }

    public void setColour(String col) {
        colour = col;
    }

    public void setLongitude(Double longCoord) {
        longitude = longCoord;
    }

    public void setIsFocus(boolean focus) {
        isFocus = focus;
    }

    public void setId(long pinId) {id = pinId;}


}
