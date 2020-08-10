package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    

}
