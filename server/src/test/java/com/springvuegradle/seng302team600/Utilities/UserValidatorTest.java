package com.springvuegradle.seng302team600.Utilities;

import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@WebMvcTest(ActivityTypeRepository.class)
//@RunWith(SpringRunner.class)
public class UserValidatorTest {

    final static private String nameRegex = "^[\\p{L} \\-']+([\\p{L} \\-']+)*$";
    final static private int MAX_NAME_LEN = 45;
    final static private int BIO_LEN = 255;
    final static public int MIN_AGE = 13;
    final static public int MAX_AGE = 150;
    final static public String NAME_ERROR = "Name must contain at least one letter and no non-letter characters";

    @MockBean
    private ActivityTypeRepository repo;
    private UserValidator userValidator;
    private User testUser;
    private User nullUser;
    private List<String> passports;
    private List<ActivityType> activityTypeList = new ArrayList<>();
    private Set<ActivityType> activityTypeSet;
    private List<String> addEmails;

    @BeforeEach
    public void setUp() {

        ActivityType activityType1 = new ActivityType("Test type 1");
        ActivityType activityType2 = new ActivityType("Test type 2");
        activityTypeList.add(activityType1);
        activityTypeList.add(activityType2);
        when(repo.findAll()).thenAnswer(i -> { return activityTypeList; });

        MockitoAnnotations.initMocks(this);

        activityTypeSet = new HashSet<>(activityTypeList);
        userValidator  = new UserValidator(repo);
        testUser = new User();
        // create valid user here
        testUser.setFirstName("mc'John");
        testUser.setMiddleName("Jimmy");
        testUser.setLastName("Jones Jamison");
        testUser.setNickName("Big J 68");
        Date DoB = new GregorianCalendar(1987, 03, 06).getTime();
        testUser.setDateOfBirth(DoB);
        testUser.setBio("Lifting up up up in a b-b-bucket truck\\nClimbing high high high in the sky-y\\nWhen it’s tough tough tough and a ladder’s not enough\\nYou need a b-b-b-b-bucket\\nA bucket on a truck");
        addEmails = new ArrayList<String>();
        addEmails.add("jimmy@email.com");
        addEmails.add("jones@email.com");
        testUser.setPrimaryEmail("john@email.com");
        testUser.setAdditionalEmails(addEmails);
        testUser.setFitnessLevel(3);
        passports = new ArrayList<>();
        passports.add("New Zealand");
        passports.add("USA");
        testUser.setPassports(passports);
        testUser.setActivityTypes(activityTypeSet);
        nullUser = new User();
    }

    // everything valid
    @Test
    public void validUserTest() {
        assertTrue(userValidator.validate(testUser));
    }

    //first name invalid
    @Test
    public void invalidFirstName() {
        //Valid
        assertTrue(userValidator.validate(testUser));

        //Name with number
        testUser.setFirstName("John06");
        Exception exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid first name. Name must contain at least one letter and no non-letter characters\"", exception.getMessage());

