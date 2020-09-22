package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.Location;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(LocationSearchService.class)
public class LocationSearchServiceTest {

        @MockBean
        private ActivityRepository activityRepository;

        @Autowired
        private ActivityPinService LocationSearchService;

    private List<Activity> activityList = new ArrayList<>();

    private final int PAGE_ONE = 0;
    private final int PAGE_TWO = 1;

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

        //todo: Mock:
        // activityTypeRepository.findActivityTypeIdsByNames(types)

        //todo: Mock:
        // activityRepository.findAllWithinDistanceByAllActivityTypeIds(
        //                        coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance,
        //                        activityTypeIds, numActivityTypes, activitiesBlock)

        //todo: Mock:
        // activityRepository.findAllWithinDistanceBySomeActivityTypeIds(
        //                        coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance,
        //                        activityTypeIds, activitiesBlock);

        //todo: Mock:
        // activityRepository.findAllWithinDistance(
        //                    coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance, activitiesBlock);
    }
}
