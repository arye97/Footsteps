package com.springvuegradle.seng302team600.Utilities;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.springvuegradle.seng302team600.model.User;

import java.util.*;

public class UserValidator {

    final static private String nameRegex = "^[a-zA-Z_]+([a-zA-Z_]+)*$";
    final static private String middleNameRegex = "^[a-zA-Z_]*$";
    final static private int MAX_NAME_LEN = 45;
    final static private int BIO_LEN = 255;
    final static public int MIN_AGE = 13;
    final static public int MAX_AGE = 150;
    final static public String NAME_ERROR = "Name must contain at least one letter and no non-letter characters";

    /**
     * Check if the user's attributes are all valid.
     * ResponseStatusException is thrown and stops execution if
     * invalid attributes are found.
     *
     * @param user the user to be validated
     * @return true if valid user, otherwise throw ResponseStatusException
     */

    public static boolean validate(User user) {
//
        validateName(user.getFirstName(), "first");
        validateName(user.getLastName(), "last");
        validateName(user.getMiddleName(), "middle");
        validateAge(user.getDateOfBirth(), MIN_AGE, true);
        validateAge(user.getDateOfBirth(), MAX_AGE, false);

        return true;
    }

    /**
     * Checks name to ensure:
     *  name is not null (unless middle name)
     *  name conforms to regex for given part
     *  name does not exceed the character limit
     * @param name name to be checked
     * @param part the part of the full name that the given name represents [first, middle, last]
     * @throws ResponseStatusException thrown if the given name is invalid
     */
    private static void validateName(String name, String part) {
        if (name == null) {
            if (part.equals("middle")) {
                return;
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + part + " name. " + NAME_ERROR);
        }
        if (name.length() > MAX_NAME_LEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + part + " name. " + NAME_ERROR);
        }
        if (!part.equals("middle")) {
            if (!name.matches(nameRegex)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + part + " name. " + NAME_ERROR);
            }
        } else {
            if (!name.matches(middleNameRegex) && !name.trim().isEmpty() ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + part + " name. " + NAME_ERROR);
            }
        }
    }



    /**
     * Checks if the given date of birth would result in the user being younger or older than the given age
     * @param DoB     date of birth for perspective new user
     * @param age     age to check against
     * @param younger boolean tag to determine if checking if the person is younger or older (false:older, true:younger)
     * @throws ResponseStatusException given DoB results in the user being younger/older than the given age
     */
    private static void validateAge(Date DoB, int age, boolean younger) {
        Calendar calendar = Calendar.getInstance();
        //Lock calender time to end of day to ensure comparison is accurate and reliable
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 99);
        calendar.add(Calendar.YEAR, -age);
        Date ageDate = calendar.getTime();
        if (younger) {
            if (ageDate.before(DoB)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must be at least 13 years old to register for this app");
            }
        } else {
            if (ageDate.after(DoB)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date of birth");
            }
        }
    }
}