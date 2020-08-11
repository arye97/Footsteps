package com.springvuegradle.seng302team600.controller;


import com.springvuegradle.seng302team600.model.Outcome;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class OutcomeController {

    private final UserAuthenticationService userAuthenticationService;
    private final OutcomeRepository outcomeRepository;
    // TODO uncomment the below attribute when ResultRepository exists
//    private final ResultRepository resultRepository;

    public OutcomeController(UserAuthenticationService userAuthenticationService, OutcomeRepository outcomeRepository) {
        this.userAuthenticationService = userAuthenticationService;
        this.outcomeRepository = outcomeRepository;
    }

    @PostMapping("/activities/{activityId}/outcomes")
    public void createNewActivityOutcome(HttpServletRequest request, @PathVariable Long activityId) {
        //ToDo: Implement this method, and add a DocString please
    }

    @GetMapping("/activities/{activityId}/outcomes")
    public List<Outcome> getActivityOutcomes(HttpServletRequest request, @PathVariable Long activityId) {
        //ToDo: Implement this method, and add a DocString please
        return null;
    }

    @PutMapping("/activities/{activityId}/outcomes")
    public void updateActivityOutcomes(HttpServletRequest request, @PathVariable Long activityId) {
        //ToDo: Implement this method, and add a DocString please
    }
}
