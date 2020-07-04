package com.springvuegradle.seng302team600.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("static")
public class ActivityTypeProperties {

    private final List<ActivityType> activityTypes;

    public ActivityTypeProperties(List<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public List<ActivityType> getActivityTypes(){
        return this.activityTypes;
    }

}