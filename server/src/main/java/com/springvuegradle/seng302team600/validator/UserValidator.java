package com.springvuegradle.seng302team600.validator;

import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.model.Email;

import java.util.*;

@Service("userValidator")
public class UserValidator {

    final static private String nameRegex = "^[\\p{L} \\-']+([\\p{L} \\-']+)*$";
    final static private String emailRegex = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    final static private int MAX_NAME_LEN = 45;
    final static private int MAX_BIO_LEN = 255;
    final static private int MAX_EMAIL_LEN = 255;
    final static public int MIN_AGE = 13;
    final static public int MAX_AGE = 150;
    final static public String NAME_ERROR = "Name must contain at least one letter and no non-letter characters";
    final static public int MIN_FITNESS_LEVEL = -1;
    final static public int MAX_FITNESS_LEVEL = 4;
    final static public int MAX_PASSPORT_LEN = 255;

    @Autowired
    private final ActivityTypeRepository activityTypeRepository;

    public UserValidator(ActivityTypeRepository activityTypeRepository) {
        this.activityTypeRepository = activityTypeRepository;
    }

    /**
     * Check if the user's attributes are all valid.
     * ResponseStatusException is thrown and stops execution if
     * invalid attributes are found.
     *
     * @param user the user to be validated
     * @return true if valid user, otherwise throw ResponseStatusException
     */

    public boolean validate(User user) throws ResponseStatusException {
//
        validateName(user.getFirstName(), "first");
        validateName(user.getLastName(), "last");
        validateName(user.getMiddleName(), "middle");
        validateName(user.getNickName(), "nick");
        validateAge(user.getDateOfBirth(), MIN_AGE, true);
        validateAge(user.getDateOfBirth(), MAX_AGE, false);
        validateBio(user.getBio());
        for (Email emailObj : user.getEmails()) {
            validateEmail(emailObj.getEmail());
        }
        validateFitnessLevel(user.getFitnessLevel());
        List<String> userPassports = user.getPassports();
        if (userPassports != null) {
            for (String passport : userPassports) {
                validatePassport(passport);
            }
        }
        Set<ActivityType> activityTypes = new HashSet<>(activityTypeRepository.findAll());
        Set<ActivityType> userActivityTypes = user.getActivityTypes();
        if (userActivityTypes != null) {
            for (ActivityType activityType : userActivityTypes) {
                validateActivityType(activityType, activityTypes);
            }
        }
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
    public boolean validateName(String name, String part) {
        if (name == null) {
            if (part.equals("middle") || part.equals("nick")) {
                return true;
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + part + " name. " + NAME_ERROR);
        }
        if (name.length() > MAX_NAME_LEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + part + " name. Name is too long");
        }
        if (!part.equals("nick")) {
            if (part.equals("middle")) {
                if (!name.matches(nameRegex) && !name.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + part + " name. " + NAME_ERROR);
                }
            } else {
                if (!name.matches(nameRegex)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + part + " name. " + NAME_ERROR);
                }
            }
        }
        return true;
    }


    /**
     * Checks if the given date of birth would result in the user being younger or older than the given age
     * @param DoB     date of birth for perspective new user
     * @param age     age to check against
     * @param younger boolean tag to determine if checking if the person is younger or older (false:older, true:younger)
     * @throws ResponseStatusException given DoB results in the user being younger/older than the given age
     */
    public boolean validateAge(Date DoB, int age, boolean younger) {
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
        return true;
    }

    /**
     * Checks if the given users bio will exceed the string character limit of the database (see MAX_BIO_LEN)
     * @param bio is the bio string for the potntial user that is to be validated
     * @throws ResponseStatusException if the length of the bio string exceeds MAX_BIO_LEN
     */
    public boolean validateBio(String bio) {
        if (bio == null) {
            return true;
        }
        if (bio.length() > MAX_BIO_LEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bio exceeds maximum length");
        }
        return true;
    }

    /**
     * Checks if the given users email is valid
     *  Checks if the email string length exceeds the MAX_EMAIL_LEN
     *  Checks if the email string matches the email validation regex
     * @param email is the string of an email for the given user
     * @throws ResponseStatusException if the given email string is invalid
     */
    public boolean validateEmail(String email) {
        if (email.length() > MAX_EMAIL_LEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Email exceeds maximum length: '%s'", email));
        }
        if (!email.matches(emailRegex)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Invalid email: '%s'", email));
        }
        return true;
    }

    /**
     * Checks if the given users fitness level is valid
     *  Fitness level is valid if the level int is in the range -1 to 4 (inclusive)
     * @param level is the int representation of the users fitness level
     * @throws ResponseStatusException if the given fitness level is invalid
     */
    public boolean validateFitnessLevel(int level) {
        if (level < MIN_FITNESS_LEVEL || level > MAX_FITNESS_LEVEL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid fitness level received");
        }
        return true;
    }

    /**
     * Checks if the given user's passport country is valid
     *  Passport country is valid if the string length does not exceed MAX_PASSPORT_LEN
     * @param passport is the passport country string of the user
     * @throw ResponseStatusException if the passport string is invalid
     */
    public boolean validatePassport(String passport) {
        if (passport == null) {
            return true;
        }
        if (passport.length() > MAX_PASSPORT_LEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum character limit exceeded for passport country");
        }
        else if (passport.length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Blank/empty passport country string is not valid");
        }
        return true;
    }

    public boolean validateActivityType(ActivityType activityType, Set<ActivityType> activityTypes) {
        if (!activityTypes.contains(activityType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid activity type received");
        }
        return true;
    }
}