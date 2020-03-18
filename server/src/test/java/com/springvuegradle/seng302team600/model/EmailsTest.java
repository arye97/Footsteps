package com.springvuegradle.seng302team600.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EmailsTest {

    String email1;
    String email2;
    String email3;
    String email4;
    String email5;
    String email6;
    ArrayList<String> secondaries;
    Emails emails1;
    Emails emails2;

    @BeforeEach
    void setUp() {
        email1 = "bgt26@uclive.ac.nz";
        email2 = "example@gmail.com";
        email3 = "john.smith@hotmail.com";
        email4 = "jane.doe@yahoo.com";
        email5 = "very.common@canterbury.ac.nz";
        email6 = "admin@mailserver1.com";
        secondaries = new ArrayList<String>(Arrays.asList(email2, email3, email4, email5));
        emails1 = new Emails(email1);
        emails2 = new Emails(email1, secondaries);
    }

    @Test
    void addSecondaryEmail() throws Exception {
        emails1.addSecondaryEmail(email2);
        assertEquals(emails1.size(), 2);
    }

    @Test
    void addSecondaryEmail1() throws Exception {
        emails2.removeEmail(2);
        emails2.addSecondaryEmail(email4, 2);
    }

    @Test
    /**Check when a new unique email is added*/
    void setPrimaryEmail() throws Exception{
        emails1.setPrimaryEmail(email2);
        assertEquals(emails1.getPrimaryEmail(), email2);
        assertEquals(emails1.getSecondaryEmails().get(0), email1);
    }

    @Test
    /**Check when an existing secondary is made primary*/
    void setPrimaryEmail1() throws Exception {
        int i = emails2.getSecondaryEmails().indexOf(email3);
        emails2.setPrimaryEmail(email3);
        assertEquals(emails2.getPrimaryEmail(), email3);
        assertEquals(emails2.getSecondaryEmails().get(i), email1);
    }

    @Test
    /**Check when making an email at an index primary*/
    void setPrimaryEmail2() throws Exception {
        emails2.setPrimaryEmail(2);
        assertEquals(emails2.getPrimaryEmail(), email4);
        assertEquals(emails2.getSecondaryEmails().get(2), email1);
    }

    @Test
    void removePrimaryEmail() {
        emails2.removePrimaryEmail();
        assertEquals(emails2.getPrimaryEmail(), email2);
    }

    @Test
    /**Check removing primary email by name*/
    void removeEmail() {
        emails2.removeEmail(email1);
        assertEquals(emails2.getPrimaryEmail(), email2);
    }

    @Test
    /**Check removing secondary by name*/
    void removeEmail1() {
        boolean b = emails2.removeEmail(email2);
        assertEquals(emails2.getSecondaryEmails().get(0), email3);
        assertTrue(b);
    }

    @Test
    /**Check removing by index*/
    void removeEmail2() {
        String s = emails2.removeEmail(1);
        assertEquals(emails2.getSecondaryEmails().get(1), email4);
        assertEquals(s, email3);
    }

    @Test
    /**Shift positive*/
    void shiftSecondaryEmail() {
        int i = emails2.getSecondaryEmails().indexOf(email3);
        emails2.shiftSecondaryEmail(email3, 2);
        assertEquals(emails2.getSecondaryEmails().get(i + 2), email3);
    }

    @Test
    /**Shift negative*/
    void shiftSecondaryEmail1() {
        int i = emails2.getSecondaryEmails().indexOf(email4);
        emails2.shiftSecondaryEmail(email4, -2);
        assertEquals(emails2.getSecondaryEmails().get(i - 2), email4);
    }

    @Test
    /**Shift negative wrap*/
    void shiftSecondaryEmail2() {
        int i = emails2.getSecondaryEmails().indexOf(email4);
        emails2.shiftSecondaryEmail(email4, -5);
        assertEquals(emails2.getSecondaryEmails().get(i - 1), email4);
    }

    @Test
    /**Shift positive wrap*/
    void shiftSecondaryEmail3() {
        int i = emails2.getSecondaryEmails().indexOf(email4);
        emails2.shiftSecondaryEmail(email4, 7);
        assertEquals(emails2.getSecondaryEmails().get(i - 1), email4);
    }
}