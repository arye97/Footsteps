package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.Utilities.ResultValidator;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.model.Result;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.ResultRequest;
import com.springvuegradle.seng302team600.payload.ResultResponse;
import com.springvuegradle.seng302team600.repository.ActivityParticipantRepository;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.repository.ResultRepository;
import com.springvuegradle.seng302team600.service.FeedEventService;
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
public class ResultController {

    private final UserAuthenticationService userAuthenticationService;
    private final OutcomeRepository outcomeRepository;
    private final ResultRepository resultRepository;
    private final ActivityParticipantRepository participantRepository;
    private final FeedEventService feedEventService;
    private final ActivityRepository activityRepository;


    public ResultController(UserAuthenticationService userAuthenticationService, OutcomeRepository outcomeRepository,
                            ResultRepository resultRepository, ActivityParticipantRepository participantRepository,
                            FeedEventService feedEventService, ActivityRepository activityRepository) {
        this.userAuthenticationService = userAuthenticationService;
        this.outcomeRepository = outcomeRepository;
        this.resultRepository = resultRepository;
        this.participantRepository = participantRepository;
        this.feedEventService = feedEventService;
        this.activityRepository = activityRepository;
    }

    /**
     * POST request for generating a new Result for the given Outcome.
     * @param resultRequest the Result to be saved
     * @param outcomeId the Outcome ID being referenced
     * @param request the Http request from front-end
     * @param response the Http response to front-end
     */
    @PostMapping("/outcomes/{outcomeId}/results")
    public void createNewOutcomeResult(@Validated @RequestBody ResultRequest resultRequest, @PathVariable Long outcomeId,
                                       HttpServletRequest request, HttpServletResponse response) {
        // Authentication for session User
        String token = request.getHeader("Token");
        User user = userAuthenticationService.findByToken(token);

        Outcome outcome = outcomeRepository.findByOutcomeId(outcomeId);
        if (outcome == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Outcome not found");
        }

        Result result = new Result(resultRequest, outcome);

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

        Activity activity = activityRepository.findByActivityId(outcome.getActivityId());

        // Check if the user is participating in this outcome's activity
        Long followCount = participantRepository.existsByActivityIdAndUserId(
                outcome.getActivityId(), result.getUserId());
        if (followCount <= 0 && !activity.getCreatorUserId().equals(result.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User is not a participant of this outcome's activity");
        }

        if (!userAuthenticationService.hasAdminPrivileges(user) && !user.getUserId().equals(result.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User is Forbidden from adding a result for this user ID");
        }

        // Result table will be updated when saving this Outcome
        result.setOutcome(outcome);
        outcome.addResult(result);
        outcomeRepository.save(outcome);

        //Create FeedEvent to add result for both participants and creator
        feedEventService.addResultToActivityEvent(activity, result.getUserId(), outcome.getTitle());

        response.setStatus(HttpServletResponse.SC_CREATED); //201
    }


    /**
     * Return a list of all Result objects that are associated with an outcome
     *
     * @param outcomeId id of the outcome you want Result from
     * @param request the Http request from front-end
     * @param response the Http response to front-end
     * @return list of Result objects
     */
    @GetMapping("/outcomes/{outcomeId}/results")
    public List<ResultResponse> getOutcomeResults(@PathVariable Long outcomeId, HttpServletRequest request,
                                                  HttpServletResponse response) {
        // Authentication for session User
        String token = request.getHeader("Token");
        userAuthenticationService.findByToken(token);

        Outcome outcome = outcomeRepository.findByOutcomeId(outcomeId);

        List<Result> results = resultRepository.findByOutcome(outcome);



        if (outcome == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Outcome not found");
        }

        if (results == null) {
            return new ArrayList<>();
        }

        List<ResultResponse> resultResponses = new ArrayList<>();

        for (Result result : results) {
            User user = userAuthenticationService.viewUserById(result.getUserId(), token);

            resultResponses.add(new ResultResponse(result, user));
        }
        return resultResponses;
    }
}
