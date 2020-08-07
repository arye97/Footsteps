package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.payload.IsFollowingResponse;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.FeedEventRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FeedEventController Contains the endpoints for GET, POST and DELETE request for the client.
 * The FeedEventRepository is the access point to the database for querying Feed Events.
 */
@RestController
public class FeedEventController {

    private final UserAuthenticationService userService;

    private final FeedEventRepository feedEventRepository;
    private final ActivityRepository activityRepository;

    public FeedEventController(UserAuthenticationService userService, FeedEventRepository feedEventRepository, ActivityRepository activityRepository) {
        this.userService = userService;
        this.feedEventRepository = feedEventRepository;
        this.activityRepository = activityRepository;
    }

    @PostMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    public void followAnActivity(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable Long profileId, @PathVariable Long activityId) {
        //ToDo This method is already implemented, ask Ryan F, also add DocString
    }

    @DeleteMapping("/profiles/{profileId/subscriptions/activities/{activityId}")
    public void unFollowAnActivity(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable Long profileId, @PathVariable Long activityId) {
        //ToDo Implement this method, also add DocString
    }

    @GetMapping()
    public IsFollowingResponse isFollowingAnActivity() {
        //ToDo Implement this method, also add DocString
        return null;
    }
}
