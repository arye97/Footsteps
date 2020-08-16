package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.enumeration.UnitType;
import com.springvuegradle.seng302team600.model.*;
import com.springvuegradle.seng302team600.repository.ActivityParticipantRepository;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.repository.ResultRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResultController.class)
public class ResultControllerTest {

    @MockBean
    private UserAuthenticationService userAuthenticationService;
    @MockBean
    private OutcomeRepository outcomeRepository;
    @MockBean
    private ResultRepository resultRepository;
    @MockBean
    private ActivityParticipantRepository participantRepository;
    @MockBean
    private ActivityRepository activityRepository;
    @Autowired
    private MockMvc mvc;

    private static final Long USER_ID_1 = 1L;
    private static final Long USER_ID_2 = 2L;
    private static final Long USER_ID_3 = 3L;
    private User dummyUser1;
    private User dummyUser2;
    private final String validToken = "valid";
    private final String validToken2 = "valid2";
    private static final Long ACTIVITY_ID_1 = 1L;
    private Activity dummyActivity;
    private static final Long OUTCOME_ID_1 = 1L;
    private Outcome dummyOutcome;
    private static final Long UNIT_ID_1 = 1L;
    private List<Result> resultTable;
    private Long nextResultId;

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
        dummyActivity.setParticipants(new HashSet<>());
        dummyActivity.setCreatorUserId(USER_ID_3);
        ReflectionTestUtils.setField(dummyActivity, "activityId", ACTIVITY_ID_1);

        dummyOutcome = new Outcome();
        ReflectionTestUtils.setField(dummyOutcome, "outcomeId", OUTCOME_ID_1);
        dummyOutcome.setActivityId(ACTIVITY_ID_1);
        dummyOutcome.setResults(new HashSet<>());
        dummyOutcome.setUnitType(UnitType.TEXT);

        resultTable = new ArrayList<>();
        nextResultId = 1L;

        //Mocking ActivityRepository
        when(activityRepository.findByActivityId(Mockito.any())).thenAnswer(i -> dummyActivity);

        //Mocking OutcomeRepository
        when(outcomeRepository.findByOutcomeId(Mockito.anyLong())).thenAnswer(i -> {
            Long outcomeId = i.getArgument(0);
            if (outcomeId.equals(OUTCOME_ID_1)) {
                return dummyOutcome;
            } else {
                return null;
            }
        });

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
        when(userAuthenticationService.hasAdminPrivileges(Mockito.any())).thenAnswer(i ->
                ((User) i.getArgument(0)).getRole() >= 10);

        // Mocking OutcomeRepository
        when(outcomeRepository.save(Mockito.any())).thenAnswer(i -> {
            Outcome outcome = i.getArgument(0);
            for (Result result : outcome.getResults()) {
                if (result.getResultId() == null) {
                    ReflectionTestUtils.setField(result, "resultId", nextResultId);
                    nextResultId++;
                } else {
                    int index = 0;
                    for (Result oldResult : resultTable) {
                        if (oldResult.getResultId().equals(result.getResultId())) {
                            resultTable.remove(index);
                        }
                        index++;
                    }
                }
                result.setOutcome(outcome);
                resultTable.add(result);
            }
            return outcome;
        });

        // Mocking ActivityParticipantRepository
        when(participantRepository.existsByActivityIdAndUserId(Mockito.anyLong(),Mockito.anyLong())).thenAnswer(i -> {
            Long activityId = i.getArgument(0);
            Long userId = i.getArgument(1);
            if (activityId.equals(ACTIVITY_ID_1) && userId.equals(USER_ID_1)) {
                return 1L;
            } else {
                return 0L;
            }
        });

        // Mocking ResultRepository
        when(resultRepository.existsByOutcomeAndUserId(Mockito.any(), Mockito.any())).thenAnswer(i -> {
            Outcome outcome = i.getArgument(0);
            Long userId = i.getArgument(1);
            for (Result result : resultTable) {
                if (result.getUserId().equals(userId)
                        && result.getOutcome().getOutcomeId().equals(outcome.getOutcomeId())) {
                    return true;
                }
            }
            return false;
        });
    }

    private final String newResultJson = JsonConverter.toJson(true,
            "user_id", USER_ID_1,
            "value", "someValue",
            "did_not_finish", "false");

    @Test
    public void resultCreationSuccess() throws Exception {
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/outcomes/{outcomeId}/results", 1L)
                .content(newResultJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);

        mvc.perform(httpReq)
                .andExpect(status().isCreated());
        assertNotNull(resultTable.get(0));
    }

    private final String resultAlreadySubmittedJson = JsonConverter.toJson(true,
            "comment", "Some type of comment",
            "values", new Object[]{
                    JsonConverter.toMap(
                            "unit_id", UNIT_ID_1,
                            "value", "someValue",
                            "did_not_finish", "false")});

    @Test
    public void resultAlreadySubmittedBadRequest() throws Exception {
        assertEquals(0, resultTable.size());
        Result result = new Result();
        result.setUserId(USER_ID_1);
        dummyOutcome.addResult(result);
        outcomeRepository.save(dummyOutcome);

        assertEquals(1, resultTable.size());
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/outcomes/{outcomeId}/results", 1L)
                .content(resultAlreadySubmittedJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);

        mvc.perform(httpReq)
                .andExpect(status().isBadRequest());
        assertEquals(1, resultTable.size());
    }

    private final String resultNotParticipantJson = JsonConverter.toJson(true,
            "user_id", USER_ID_2,
            "value", "someValue",
            "did_not_finish", "false");

    @Test
    public void newResultByNotParticipantForbidden() throws Exception {
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/outcomes/{outcomeId}/results", 1L)
                .content(resultNotParticipantJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken2);

        mvc.perform(httpReq)
                .andExpect(status().isForbidden());
    }

    private final String resultForbiddenJson = JsonConverter.toJson(true,
            "user_id", USER_ID_1,
            "value", "someValue",
            "did_not_finish", "false");

    @Test
    public void newResultByNotAdminForbidden() throws Exception {
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/outcomes/{outcomeId}/results", 1L)
                .content(resultForbiddenJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken2);

        mvc.perform(httpReq)
                .andExpect(status().isForbidden());
    }
}
