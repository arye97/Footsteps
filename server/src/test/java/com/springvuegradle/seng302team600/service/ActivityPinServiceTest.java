package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.*;
import com.springvuegradle.seng302team600.payload.pins.Pin;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest(ActivityPinService.class)
class ActivityPinServiceTest {

    @MockBean
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityPinService activityPinService;

    private List<Activity> activityList = new ArrayList<>();

    private final int PAGE_ONE = 0;
    private final int PAGE_TWO = 1;

    private final Long DUMMY_USER_ID_1 = 1L;
    private final Long DUMMY_USER_ID_2 = 2L;

    private final Long DUMMY_ACTIVITY_ID_1 = 1L;
    private final Long DUMMY_ACTIVITY_ID_2 = 2L;
    private final Long DUMMY_ACTIVITY_ID_3 = 3L;
    private Long DUMMY_ACTIVITY_STUB_ID = 4L;

    private final Double LONDON_LAT = 51.507351D;
    private final Double LONDON_LON = -0.127758D;

    private final Double BERLIN_LAT = 52.520008D;
    private final Double BERLIN_LON = 13.404954D;

    private final Double WARSAW_LAT = 52.229675D;
    private final Double WARSAW_LON = 21.012230D;

    private final Double MOSCOW_LAT = 37.617298D;
    private final Double MOSCOW_LON = 55.755825D;

    private final Double DUMMY_LAT = 12.345678D;
    private final Double DUMMY_LON = 12.345678D;

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
        dummyActivity3.setCreatorUserId(DUMMY_USER_ID_1);

        activityList.add(dummyActivity1);
        activityList.add(dummyActivity2);
        activityList.add(dummyActivity3);

        when(activityRepository.findAllByUserId(Mockito.anyLong(), Mockito.any(Pageable.class))).thenAnswer(i -> {
            Long userId = i.getArgument(0);
            Pageable pageWithTwentyActivities = i.getArgument(1);
            int pageNumber = pageWithTwentyActivities.getPageNumber();
            int pageSize = pageWithTwentyActivities.getPageSize();
            List<Activity> foundActivities = new ArrayList<>();
            for (Activity activity : activityList) {
                if (activity.getCreatorUserId().equals(userId)) {
                    foundActivities.add(activity);
                } else {
                    for (User participant : activity.getParticipants()) {
                        if (participant.getUserId().equals(userId)) {
                            foundActivities.add(activity);
                        }
                    }
                }
            }
            if (foundActivities.size() > 0) {
                int startIndex = pageNumber * pageSize;
                int endIndex = (pageNumber + 1) * pageSize;
                List<Activity> paginatedActivityBlocks;
                if (startIndex > foundActivities.size()) {
                    return null;
                } else if (endIndex > foundActivities.size()) {
                    endIndex = foundActivities.size();
                }
                paginatedActivityBlocks = foundActivities.subList(startIndex, endIndex);
                return new PageImpl(paginatedActivityBlocks);
            } else {
                return null;
            }
        });
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getActivityPinsByUserIdAndActivityListSuccess() throws Exception {
        List<Pin> pins = activityPinService.getPins(dummyUser1, activityList);

        assertNotNull(pins);
        assertEquals(3, pins.size());

        assertEquals(BERLIN_LAT, pins.get(0).getLatitude());
        assertEquals(BERLIN_LON, pins.get(0).getLongitude());

        assertEquals(WARSAW_LAT, pins.get(1).getLatitude());
        assertEquals(WARSAW_LON, pins.get(1).getLongitude());

        assertEquals(MOSCOW_LAT, pins.get(2).getLatitude());
        assertEquals(MOSCOW_LON, pins.get(2).getLongitude());
    }

    @Test
    void getActivityPinsByUserIdAndActivityListFailure() throws Exception {
        List<Pin> pins = activityPinService.getPins(new User(), new ArrayList<>());
        assertNotNull(pins);
        assertEquals(0, pins.size());
    }

    @Test
    void getPageOneOfPaginatedBlockActivityListSuccess() {
        Slice<Activity> blockOfActivities = activityPinService.getPaginatedActivityList(dummyUser1, PAGE_ONE);
        assertEquals(3, blockOfActivities.getSize());
    }

    @Test
    void getEmptyPageTwoOfPaginatedBlockActivityListNull() {
        Slice<Activity> blockOfActivities = activityPinService.getPaginatedActivityList(dummyUser1, PAGE_TWO);
        assertNull(blockOfActivities);
    }

    @Test
    void getPopulatedPageOneOfPaginatedBlockActivityListSuccess() {
        populateActivityList();
        Slice<Activity> blockOfActivities = activityPinService.getPaginatedActivityList(dummyUser1, PAGE_ONE);
        assertEquals(20, blockOfActivities.getSize());
    }

    @Test
    void getPopulatedPageTwoOfPaginatedBlockActivityListSuccess() {
        populateActivityList();
        Slice<Activity> blockOfActivities = activityPinService.getPaginatedActivityList(dummyUser1, PAGE_TWO);
        assertEquals(3, blockOfActivities.getSize());
    }

    /**
     * Populates activityList with an extra 20 activities for testing pagination
     */
    public void populateActivityList() {
        int EXTRA_ACTIVITIES_COUNT = 20;
        for (int i = 0; i < EXTRA_ACTIVITIES_COUNT; i++) {
            Activity dummyActivityStub = new Activity();
            ReflectionTestUtils.setField(dummyActivityStub, "activityId", DUMMY_ACTIVITY_STUB_ID);
            dummyActivityStub.setLocation(new Location(DUMMY_LAT, DUMMY_LON, "Qatar"));
            dummyActivityStub.setCreatorUserId(DUMMY_USER_ID_1);
            activityList.add(dummyActivityStub);
            DUMMY_ACTIVITY_STUB_ID++;
        }
    }
}