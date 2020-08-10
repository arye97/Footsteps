package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.enumeration.FeedPostType;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.FeedEvent;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.IsFollowingResponse;
import com.springvuegradle.seng302team600.repository.ActivityParticipantRepository;
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
    private final ActivityParticipantRepository activityParticipantRepository;

    public FeedEventController(FeedEventRepository feedEventRepository, ActivityRepository activityRepository,
                               UserAuthenticationService userAuthenticationService, ActivityParticipantRepository activityParticipantRepository) {
        this.feedEventRepository = feedEventRepository;
        this.activityRepository = activityRepository;
        this.userAuthenticationService = userAuthenticationService;
        this.activityParticipantRepository = activityParticipantRepository;
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
        activity.addParticipant(user);
        activityRepository.save(activity);

        FeedEvent followEvent = new FeedEvent();
        //Check that the user isn't ALREADY following this event to prevent adding feedevents over and over again
        FeedEvent checkUserFollowingFeedEvent = feedEventRepository.findByActivityIdAndViewerIdAndFeedEventType(
                activityId, profileId, FeedPostType.FOLLOW);
        if (checkUserFollowingFeedEvent != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already following this activity");
        }
        //The user is not following the event so continue
        // Create the Follow Feed Event and save it to the db
        followEvent.setActivityId(activityId); // the id of the activity
        followEvent.setAuthorId(profileId); // the user id of user who caused the feed event
        followEvent.setFeedEventType(FeedPostType.FOLLOW); // the type of event: here it should always be FOLLOW
        followEvent.setTimeStampNow(); //set the time as right now
        followEvent.setViewerId(profileId); // the user if that views this feed event
        feedEventRepository.save(followEvent); //save the event!
    }

    @DeleteMapping("/profiles/{profileId/subscriptions/activities/{activityId}")
    public void unFollowAnActivity(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable Long profileId, @PathVariable Long activityId) {
        //ToDo Implement this method, also add DocString
    }

    /**
     * Gets whether or not the user is following an activity
     * @return true if the user is a participant in the activity
     */
    @GetMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    public IsFollowingResponse isFollowingAnActivity(HttpServletRequest request, HttpServletResponse response,
                                                     @PathVariable Long profileId, @PathVariable Long activityId) {
        String token = request.getHeader("Token");
        User user = userAuthenticationService.findByUserId(token, profileId);
        Long followingCount = activityParticipantRepository.existsByActivityIdAndUserId(activityId, profileId);
        IsFollowingResponse isFollowing = new IsFollowingResponse(followingCount > 0);
        return isFollowing;
    }
}
