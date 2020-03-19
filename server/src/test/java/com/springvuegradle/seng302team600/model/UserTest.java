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

        assertEquals(4, actualAdditionalEmailsSize);
        assertEquals(5, actualEmailsSize);

        for (Email email: userTest.getEmails()) {
            if (!email.getEmail().equals(primaryEmail)) {
                assertFalse(email.getIsPrimary());
            }
        }
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

        Exception maximumEmailsException = assertThrows(MaximumEmailsException.class, () -> {
            additionalEmails.add("davidthebacker@parking.org");
            userTest.setAdditionalEmails(additionalEmails);
        });

        String expectedMessage = "Maximum email limit reached";
        String actualMessage = maximumEmailsException.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void setNewPrimaryEmail_Successful_WhenReplacedWithExistingEmail() throws MustHavePrimaryEmailException, MaximumEmailsException {
        String previousPrimaryEmail = "terry_tester@yahoo.com";
        userTest.setPrimaryEmail(previousPrimaryEmail);

        List<String> additionalEmails = new ArrayList<>();
        additionalEmails.add("nicholasmiller@from.chicago.com");
        userTest.setAdditionalEmails(additionalEmails);

        // maybe make a rule that we never directly touch the array list of emails
//        String currentPrimaryEmail = additionalEmails.get(0);
//        userTest.updatePrimaryEmail(currentPrimaryEmail);
//
//        assertEquals(previousPrimaryEmail, userTest.getAdditionalEmails().get(0));
//        assertEquals(currentPrimaryEmail, userTest.getPrimaryEmail());
//
//        for (Email email: userTest.getEmails()) {
//            if (email.getEmail().equals(currentPrimaryEmail)) {
//                assertTrue(email.getIsPrimary());
//            } else {
//                assertFalse(email.getIsPrimary());
//            }
//        }
    }
}
