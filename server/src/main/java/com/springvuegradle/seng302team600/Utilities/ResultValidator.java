package com.springvuegradle.seng302team600.Utilities;

import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.Result;
import com.springvuegradle.seng302team600.model.Unit;
import com.springvuegradle.seng302team600.model.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

/**
 * Validator for Result and the Value objects it contains.
 */
public class ResultValidator {

    final static private int COMMENT_LEN = 75;
    final static private int VALUE_LEN = 1500;
    final static private String NUMBER_REGEX = "\\d+";
    final static private String BOOLEAN_REGEX = "(true|false)";

    /**
     * Validates the given Result and the Value objects it contains.
     * @param result the Result to be validated
     * @param outcome the Result's Outcome
     * @return true if valid, otherwise throw ResponseStatusException with BAD_REQUEST (400)
     */
    public static boolean validate(Result result, Outcome outcome) {
        result.setOutcome(null);
        validateComment(result.getComment());
        validateValues(result.getValues(), outcome.getUnits());
        return true;
    }

    /**
     * Validates the comment.
     * @param comment the Result's comment
     */
    private static void validateComment(String comment) {
        if (comment == null) return;
        if (comment.length() > COMMENT_LEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Result comment length is too long");
        }
    }

    /**
     * Validates a single Value.
     * @param value the Value to be validated
     * @param unit the Unit the Value should follow
     */
    private static void validateValue(Value value, Unit unit) {
        if (value.getValue() == null) return;
        if (value.getValue().length() > VALUE_LEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least one input value is too large");
        }
        switch (unit.getUnitType()) {
            case TEXT:
                // Will always be a String
                break;
            case NUMBER:
                if (!value.getValue().matches(NUMBER_REGEX)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Value does not convert to an integer");
                }
                break;
            case BOOLEAN:
                if (!value.getValue().matches(BOOLEAN_REGEX)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Value does not convert to a boolean");
                }
                break;
            case DATE:
                // TODO implement when doing feature to use different unit data types
                break;
            case TIME:
                // TODO implement when doing feature to use different unit data types
                break;
            case DATE_AND_TIME:
                // TODO implement when doing feature to use different unit data types
                break;
        }
    }

    /**
     * Validate the Value objects.
     * @param values the Values to be validated
     * @param units the Units, to check they use them
     */
    private static void validateValues(Set<Value> values, Set<Unit> units) {
        if (values.size() != units.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The number of values does not equal the number of units for the outcome");
        }

        boolean looking;
        for (Unit unit : units) {
            looking = true;
            for (Value value : values) {
                if (value.getUnitId().equals(unit.getUnitId())) {
                    looking = false;
                    validateValue(value, unit);
                }
            }
            if (looking) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Multiple values have been submitted for the same unit");
            }
        }
    }
}
