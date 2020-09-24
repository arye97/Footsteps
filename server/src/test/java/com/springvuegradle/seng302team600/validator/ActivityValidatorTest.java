package com.springvuegradle.seng302team600.validator;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.model.Location;
import org.apache.coyote.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityValidatorTest {

    private static final Long MILLISECONDS_PER_DAY = 86400000L;
    private static final int NAME_LEN = 75;
    private static final int DESCRIPTION_LEN = 1500;

    private Activity activity;
    private Location location;
    private ResponseStatusException exception;

    @BeforeEach
    public void setUp() {
        activity = new Activity();
        activity.setName("Snow day");
        activity.setDescription("Skiing and snowboarding for a whole day!");
        activity.setFitnessLevel(4);

        Set<ActivityType> activityTypes = new HashSet<>();
        ActivityType activityType = new ActivityType();
        activityType.setName("Skiing");
        activityTypes.add(activityType);

        activity.setActivityTypes(activityTypes);
        activity.setContinuous(false);
        Date start = new Date();
        activity.setStartTime(start);
        activity.setEndTime(new Date(start.getTime() + MILLISECONDS_PER_DAY));

        location = new Location();
        location.setLatitude(10.0);
        location.setLongitude(-30.45);
        location.setLocationName("Location name");
        activity.setLocation(location);
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

    @Test
    public void locationNameIsNullCausesError() {
        location.setLocationName(null);
        activity.setLocation(location);
        exception = assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
        assertEquals( "400 BAD_REQUEST \"Location must have a name\"", exception.getMessage());
    }

    @Test
    public void locationLatitudeIsNullCausesError() {
        location.setLatitude(null);
        activity.setLocation(location);
        exception = assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
        assertEquals("400 BAD_REQUEST \"Location must have a valid latitude value\"", exception.getMessage());
    }

    @Test
    public void tooSmallLocationLatitudeCausesError() {
        location.setLatitude(-90.1);
        activity.setLocation(location);
        exception = assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
        assertEquals("400 BAD_REQUEST \"Location latitude must exist (be between -90 and 90 degrees)\"", exception.getMessage());
    }

    @Test
    public void tooLargeLocationLatitudeCausesError() {
        location.setLatitude(90.0001);
        activity.setLocation(location);
        exception = assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
        assertEquals("400 BAD_REQUEST \"Location latitude must exist (be between -90 and 90 degrees)\"", exception.getMessage());
    }

    @Test
    public void locationLongitudeIsNullCausesError() {
        location.setLongitude(null);
        activity.setLocation(location);
        exception = assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
        assertEquals("400 BAD_REQUEST \"Location must have a valid longitude value\"", exception.getMessage());
    }

    @Test
    public void tooSmallLocationLongitudeCausesError() {
        location.setLongitude(-180.0001);
        activity.setLocation(location);
        exception = assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
        assertEquals("400 BAD_REQUEST \"Location longitude must exist (be between -180 and 180 degrees)\"", exception.getMessage());
    }

    @Test
    public void tooLargeLocationLongitudeCausesError() {
        location.setLongitude(180.12);
        activity.setLocation(location);
        exception = assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
        assertEquals("400 BAD_REQUEST \"Location longitude must exist (be between -180 and 180 degrees)\"", exception.getMessage());
    }

    @Test
    public void aboveMaxFitnessLevelCausesError() {
        activity.setFitnessLevel(5);
        assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
    }

    @Test
    public void belowMinFitnessLevelCausesError() {
        activity.setFitnessLevel(-2);
        assertThrows(ResponseStatusException.class, () -> ActivityValidator.validate(activity));
    }
}
