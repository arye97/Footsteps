package com.springvuegradle.seng302team600.controller;


import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.payload.OutcomeResponse;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class OutcomeController {

    private final UserAuthenticationService userAuthenticationService;
    private final OutcomeRepository outcomeRepository;
    private final ActivityRepository activityRepository;

    public OutcomeController(UserAuthenticationService userAuthenticationService, OutcomeRepository outcomeRepository, ActivityRepository activityRepository) {
        this.userAuthenticationService = userAuthenticationService;
        this.outcomeRepository = outcomeRepository;
        this.activityRepository = activityRepository;
    }

    @PostMapping("/activities/outcomes")
    public void createNewActivityOutcomes(@Validated @RequestBody Outcome outcome, HttpServletRequest request,
                                          HttpServletResponse response) {
        //ToDo: Implement this method, and add a DocString please
        // Must set the activityId before saving!!!
    }


    /**
     * Return a list of all OutcomeResponse objects that are associated with an activityId
     * @param activityId id of the activity you want Outcomes from
     * @return list of OutcomeResponse objects
     */
    @GetMapping("/activities/{activityId}/outcomes")
    public List<OutcomeResponse> getActivityOutcomes(@PathVariable Long activityId) {  // ToDo can we remove request
        //ToDo: add a DocString please
        List<Outcome> outcomes = outcomeRepository.findByActivityId(activityId);

        // Return an empty list of OutcomeResponses if null
        // Should it throw an error?  If there are no outcomes it seems that it would return an empty List, not null
        if (outcomes == null) {
            return new ArrayList<>();
        }

        // Convert Outcomes to OutcomeResponse
        List<OutcomeResponse> outcomeResponses = new ArrayList<>(outcomes.size());
        for (Outcome outcome: outcomes) {
            outcomeResponses.add(new OutcomeResponse(outcome));
        }

        return outcomeResponses;
    }

}
