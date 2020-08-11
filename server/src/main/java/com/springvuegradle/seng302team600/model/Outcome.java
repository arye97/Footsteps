package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * An Outcome should have zero or many Results, one per user (participant/follower). Can have many Unit instances.
 * Used for payload for POST and PUT endpoints.
 *
 * :::NOTE:::
 * One Outcome will be created or modified at a time.
 * Hence, one Outcome per endpoint call.
 * ::::::::::
 *
 * Json should be formatted as follows:
 * - outcome_id
 * - title
 * - description
 * - activity_id
 * - units (this is a set of Unit objects. Look at model.Unit.java for Json params for each of these objects)
 */
public class Outcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outcome_id", nullable = false)
    @JsonProperty("outcome_id")
    private Long outcomeId;

    @NotNull(message = "This outcome needs a title")
    @Column(name = "title", nullable = false)
    @JsonProperty("title")
    private String title;

    @NotNull(message = "This outcome needs a description")
    @Column(name = "description", nullable = false)
    @JsonProperty("description")
    private String description;

    @NotNull(message = "This outcome needs an activity ID")
    @Column(name = "activity_id", nullable = false)
    @JsonProperty("activity_id")
    private Long activityId;

    public Long getOutcomeId() {
        return outcomeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}
