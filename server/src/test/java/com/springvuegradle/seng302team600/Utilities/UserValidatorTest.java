package com.springvuegradle.seng302team600.Utilities;

import com.springvuegradle.seng302team600.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidatorTest {

    final static private String nameRegex = "^[\\p{L} \\-']+([\\p{L} \\-']+)*$";
    final static private int MAX_NAME_LEN = 45;
    final static private int BIO_LEN = 255;
    final static public int MIN_AGE = 13;
    final static public int MAX_AGE = 150;
    final static public String NAME_ERROR = "Name must contain at least one letter and no non-letter characters";

    @BeforeEach
    public void setUp() {
        User testUser = new User();
    }

}