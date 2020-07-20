package com.springvuegradle.seng302team600.model;

import com.springvuegradle.seng302team600.Utilities.UserValidator;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class UserTest {

    private User userTest;
    private UserValidator validator;

    private Date getAgeDate(int age) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        return calendar.getTime();
    }

    @BeforeEach
    void setUp() {
        userTest = new User();
        userTest.setFirstName("Jimmy");
        userTest.setLastName("Jones");
        userTest.setDateOfBirth(getAgeDate(50));
        ActivityTypeRepository mockActivityTypeRepository = mock(ActivityTypeRepository.class);
        validator = new UserValidator(mockActivityTypeRepository);
    }

    @Test
    /** Check lower bound of age (age<=13)
     * Checks ages 12, 13 and today (0)
     */
    void ageTooYoung() {
        assertTrue(validator.validate(userTest));
        userTest.setDateOfBirth(getAgeDate(12));
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setDateOfBirth(Calendar.getInstance().getTime());
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setDateOfBirth(getAgeDate(13));
        assertTrue(validator.validate(userTest));
        userTest.setDateOfBirth(getAgeDate(14));
        assertTrue(validator.validate(userTest));
    }

    @Test
    /** Check upper bound of age (age<150)
     * Checks ages 151, 150 and 149
     */
    void ageTooOld() {
        assertTrue(validator.validate(userTest));
        userTest.setDateOfBirth(getAgeDate(151));
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setDateOfBirth(getAgeDate(150));
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setDateOfBirth(getAgeDate(149));
        assertTrue(validator.validate(userTest));
    }

    @Test
    void invalidFirstNameEmpty() {
        assertTrue(validator.validate(userTest));
        userTest.setFirstName("");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setFirstName("    ");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
    }

    @Test
    void invalidFirstNameNull() {
        assertTrue(validator.validate(userTest));
        userTest.setFirstName("");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
    }

    @Test
    void invalidFirstNameNonAlphaChar() {
        assertTrue(validator.validate(userTest));
        userTest.setFirstName("Jimmy7");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setFirstName("Jimmy@");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setFirstName("!");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
    }

    @Test
    void invalidLastNameEmpty() {
        assertTrue(validator.validate(userTest));
        userTest.setLastName("");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setLastName("     ");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
    }

    @Test
    void invalidLastNameNonAlphaChar() {
        assertTrue(validator.validate(userTest));
        userTest.setLastName("Jones7");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setLastName("Jones^");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setLastName("%");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
    }

    @Test
    void validMiddleNameEmpty() {
        assertTrue(validator.validate(userTest));
        userTest.setMiddleName("");
        assertTrue(validator.validate(userTest));
        userTest.setMiddleName("     ");
        assertTrue(validator.validate(userTest));
    }

    @Test
    void validMiddleNameNull() {
        assertTrue(validator.validate(userTest));
        userTest.setMiddleName(null);
        assertTrue(validator.validate(userTest));
    }

    @Test
    void validBioNull() {
        assertTrue(validator.validate(userTest));
        userTest.setBio(null);
        assertTrue(validator.validate(userTest));
    }

    @Test
    void validNicknameNull() {
        assertTrue(validator.validate(userTest));
        userTest.setNickName(null);
        assertTrue(validator.validate(userTest));
    }

    @Test
    void invalidMiddleNameNonAlphaChar() {
        assertTrue(validator.validate(userTest));
        userTest.setMiddleName("Johnny7");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setMiddleName("Johnny*");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
        userTest.setMiddleName("+");
        assertThrows(ResponseStatusException.class, () -> validator.validate(userTest));
    }

    @Test
    void setOnePrimaryEmail_Success_WhenCreatingUser() {
        String email = "terry_tester@yahoo.com";
        userTest.setPrimaryEmail(email);

        assertEquals(email, userTest.getPrimaryEmail());
        assertEquals(1, userTest.getEmails().size());

        assertTrue(userTest.getEmails().get(0).getIsPrimary());
        assertEquals(userTest.getEmails().get(0).getUser(), userTest);
    }


    @Test
    void setAdditionalEmails_Success_WhenAddingAdditionalEmails() {
        String primaryEmail = "maria_dianna_bart@database.com";
        userTest.setPrimaryEmail(primaryEmail);

        List<String> additionalEmails = new ArrayList<>();
        additionalEmails.add("alexosbourne@burns.com");
        additionalEmails.add("olivesnook@pie.com");
        additionalEmails.add("magicalcat@cats.com");
        additionalEmails.add("lizardman@cow.com");
        userTest.setAdditionalEmails(additionalEmails);

        int actualAdditionalEmailsSize = userTest.getAdditionalEmails().size();
        int actualEmailsSize = userTest.getEmails().size();
        int primaryEmailCount = 0;

        assertEquals(4, actualAdditionalEmailsSize);
        assertEquals(5, actualEmailsSize);

        for (Email email: userTest.getEmails()) {
            if (!email.getEmail().equals(primaryEmail)) {
                assertFalse(email.getIsPrimary());
            } else {
                primaryEmailCount++;
            }
        }

        // Checks if there is only one primary email in list of Email objects
        assertEquals(1, primaryEmailCount);
    }

    @Test
    void setAdditionalEmails_ExceptionThrown_WhenPrimaryEmailDoesNotExist() {
        List<String> additionalEmails = new ArrayList<>();
        additionalEmails.add("guavaperson@cucumber.com");
        additionalEmails.add("pam@pam.pam");

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            userTest.setAdditionalEmails(additionalEmails);
        });

        String expectedMessage = "Primary email must be created before additional emails are added";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        // Potentially check error value
    }

    @Test
    void setAdditionalEmails_ExceptionThrown_WhenEmailLimitReached() {
        String primaryEmail = "maria_dianna_bart@database.com";
        userTest.setPrimaryEmail(primaryEmail);

        List<String> additionalEmails = new ArrayList<>();
        additionalEmails.add("alexosbourne@burns.com");
        additionalEmails.add("olivesnook@pie.com");
        additionalEmails.add("magicalcat@cats.com");
        additionalEmails.add("lizardman@cow.com");

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            additionalEmails.add("davidthebacker@parking.org");
            userTest.setAdditionalEmails(additionalEmails);
        });

        String expectedMessage = "Maximum email limit reached";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        // Potentially check error value
    }


    @Test
    void setNewPrimaryEmail_Success_WhenReplacedWithExistingEmail() {
        String oldPrimaryEmail = "terry_tester@yahoo.com";
        userTest.setPrimaryEmail(oldPrimaryEmail);

        List<String> additionalEmails = new ArrayList<>();
        additionalEmails.add("nicholasmiller@from.chicago.com");
        userTest.setAdditionalEmails(additionalEmails);

        String newPrimaryEmail = additionalEmails.get(0);
        userTest.setPrimaryEmail(newPrimaryEmail);

        assertEquals(oldPrimaryEmail, userTest.getAdditionalEmails().get(0));
        assertEquals(newPrimaryEmail, userTest.getPrimaryEmail());

        for (Email email: userTest.getEmails()) {
            if (email.getEmail().equals(newPrimaryEmail)) {
                assertTrue(email.getIsPrimary());
            } else {
                assertFalse(email.getIsPrimary());
            }
        }
    }


    @Test
    void deleteAdditionalEmail_Success_WhenRemovingEmailFromUser() {
        String primaryEmail = "terry_tester@yahoo.com";
        userTest.setPrimaryEmail(primaryEmail);

        List<String> additionalEmails = new ArrayList<>();
        String additionalEmailToBeRemoved = "nicholasmiller@from.chicago.com";
        additionalEmails.add(additionalEmailToBeRemoved);
        userTest.setAdditionalEmails(additionalEmails);

        userTest.deleteAdditionalEmail(additionalEmailToBeRemoved);

        assertEquals(0, userTest.getAdditionalEmails().size());
        assertEquals(1, userTest.getEmails().size());
    }


    @Test
    void setTransientEmailStrings_DoesNotDuplicatePrimaryAndAdditionalEmails() {
        String primaryEmail = "lorenzo_haschestpain@yahoo.com";
        userTest.setPrimaryEmail(primaryEmail);

        List<String> additionalEmails = new ArrayList<>();
        String additionalEmail1 = "lorenzos_mum_stayshealthy_and_makesfun_of_lorenzo@yahoo.com";
        String additionalEmail2 = "lorenzo_hates_mum@yahoo.com";
        additionalEmails.add(additionalEmail1);
        additionalEmails.add(additionalEmail2);
        userTest.setAdditionalEmails(additionalEmails);

        assertEquals(primaryEmail, userTest.getPrimaryEmail());
        assertEquals(additionalEmails, userTest.getAdditionalEmails());
        userTest.setTransientEmailStrings();
        assertEquals(primaryEmail, userTest.getPrimaryEmail());
        assertEquals(additionalEmails, userTest.getAdditionalEmails());
    }

    @Test
    void setPassword_ensurePasswordNotPlaintext() {
        String password = "ThisIsAPassword";
        Field passwordField = ReflectionUtils.findField(User.class, "password");
        passwordField.setAccessible(true);
        userTest.setPassword(password);
        //Use reflection to access the stored password and compare it to the actual password
        //This is admittedly kind of dirty, but the passwords are intentionally hard to access
        assertNotEquals(password, ReflectionUtils.getField(passwordField, userTest));
    }
}
