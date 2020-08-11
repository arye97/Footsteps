package com.springvuegradle.seng302team600.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

public class Outcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outcome_id", nullable = false)
    private Long outcomeId;

    @NotNull(message = "This outcome needs a title")
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull(message = "This outcome needs a description")
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull(message = "This outcome needs an activity ID")
    @Column(name = "activity_id", nullable = false)
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
