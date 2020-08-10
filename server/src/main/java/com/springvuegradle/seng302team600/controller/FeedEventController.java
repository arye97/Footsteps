package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.enumeration.FeedPostType;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.FeedEvent;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.IsFollowingResponse;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.FeedEventRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FeedEventController Contains the endpoints for GET, POST and DELETE request for the client.
 * The FeedEventRepository is the access point to the database for querying Feed Events.
 */
@RestController
public class FeedEventController {

    private final FeedEventRepository feedEventRepository;
    private final ActivityRepository activityRepository;
    private final UserAuthenticationService userAuthenticationService;

    public FeedEventController(FeedEventRepository feedEventRepository,
                               ActivityRepository activityRepository, UserAuthenticationService userAuthenticationService) {
        this.feedEventRepository = feedEventRepository;
        this.activityRepository = activityRepository;
        this.userAuthenticationService = userAuthenticationService;
    }

    /**
     * POST request endpoint for a user to follow an activity
     * @param profileId the id of the user to be the follower
     * @param activityId the id of the activity to be followed
     */
    @PostMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    public void followAnActivity(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable Long profileId, @PathVariable Long activityId) {
        //Add the user as a participant of the activity
        String token = request.getHeader("Token");
        User user = userAuthenticationService.findByUserId(token, profileId);
        Activity activity = activityRepository.findByActivityId(activityId);

        //Check that the user is participating in the event before trying to un-follow
        if (activity.getParticipants().contains(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User can't re-follow an event they're currently participating in.");
        }


        //The user is not following the event so continue
        // Create the Follow Feed Event and save it to the db
        FeedEvent followEvent = new FeedEvent(activityId, profileId, profileId, FeedPostType.FOLLOW);
        feedEventRepository.save(followEvent); //save the event!

        // Add participant at the end, in case there are errors before then
        activity.addParticipant(user);
        activityRepository.save(activity);
    }

    @DeleteMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    public void unFollowAnActivity(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable Long profileId, @PathVariable Long activityId) {
        //Remove the user as a participant of the activity
        String token = request.getHeader("Token");
        User user = userAuthenticationService.findByUserId(token, profileId);
        Activity activity = activityRepository.findByActivityId(activityId);

        //Check that the user is participating in the event before trying to un-follow
        if (!activity.getParticipants().contains(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User can't un-follow an event they're not participating in.");
        }


        //The user has not un-followed the event so continue
        // Create the Follow Feed Event and save it to the db
        FeedEvent unFollowEvent = new FeedEvent(activityId, profileId, profileId, FeedPostType.UNFOLLOW);
        feedEventRepository.save(unFollowEvent); //save the event!

        // Remove participant at the end, in case there are errors before then
        activity.removeParticipant(user);
        activityRepository.save(activity);
    }

    @GetMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    public IsFollowingResponse isFollowingAnActivity() {
        //ToDo Implement this method, also add DocString
        return null;
    }
}
