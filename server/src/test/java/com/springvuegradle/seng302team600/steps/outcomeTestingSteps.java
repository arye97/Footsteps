package com.springvuegradle.seng302team600.steps;

import com.springvuegradle.seng302team600.controller.JsonConverter;
import com.springvuegradle.seng302team600.controller.OutcomeController;
import com.springvuegradle.seng302team600.cucumberSpringBase;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.repository.ResultRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import com.springvuegradle.seng302team600.model.Outcome;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OutcomeController.class)

public class outcomeTestingSteps extends cucumberSpringBase {

    @Mock
    private UserAuthenticationService userAuthenticationService;
    @Mock
    private OutcomeRepository outcomeRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private ResultRepository resultRepository;
    @Autowired
    private MockMvc mvc;

    private Outcome dummyOutcome;
    private Activity dummyActivity;
    private final String validToken = "valid";
    private static final Long CREATOR_ID = 1L;
    private static final Long ACTIVITY_ID = 2L;
    private static final Long OUTCOME_ID = 3L;
    private Long nextOutcomeId = 1L;


    private final String EditedOutcomeJson = JsonConverter.toJson(true,
            "title", "EditedTitle",
            "activity_id", ACTIVITY_ID,
            "unit_name", "EditedUnitName",
            "unit_type", "TEXT"
    );

    private List<Outcome> outcomeTable;
    private List<Activity> activityTable;

    @Before
    public void setupMocking() {
        //Manually instantiate mvc - needed to fix NullPointerException error
        MockitoAnnotations.initMocks(this);
        OutcomeController outcomeController = new OutcomeController(userAuthenticationService, outcomeRepository, activityRepository, resultRepository);
        mvc = MockMvcBuilders.standaloneSetup(outcomeController).build();

        // Mocking OutcomeRepository
        when(outcomeRepository.findByActivityId(Mockito.any())).thenAnswer(i -> {
            Long activityId = i.getArgument(0);
            List<Outcome> resultList = new ArrayList<>();
            for (Outcome outcome : outcomeTable) {
                if (activityId.equals(outcome.getActivityId())) {
                    resultList.add(outcome);
                }
            }
            return resultList;
        });
        when(outcomeRepository.findByOutcomeId(Mockito.anyLong())).thenAnswer(i -> {
            Long outcomeId = i.getArgument(0);
            for (Outcome outcome : outcomeTable) {
                if (outcome.getOutcomeId().equals(outcomeId)) {
                    return outcome;
                }
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find outcome.");
        });
        when(outcomeRepository.save(Mockito.any())).thenAnswer(i -> {
            Outcome outcome = i.getArgument(0);
            if (outcome.getOutcomeId() == null) {
                ReflectionTestUtils.setField(outcome, "outcomeId", nextOutcomeId);
                nextOutcomeId++;
            } else {
                int index = 0;
                Outcome oldOutcome;
                for (int j = 0; j < outcomeTable.size(); j++) {
                    oldOutcome = outcomeTable.get(j);
                    if (oldOutcome.getOutcomeId().equals(outcome.getOutcomeId())) {
                        outcomeTable.remove(index);
                    }
                    index++;
                }
            }
            outcomeTable.add(outcome);
            return outcome;
        });

        //Mocking ActivityRepository
        when(activityRepository.findByActivityId(Mockito.anyLong())).thenAnswer(i -> {
            Long activityId = i.getArgument(0);
            for (Activity activity : activityTable) {
                if (activity.getActivityId().equals(activityId)) {
                    return activity;
                }
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find activity.");
        });

    }


    @And("I am the creator of a basic activity")
    public void i_am_the_creator_of_an_activity() {
        dummyActivity = new Activity();
        dummyActivity.setCreatorUserId(CREATOR_ID);
        ReflectionTestUtils.setField(dummyActivity, "activityId", ACTIVITY_ID);
        activityTable = new ArrayList<>();
        activityTable.add(dummyActivity);
    }

    @Given("My activity has an outcome with the title {string} and unitName {string}")
    public void i_am_the_creator_of_an_outcome_with_ID_and_title_and_unitName(String title, String unitName) {
        dummyOutcome = new Outcome();
        dummyOutcome.setTitle(title);
        dummyOutcome.setUnitName(unitName);
        ReflectionTestUtils.setField(dummyOutcome, "activityId", ACTIVITY_ID);
        ReflectionTestUtils.setField(dummyOutcome, "outcomeId", OUTCOME_ID);
        outcomeTable = new ArrayList<>();
        outcomeTable.add(dummyOutcome);
    }

    @When("I try to edit my outcome")
    public void i_try_to_edit_my_outcome() throws Exception {
        //Put request to the endpoint
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put("/activities/{outcomeId}/outcomes", 3L)
                .header("Token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(EditedOutcomeJson);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
    }


    @Then("My outcome now has the title {string} and unitName {string}")
    public void myOutcomeNowHasTheTitleAndUnitName(String editedTitle, String editedUnitName) {
        assertEquals("EditedTitle", outcomeTable.get(0).getTitle());
        assertEquals("EditedUnitName", outcomeTable.get(0).getUnitName());
    }



}


