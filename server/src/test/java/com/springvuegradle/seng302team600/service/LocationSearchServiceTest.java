package com.springvuegradle.seng302team600.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.model.Location;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest(LocationSearchService.class)
public class LocationSearchServiceTest {

    @MockBean
    private ActivityRepository activityRepository;

    @MockBean
    private ActivityTypeRepository activityTypeRepository;

    @Autowired
    private LocationSearchService locationSearchService;

    private List<Activity> activityList = new ArrayList<>();
    private Set<ActivityType> activityTypeSet = new HashSet<>();

    private final int PAGE_ONE = 0;
    private final int PAGE_TWO = 1;
    private final int BLOCK_SIZE = 5;

    private final Long DUMMY_ACTIVITY_ID_1 = 1L;
    private final Long DUMMY_ACTIVITY_ID_2 = 2L;
    private final Long DUMMY_ACTIVITY_ID_3 = 3L;
    private Long DUMMY_ACTIVITY_STUB_ID = 4L;

    private final Long DUMMY_TYPE_ID_1 = 1L;
    private final Long DUMMY_TYPE_ID_2 = 2L;
    private final Long DUMMY_TYPE_ID_3 = 3L;
    private final Long DUMMY_TYPE_ID_4 = 4L;

    private final Double NEGATIVE_DISTANCE = -234D;
    private final Double SHORT_DISTANCE = 120D;
    private final Double MEDIUM_DISTANCE = 4300D;
    private final Double LONG_DISTANCE = 8900D;
    private final Double VERY_LONG_DISTANCE = 11000D;

    private final Double LAT_A = -45.8667783;
    private final Double LNG_A = 170.4910567;
    private final String STRING_COORDINATES_A = "{\"lat\":\"" + LAT_A + "\",\"lng\":\"" + LNG_A + "\"}";
    private final Double LAT_BAD = 90.8667783;      // Valid range of 90,-90
    private final Double LNG_BAD = -189.810567;     // Valid range of 180,-180
    private final String STRING_COORDINATES_BAD_LNG = "{\"lat\":\"" + LAT_A + "\",\"lng\":\"" + LNG_BAD + "\"}";
    private final String STRING_COORDINATES_BAD_LAT = "{\"lat\":\"" + LAT_BAD + "\",\"lng\":\"" + LNG_A + "\"}";
    private final String STRING_COORDINATES_INVALID = "Nonsensical string. idk just hit bloq";

    private final Double LONDON_LAT = 51.507351D;
    private final Double LONDON_LON = -0.127758D;
    private final Location LONDON = new Location(LONDON_LON, LONDON_LAT, "London");

    private final Double BERLIN_LAT = 52.520008D;
    private final Double BERLIN_LON = 13.404954D;
    private final Location BERLIN = new Location(BERLIN_LON, BERLIN_LAT, "Berlin");

    private final Double WARSAW_LAT = 52.229675D;
    private final Double WARSAW_LON = 21.012230D;
    private final Location WARSAW = new Location(WARSAW_LON, WARSAW_LAT, "Warsaw");

