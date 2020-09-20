package com.springvuegradle.seng302team600.payload.pins;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.Activity;

import java.util.Date;

public class ActivityPin extends Pin {

    @JsonProperty("location_name")
    private final String locationName;

    @JsonProperty("title")
    private final String title;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("start_time")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("end_time")
    private Date endTime;

    public ActivityPin(Activity activity, Long userId) {
        super(activity, userId);
        this.title = activity.getName();
        this.locationName = activity.getLocation().getLocationName();
        if (!activity.isContinuous()) {
            this.startTime = activity.getStartTime();
            this.endTime = activity.getEndTime();
        }
    }
}
