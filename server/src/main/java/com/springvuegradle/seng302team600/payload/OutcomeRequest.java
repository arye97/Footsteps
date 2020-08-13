package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.Unit;

import java.util.Set;

public class OutcomeRequest {

    @JsonProperty("activity_id")
    private Long activityId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("units")
    private Set<Unit> units;

    public OutcomeRequest() {}

    public Long getActivityId() {
        return activityId;
    }

    public Set<Unit> getUnits() {
        return units;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }
}
