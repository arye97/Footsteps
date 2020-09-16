package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.enumeration.UnitType;
import com.springvuegradle.seng302team600.payload.OutcomeRequest;

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
 * - unit_name
 * - unit_type
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

    @NotNull(message = "This outcome needs an activity ID")
    @Column(name = "activity_id", nullable = false)
    @JsonProperty("activity_id")
    private Long activityId;

    @NotNull(message = "This outcome needs a unit name")
    @Column(name = "unit_name", nullable = false)
    @JsonProperty("unit_name")
    private String unitName;

    @NotNull(message = "This outcome needs a unit type")
    @Column(name = "unit_type", nullable = false)
    @JsonProperty("unit_type")
    private UnitType unitType;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "outcome", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Result> results;

    public Outcome() {}

    public Outcome(OutcomeRequest outcomeRequest) {
        this.title = outcomeRequest.getTitle();
        this.activityId = outcomeRequest.getActivityId();
        this.unitName = outcomeRequest.getUnitName();
        this.unitType = outcomeRequest.getUnitType();
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

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String name) {
        this.unitName = name;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType type) {
        this.unitType = type;
    }

    public Set<Result> getResults() {
        return results;
    }

    public void setResults(Set<Result> results) {
        this.results = results;
    }

    public void addResult(Result result) {
        results.add(result);
    }

    @Override
    public String toString() {
        return String.format("%d %s, with %s units, %d results",
                outcomeId,
                title,
                unitName,
                results == null ? null : results.size());
    }
}
