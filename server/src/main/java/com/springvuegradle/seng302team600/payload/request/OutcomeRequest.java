package com.springvuegradle.seng302team600.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.enumeration.UnitType;


public class OutcomeRequest {

    @JsonProperty("activity_id")
    private Long activityId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("unit_name")
    private String unitName;

    @JsonProperty("unit_type")
    private UnitType unitType;

    public OutcomeRequest() {}

    public Long getActivityId() {
        return activityId;
    }

    public String getUnitName() {
        return unitName;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public String getTitle() {
        return title;
    }
}
