package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.GenerationType;
import javax.persistence.*;

/**
 * A class that represents the Value inputs
 * that a participant enters to a Result.
 */
public class Value {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "value_id", nullable = false)
    @JsonProperty("value_id")
    private Long valueId;

    @Column(name = "unit_id", nullable = false)
    @JsonProperty("unit_id")
    private Long unitId;

    // Actual value of a Value object, represented as a string.
    @Column(name = "value")
    private String value;

    @Column(name = "did_not_finish", nullable = false)
    @JsonProperty("did_not_finish")
    private boolean didNotFinish;

    public Value() {

    }

    public Long getValueId() {
        return valueId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isDidNotFinish() {
        return didNotFinish;
    }

    public void setDidNotFinish(boolean didNotFinish) {
        this.didNotFinish = didNotFinish;
    }
}
