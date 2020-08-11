package com.springvuegradle.seng302team600.controller;


import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.payload.OutcomeResponse;
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

    @PostMapping("/activities/outcomes")
    public void createNewActivityOutcomes(@Validated @RequestBody Outcome outcome, HttpServletRequest request,
                                          HttpServletResponse response) {
        //ToDo: Implement this method, and add a DocString please
        // Must set the activityId before saving!!!
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
