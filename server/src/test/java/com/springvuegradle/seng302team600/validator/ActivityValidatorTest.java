package com.springvuegradle.seng302team600.validator;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.ActivityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActivityValidatorTest {

    final static private Long MILLISECONDS_PER_DAY = 86400000L;
    final static private int NAME_LEN = 75;
    final static private int DESCRIPTION_LEN = 1500;

    private Activity activity;

    @BeforeEach
    public void setUp() {
        activity = new Activity();
        activity.setName("Snow day");
        activity.setDescription("Skiing and snowboarding for a whole day!");

        Set<ActivityType> activityTypes = new HashSet<>();
        ActivityType activityType = new ActivityType();
        activityType.setName("Skiing");
        activityTypes.add(activityType);

        activity.setActivityTypes(activityTypes);
        activity.setContinuous(false);
        Date start = new Date();
        activity.setStartTime(start);
        activity.setEndTime(new Date(start.getTime() + MILLISECONDS_PER_DAY));
    }

    @Test
    public void validActivityTest() {
        assertTrue(ActivityValidator.validate(activity));
    }

    @Test
    public void nameInvalidTest() {
        char[] name = new char[NAME_LEN + 1];
        Arrays.fill(name, 'N');
        activity.setName(new String(name));
        assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));

        activity.setName(" ");
        assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
    }

    @Test
    public void descriptionInvalidTest() {
        char[] desc = new char[DESCRIPTION_LEN + 1];
        Arrays.fill(desc, 'D');
        activity.setDescription(new String(desc));
        assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));

        activity.setDescription(" ");
        assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
    }

    @Test
    public void activityTypeSetInvalidTest() {
        activity.setActivityTypes(new HashSet<>()); // Set empty HashSet
        assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
    }

    @Test
    public void datesInvalidTest() {
        Date start = new Date();
        activity.setEndTime(start);
        activity.setStartTime(new Date(start.getTime() + MILLISECONDS_PER_DAY));
        assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
    }
}
