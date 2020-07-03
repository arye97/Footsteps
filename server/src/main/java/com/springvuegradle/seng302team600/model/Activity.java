package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.payload.ActivityCreateRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
public class Activity {

    final static private int NAME_LEN = 75;
    final static private int DESCRIPTION_LEN = 1500;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    // This could be a way to link to the creator of this activity...
    private Long creatorUserId;

    @NotNull(message = "This Activity needs a name")
    @Column(name = "activity_name", length = NAME_LEN, nullable = false)
    @JsonProperty("activity_name")
    private String name;
    
    @Column(name = "description", length = DESCRIPTION_LEN)
    @JsonProperty("description")
    private String description;

    // ToDo fix to the correct mapping
//    @NotNull(message = "This Activity needs one or more ActivityTypes associated with it")
//    @ManyToMany(cascade=CascadeType.ALL, mappedBy = "activity", fetch = FetchType.EAGER)
//    @ElementCollection(targetClass=ActivityType.class)
//    @JsonProperty("activity_type")
//    private Set<ActivityType> activityTypes = new HashSet<>();

    @NotNull(message = "This Activity needs to be either continuous or have a durration")
    @Column(name = "is_continuous", columnDefinition = "boolean", nullable = false)
    @JsonProperty("continuous")
    private boolean continuous;

    @Column(name = "start_time", columnDefinition = "TIMESTAMP")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("start_time")
    private Date startTime = new Date(0);

    @Column(name = "end_time", columnDefinition = "TIMESTAMP")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("end_time")
    private Date endTime = new Date(0);

    // ToDO These tags need to be modified in U9 (see  api-minimal-spec2.1.txt)
    @NotNull(message = "This Activity needs a location")
    @Column(name = "location", length = NAME_LEN, nullable = false)
    @JsonProperty("location")
    private String location;   // ToDo change String to Location in U9


    /**
     * Default constructor for Activity.
     * Mandatory for repository actions?
     */
    public Activity() {}

    /**
     * Builds Activity from the payload, using getters and setters.  Use when creating new Activity from data.
     * @param activityData payload for creating.
     */
    public Activity(ActivityCreateRequest activityData, Long creatorUserId) {
        this();
        this.builder(activityData, creatorUserId);
    }

    /**
     * Builds Activity from the payload, using getters and setters.  Use when preserving Id, etc.
     * @param activityData payload for creating.
     * @return the built Activity.
     */
    public Activity builder(ActivityCreateRequest activityData, Long creatorUserId) {
        setName(activityData.getName());
        setDescription(activityData.getDescription());
//        setActivityTypes(activityData.getActivityTypes());
        setContinuous(activityData.isContinuous());
        setStartTime(activityData.getStartTime());
        setEndTime(activityData.getEndTime());
        setLocation(activityData.getLocation());
        setCreatorUserId(creatorUserId);
        return this;
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

//    public Set<ActivityType> getActivityTypes() {
//        return activityTypes;
//    }
//
//    public void setActivityTypes(Set<ActivityType> activityTypes) {
//        this.activityTypes = activityTypes;
//    }

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
