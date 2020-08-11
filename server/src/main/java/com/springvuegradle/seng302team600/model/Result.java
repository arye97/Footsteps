package com.springvuegradle.seng302team600.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id", nullable = false)
    @JsonProperty("result_id")
    private Long resultId;

    @Column(name = "userId", nullable = false)
    @JsonProperty("user_id")
    private Long userId;

    @Column(name = "outcome_id", nullable = false)
    @JsonProperty("outcome_id")
    private Long outcomeId;

    @Column(name = "values", nullable = false)
    private Set<Value> values;

    @Column(name = "comment")
    private String comment;

    public Result() {}

    public Result(Long userId, Long outcomeId, Set<Value> values, String comment) {
        this.userId = userId;
        this.outcomeId = outcomeId;
        this.values = values;
        this.comment = comment;
    }

    public Long getOutcomeId() {
        return outcomeId;
    }

    public Long getResultId() {
        return resultId;
    }

    public Set<Value> getValues() {
        return values;
    }

    public Long getUserId() {
        return userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setOutcomeId(Long outcomeId) {
        this.outcomeId = outcomeId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setValues(Set<Value> values) {
        this.values = values;
    }
}
