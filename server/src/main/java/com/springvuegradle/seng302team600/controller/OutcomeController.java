package com.springvuegradle.seng302team600.controller;


import com.springvuegradle.seng302team600.model.Outcome;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class OutcomeController {

    /*
        Set the repositories and services you will need here
        eg:
        private final userAuthenticationService = new UserAuthenticationService();
        and remember to put it in the constructor as a parameter!
     */

    public OutcomeController() {}


    @GetMapping("/activities/{activityId}/outcomes")
    public List<Outcome> getActivityOutcomes(HttpServletRequest request,
                                             @PathVariable Long activityId) {
        //ToDo: Implement this method, and add a DocString please
        return null;
    }

    @PutMapping("/profile/{profileId}/activities/{activityId}/outcomes")
    public void updateActivityOutcomes(HttpServletRequest request,
                                       @PathVariable Long profileId, @PathVariable Long activityId) {
        //ToDo: Implement this method, and add a DocString please
    }
}
