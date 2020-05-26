package com.springvuegradle.seng302team600.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultAdminUserTest {

    private User defaultAdminTest;

    private Date getAgeDate(int age) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        return calendar.getTime();
    }

    @BeforeEach
    void setUp() {
        defaultAdminTest = new DefaultAdminUser();
    }

    @Test
    void primaryEmailCantBeChanged() {
        String primaryEmail = defaultAdminTest.getPrimaryEmail();
        defaultAdminTest.setPrimaryEmail("new" + primaryEmail);
        assertEquals(primaryEmail, defaultAdminTest.getPrimaryEmail());
    }

    @Test
    void additionalEmailsCantBeChanged() {
        List<String> additionalEmails = defaultAdminTest.getAdditionalEmails();
        String newEmail = "sally@hotmail.com";
        additionalEmails.add(newEmail);
        defaultAdminTest.setAdditionalEmails(additionalEmails);
        assertNotEquals(additionalEmails.size(), defaultAdminTest.getAdditionalEmails().size());
        assertFalse(defaultAdminTest.getAdditionalEmails().contains(newEmail));
    }
}
