package com.springvuegradle.seng302team600.Utilities;

import com.springvuegradle.seng302team600.enumeration.UnitType;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.HttpRetryException;
import java.util.Set;

/**
 *  Validates the activities Outcome created by the activity author
 *  Checks that the title both exists and is less than 75 chars long
 *  Checks that the description both exists and is less than 1500 chars long
 *  Checks that the activity id exists, so as to be associated with one
 *  Checks that if this outcome has associated units exist then they will
 *      both exist and have titles of length between 0 and 75 chars long
 */
public class OutcomeValidator {

    private static final int TITLE_LENGTH = 75;
    private static final int DESCRIPTION_LEN = 1500;

    public static boolean validate(Outcome outcome) {
        validateTitle(outcome.getTitle());
        validateActivityId(outcome.getActivityId());
        validateUnits(outcome.getUnitType(), outcome.getUnitName());
        return true;
    }

    /**
     * @param title must be 1 >= x <= 75 chars long
     */
    public static void validateTitle(String title) {
        if (title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must be filled in");
        }
        if (title.length() > TITLE_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must be less than 45 characters long");
        }
    }

    /**
     * @param activityId must not be null
     */
    public static void validateActivityId(Long activityId) {
        if (activityId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ActivityId must be set");
        }
    }


    /**
     * @param unitType the type of the outcome unit
     * @param unitName the type of the outcome unit
     */
    public static void validateUnits(UnitType unitType, String unitName) {
        if (unitType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unit must have a unit type set");
        }
        if (unitName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unit name must be set");
        } else {
            if (unitName.length() > TITLE_LENGTH) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unit name must be less than 75 characters long");
            }
        }
    }
}
