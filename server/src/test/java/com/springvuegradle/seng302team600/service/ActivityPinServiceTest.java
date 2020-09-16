package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.*;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest(ActivityPinService.class)
class ActivityPinServiceTest {

    @MockBean
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityPinService activityPinService;

    private List<Activity> activityList = new ArrayList<>();

    private final Long DUMMY_USER_ID_1 = 1L;
    private final Long DUMMY_USER_ID_2 = 2L;

    private final Long DUMMY_ACTIVITY_ID_1 = 1L;
    private final Long DUMMY_ACTIVITY_ID_2 = 2L;
    private final Long DUMMY_ACTIVITY_ID_3 = 3L;

    private final Double LONDON_LAT = 51.507351D;
    private final Double LONDON_LON = -0.127758D;

    private final Double BERLIN_LAT = 52.520008D;
    private final Double BERLIN_LON = 13.404954D;

    private final Double WARSAW_LAT = 52.229675D;
    private final Double WARSAW_LON = 21.012230D;

    private final Double MOSCOW_LAT = 37.617298D;
    private final Double MOSCOW_LON = 55.755825D;

    private User dummyUser1;

    /**
     * Mock Activity repository actions
     */
    @BeforeEach
    void setupActivityPinRepository() {
        dummyUser1 = new User();
        ReflectionTestUtils.setField(dummyUser1, "userId", 1L);
        dummyUser1.setPrivateLocation(new Location(0D, 0D, "Nigerian Coast"));
        dummyUser1.setPublicLocation(new Location(LONDON_LON, LONDON_LAT, "London"));

        Activity dummyActivity1 = new Activity();
        ReflectionTestUtils.setField(dummyActivity1, "activityId", DUMMY_ACTIVITY_ID_1);
        dummyActivity1.setLocation(new Location(BERLIN_LON, BERLIN_LAT, "Berlin"));
        Activity dummyActivity2 = new Activity();
        ReflectionTestUtils.setField(dummyActivity2, "activityId", DUMMY_ACTIVITY_ID_2);
        dummyActivity2.setLocation(new Location(WARSAW_LON, WARSAW_LAT, "Warsaw"));
        Activity dummyActivity3 = new Activity();
        ReflectionTestUtils.setField(dummyActivity3, "activityId", DUMMY_ACTIVITY_ID_3);
        dummyActivity3.setLocation(new Location(MOSCOW_LON, MOSCOW_LAT, "Moscow"));

        dummyActivity1.setCreatorUserId(DUMMY_USER_ID_1);
        dummyActivity2.setCreatorUserId(DUMMY_USER_ID_1);
        dummyActivity3.setCreatorUserId(DUMMY_USER_ID_2);

        activityList.add(dummyActivity1);
        activityList.add(dummyActivity2);
        activityList.add(dummyActivity3);

        // findAllByUserId
        when(activityRepository.findAllByUserId(Mockito.anyLong())).thenAnswer(i -> {
            Long userId = i.getArgument(0);
            List<Activity> foundActivites = new ArrayList<>();
            for (Activity activity : activityList) {
                if (activity.getCreatorUserId().equals(userId)) {
                    foundActivites.add(activity);
                } else {
                    for (User participant : activity.getParticipants()) {
                        if (participant.getUserId().equals(userId)) {
                            foundActivites.add(activity);
                        }
                    }
                }
            }
            return foundActivites;
        });
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getPinsByUserIdSuccess() throws Exception {
        List<Pin> pins = activityPinService.getPins(dummyUser1);

        assertNotNull(pins);
        assertEquals(3, pins.size()); // 2 for the activities + 1 for the user

        //not equals as this has a private address set
        assertNotEquals(LONDON_LAT, pins.get(0).getLatitude());
        assertNotEquals(LONDON_LON, pins.get(0).getLongitude());

        assertEquals(0D, pins.get(0).getLatitude());
        assertEquals(0D, pins.get(0).getLongitude());

        assertEquals(BERLIN_LAT, pins.get(1).getLatitude());
        assertEquals(BERLIN_LON, pins.get(1).getLongitude());

        assertEquals(WARSAW_LAT, pins.get(2).getLatitude());
        assertEquals(WARSAW_LON, pins.get(2).getLongitude());
    }

    @Test
    void getPinsByUserIdFailure() throws Exception {
        List<Pin> pins = activityPinService.getPins(new User());
        assertNotNull(pins);
        assertEquals(0, pins.size());
    }
}