    private final Double MOSCOW_LAT = 37.617298D;
    private final Double MOSCOW_LON = 55.755825D;
    private final Location MOSCOW = new Location(MOSCOW_LON, MOSCOW_LAT, "Moscow");
    /**
     * Mock Activity repository actions
     */
    @BeforeEach
    public void setupActivityPinRepository() {

        Activity dummyActivity1 = new Activity();
        ReflectionTestUtils.setField(dummyActivity1, "activityId", DUMMY_ACTIVITY_ID_1);
        dummyActivity1.setFitnessLevel(2);
        dummyActivity1.setLocation(BERLIN);
        Activity dummyActivity2 = new Activity();
        ReflectionTestUtils.setField(dummyActivity2, "activityId", DUMMY_ACTIVITY_ID_2);
        dummyActivity2.setFitnessLevel(4);
        dummyActivity2.setLocation(WARSAW);
        Activity dummyActivity3 = new Activity();
        ReflectionTestUtils.setField(dummyActivity3, "activityId", DUMMY_ACTIVITY_ID_3);
        dummyActivity3.setFitnessLevel(3);
        dummyActivity3.setLocation(MOSCOW);

        activityList.add(dummyActivity1);
        activityList.add(dummyActivity2);
        activityList.add(dummyActivity3);

        ActivityType dummyActivityType1 = new ActivityType("Hiking");
        ReflectionTestUtils.setField(dummyActivityType1, "activityTypeId", DUMMY_TYPE_ID_1);
        ActivityType dummyActivityType2 = new ActivityType("Biking");
        ReflectionTestUtils.setField(dummyActivityType2, "activityTypeId", DUMMY_TYPE_ID_2);
        ActivityType dummyActivityType3 = new ActivityType("Kiting");
        ReflectionTestUtils.setField(dummyActivityType3, "activityTypeId", DUMMY_TYPE_ID_3);
        ActivityType dummyActivityType4 = new ActivityType("Writing");
        ReflectionTestUtils.setField(dummyActivityType4, "activityTypeId", DUMMY_TYPE_ID_4);

        activityTypeSet.add(dummyActivityType1);
        activityTypeSet.add(dummyActivityType2);
        activityTypeSet.add(dummyActivityType3);
        activityTypeSet.add(dummyActivityType4);

        //Hiking Biking
        Set<ActivityType> HBset = new HashSet<>();
        HBset.add(dummyActivityType1);
        HBset.add(dummyActivityType2);
        dummyActivity1.setActivityTypes(HBset);

        //Biking Kiting
        Set<ActivityType> BKset = new HashSet<>();
        BKset.add(dummyActivityType2);
        BKset.add(dummyActivityType3);
        dummyActivity2.setActivityTypes(BKset);

        //Kiting Hiking
        Set<ActivityType> KHset = new HashSet<>();
        KHset.add(dummyActivityType3);
        KHset.add(dummyActivityType1);
        dummyActivity3.setActivityTypes(KHset);


        when(activityTypeRepository.findActivityTypeIdsByNames(Mockito.anyList())).thenAnswer(i -> {
            List<String> activityTypeStrings = i.getArgument(0);
            List<Long> resultList = new ArrayList<>();
            List<ActivityType> allActivityTypes = new ArrayList<>(activityTypeSet);
            for (ActivityType activityType : allActivityTypes) {
                if (activityTypeStrings.contains(activityType.getName())) {
                    resultList.add(activityType.getActivityTypeId());
                }
            }
            return resultList;
        });


        // activityRepository.findAllWithinDistanceByAllActivityTypeIds(
        //                        coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance,
        //                        activityTypeIds, numActivityTypes, activitiesBlock)
        when(activityRepository.findAllWithinDistanceByAllActivityTypeIds(Mockito.anyDouble(), Mockito.anyDouble(),
                Mockito.anyDouble(), Mockito.anyList(), Mockito.anyInt(), Mockito.eq(2), Mockito.eq(3),
                Mockito.any(Pageable.class))).thenAnswer(i -> {
            List<Long> activityTypeIds = i.getArgument(3);
            int numActivityTypes = i.getArgument(4);

            Pageable pageable = i.getArgument(7);
            List<Activity> resultList = new ArrayList<>();
            int matchCount = 0;

            for (Activity activity : activityList) {
                for (ActivityType activityType : activity.getActivityTypes()) {
                    if (activityTypeIds.contains(activityType.getActivityTypeId())) {
                        matchCount++;
                    }
                }
                if (matchCount == numActivityTypes && activity.getFitnessLevel() <= 3 && activity.getFitnessLevel() >= 2) {
                    resultList.add(activity);
                }
                matchCount = 0;
            }

            Slice<Activity> resultSlice;
            if (resultList.size() > BLOCK_SIZE) {
                int leftIndex = PAGE_ONE * BLOCK_SIZE;
                int rightIndex = PAGE_TWO * BLOCK_SIZE + BLOCK_SIZE;
                resultSlice = new SliceImpl<>(resultList.subList(leftIndex, rightIndex), pageable, true);
            } else {
                resultSlice = new SliceImpl<>(resultList, pageable, false);
            }
            return resultSlice;
        });


        // activityRepository.findAllWithinDistanceBySomeActivityTypeIds(
        //                        coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance,
        //                        activityTypeIds, activitiesBlock);
        when(activityRepository.findAllWithinDistanceBySomeActivityTypeIds(Mockito.anyDouble(), Mockito.anyDouble(),
                Mockito.anyDouble(), Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.any(Pageable.class))).thenAnswer(i -> {
            List<Long> activityTypeIds = i.getArgument(3);
            Pageable pageable = i.getArgument(6);
            List<Activity> resultList = new ArrayList<>();

            for (Activity activity : activityList) {
                for (ActivityType activityType : activity.getActivityTypes()) {
                    if (activityTypeIds.contains(activityType.getActivityTypeId())) {
                        resultList.add(activity);
                        break;
                    }
                }
            }

            Slice<Activity> resultSlice;
            if (resultList.size() > BLOCK_SIZE) {
                int leftIndex = PAGE_ONE * BLOCK_SIZE;
                int rightIndex = PAGE_TWO * BLOCK_SIZE + BLOCK_SIZE;
                resultSlice = new SliceImpl<>(resultList.subList(leftIndex, rightIndex), pageable, true);
            } else {
                resultSlice = new SliceImpl<>(resultList, pageable, false);
            }
            return resultSlice;
        });
        when(activityRepository.findAllWithinDistanceBySomeActivityTypeIds(Mockito.anyDouble(), Mockito.anyDouble(),
                Mockito.anyDouble(), Mockito.anyList(), Mockito.eq(2), Mockito.eq(3),
                Mockito.any(Pageable.class))).thenAnswer(i -> {
            List<Long> activityTypeIds = i.getArgument(3);
            Pageable pageable = i.getArgument(6);
            List<Activity> resultList = new ArrayList<>();

            Integer minFitness = i.getArgument(4);
            Integer maxFitness = i.getArgument(5);

            for (Activity activity : activityList) {
                for (ActivityType activityType : activity.getActivityTypes()) {
                    if (activityTypeIds.contains(activityType.getActivityTypeId())
                            && activity.getFitnessLevel() >= minFitness
                            && activity.getFitnessLevel() <= maxFitness) {
                        resultList.add(activity);
                        break;
                    }
                }
            }

            Slice<Activity> resultSlice;
            if (resultList.size() > BLOCK_SIZE) {
                int leftIndex = PAGE_ONE * BLOCK_SIZE;
                int rightIndex = PAGE_TWO * BLOCK_SIZE + BLOCK_SIZE;
                resultSlice = new SliceImpl<>(resultList.subList(leftIndex, rightIndex), pageable, true);
            } else {
                resultSlice = new SliceImpl<>(resultList, pageable, false);
            }
            return resultSlice;
        });


        // activityRepository.findAllWithinDistance(
        //                    coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance, activitiesBlock);
        when(activityRepository.findAllWithinDistance(Mockito.anyDouble(), Mockito.anyDouble(),
                Mockito.anyDouble(), Mockito.any(Pageable.class))).thenAnswer(i -> {
            Pageable pageable = i.getArgument(3);
            List<Activity> resultList = new ArrayList<>(activityList);

            Slice<Activity> resultSlice;
            if (resultList.size() > BLOCK_SIZE) {
                int leftIndex = PAGE_ONE * BLOCK_SIZE;
                int rightIndex = PAGE_TWO * BLOCK_SIZE + BLOCK_SIZE;
                resultSlice = new SliceImpl<>(resultList.subList(leftIndex, rightIndex), pageable, true);
            } else {
                resultSlice = new SliceImpl<>(resultList, pageable, false);
            }
            return resultSlice;
        });


        when(activityRepository.countAllWithinDistance(
                Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble())).thenAnswer(i -> activityList.size());


        when(activityRepository.countAllWithinDistanceByAllActivityTypeIds(Mockito.anyDouble(), Mockito.anyDouble(),
                Mockito.anyDouble(), Mockito.anyList(), Mockito.anyInt())).thenAnswer(i -> {
            List<Long> activityTypeIds = i.getArgument(3);
            int numActivityTypes = i.getArgument(4);
            List<Activity> resultList = new ArrayList<>();
            int matchCount = 0;
            for (Activity activity : activityList) {
                for (ActivityType activityType : activity.getActivityTypes()) {
                    if (activityTypeIds.contains(activityType.getActivityTypeId())) {
                        matchCount++;
                    }
                }
                if (matchCount == numActivityTypes) {
                    resultList.add(activity);
                }
                matchCount = 0;
            }
            return resultList.size();
        });


        when(activityRepository.countAllWithinDistanceBySomeActivityTypeIds(Mockito.anyDouble(), Mockito.anyDouble(),
                Mockito.anyDouble(), Mockito.anyList())).thenAnswer(i -> {
            List<Long> activityTypeIds = i.getArgument(3);
            List<Activity> resultList = new ArrayList<>();
            for (Activity activity : activityList) {
                for (ActivityType activityType : activity.getActivityTypes()) {
                    if (activityTypeIds.contains(activityType.getActivityTypeId())) {
                        resultList.add(activity);
                        break;
                    }
                }
            }
            return resultList.size();
        });
    }

