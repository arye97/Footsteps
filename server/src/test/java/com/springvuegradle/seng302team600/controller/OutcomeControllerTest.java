package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.seng302team600.enumeration.UnitType;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.Unit;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.repository.ResultRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OutcomeController.class)
public class OutcomeControllerTest {

    @MockBean
    private UserAuthenticationService userAuthenticationService;
    @MockBean
    private OutcomeRepository outcomeRepository;
    @MockBean
    private ActivityRepository activityRepository;
    @MockBean
    private ResultRepository resultRepository;
    @Autowired
    private MockMvc mvc;

    private static final Long USER_ID_1 = 1L;
    private static final Long USER_ID_2 = 2L;
    private User dummyUser1;
    private User dummyUser2;
    private final String validToken = "valid";
    private final String validToken2 = "valid2";
    private static final Long ACTIVITY_ID_1 = 1L;
    private Activity dummyActivity;
    private Outcome dummyOutcome;
    private List<Outcome> outcomeTable;
    private Long nextOutcomeId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        dummyUser1 = new User();
        dummyUser1.setFirstName("John");
        dummyUser1.setToken(validToken);
        ReflectionTestUtils.setField(dummyUser1, "userId", USER_ID_1);
        dummyUser2 = new User();
        dummyUser2.setFirstName("Douglas");
        dummyUser2.setToken(validToken2);
        ReflectionTestUtils.setField(dummyUser2, "userId", USER_ID_2);

        dummyActivity = new Activity();
        dummyActivity.setCreatorUserId(USER_ID_1);
        dummyActivity.setParticipants(new HashSet<>());
        ReflectionTestUtils.setField(dummyActivity, "activityId", ACTIVITY_ID_1);

        outcomeTable = new ArrayList<>();
        nextOutcomeId = 1L;

        dummyOutcome = new Outcome();
        ReflectionTestUtils.setField(dummyOutcome, "outcomeId", 1L);
        dummyOutcome.setActivityId(ACTIVITY_ID_1);
        dummyOutcome.setDescription("These are my outcome results");
        dummyOutcome.setTitle("This is my outcome");
        Set<Unit> units = new HashSet<>();
        Unit unit = new Unit();
        unit.setName("Unit name");
        unit.setUnitType(UnitType.NUMBER);
        unit.setMeasurementUnit("km");
        units.add(unit);
        dummyOutcome.setUnits(units);

        // Mocking UserAuthenticationService
        when(userAuthenticationService.findByUserId(Mockito.any(), Mockito.any(Long.class))).thenAnswer(i -> {
            Long id = i.getArgument(1);
            if (id.equals(USER_ID_1)) {
                return dummyUser1;
            } else if (id.equals(USER_ID_2)) {
                return dummyUser2;
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        });
        when(userAuthenticationService.findByToken(Mockito.any())).thenAnswer(i -> {
            String token = i.getArgument(0);
            if (token.equals(validToken)) {
                return dummyUser1;
            } else if (token.equals(validToken2)) {
                return dummyUser2;
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
        });

        // Mocking OutcomeRepository
        when(outcomeRepository.findByActivityId(Mockito.any())).thenAnswer(i -> {
            Long activityId = i.getArgument(0);
            List<Outcome> result = new ArrayList<>();
            for (Outcome outcome : outcomeTable) {
                if (activityId.equals(outcome.getActivityId())) {
                    result.add(outcome);
                }
            }
            return result;
        });
        when(outcomeRepository.save(Mockito.any())).thenAnswer(i -> {
            Outcome outcome = i.getArgument(0);
            if (outcome.getOutcomeId() == null) {
                ReflectionTestUtils.setField(outcome, "outcomeId", nextOutcomeId);
                nextOutcomeId++;
            } else {
                int index = 0;
                for (Outcome oldOutcome : outcomeTable) {
                    if (oldOutcome.getOutcomeId().equals(outcome.getOutcomeId())) {
                        outcomeTable.remove(index);
                    }
                    index++;
                }
            }
            outcomeTable.add(outcome);
            return outcome;
        });

        // Mocking ActivityRepository
        when(activityRepository.findByActivityId(Mockito.any())).thenAnswer(i -> {
            Long activityId = i.getArgument(0);
            if (activityId.equals(ACTIVITY_ID_1)) {
                return dummyActivity;
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        });
    }

    private final String outcomeJson = JsonConverter.toJson(true,
            "title", "This is my outcome",
            "description", "This is my outcome, it does outcome things.",
            "activity_id", ACTIVITY_ID_1
            );
    @Test
    public void saveOutcome() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        String outcomeJson = objectMapper.writeValueAsString(dummyOutcome);
        MockHttpServletRequestBuilder createOutcome = MockMvcRequestBuilders
                .post("/activities/outcomes")
                .header("Token", validToken)
                .content(outcomeJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(createOutcome)
                .andExpect(status().isOk())
                .andReturn();

        //Check the title of the saved outcome is the same as the original one
        assertTrue(outcomeTable.get(0).getTitle().equals(dummyOutcome.getTitle()));

        //make sure the response is null as a post request is <void>
        assertNull(result.getResponse().getContentType());
        
    }
}
