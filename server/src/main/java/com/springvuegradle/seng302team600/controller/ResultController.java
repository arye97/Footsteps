package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.Utilities.ResultValidator;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.Result;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.ActivityParticipantRepository;
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
import java.util.List;

@RestController
public class ResultController {

    private final UserAuthenticationService userAuthenticationService;
    private final OutcomeRepository outcomeRepository;
    private final ResultRepository resultRepository;
    private final ActivityParticipantRepository participantRepository;
    private final ActivityRepository activityRepository;


    public ResultController(UserAuthenticationService userAuthenticationService, OutcomeRepository outcomeRepository,
                            ResultRepository resultRepository, ActivityParticipantRepository participantRepository, ActivityRepository activityRepository) {
        this.userAuthenticationService = userAuthenticationService;
        this.outcomeRepository = outcomeRepository;
        this.resultRepository = resultRepository;
        this.participantRepository = participantRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * POST request for generating a new Result for the given Outcome.
     * @param result the Result to be saved
     * @param outcomeId the Outcome ID being referenced
     * @param request the Http request from front-end
     * @param response the Http response to front-end
     */
    @PostMapping("/outcomes/{outcomeId}/results")
    public void createNewOutcomeResult(@Validated @RequestBody Result result, @PathVariable Long outcomeId,
                                       HttpServletRequest request, HttpServletResponse response) {
        // Authentication for session User
        String token = request.getHeader("Token");
        User user = userAuthenticationService.findByToken(token);

        Outcome outcome = outcomeRepository.findByOutcomeId(outcomeId);
        if (outcome == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Outcome not found");
        }

        // Validate the submitted Result. This includes validating the Value objects too.
        ResultValidator.validate(result, outcome);

        // If no userId is given, set userId to this user's ID
        if (result.getUserId() == null) {
            result.setUserId(user.getUserId());
        }

        if (resultRepository.existsByOutcomeAndUserId(outcome, result.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User's result for given outcome already exists");
        }

        // Check if the user is participating in this outcome's activity
        Long followCount = participantRepository.existsByActivityIdAndUserId(
                outcome.getActivityId(), result.getUserId());
        if (followCount <= 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User is not a participant of this outcome's activity");
        }

        if (!userAuthenticationService.hasAdminPrivileges(user) && !user.getUserId().equals(result.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User is Forbidden from added a result for this user ID");
        }

        // Result table will be updated when saving this Outcome
        outcome.addResult(result);
        outcomeRepository.save(outcome);
    }

    @GetMapping("/outcomes/{outcomeId}/results")
    public List<Result> getOutcomeResults(@PathVariable Long outcomeId, HttpServletRequest request,
                                          HttpServletResponse response) {
        //TODO Implement this method, and add a DocString please
        return null;
    }
}
