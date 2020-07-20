package com.springvuegradle.seng302team600.Utilities;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.ActivityType;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

public class ActivityValidator {

    final static private int NAME_LEN = 75;
    final static private int DESCRIPTION_LEN = 1500;
    final static private int MIN_ACTIVITY_TYPE_COUNT = 1;

    /**
     * Check if the activity's attributes are all valid.
     * ResponseStatusException is thrown and stops execution if
     * invalid attributes are found.
     * @param activity the activity to be validated
     * @return true if valid activity, otherwise throw ResponseStatusException
     */
    public static boolean validate(Activity activity) {
        validateName(activity.getName());
        validateDescription(activity.getDescription());
        validateActivityTypes(activity.getActivityTypes());
        if (!activity.isContinuous()) {
            validateDates(activity.getStartTime(), activity.getEndTime());
        }
        return true;
    }

    /**
     * Check activity name for length. If invalid throw ResponseStatusException
     * @param name the name to be checked
     * @throws ResponseStatusException if invalid then bad request (400) should be returned
     */
    private static void validateName(String name) {
        if (name.length() > NAME_LEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity name is too long");
        }
        if (name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity name is blank");
        }
    }

    /**
     * Check activity description for length. If invalid throw ResponseStatusException
     * @param description the description to be checked
     * @throws ResponseStatusException if invalid then bad request (400) should be returned
     */
    private static void validateDescription(String description) {
        if (description.length() > DESCRIPTION_LEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity description is too long");
        }
        if (description.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity description is blank");
        }
    }

    /**
     * Check the activity type set for length. If invalid throw ResponseStatusException
     * @param activityTypes the activity type set to be checked
     * @throws ResponseStatusException if invalid then bad request (400) should be returned
     */
    private static void validateActivityTypes(Set<ActivityType> activityTypes) {
        if (activityTypes.size() < MIN_ACTIVITY_TYPE_COUNT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must have at least one activity type");
        }
    }

    /**
     * Checks activity start and end dates. Invalid If start is after end
     * - Start date must be before end date
     * - Shouldn't create a date before 1970, which causes an SQL error.
     * @param start the starting date
     * @param end the ending date
     */
    private static void validateDates(Date start, Date end) {
        if (start.after(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }
        if (start.getTime() < 0 || end.getTime() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date and end date must occur after 1970");
        }
    }
}
