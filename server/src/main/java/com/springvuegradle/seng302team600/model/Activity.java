package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
public class Activity {

    final static private int NAME_LEN = 75;
    final static private int DESCRIPTION_LEN = 1500;

    // This could be a way to link to the creator of this activity...
    private Long creatorUserId;

    @NotNull(message = "This Activity needs a name")
    @Column(name = "activity_name", length = NAME_LEN, nullable = false)
    @JsonProperty("activity_name")
    private String name;
    
    @Column(name = "description", length = DESCRIPTION_LEN)
    @JsonProperty("description")
    private String description;

    // These tags may need changing
    @NotNull
    @JsonProperty("activity_type")
    private Set<ActivityType> activityTypes = new HashSet<>();

    @Column(name = "isContinuous", columnDefinition = "boolean", nullable = false)
    private boolean continuous;

    @NotNull
    @JsonProperty("start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime = new Date(0);

    @NotNull
    @JsonProperty("end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime = new Date(0);

    // ToDO These tags need to be modified in U9 (see  api-minimal-spec2.1.txt)
    @NotNull(message = "This Activity needs a location")
    @Column(name = "location", length = NAME_LEN, nullable = false)
    @JsonProperty("location")
    private String location;   // ToDo change String to Location in U9


    /**
     * Default constructor for Email.
     * Mandatory for repository actions?
     */
    public Activity() {}


    public Activity(String name, String location, Collection<ActivityType> activityTypes, User creatorUser) {
        this.name = name;
        this.location = location;
        this.activityTypes = new HashSet<>(activityTypes);
        this.creatorUserId = creatorUser.getUserId();
    }


    public Long getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(Long creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(Set<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
