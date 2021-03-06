package com.springvuegradle.seng302team600.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.payload.ResultRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * This class is to link User to Value. The Outcome will reference this class to get the result for a user.
 * Results contains the users values specific to an activity's outcome.
 *
 * This is also the payload to be returned to the front-end for the GET Result endpoint.
 * Json should be formatted as follows:
 * - result_id
 * - user_id (You don't need to add this when POST/creating a result. Will default to your own ID)
 * - values (This is a set of Value objects. Look at model.Value.java for its Json params)
 * - comment
 */

@Entity
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id", nullable = false)
    @JsonProperty("result_id")
    private Long resultId;

    @NotNull(message = "This result needs a user id")
    @Column(name = "userId", nullable = false)
    @JsonProperty("user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "outcome_id")
    private Outcome outcome;

    @NotNull(message = "This result needs a value")
    @Column(name = "value")
    private String value;

    @Column(name = "did_not_finish", nullable = false)
    @JsonProperty("did_not_finish")
    private boolean didNotFinish;

    @Column(name = "comment")
    private String comment;

    public Result() {}

    public Result(ResultRequest resultRequest, Outcome outcome) {
        userId = resultRequest.getUserId();
        this.outcome = outcome;
        value = resultRequest.getValue();
        comment = resultRequest.getComment();
        didNotFinish = resultRequest.getDidNotFinish();
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public Long getResultId() {
        return resultId;
    }

    public String getValue() {
        return value;
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

    public void setOutcome(Outcome outcome) {
        this.outcome = outcome;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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