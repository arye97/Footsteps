package com.springvuegradle.seng302team600.model;

import com.springvuegradle.seng302team600.exception.InvalidDateOfBirthException;
import com.springvuegradle.seng302team600.exception.InvalidUserNameException;
import com.springvuegradle.seng302team600.exception.UserTooYoungException;
import com.springvuegradle.seng302team600.exception.MaximumEmailsException;
import com.springvuegradle.seng302team600.exception.MustHavePrimaryEmailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User userTest;

    private Date getAgeDate(int age) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        return calendar.getTime();
    }

    @BeforeEach
    void setUp() {
         user1 = new User();
         user1.setFirstName("Jimmy");
         user1.setMiddleName(null);
         user1.setLastName("Jones");
         user1.setDateOfBirth(getAgeDate(50));
    }

    @Test
    /** Check lower bound of age (age<=13)
     * Checks ages 12, 13 and today (0)
     */
    void ageTooYoung() throws UserTooYoungException, InvalidUserNameException, InvalidDateOfBirthException {
        assertTrue(user1.isValid());
        user1.setDateOfBirth(getAgeDate(12));
        assertThrows(UserTooYoungException.class, () -> user1.isValid());
        user1.setDateOfBirth(Calendar.getInstance().getTime());
        assertThrows(UserTooYoungException.class, () -> user1.isValid());
        user1.setDateOfBirth(getAgeDate(13));
        assertTrue(user1.isValid());
        user1.setDateOfBirth(getAgeDate(14));
        assertTrue(user1.isValid());
    }

    @Test
    /** Check upper bound of age (age<150)
     * Checks ages 151, 150 and 149
     */
    void ageTooOld() throws UserTooYoungException, InvalidUserNameException, InvalidDateOfBirthException {
        assertTrue(user1.isValid());
        user1.setDateOfBirth(getAgeDate(151));
        assertThrows(InvalidDateOfBirthException.class, () -> user1.isValid());
        user1.setDateOfBirth(getAgeDate(150));
        assertThrows(InvalidDateOfBirthException.class, () -> user1.isValid());
        user1.setDateOfBirth(getAgeDate(149));
        assertTrue(user1.isValid());
    }

    @Test
    void invalidFirstNameEmpty() throws UserTooYoungException, InvalidUserNameException, InvalidDateOfBirthException {
        assertTrue(user1.isValid());
        user1.setFirstName("");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
        user1.setFirstName("    ");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
    }

    @Test
    void invalidFirstNameNull() throws UserTooYoungException, InvalidUserNameException, InvalidDateOfBirthException {
        assertTrue(user1.isValid());
        user1.setFirstName(null);
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
    }

    @Test
    void invalidFirstNameNonAlphaChar() throws UserTooYoungException, InvalidUserNameException, InvalidDateOfBirthException {
        assertTrue(user1.isValid());
        user1.setFirstName("Jimmy7");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
        user1.setFirstName("Jimmy@");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
        user1.setFirstName("!");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
    }

    @Test
    void invalidLastNameEmpty() throws UserTooYoungException, InvalidUserNameException, InvalidDateOfBirthException {
        assertTrue(user1.isValid());
        user1.setLastName("");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
        user1.setLastName("     ");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
    }

    @Test
    void invalidLastNameNull() throws UserTooYoungException, InvalidUserNameException, InvalidDateOfBirthException {
        assertTrue(user1.isValid());
        user1.setLastName(null);
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
    }

    @Test
    void invalidLastNameNonAlphaChar() throws UserTooYoungException, InvalidUserNameException, InvalidDateOfBirthException {
        assertTrue(user1.isValid());
        user1.setLastName("Jones7");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
        user1.setLastName("Jones^");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
        user1.setLastName("%");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
    }

    @Test
    void invalidMiddleNameEmpty() throws UserTooYoungException, InvalidUserNameException, InvalidDateOfBirthException {
        assertTrue(user1.isValid());
        user1.setMiddleName("");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
        user1.setMiddleName("     ");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
    }

    @Test
    void validMiddleNameNull() throws UserTooYoungException, InvalidUserNameException, InvalidDateOfBirthException {
        assertTrue(user1.isValid());
        user1.setMiddleName(null);
        assertTrue(user1.isValid());
    }

    @Test
    void invalidMiddleNameNonAlphaChar() throws UserTooYoungException, InvalidUserNameException, InvalidDateOfBirthException {
        assertTrue(user1.isValid());
        user1.setMiddleName("Johnny7");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
        user1.setMiddleName("Johnny*");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
        user1.setMiddleName("+");
        assertThrows(InvalidUserNameException.class, () -> user1.isValid());
    }
}

    @Test
    void setOnePrimaryEmail_Success_WhenCreatingUser() throws MustHavePrimaryEmailException {
        String email = "terry_tester@yahoo.com";
        userTest.setPrimaryEmail(email);

        assertEquals(email, userTest.getPrimaryEmail());
        assertEquals(1, userTest.getEmails().size());

        assertTrue(userTest.getEmails().get(0).getIsPrimary());
        assertEquals(userTest.getEmails().get(0).getUser(), userTest);
    }


    @Test
    void setAdditionalEmails_Success_WhenAddingAdditionalEmails() throws MustHavePrimaryEmailException, MaximumEmailsException {
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
    void setAdditionalEmails_ExceptionThrown_WhenPrimaryEmailDoesNotExist() throws MustHavePrimaryEmailException, MaximumEmailsException {
        List<String> additionalEmails = new ArrayList<>();
        additionalEmails.add("guavaperson@cucumber.com");
        additionalEmails.add("pam@pam.pam");

        Exception exception = assertThrows(MustHavePrimaryEmailException.class, () -> {
            userTest.setAdditionalEmails(additionalEmails);
        });

        String expectedMessage = "Primary email must be created before additional emails are added";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        // Potentially check error value
    }

    @Test
    void setAdditionalEmails_ExceptionThrown_WhenEmailLimitReached() throws MustHavePrimaryEmailException, MaximumEmailsException {
        String primaryEmail = "maria_dianna_bart@database.com";
        userTest.setPrimaryEmail(primaryEmail);

        List<String> additionalEmails = new ArrayList<>();
        additionalEmails.add("alexosbourne@burns.com");
        additionalEmails.add("olivesnook@pie.com");
        additionalEmails.add("magicalcat@cats.com");
        additionalEmails.add("lizardman@cow.com");

        Exception exception = assertThrows(MaximumEmailsException.class, () -> {
            additionalEmails.add("davidthebacker@parking.org");
            userTest.setAdditionalEmails(additionalEmails);
        });

        String expectedMessage = "Maximum email limit reached";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        // Potentially check error value
    }


    @Test
    void setNewPrimaryEmail_Success_WhenReplacedWithExistingEmail() throws MustHavePrimaryEmailException, MaximumEmailsException {
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
            System.out.println(email.getEmail());
            System.out.println(email.getIsPrimary());
            if (email.getEmail().equals(newPrimaryEmail)) {
                assertTrue(email.getIsPrimary());
            } else {
                assertFalse(email.getIsPrimary());
            }
        }
    }

    @Test
    void deleteAdditionalEmail_Success_WhenRemovingEmailFromUser() throws MustHavePrimaryEmailException, MaximumEmailsException {
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
}
