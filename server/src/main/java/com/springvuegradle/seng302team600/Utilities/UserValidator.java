package com.springvuegradle.seng302team600.Utilities;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.springvuegradle.seng302team600.model.User;

import java.util.*;

public class UserValidator {

    final static private String nameRegex = "^[a-zA-Z_]+([a-zA-Z_]+)*$";
    final static private String middleNameRegex = "^[a-zA-Z_]*$";
    final static private int NAME_PART_LEN = 45;
    final static private int BIO_LEN = 255;
    final static public int MIN_AGE = 13;
    final static public int MAX_AGE = 150;

    /**
     * Check if the user's attributes are all valid.
     * ResponseStatusException is thrown and stops execution if
     * invalid attributes are found.
     * @param user the user to be validated
     * @return true if valid user, otherwise throw ResponseStatusException
     */
    public static boolean validate(User user) {
//        String nameError = "Name must contain at least one letter and no non-letter characters";
        validateFirstName(user.getFirstName());
//        if (firstName == null || lastName == null) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name. " + nameError); }
//        if (! firstName.matches(nameRegex) ) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid first name. " + nameError); }
//        if (! lastName.matches(nameRegex) ) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid last name. " + nameError); }
//        if (middleName != null) {
//            if (! middleName.matches(middleNameRegex) && ! middleName.trim().isEmpty() ) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid middle name. " + nameError); }
//        }
//        if (ageCheck(dateOfBirth, MIN_AGE, true)) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must be at least 13 years old to register for this app"); }
//        if (ageCheck(dateOfBirth, MAX_AGE, false)) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date of birth"); }

        validateDescription(activity.getDescription());
        validateActivityTypes(activity.getActivityTypes());
        if (!activity.isContinuous()) {
            validateDates(activity.getStartTime(), activity.getEndTime());
        }
        return true;
    }

    /**
         * Runs a sanity check on the user and throws errors if the are invalid fields
         * @throws ResponseStatusException thrown if the users first, middle or last names are invalid
         * thrown if the user is younger than 13
         * thrown if the user is older than 150yr
         * @return returns true if valid user
         */
        }

        /**
         * Checks if the given date of birth would result in the user being younger or older than the given age
         * @param DoB date of birth for prespective new user
         * @param age age to check against
         * @param younger boolean tag to determine if checking if the person is younger or older (false:older, true:younger)
         * @return boolean tag denoting how given DoB compares to given age with respect to younger tag
         */
        private boolean ageCheck(Date DoB, int age, boolean younger) {
            Calendar calendar = Calendar.getInstance();
            //Lock calender time to end of day to ensure comparison is accurate and reliable
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 99);
            calendar.add(Calendar.YEAR, -age);
            Date ageDate = calendar.getTime();
            if ( younger ) {
                return ageDate.before(DoB);
            } else {
                return ageDate.after(DoB);
            }
        }