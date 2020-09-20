package com.springvuegradle.seng302team600.payload.pins;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.Activity;

import java.util.Date;

public class ActivityPin extends Pin {

    @JsonProperty("location_name")
    private String locationName;

    @JsonProperty("title")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("start_time")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("end_time")
    private Date endTime;

    public ActivityPin() {
    }

    public ActivityPin(Activity activity, Long userId) {
        super(activity, userId);
        this.title = activity.getName();
        this.locationName = activity.getLocation().getLocationName();
        if (!activity.isContinuous()) {
            this.startTime = activity.getStartTime();
            this.endTime = activity.getEndTime();
        }
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
