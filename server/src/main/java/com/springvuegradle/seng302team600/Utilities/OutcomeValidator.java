package com.springvuegradle.seng302team600.Utilities;

import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.Result;
import com.springvuegradle.seng302team600.model.Unit;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.HttpRetryException;
import java.util.Set;

public class OutcomeValidator {

    private static final int TITLE_LENGTH = 75;
    private static final int DESCRIPTION_LEN = 1500;

    public static boolean validate(Outcome outcome) {
        validateTitle(outcome.getTitle());
        validateDescription(outcome.getDescription());
        validateActivityId(outcome.getActivityId());
        validateUnits(outcome.getUnits());
        return true;
    }

    public static void validateTitle(String title) {
        if (title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must be filled in");
        }
        if (title.length() > TITLE_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must be less than 45 characters long");
        }
    }

    public static void validateDescription(String description) {
        if (description.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description must be filled in");
        }
        if (description.length() > DESCRIPTION_LEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description must be less than 45 characters long");
        }
    }

    public static void validateActivityId(Long activityId) {
        if (activityId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ActivityId must be set");
        }
    }

    public static void validateUnits(Set<Unit> units) {
        if (!units.isEmpty()) {
            for (Unit unit : units) {
                if (unit.getUnitType() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unit must have a unit type set");
                }
                if (unit.getName().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unit name must be set");
                } else {
                    if (unit.getName().length() > TITLE_LENGTH) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unit name must be less than 75 characters long");
                    }
                }
            }
        }
    }
}
