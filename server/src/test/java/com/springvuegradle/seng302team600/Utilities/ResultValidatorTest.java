package com.springvuegradle.seng302team600.Utilities;

import com.springvuegradle.seng302team600.enumeration.UnitType;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.Result;
import com.springvuegradle.seng302team600.model.Unit;
import com.springvuegradle.seng302team600.model.Value;
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
    private Unit unit1;
    private Unit unit2;
    private Result result;
    private Value value1;
    private Value value2;

    @BeforeEach
    public void setUp() {
        Set<Unit> units = new HashSet<>();
        unit1 = new Unit();
        unit1.setUnitType(UnitType.TEXT);
        ReflectionTestUtils.setField(unit1, "unitId", ID_1);
        units.add(unit1);
        // Unit2 is not initially added to the set
        unit2 = new Unit();
        ReflectionTestUtils.setField(unit2, "unitId", ID_2);
        unit2.setUnitType(UnitType.TEXT);

        outcome = new Outcome();
        ReflectionTestUtils.setField(outcome, "outcomeId", ID_1);
        outcome.setUnits(units);

        Set<Value> values = new HashSet<>();
        value1 = new Value();
        ReflectionTestUtils.setField(value1, "valueId", ID_1);
        value1.setDidNotFinish(false);
        value1.setUnitId(ID_1);
        value1.setValue("Some valid Text");
        values.add(value1);
        // value2 is not initially added to the set
        value2 = new Value();
        ReflectionTestUtils.setField(value2, "valueId", ID_2);
        value2.setDidNotFinish(false);
        value2.setUnitId(ID_2);
        value2.setValue("Some valid Text");

        result = new Result();
        result.setComment("Some valid comment");
        result.setValues(values);
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
        value1.setValue(new String(invalidValue));
        assertThrows(ResponseStatusException.class, () -> ResultValidator.validate(result, outcome));
    }

    @Test
    public void valueTypeNumberValidTest() {
        unit1.setUnitType(UnitType.NUMBER);
        value1.setValue("1234567890"); // All the digits
        assertTrue(ResultValidator.validate(result, outcome));
    }

    @Test
    public void valueTypeNumberInvalidTest() {
        unit1.setUnitType(UnitType.NUMBER);
        assertThrows(ResponseStatusException.class, () -> ResultValidator.validate(result, outcome));
    }

    @Test
    public void valueTypeBooleanValidTest() {
        unit1.setUnitType(UnitType.BOOLEAN);
        value1.setValue("true");
        assertTrue(ResultValidator.validate(result, outcome));
        value1.setValue("false");
        assertTrue(ResultValidator.validate(result, outcome));
    }

    @Test
    public void valueTypeBooleanInvalidTest() {
        unit1.setUnitType(UnitType.BOOLEAN);
        assertThrows(ResponseStatusException.class, () -> ResultValidator.validate(result, outcome));
    }

    @Test
    public void multipleValuesAndUnitsValidTest() {
        Set<Unit> units = new HashSet<>();
        units.add(unit1);
        units.add(unit2);
        outcome.setUnits(units);

        Set<Value> values = new HashSet<>();
        values.add(value1);
        values.add(value2);
        result.setValues(values);
        assertTrue(ResultValidator.validate(result, outcome));
    }

    @Test
    public void notEnoughValuesTest() {
        Set<Unit> units = new HashSet<>();
        units.add(unit1);
        units.add(unit2);
        outcome.setUnits(units);
        assertThrows(ResponseStatusException.class, () -> ResultValidator.validate(result, outcome));
    }

    @Test
    public void tooManyValuesTest() {
        Set<Value> values = new HashSet<>();
        values.add(value1);
        values.add(value2);
        result.setValues(values);
        assertThrows(ResponseStatusException.class, () -> ResultValidator.validate(result, outcome));
    }

    @Test
    public void multipleValuesForOneUnitTest() {
        Set<Unit> units = new HashSet<>();
        units.add(unit1);
        units.add(unit2);
        outcome.setUnits(units);

        Set<Value> values = new HashSet<>();
        values.add(value1);
        values.add(value1);
        result.setValues(values);
        assertThrows(ResponseStatusException.class, () -> ResultValidator.validate(result, outcome));
    }
}
