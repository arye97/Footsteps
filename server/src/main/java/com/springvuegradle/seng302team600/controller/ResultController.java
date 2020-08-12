package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.Result;
import com.springvuegradle.seng302team600.repository.ActivityParticipantRepository;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.repository.ResultRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/outcomes/{outcomeId}/results")
    public void createNewOutcomeResult(@Validated @RequestBody Result result, @PathVariable Long outcomeId,
                                       HttpServletRequest request, HttpServletResponse response) {
        //TODO Implement this method, and add a DocString please
        // Must check if user is a participant
        // Must set the Outcome for this Result
    }

    @GetMapping("/outcomes/{outcomeId}/results")
    public List<Result> getOutcomeResults(@PathVariable Long outcomeId, HttpServletRequest request,
                                          HttpServletResponse response) {
        //TODO Implement this method, and add a DocString please
        return null;
    }
}
