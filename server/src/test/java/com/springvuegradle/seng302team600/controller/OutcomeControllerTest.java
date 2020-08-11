package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(OutcomeController.class)
public class OutcomeControllerTest {

    @MockBean
    private UserAuthenticationService userAuthenticationService;
    @MockBean
    private OutcomeRepository outcomeRepository;
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
        dummyActivity.setParticipants(new HashSet<>());
        ReflectionTestUtils.setField(dummyActivity, "activityId", ACTIVITY_ID_1);

        outcomeTable = new ArrayList<>();
        nextOutcomeId = 1L;


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
    }

}