    //    ---- Tests ----
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getOrActivityByLocationSuccess() throws Exception {
        String method = "or";
        String activitiesString = "Kiting Hiking";
        Slice<Activity> resultSlice = locationSearchService.getActivitiesByLocation(STRING_COORDINATES_A, activitiesString,
                MEDIUM_DISTANCE, method, BLOCK_SIZE, PAGE_ONE, -1, 4);
        List<Activity> activities = resultSlice.getContent();
        assertTrue(activities.size() <= BLOCK_SIZE);
        assertEquals(3, activities.size()); // All 3 activities in activityList are expected to return
    }

    @Test
    public void getOrActivityByLocationWithFitnessLevelBetween2And3Success() throws Exception {
        String method = "or";
        String activitiesString = "Kiting Hiking";
        Slice<Activity> resultSlice = locationSearchService.getActivitiesByLocation(STRING_COORDINATES_A, activitiesString,
                MEDIUM_DISTANCE, method, BLOCK_SIZE, PAGE_ONE, 2, 3);
        List<Activity> activities = resultSlice.getContent();
        assertTrue(activities.size() <= BLOCK_SIZE);
        assertEquals(2, activities.size()); // 2 activities in activityList are expected to return
    }

    @Test
    public void getOrActivityByLocationNone() throws Exception {
        String method = "or";
        String activitiesString = "Writing";
        Slice<Activity> resultSlice = locationSearchService.getActivitiesByLocation(STRING_COORDINATES_A, activitiesString,
                MEDIUM_DISTANCE, method, BLOCK_SIZE, PAGE_ONE, -1, 4);
        List<Activity> activities = resultSlice.getContent();
        assertTrue(activities.size() <= BLOCK_SIZE);
        assertEquals(0, activities.size()); // No activities with writing
    }