        //Characterless name
        testUser.setFirstName("");
        exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid first name. Name must contain at least one letter and no non-letter characters\"", exception.getMessage());

        //Test upper limit of name length (len == MAX_NAME_LEN)
        testUser.setFirstName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // "A"*45
        assertTrue(userValidator.validate(testUser));

        //Test exceeding upper limit of name length (len == MAX_NAME_LEN + 1)
        testUser.setFirstName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid first name. Name is too long\"", exception.getMessage());

        //Test null first name
        exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validateName(nullUser.getFirstName(), "first");});
        assertEquals("400 BAD_REQUEST \"Invalid first name. Name must contain at least one letter and no non-letter characters\"", exception.getMessage());
    }

    // middle name invalid
    @Test
    public void invalidMiddleName() {
        //Valid
        assertTrue(userValidator.validate(testUser));

        //Name with number
        testUser.setMiddleName("Jimmy06");
        Exception exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid middle name. Name must contain at least one letter and no non-letter characters\"", exception.getMessage());

        //Characterless name
        testUser.setMiddleName("");
        assertTrue(userValidator.validate(testUser));

        //Test upper limit of name length (len == MAX_NAME_LEN)
        testUser.setMiddleName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // "A"*45
        assertTrue(userValidator.validate(testUser));

        //Test exceeding upper limit of name length (len == MAX_NAME_LEN + 1)
        testUser.setMiddleName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid middle name. Name is too long\"", exception.getMessage());

        //Test null middle name
        assertTrue(userValidator.validateName(nullUser.getMiddleName(), "middle"));
    }

    // last name invalid
    @Test
    public void invalidLastName() {
        //Valid
        assertTrue(userValidator.validate(testUser));

        //Name with number
        testUser.setLastName("Jones J4mison");
        Exception exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid last name. Name must contain at least one letter and no non-letter characters\"", exception.getMessage());

        //Characterless name
        testUser.setLastName("");
        exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid last name. Name must contain at least one letter and no non-letter characters\"", exception.getMessage());

        //Test upper limit of name length (len == MAX_NAME_LEN)
        testUser.setLastName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // "A"*45
        assertTrue(userValidator.validate(testUser));

        //Test exceeding upper limit of name length (len == MAX_NAME_LEN + 1)
        testUser.setLastName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid last name. Name is too long\"", exception.getMessage());

        //Test null last name
        exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validateName(nullUser.getLastName(), "last");});
        assertEquals("400 BAD_REQUEST \"Invalid last name. Name must contain at least one letter and no non-letter characters\"", exception.getMessage());
    }

    //nick name
    @Test
    public void invalidNickName() {
        //Valid
        assertTrue(userValidator.validate(testUser));

        //Name without number (testUser has number in name by default)
        testUser.setNickName("Big J");
        assertTrue(userValidator.validate(testUser));

        //Characterless name
        testUser.setNickName("");
        assertTrue(userValidator.validate(testUser));

        //Test upper limit of name length (len == MAX_NAME_LEN)
        testUser.setNickName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // "A"*45
        assertTrue(userValidator.validate(testUser));

        //Test exceeding upper limit of name length (len == MAX_NAME_LEN + 1)
        testUser.setNickName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Exception exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid nick name. Name is too long\"", exception.getMessage());

        //Test null nick name
        assertTrue(userValidator.validateName(nullUser.getNickName(), "nick"));
    }

    //dob
    @Test
    public void invalidDoB() {
        //Valid
        assertTrue(userValidator.validate(testUser));

        //Younger
        testUser.setDateOfBirth(new Date());
        Exception exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"You must be at least 13 years old to register for this app\"", exception.getMessage());

        //Older
        testUser.setDateOfBirth(new GregorianCalendar(1850, 03, 06).getTime());
        exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid date of birth\"", exception.getMessage());

    }

    //bio
    @Test
    public void invalidBio() {
        //Valid
        assertTrue(userValidator.validate(testUser));

        //blank
        testUser.setBio("");
        assertTrue(userValidator.validate(testUser));

        //exceed length
        testUser.setBio("Lifting up up up in a b-b-bucket truck\\nClimbing high high high in the sky-y\\nWhen it’s tough tough tough and a ladder’s not enough\\nYou need a b-b-b-b-bucket\\nA bucket on a truck" +
                "\\n\\nNeed to trim your trees?\\nHe makes it such a breeze\\nFixing lights and wires\\nHe climbs high high higher\\n\\nHanging cable too?\\nWell he is able to" +
                "\\nStrong like a table too\\nAnd just as stable too\\n\\nBut so that you don’t fall\\nA harness is a good call");
        Exception exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Bio exceeds maximum length\"", exception.getMessage());
    }

    //emails
    @Test
    public void invalidEmail() {
        //Valid
        assertTrue(userValidator.validate(testUser));

        //invalid email (not matching regex)
        addEmails.add("email here");
        testUser.setAdditionalEmails(addEmails);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().startsWith("400 BAD_REQUEST \"Invalid email"));

        //email exceeds max length
        addEmails.set(2, "jones@email.comAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        testUser.setAdditionalEmails(addEmails);
        exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertTrue(exception.getMessage().startsWith("400 BAD_REQUEST \"Email exceeds maximum length"));
    }

    //fitness level
    @Test
    public void invalidFitnessLevel() {
        //Valid
        assertTrue(userValidator.validate(testUser));

        //Invalid number
        testUser.setFitnessLevel(-2);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid fitness level received\"", exception.getMessage());

        //Valid number
        testUser.setFitnessLevel(-1);
        assertTrue(userValidator.validate(testUser));
        testUser.setFitnessLevel(4);
        assertTrue(userValidator.validate(testUser));

        //Invalid number
        testUser.setFitnessLevel(5);
        exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid fitness level received\"", exception.getMessage());
    }

    //passport
    @Test
    public void invalidPassports() {
        //valid
        assertTrue(userValidator.validate(testUser));

        //Invalid blank
        passports.add("");
        testUser.setPassports(passports);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Blank/empty passport country string is not valid\"", exception.getMessage());

        //Invalid exceed length
        passports.set(2, "long name country AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        testUser.setPassports(passports);
        exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Maximum character limit exceeded for passport country\"", exception.getMessage());
    }

    // activity types invalid
    @Test
    public void invalidActivityTypeTest() {
        ActivityType invalidActivityType = new ActivityType("Bad type");
        activityTypeSet.add(invalidActivityType);
        testUser.setActivityTypes(activityTypeSet);

        Exception exception = assertThrows(ResponseStatusException.class, () -> {userValidator.validate(testUser);});
        assertEquals("400 BAD_REQUEST \"Invalid activity type received\"", exception.getMessage());
    }
}