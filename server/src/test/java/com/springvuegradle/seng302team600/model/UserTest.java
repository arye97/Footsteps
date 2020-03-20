package com.springvuegradle.seng302team600.model;

import com.springvuegradle.seng302team600.exception.MaximumEmailsException;
import com.springvuegradle.seng302team600.exception.MustHavePrimaryEmailException;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User userTest;


    @BeforeEach
    void setup() {
        userTest = new User();
        userTest.setFirstName("Terry");
        userTest.setLastName("Tester");
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
