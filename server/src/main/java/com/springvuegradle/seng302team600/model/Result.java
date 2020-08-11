package com.springvuegradle.seng302team600.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Set;

/**
 * This class is to link User to Value. The Outcome will reference this class to get the result for a user.
 * Results contains the users values specific to an activity's outcome.
 */

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

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "outcome_id", nullable = false)
    private Outcome outcome;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    //orphan removal removes 'child' when 'parent' is deleted
    @Column(name = "values", nullable = false)
    private Set<Value> values;

    @Column(name = "comment")
    private String comment;

    public Result() {}

    public Outcome getOutcome() {
        return outcome;
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

    public void setOutcome(Outcome outcome) {
        this.outcome = outcome;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setValues(Set<Value> values) {
        this.values = values;
    }
}