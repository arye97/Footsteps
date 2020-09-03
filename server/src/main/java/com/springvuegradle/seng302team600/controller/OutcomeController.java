package com.springvuegradle.seng302team600.controller;


import com.springvuegradle.seng302team600.validator.OutcomeValidator;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.OutcomeRequest;
import com.springvuegradle.seng302team600.payload.OutcomeResponse;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.repository.ResultRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
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
    private final ResultRepository resultRepository;

    public OutcomeController(UserAuthenticationService userAuthenticationService, OutcomeRepository outcomeRepository,
                             ActivityRepository activityRepository, ResultRepository resultRepository) {
        this.userAuthenticationService = userAuthenticationService;
        this.outcomeRepository = outcomeRepository;
        this.activityRepository = activityRepository;
        this.resultRepository = resultRepository;
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

    /**
     * Put request for editing an outcome created by a user
     * Checks all possible inputs to see if it is there and needs updating
     * Will throw an error if the outcome you're trying to edit has results already
     * @param outcome the Outcome object we are updating
     */
    @PutMapping("/activities/outcomes")
    public void updateActivityOutcomes(@Validated @RequestBody Outcome outcome,
                                       HttpServletRequest request, HttpServletResponse response) {

        String token = request.getHeader("Token");
        Long outcomeId = outcome.getOutcomeId();

        //Get old outcome and check that it's not null
        Outcome oldOutcome = outcomeRepository.findByOutcomeId(outcomeId);
        if(oldOutcome == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Outcome not found");
        }

        //Get required info & check user is allowed to edit
        Long activityId = oldOutcome.getActivityId();
        Activity activity = activityRepository.findByActivityId(activityId);
        Long profileId = activity.getCreatorUserId();
        User creator = userAuthenticationService.findByUserId(token, profileId);

        Boolean resultsExist = true;
        resultsExist = resultRepository.existsByOutcome(oldOutcome);
        if(resultsExist){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot edit outcome ID " + outcomeId + " due to results already existing");
        }
        if((!activity.getCreatorUserId().equals(profileId))
                && (!userAuthenticationService.hasAdminPrivileges(creator))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is forbidden from editing outcome with ID: " + outcomeId);
        }
        OutcomeValidator.validate(outcome);

        //Overwrite old outcome data with new data and save
        oldOutcome.setTitle(outcome.getTitle());
        oldOutcome.setUnitName(outcome.getUnitName());
        oldOutcome.setUnitType(outcome.getUnitType());
        outcomeRepository.save(oldOutcome);
        response.setStatus(HttpServletResponse.SC_OK); //200
    }

    /**
     * Removes the outcome identified by its outcomeId.
     * The user requesting the outcomes removal must be the author of the activity the outcome belongs too
     * - or is an admin.
     * @param outcomeId the ID of the outcome that is to be deleted
     * @param request the request that contains the current user token
     */
    @DeleteMapping("/activities/{outcomeId}/outcomes")
    public void deleteActivityOutcomes(@PathVariable Long outcomeId, HttpServletRequest request) {
        Outcome outcome = outcomeRepository.findByOutcomeId(outcomeId);
        if (outcome == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Outcome with id " + outcomeId.toString() + "could not be found");
        }
        Activity activity = activityRepository.findByActivityId(outcome.getActivityId());
        String token = request.getHeader("Token");
        User user = userAuthenticationService.findByUserId(token, activity.getCreatorUserId());
        if (user == null || !user.getUserId().equals(activity.getCreatorUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is forbidden from removing this outcome.");
        }
        outcomeRepository.delete(outcome);
    }

}
