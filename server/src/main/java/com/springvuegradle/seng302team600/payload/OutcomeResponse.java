package com.springvuegradle.seng302team600.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.enumeration.UnitType;
import com.springvuegradle.seng302team600.model.Outcome;

import java.util.Set;

/**
 * The payload to be returned to the front-end for the GET outcome endpoint.
 * Json should be formatted as follows:
 * - outcome_id
 * - title
 * - description
 * - activity_id
 * - unit_name
 * - unit_type
 */
public class OutcomeResponse {

    @JsonProperty("outcome_id")
    private Long outcomeId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("activity_id")
    private Long activityId;

    @JsonProperty("unit_name")
    private String unitName;

    @JsonProperty("unit_type")
    private UnitType unitType;

    /**
     * The constructor does not include results. Results are gotten via their own endpoint and ResultResponse.
     * @param outcome the Outcome to return to the user.
     */
    public OutcomeResponse(Outcome outcome) {
        outcomeId = outcome.getOutcomeId();
        title = outcome.getTitle();
        activityId = outcome.getActivityId();
        unitName = outcome.getUnitName();
        unitType = outcome.getUnitType();
    }
}
