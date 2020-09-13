package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.model.Location;

import java.util.Date;
import java.util.Set;

public class ActivityPutRequest {

    @JsonProperty("id")
    private final Long activityId;

    @JsonProperty("creatorUserId")
    private final Long creatorUserId;

    @JsonProperty("activity_name")
    private final String name;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("activity_type")
    private final Set<ActivityType> activityTypes;

    @JsonProperty("continuous")
    private final boolean continuous;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("start_time")
    private final Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("end_time")
    private final Date endTime;

    @JsonProperty("location")
    private final Location location; //TODO change to payload

    public ActivityPutRequest(Long activityId, Long creatorUserId, String name,
                              String description, Set<ActivityType> activityTypes,
                              boolean continuous, Date startTime, Date endTime, Location location) {
        this.activityId = activityId;
        this.creatorUserId = creatorUserId;
        this.name = name;
        this.description = description;
        this.activityTypes = activityTypes;
        this.continuous = continuous;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    public Long getActivityId() {
        return activityId;
    }

    public Long getCreatorUserId() {
        return creatorUserId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Location getLocation() {
        return location;
    }
}