    @Test
    public void getAndActivityByLocationSuccess() throws Exception {
        String method = "and";
        String activitiesString = "Biking Hiking";
        Slice<Activity> resultSlice = locationSearchService.getActivitiesByLocation(STRING_COORDINATES_A, activitiesString,
                MEDIUM_DISTANCE, method, BLOCK_SIZE, PAGE_ONE, -1, 4);
        List<Activity> activities = resultSlice.getContent();
        assertTrue(activities.size() <= BLOCK_SIZE);
        assertEquals(1, activities.size()); // Only activity with Biking and Hiking are expected to return
    }

    @Test
    public void getAndActivityByLocationBadCombo() throws Exception {
        String method = "and";
        String activitiesString = "Biking Hiking Kiting";
        Slice<Activity> resultSlice = locationSearchService.getActivitiesByLocation(STRING_COORDINATES_A, activitiesString,
                MEDIUM_DISTANCE, method, BLOCK_SIZE, PAGE_ONE, -1, 4);
        List<Activity> activities = resultSlice.getContent();
        assertTrue(activities.size() <= BLOCK_SIZE);
        assertEquals(0, activities.size()); // No activities with all 3 types
    }

    @Test
    public void getAndActivityByLocationNone() throws Exception {
        String method = "and";
        String activitiesString = "Writing";
        Slice<Activity> resultSlice = locationSearchService.getActivitiesByLocation(STRING_COORDINATES_A, activitiesString,
                MEDIUM_DISTANCE, method, BLOCK_SIZE, PAGE_ONE, -1, 4);
        List<Activity> activities = resultSlice.getContent();
        assertTrue(activities.size() <= BLOCK_SIZE);
        assertEquals(0, activities.size()); // No activity has type writing
    }

