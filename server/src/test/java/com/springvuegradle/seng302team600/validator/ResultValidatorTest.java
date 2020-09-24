package com.springvuegradle.seng302team600.validator;

import com.springvuegradle.seng302team600.enumeration.UnitType;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultValidatorTest {

    final static private int COMMENT_LEN = 75;
    final static private int VALUE_LEN = 1500;

    private static final Long ID_1 = 1L;
    private static final Long ID_2 = 2L;

    private Outcome outcome;
    private UnitType unitType1;
    private Result result;
    private String value1;

    @BeforeEach
    public void setUp() {
        unitType1 = UnitType.TEXT;
        value1 = "Some valid Text";

        outcome = new Outcome();
        ReflectionTestUtils.setField(outcome, "outcomeId", ID_1);
        outcome.setUnitType(unitType1);

        result = new Result();
        result.setComment("Some valid comment");
        result.setValue(value1);
        result.setDidNotFinish(false);
    }

    @Test
    public void validResultTest() {
        assertTrue(ResultValidator.validate(result, outcome));
    }

    @Test
    public void commentInvalidTest() {
        char[] invalidComment = new char[COMMENT_LEN + 1];
        Arrays.fill(invalidComment, 'C');
        result.setComment(new String(invalidComment));
        assertThrows(ResponseStatusException.class, () -> ResultValidator.validate(result, outcome));
    }

    @Test
    public void valueLengthInvalidTest() {
        char[] invalidValue = new char[VALUE_LEN + 1];
        Arrays.fill(invalidValue, 'V');
        result.setValue(new String(invalidValue));
        assertThrows(ResponseStatusException.class, () -> ResultValidator.validate(result, outcome));
    }

    @Test
    public void valueTypeNumberValidTest() {
        outcome.setUnitType(UnitType.NUMBER);
        result.setValue("1234567890"); // All the digits
        assertTrue(ResultValidator.validate(result, outcome));
    }

    @Test
    public void valueTypeNumberInvalidTest() {
        outcome.setUnitType(UnitType.NUMBER);
        assertThrows(ResponseStatusException.class, () -> ResultValidator.validate(result, outcome));
    }

    @Test
    public void valueTypeBooleanValidTest() {
        outcome.setUnitType(UnitType.BOOLEAN);
        result.setValue("true");
        assertTrue(ResultValidator.validate(result, outcome));
        result.setValue("false");
        assertTrue(ResultValidator.validate(result, outcome));
    }

    @Test
    public void valueTypeBooleanInvalidTest() {
        outcome.setUnitType(UnitType.BOOLEAN);
        assertThrows(ResponseStatusException.class, () -> ResultValidator.validate(result, outcome));
    }
}
