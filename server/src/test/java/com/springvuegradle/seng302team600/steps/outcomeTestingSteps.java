package com.springvuegradle.seng302team600.steps;

import com.springvuegradle.seng302team600.controller.JsonConverter;
import com.springvuegradle.seng302team600.controller.OutcomeController;
import com.springvuegradle.seng302team600.cucumberSpringBase;
import com.springvuegradle.seng302team600.enumeration.UnitType;
//import org.junit.jupiter.api.BeforeAll;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.repository.ResultRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import io.cucumber.java.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import com.springvuegradle.seng302team600.model.Outcome;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
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

    private Outcome dummyOutcome;
    private final String validToken = "valid";
    private static final Long ACTIVITY_ID_1 = 1L;
    private static final Long OUTCOME_ID_1 = 3L;
    private Long nextOutcomeId = 1L;


    private final String successfulEditOutcomeJson = JsonConverter.toJson(true,
            "title", "EditedTitle",
            "activity_id", ACTIVITY_ID_1,
            "unit_name", "EditedUnitName",
            "unit_type", "TEXT"
    );



    private List<Outcome> outcomeTable;

    @Before
    private void setupMocking() {
        MockitoAnnotations.initMocks(this);
        when(outcomeRepository.findByOutcomeId(Mockito.anyLong())).thenAnswer(i -> {
            Long outcomeId = i.getArgument(0);
            for (Outcome outcome : outcomeTable) {
                if (outcome.getOutcomeId().equals(outcomeId)) {
                    return outcome;
                }
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find outcome.");
        });

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

    }




    @Given("I am the creator of an outcome with the title {string} and unitName {string}")
    public void i_am_the_creator_of_an_outcome_with_ID_and_title_and_unitName(String title, String unitName) {
        dummyOutcome = new Outcome();
        dummyOutcome.setTitle(title);
        dummyOutcome.setUnitName(unitName);
        dummyOutcome.setUnitType(UnitType.TEXT);
        ReflectionTestUtils.setField(dummyOutcome, "activityId", ACTIVITY_ID_1);
        ReflectionTestUtils.setField(dummyOutcome, "outcomeId", OUTCOME_ID_1);

        outcomeTable = new ArrayList<>();
        outcomeTable.add(dummyOutcome);
    }

    @When("I try to edit my outcome")
    public void i_try_to_edit_my_outcome() throws Exception {
        //put request to the endpoint
        System.out.println("outcomeTable is: " + outcomeTable.get(0).getTitle());
        System.out.println("Dummy outcome is: "+ dummyOutcome);


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put("/activities/{outcomeId}/outcomes", 3L)
                .header("Token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(successfulEditOutcomeJson);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
    }


    @Then("My outcome now has the title {string} and unitName {string}")
    public void myOutcomeNowHasTheTitleAndUnitName(String editedTitle, String editedUnitName) {
        assertEquals(editedTitle, outcomeTable.get(0).getTitle());
        assertEquals(editedUnitName, outcomeTable.get(0).getUnitName());
    }


}


