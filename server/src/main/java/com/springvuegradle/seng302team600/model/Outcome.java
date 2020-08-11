package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

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
@Entity
public class Outcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outcome_id", nullable = false)
    @JsonProperty("outcome_id")
    private Long outcomeId;

    @NotNull(message = "This outcome needs a title")
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull(message = "This outcome needs a description")
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull(message = "This outcome needs an activity ID")
    @Column(name = "activity_id", nullable = false)
    @JsonProperty("activity_id")
    private Long activityId;

    @JsonManagedReference
    @NotNull(message = "An outcome requires a set of unit(s)")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Unit> units;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "outcome", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Result> results;

    public Outcome() {
    }

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

    public Set<Unit> getUnits() {
        return units;
    }

    public void setUnits(Set<Unit> units) {
        this.units = units;
    }

    public Set<Result> getResults() {
        return results;
    }

    public void setResults(Set<Result> results) {
        this.results = results;
    }
}
