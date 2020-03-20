package com.springvuegradle.seng302team600.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailTest {

    private User userTest;
    private Email emailTest;

    @BeforeEach
    void setup() {
        userTest = new User();
        userTest.setFirstName("Ferryman");
        userTest.setLastName("Perry");
    }

    @Test
    void setUser_Successful_WhenUserExists() {
        String primaryEmail = "ferrymanperry@arrmatey.org";
        userTest.setPrimaryEmail(primaryEmail);
        emailTest = userTest.getEmails().get(0);

//        System.out.println(emailTest.getUser());
        assertEquals(emailTest.getEmail(), primaryEmail);
//        assertEquals(emailTest.getUser(), userTest);
    }
}
