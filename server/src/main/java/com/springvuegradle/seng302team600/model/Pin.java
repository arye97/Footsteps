package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.enumeration.PinType;

import java.util.ArrayList;
import java.util.List;


public class Pin {

    private static final String BLUE = "blue";
    private static final String RED = "red";
    private static final String GREEN = "green";


    @JsonProperty("pin_type")
    private PinType pinType;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("colour")
    private String colour;

    @JsonProperty("lng")
    private Double longitude;

    @JsonProperty("lat")
    private Double latitude;

    /**
     * Create a Pin with location specified
     *
     * @param pinType   The type of pin - set by the PinType enum
     * @param id        The id of the activity or user, which is defined by the pinType
     * @param colour    The colour of the marker represented by a string
     * @param longitude The double value of the longitudinal coordinate
     * @param latitude  The double value of the latitudinal coordinate
     */
    public Pin(long id, PinType pinType, String colour, Double longitude, Double latitude) {
        this.id = id;
        this.pinType = pinType;
        this.colour = colour;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Pin(Activity activity, Long userId) {
        this.pinType = PinType.ACTIVITY;
        this.id = activity.getActivityId();
        this.longitude = activity.getLocation().getLongitude();
        this.latitude = activity.getLocation().getLatitude();

        if (userId.equals(activity.getCreatorUserId())) {
            this.colour = BLUE;
        } else {
            this.colour = GREEN;
        }
    }

    public Pin(User user) {
        this.pinType = PinType.USER;
        this.id = user.getUserId();
        List<Double> coords = getUserCoords(user);
        this.latitude = coords.get(0);
        this.longitude = coords.get(1);
        this.colour = RED;
    }

    /**
     * Get the users coords based from their location
     *
     * @param user the user to get the location from
     * @return a list of the lat, long coords where index 0 is latitude and index 1 is longitude
     */
    private List<Double> getUserCoords(User user) {
        List<Double> coords = new ArrayList<>();
        if (user.getPrivateLocation() != null) {
            coords.add(user.getPrivateLocation().getLatitude());
            coords.add(user.getPrivateLocation().getLongitude());
        } else if (user.getPublicLocation() != null) {
            coords.add(user.getPublicLocation().getLatitude());
            coords.add(user.getPublicLocation().getLongitude());
        } else {
            //new zealand coords
            coords.add(-40.9006);
            coords.add(174.8860);
        }
        return coords;
    }

    /**
     * Default constructor for pins
     */
    public Pin() {
    }

    public long getId() {
        return id;
    }

    public PinType getPinType() {
        return pinType;
    }

    public String getColour() {
        return colour;
    }

    public Double getLongitude() {
        return longitude;
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

    public void setId(long pinId) {id = pinId;}


}
