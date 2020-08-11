package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Value {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "value_id", nullable = false)
    @JsonProperty("id")
    private Long valueId;

    private Long unitId;

    @Column(name = "value", nullable = false)
    @JsonProperty("value")
    private String value;

    @Column(name = "did_not_finish", nullable = false)
    @JsonProperty("did_not_finish")
    private boolean didNotFinish;
}