    @Test
    public void getActivityByLocationInvalidMethod() {
        String method = "flower";
        String activitiesString = "Biking";
        Exception exception = assertThrows(ResponseStatusException.class, () -> locationSearchService.getActivitiesByLocation
                (STRING_COORDINATES_A, activitiesString,MEDIUM_DISTANCE, method, BLOCK_SIZE, PAGE_ONE, -1, 4));
        assertEquals("400 BAD_REQUEST \"Method must be specified as either (AND, OR)\"", exception.getMessage());
    }

    @Test
    public void getActivityByLocationInvalidLat() {
        String method = "or";
        String activitiesString = "Biking";
        Exception exception = assertThrows(ResponseStatusException.class, () -> locationSearchService.getActivitiesByLocation
                (STRING_COORDINATES_BAD_LAT, activitiesString,MEDIUM_DISTANCE, method, BLOCK_SIZE, PAGE_ONE, -1, 4));
        assertEquals("400 BAD_REQUEST \"Latitude must exist (be between -90 and 90 degrees)\"", exception.getMessage());
    }

    @Test
    public void getActivityByLocationInvalidLng() {
        String method = "or";
        String activitiesString = "Biking";
        Exception exception = assertThrows(ResponseStatusException.class, () -> locationSearchService.getActivitiesByLocation
                (STRING_COORDINATES_BAD_LNG, activitiesString,MEDIUM_DISTANCE, method, BLOCK_SIZE, PAGE_ONE, -1, 4));
        assertEquals("400 BAD_REQUEST \"Longitude must exist (be between -180 and 180 degrees)\"", exception.getMessage());
    }

    @Test
    public void getActivityByLocationInvalidCoordString() {
        String method = "or";
        String activitiesString = "Biking";
        Exception exception = assertThrows(JsonProcessingException.class, () -> locationSearchService.getActivitiesByLocation
                (STRING_COORDINATES_INVALID, activitiesString,MEDIUM_DISTANCE, method, BLOCK_SIZE, PAGE_ONE, -1, 4));
    }

    @Test
    public void getActivityByLocationNegativeCutoff() {
        String method = "or";
        String activitiesString = "Biking";
        Exception exception = assertThrows(ResponseStatusException.class, () -> locationSearchService.getActivitiesByLocation
                (STRING_COORDINATES_A, activitiesString,NEGATIVE_DISTANCE, method, BLOCK_SIZE, PAGE_ONE, -1, 4));
        assertEquals("400 BAD_REQUEST \"Cutoff distance must be 0 or greater\"", exception.getMessage());
    }

    // --------------- Count tests ---------------
    @Test
    public void getOrNumberOfActivitiesByLocation() throws Exception {
        String method = "or";
        String activitiesString = "Kiting Hiking";
        int numberOfActivities = locationSearchService.getRowsForActivityByLocation(STRING_COORDINATES_A, activitiesString,
                MEDIUM_DISTANCE, method);
        assertEquals(3, numberOfActivities);
    }

    @Test
    public void getAndNumberOfActivitiesByLocation() throws Exception {
        String method = "and";
        String activitiesString = "Kiting Hiking";
        int numberOfActivities = locationSearchService.getRowsForActivityByLocation(STRING_COORDINATES_A, activitiesString,
                MEDIUM_DISTANCE, method);
        assertEquals(1, numberOfActivities);
    }

    @Test
    public void getNumberOfActivitiesByLocation() throws Exception {
        String method = "Doesn't matter";
        String activitiesString = ""; // Leave empty
        int numberOfActivities = locationSearchService.getRowsForActivityByLocation(STRING_COORDINATES_A, activitiesString,
                MEDIUM_DISTANCE, method);
        assertEquals(3, numberOfActivities);
    }
}
