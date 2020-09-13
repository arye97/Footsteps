package com.springvuegradle.seng302team600.validator;

import com.springvuegradle.seng302team600.enumeration.UnitType;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Validator for Result and the Value objects it contains.
 */
public class ResultValidator {

    private static final int COMMENT_LEN = 75;
    private static final int VALUE_LEN = 1500;
    private static final String NUMBER_REGEX = "\\d+";
    private static final String BOOLEAN_REGEX = "(true|false)";

    /**
     * Validates the given Result and the Value objects it contains.
     * @param result the Result to be validated
     * @param outcome the Result's Outcome
     * @return true if valid, otherwise throw ResponseStatusException with BAD_REQUEST (400)
     */
    public static boolean validate(Result result, Outcome outcome) {
        result.setOutcome(null);
        validateComment(result.getComment());
        validateValue(result.getValue(), outcome.getUnitType());
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
     * @param value the value to be validated
     * @param unitType the unit type the value should follow
     */
    private static void validateValue(String value, UnitType unitType) {
        if (value == null) return;
        if (value.length() > VALUE_LEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least one input value is too large");
        }
        switch (unitType) {
            case TEXT:
                // Will always be a String
                break;
            case NUMBER:
                if (!value.matches(NUMBER_REGEX)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Value does not convert to an integer");
                }
                break;
            case BOOLEAN:
                if (!value.matches(BOOLEAN_REGEX)) {
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
}
