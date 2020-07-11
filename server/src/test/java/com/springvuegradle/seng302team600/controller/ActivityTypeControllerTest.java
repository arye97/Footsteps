package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityTypeController.class)
class ActivityTypeControllerTest {

    @MockBean
    private ActivityTypeRepository activityTypeRepository;
    @Autowired
    private MockMvc mvc;


    private ObjectMapper objectMapper = new ObjectMapper();
    private final String validToken = "valid";
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
//            ReflectionTestUtils.setField(newActivity, "activityId", DEFAULT_ACTIVITY_TYPE_ID + activityTypeCount++);
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
    }


    // ----------- Tests -----------

    /**
     * Checks the ActivityTypes from GET "/activity-types" endpoint are correct.
     */
    @Test
    void retrieveActivityTypesThroughEndpoint() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/activity-types")
                .header("Token", validToken);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        // Deserialize json response into set of ActivityTypes
        Set<ActivityType> receivedActivityTypeList = new HashSet<>();
        String jsonResponseStr = result.getResponse().getContentAsString();
        for (JsonNode activityTypeNode: objectMapper.readTree(jsonResponseStr)) {
            receivedActivityTypeList.add( objectMapper.treeToValue(activityTypeNode, ActivityType.class) );
        }

        assertEquals(new HashSet<>(activityTypeRepository.findAll()), receivedActivityTypeList);
    }
}