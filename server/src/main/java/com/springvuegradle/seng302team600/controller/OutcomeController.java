package com.springvuegradle.seng302team600.controller;


import com.springvuegradle.seng302team600.Utilities.OutcomeValidator;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.OutcomeRequest;
import com.springvuegradle.seng302team600.payload.OutcomeResponse;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.repository.ResultRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class OutcomeController {

    private final UserAuthenticationService userAuthenticationService;
    private final OutcomeRepository outcomeRepository;
    private final ActivityRepository activityRepository;
    private final ResultRepository resultRepository;

    public OutcomeController(UserAuthenticationService userAuthenticationService, OutcomeRepository outcomeRepository, ActivityRepository activityRepository, ResultRepository resultRepository) {
        this.userAuthenticationService = userAuthenticationService;
        this.outcomeRepository = outcomeRepository;
        this.activityRepository = activityRepository;
        this.resultRepository = resultRepository;
    }


    /**
     * Create an outcome for an activity.
     * This method checks that the user from token and the creator of the activity are the same
     * else it will throw a 403 forbidden exception
     * @param outcomeRequest the outcomeResponse payload to be converted to an Outcome object
     *                       to be saved to the activity (in the outcomeRepository)
     * @param request the request packet, where we will check the user Token
     */
    @PostMapping("/activities/outcomes")
    public void createNewActivityOutcomes(@Validated @RequestBody OutcomeRequest outcomeRequest, HttpServletRequest request) {
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
    }

    @GetMapping("/activities/{activityId}/outcomes")
    public List<OutcomeResponse> getActivityOutcomes(@PathVariable Long activityId, HttpServletRequest request) {
        //ToDo: Implement this method, and add a DocString please
        return null;
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
