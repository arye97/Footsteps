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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

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

    private static final int PAGE_SIZE = 5;

    public FeedEventController(FeedEventRepository feedEventRepository, ActivityRepository activityRepository,
                               UserAuthenticationService userAuthenticationService, ActivityParticipantRepository activityParticipantRepository) {
        this.feedEventRepository = feedEventRepository;
        this.activityRepository = activityRepository;
        this.userAuthenticationService = userAuthenticationService;
        this.activityParticipantRepository = activityParticipantRepository;
    }

    /**
     * POST request endpoint for a user to follow an activity
     *
     * @param profileId  the id of the user to be the follower
     * @param activityId the id of the activity to be followed
     */
    @SuppressWarnings("Duplicates")  // I don't see a good way of not duplicating the first lines
    @PostMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    public void followAnActivity(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable Long profileId, @PathVariable Long activityId) {

        String token = request.getHeader("Token");
        User user = userAuthenticationService.findByUserId(token, profileId);
        Activity activity = activityRepository.findByActivityId(activityId);

        checkUserActivityNotNull(user, activity);

        //Check that the user is participating in the event, before trying to follow.  Throw error if already following.
        if (activity.getParticipants().contains(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User can't re-follow an event they're currently participating in.");
        }


        //The user is not following the event so continue
        // Create the Follow Feed Event and save it to the db
        FeedEvent followEvent = new FeedEvent(activityId, activity.getName(), profileId, FeedPostType.FOLLOW);
        followEvent.addViewer(user);
        feedEventRepository.save(followEvent); //save the event!

        // Add participant at the end, in case there are errors before then
        activity.addParticipant(user);
        activityRepository.save(activity);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    /**
     * DELETE request endpoint for a user to un-follow an activity
     *
     * @param profileId  the id of the user to be the follower
     * @param activityId the id of the activity to be followed
     */
    @SuppressWarnings("Duplicates")  // I don't see a good way of not duplicating the first lines
    @DeleteMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    public void unFollowAnActivity(HttpServletRequest request,
                                   @PathVariable Long profileId, @PathVariable Long activityId) {

        String token = request.getHeader("Token");
        User user = userAuthenticationService.findByUserId(token, profileId);
        Activity activity = activityRepository.findByActivityId(activityId);

        checkUserActivityNotNull(user, activity);

        //Check that the user is participating in the event before trying to un-follow.  Throw error if not following.
        if (!activity.getParticipants().contains(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User can't un-follow an event they're not participating in.");
        }


        //The user has not un-followed the event so continue
        // Create the Follow Feed Event and save it to the db
        FeedEvent unFollowEvent = new FeedEvent(activityId, activity.getName(), profileId, FeedPostType.UNFOLLOW);
        unFollowEvent.addViewer(user);
        feedEventRepository.save(unFollowEvent); //save the event!

        // Remove participant at the end, in case there are errors before then
        activity.removeParticipant(user);
        activityRepository.save(activity);
    }

    /**
     * Gets whether or not the user is following an activity
     *
     * @param profileId  the id of the user to check
     * @param activityId the id of the activity to check
     * @return true if the user is a participant in the activity
     */
    @GetMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    public IsFollowingResponse isFollowingAnActivity(HttpServletRequest request,
                                                     @PathVariable Long profileId, @PathVariable Long activityId) {
        String token = request.getHeader("Token");
        userAuthenticationService.findByUserId(token, profileId);
        Long followingCount = activityParticipantRepository.existsByActivityIdAndUserId(activityId, profileId);
        return new IsFollowingResponse(followingCount > 0);
    }

    /**
     * Checks that user and activity are not null.  If either are, throws a 400 error, bad request.
     * Helper function to reduce duplicate code.
     *
     * @throws ResponseStatusException 400 error if either id is null
     */
    private void checkUserActivityNotNull(User user, Activity activity) throws ResponseStatusException {
        // Check that user can be found.  If it isn't, could be that token is bad, or user doesn't exist.
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't find or authenticate user from profileId");
        }
        // Check that activity can be found
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't find activity from activityId.");
        }
    }

    /**
     * Gets the list of paginated feed events the user is subscribed to in descending order by timeStamp.
     * There are five FeedEvents for a single page.
     *
     * @param profileId the id of the user to check
     * @return the list of feed events
     */
    @GetMapping("/profiles/{profileId}/subscriptions")
    public List<FeedEvent> getFeedEvents(HttpServletRequest request, HttpServletResponse response,
                                         @PathVariable Long profileId) {
        String token = request.getHeader("Token");
        int pageNumber = request.getIntHeader("Page-Number");
        userAuthenticationService.findByUserId(token, profileId);
        // Instantiate pagination
        Pageable pageWithFiveFeedEvents = PageRequest.of(pageNumber, PAGE_SIZE);
        Page<FeedEvent> paginationResponseData = feedEventRepository.findByViewerIdOrderByTimeStamp(profileId, pageWithFiveFeedEvents);
        if (paginationResponseData == null) {
            return new ArrayList<>();
        }
        int totalElements = (int) paginationResponseData.getTotalElements();
        response.setIntHeader("Total-Rows", totalElements);
        return paginationResponseData.getContent();
    }
}
