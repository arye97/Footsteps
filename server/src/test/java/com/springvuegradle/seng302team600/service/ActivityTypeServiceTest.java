package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest(ActivityTypeService.class)
class ActivityTypeServiceTest {

    @MockBean
    private ActivityTypeRepository activityTypeRepository;

    private ActivityTypeService activityTypeService;

    private List<ActivityType> activityTypeMockTable = new ArrayList<>();
    private static final Long DEFAULT_ACTIVITY_TYPE_ID = 1L;
    private static Long activityTypeCount = 0L;

    /**
     * Mock ActivityType repository actions
     */
    @BeforeEach
    void setupActivityTypeRepository() {
        activityTypeCount = 0L;
        // Save
        when(activityTypeRepository.save(Mockito.any(ActivityType.class))).thenAnswer(i -> {
            ActivityType newActivityType = i.getArgument(0);
            if (!activityTypeMockTable.contains(newActivityType)) {
                ReflectionTestUtils.setField(newActivityType, "activityTypeId", DEFAULT_ACTIVITY_TYPE_ID + activityTypeCount++);
                activityTypeMockTable.add(newActivityType);
                return activityTypeMockTable.get(activityTypeMockTable.size() - 1);
            }
            return null;
        });
        // SaveAll
        when(activityTypeRepository.saveAll(Mockito.any(List.class))).thenAnswer(i -> {
            for (ActivityType activityType: (List<ActivityType>)i.getArgument(0)) {
                activityTypeRepository.save(activityType);
            }
            return activityTypeMockTable;
        });
        // FindAll
        when(activityTypeRepository.findAll()).thenAnswer(i -> {
            return activityTypeMockTable;
        });
        // FindActivityTypeByName
        when(activityTypeRepository.findActivityTypeByName(Mockito.any(String.class))).thenAnswer(i -> {
            ActivityType foundActtivityType = null;
            for (ActivityType activityType: activityTypeMockTable) {
                if (activityType.getName().equals(i.getArgument(0))) {
                    foundActtivityType = activityType;
                    break;
                }
            }
            return foundActtivityType;
        });
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void mockSetupFromYmlConfig() throws Exception {
        List<ActivityType> activityTypeTestList = new ArrayList<>();
        activityTypeTestList.add(new ActivityType("Skiing"));
        activityTypeTestList.add(new ActivityType("Trail Running"));
        activityTypeTestList.add(new ActivityType("Biking"));

        activityTypeRepository.saveAll(activityTypeTestList);
        activityTypeService = new ActivityTypeService(activityTypeRepository);
    }


    // ----------- Tests -----------

    @Test
    void replacesEntitiesFromRepository() {
        Set<ActivityType> detachedActivityTypes = new HashSet<>();
        detachedActivityTypes.add(new ActivityType("Skiing"));
        detachedActivityTypes.add(new ActivityType("Trail Running"));

        Set<ActivityType> attachedActivityTypes = activityTypeService.getMatchingEntitiesFromRepository(
                detachedActivityTypes);

        // Get from Set
        ActivityType skiing = null, trailRunning = null;
        for (ActivityType activityType: attachedActivityTypes) {
            if (activityType.getName().equals("Skiing")) {
                skiing = activityType;
            }
            if (activityType.getName().equals("Trail Running")) {
                trailRunning = activityType;
            }
        }
        assertNotNull(skiing);
        assertNotNull(trailRunning);
        assertEquals(1L, skiing.getActivityTypeId());
        assertEquals(2L, trailRunning.getActivityTypeId());
    }


}