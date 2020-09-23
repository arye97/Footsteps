package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.model.Location;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.function.Function;

import static org.mockito.Mockito.when;

@WebMvcTest(LocationSearchService.class)
public class LocationSearchServiceTest {

        @MockBean
        private ActivityRepository activityRepository;

        @MockBean
        private ActivityTypeRepository activityTypeRepository;

        @Autowired
        private ActivityPinService LocationSearchService;

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

    /**
     * Mock Activity repository actions
     */
    @BeforeEach
    void setupActivityPinRepository() {

        Activity dummyActivity1 = new Activity();
        ReflectionTestUtils.setField(dummyActivity1, "activityId", DUMMY_ACTIVITY_ID_1);
        dummyActivity1.setLocation(new Location(BERLIN_LON, BERLIN_LAT, "Berlin"));
        Activity dummyActivity2 = new Activity();
        ReflectionTestUtils.setField(dummyActivity2, "activityId", DUMMY_ACTIVITY_ID_2);
        dummyActivity2.setLocation(new Location(WARSAW_LON, WARSAW_LAT, "Warsaw"));
        Activity dummyActivity3 = new Activity();
        ReflectionTestUtils.setField(dummyActivity3, "activityId", DUMMY_ACTIVITY_ID_3);
        dummyActivity3.setLocation(new Location(MOSCOW_LON, MOSCOW_LAT, "Moscow"));

        activityList.add(dummyActivity1);
        activityList.add(dummyActivity2);
        activityList.add(dummyActivity3);

        ActivityType dummyActivityType1 = new ActivityType("Hiking");
        ReflectionTestUtils.setField(dummyActivityType1, "activityTypeId", DUMMY_TYPE_ID_1);
        ActivityType dummyActivityType2 = new ActivityType("Biking");
        ReflectionTestUtils.setField(dummyActivityType2, "activityTypeId", DUMMY_TYPE_ID_2);
        ActivityType dummyActivityType3 = new ActivityType("Kiting");
        ReflectionTestUtils.setField(dummyActivityType3, "activityTypeId", DUMMY_TYPE_ID_3);

        activityTypeSet.add(dummyActivityType1);
        dummyActivity1.setActivityTypes(activityTypeSet);

        activityTypeSet.add(dummyActivityType2);
        dummyActivity2.setActivityTypes(activityTypeSet);

        activityTypeSet.add(dummyActivityType3);
        dummyActivity3.setActivityTypes(activityTypeSet);


        when(activityTypeRepository.findActivityTypeIdsByNames(Mockito.anyList())).thenAnswer(i -> {
            List<String> activityTypeStrings = i.getArgument(0);
            List<Long> resultList = new ArrayList<>();
            List<ActivityType> allActivityTypes = new ArrayList<>();
            allActivityTypes.addAll(activityTypeSet);
            for (ActivityType activityType : allActivityTypes) {
                if (activityTypeStrings.contains(activityType.getName())) {
                    resultList.add(activityType.getActivityTypeId());
                }
            }
            return resultList;
        });

        //todo: Mock:
        // activityRepository.findAllWithinDistanceByAllActivityTypeIds(
        //                        coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance,
        //                        activityTypeIds, numActivityTypes, activitiesBlock)
        when(activityRepository.findAllWithinDistanceByAllActivityTypeIds(Mockito.anyDouble(), Mockito.anyDouble(),
                Mockito.anyDouble(), Mockito.anyList(), Mockito.anyInt(), Mockito.any(Pageable.class))).thenAnswer(i -> {
            List<Long> activityTypeIds = i.getArgument(3);
            int numActivityTypes = i.getArgument(4);

            Pageable pageable = i.getArgument(5);
            List<Activity> resultList = new ArrayList<>();
            Set<ActivityType> activitiesTypes= new HashSet<>();
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

            int leftIndex = PAGE_ONE * BLOCK_SIZE;
            int rightIndex = PAGE_TWO * BLOCK_SIZE + BLOCK_SIZE;
            return new SliceImpl<>(resultList.subList(leftIndex, rightIndex), pageable, false);
        });

        //todo: Mock:
        // activityRepository.findAllWithinDistanceBySomeActivityTypeIds(
        //                        coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance,
        //                        activityTypeIds, activitiesBlock);
        when(activityRepository.findAllWithinDistanceBySomeActivityTypeIds(Mockito.anyDouble(), Mockito.anyDouble(),
                Mockito.anyDouble(), Mockito.anyList(), Mockito.any(Pageable.class))).thenAnswer(i -> {
            List<Long> activityTypeIds = i.getArgument(3);
            Pageable pageable = i.getArgument(4);
            List<Activity> resultList = new ArrayList<>();

            for (Activity activity : activityList) {
                for (ActivityType activityType : activity.getActivityTypes()) {
                    if (activityTypeIds.contains(activityType.getActivityTypeId())) {
                        resultList.add(activity);
                        break;
                    }
                }
            }

            int leftIndex = PAGE_ONE * BLOCK_SIZE;
            int rightIndex = PAGE_TWO * BLOCK_SIZE + BLOCK_SIZE;
            return new SliceImpl<>(resultList.subList(leftIndex, rightIndex), pageable, false);
        });

        //todo: Mock:
        // activityRepository.findAllWithinDistance(
        //                    coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance, activitiesBlock);
        when(activityRepository.findAllWithinDistance(Mockito.anyDouble(), Mockito.anyDouble(),
                Mockito.anyDouble(), Mockito.any(Pageable.class))).thenAnswer(i -> {
            Pageable pageable = i.getArgument(3);
            List<Activity> resultList = new ArrayList<>();

            resultList.addAll(activityList);

            int leftIndex = PAGE_ONE * BLOCK_SIZE;
            int rightIndex = PAGE_TWO * BLOCK_SIZE + BLOCK_SIZE;
            return new SliceImpl<>(resultList.subList(leftIndex, rightIndex), pageable, false);
        });
    }

//    ---- Tests ----
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


}
