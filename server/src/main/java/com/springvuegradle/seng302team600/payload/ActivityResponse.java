package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.ActivityType;

import java.util.Date;
import java.util.Set;

public class ActivityResponse {

    @JsonProperty("id")
    private Long activityId;

    @JsonProperty("creatorUserId")
    private Long creatorUserId;

    @JsonProperty("activity_name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("activity_type")
    private Set<ActivityType> activityTypes;

    @JsonProperty("continuous")
    private boolean continuous;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("start_time")
    private Date startTime;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("end_time")
    private Date endTime;

    @JsonProperty("location")
    private String location;

    public ActivityResponse() { }

    public ActivityResponse(Activity activity) {
        activityId = activity.getActivityId();
        creatorUserId = activity.getCreatorUserId();
        name = activity.getName();
        description = activity.getDescription();
        activityTypes = activity.getActivityTypes();
        continuous = activity.isContinuous();
        startTime = activity.getStartTime();
        endTime = activity.getEndTime();
        location = activity.getLocation();
    }
}
