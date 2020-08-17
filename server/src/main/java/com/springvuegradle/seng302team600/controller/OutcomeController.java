package com.springvuegradle.seng302team600.controller;


import com.springvuegradle.seng302team600.Utilities.OutcomeValidator;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.OutcomeRequest;
import com.springvuegradle.seng302team600.payload.OutcomeResponse;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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


    /**
     * Create an outcome for an activity.
     * This method checks that the user from token and the creator of the activity are the same
     * else it will throw a 403 forbidden exception
     *
     * @param outcomeRequest the outcomeResponse payload to be converted to an Outcome object
     *                       to be saved to the activity (in the outcomeRepository)
     * @param request        the request packet, where we will check the user Token
     */
    @PostMapping("/activities/outcomes")
    public void createNewActivityOutcomes(@Validated @RequestBody OutcomeRequest outcomeRequest, HttpServletResponse response, HttpServletRequest request) {
        Outcome outcome = new Outcome(outcomeRequest);
        OutcomeValidator.validate(outcome);
        String token = request.getHeader("Token");
        User user = userAuthenticationService.findByToken(token);
        Activity activity = activityRepository.findByActivityId(outcome.getActivityId());
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity included in outcome not found");
        }
        if (!activity.getCreatorUserId().equals(user.getUserId()) && !userAuthenticationService.hasAdminPrivileges(user)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "User is forbidden from creating outcomes for this activity");
        }
        outcomeRepository.save(outcome);
        response.setStatus(HttpServletResponse.SC_CREATED); //201
    }


    /**
     * Return a list of all OutcomeResponse objects that are associated with an activityId
     *
     * @param activityId id of the activity you want Outcomes from
     * @return list of OutcomeResponse objects
     */
    @GetMapping("/activities/{activityId}/outcomes")
    public List<OutcomeResponse> getActivityOutcomes(@PathVariable Long activityId) {

        List<Outcome> outcomes = outcomeRepository.findByActivityId(activityId);

        // Return an empty list of OutcomeResponses if null
        if (outcomes == null) {
            return new ArrayList<>();
        }

        // Convert Outcomes to OutcomeResponse
        List<OutcomeResponse> outcomeResponses = new ArrayList<>(outcomes.size());
        for (Outcome outcome : outcomes) {
            outcomeResponses.add(new OutcomeResponse(outcome));
        }

        return outcomeResponses;
    }


    @PutMapping("/activities/outcomes")
    public void updateActivityOutcomes(@Validated @RequestBody Outcome outcome, HttpServletRequest request) {
        //ToDo: Implement this method, and add a DocString please
    }


    @DeleteMapping("/activities/{outcomeId}/outcomes")
    public void deleteActivityOutcomes(@PathVariable Long outcomeId, HttpServletRequest request) {
        //ToDo: Implement this method, and add a DocString please
    }

}
