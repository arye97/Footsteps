package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.Unit;

import java.util.Set;

/**
 * The payload to be returned to the front-end for the GET outcome endpoint.
 * Json should be formatted as follows:
 * - outcome_id
 * - title
 * - description
 * - activity_id
 * - units (this is a set of Unit objects. Look at model.Unit.java for Json params for each of these objects)
 */
public class OutcomeResponse {

    @JsonProperty("outcome_id")
    private Long outcomeId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("activity_id")
    private Long activityId;

    @JsonProperty("units")
    private Set<Unit> units;

    /**
     * The constructor does not include results. Results are gotten via their own endpoint and ResultResponse.
     * @param outcome the Outcome to return to the user.
     */
    public OutcomeResponse(Outcome outcome) {
        outcomeId = outcome.getOutcomeId();
        title = outcome.getTitle();
        description = outcome.getDescription();
        activityId = outcome.getActivityId();
        // TODO uncomment the below code when Unit class is linked to Outcome class
        //units = outcome.getUnits();
    }
}
