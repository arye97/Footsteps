package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.Utilities.ActivityValidator;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.ActivityParticipantRepository;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.service.ActivityTypeService;
import com.springvuegradle.seng302team600.service.FeedEventService;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;

/**
 * Controller to manage activities and activity type
 */
@RestController
public class ActivityController {

    private final ActivityRepository activityRepository;
    private final UserAuthenticationService userAuthenticationService;
    private final ActivityTypeService activityTypeService;
    private final FeedEventService feedEventService;
    private final ActivityParticipantRepository activityParticipantRepository;

    public ActivityController(ActivityRepository activityRepository, UserAuthenticationService userAuthenticationService,
                              ActivityTypeService activityTypeService, FeedEventService feedEventService, ActivityParticipantRepository activityParticipantRepository) {
        this.activityRepository = activityRepository;
        this.userAuthenticationService = userAuthenticationService;
        this.activityTypeService = activityTypeService;
        this.feedEventService = feedEventService;
        this.activityParticipantRepository = activityParticipantRepository;
    }

    /**
     * Create a new Activity.
     * @param newActivity the new Activity
     * @param response Used to set status of operation
     * @param profileId the Id of the User who created the activity
     */
    @PostMapping("/profiles/{profileId}/activities")
    public void newActivity(@Validated @RequestBody Activity newActivity,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            @PathVariable(value = "profileId") Long profileId) {
        String token = request.getHeader("Token");
        // Throws error if token doesn't match the profileId, i.e. you can't create an activity with a creatorUserId that isn't your own
        userAuthenticationService.findByUserId(token, profileId);
        // Use ActivityType entities from the database.  Don't create duplicates.
        newActivity.setActivityTypes(
                activityTypeService.getMatchingEntitiesFromRepository(newActivity.getActivityTypes())
        );
        newActivity.setCreatorUserId(profileId);
        // Check the user input and throw ResponseStatusException if invalid stopping execution
        ActivityValidator.validate(newActivity);
        activityRepository.save(newActivity);
        response.setStatus(HttpServletResponse.SC_CREATED); //201
    }

    /**
     * Get an activity by Id
     * @param activityId the Id of the Activity
     * @return the found activity
     */
    @GetMapping("/activities/{activityId}")
    public Activity findUserActivity(@PathVariable Long activityId) {
        Activity activity = activityRepository.findByActivityId(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found");
        }
        return activity;
    }

    /**
     * Get all the Activities in the Database
     * @return List of all Activities
     */
    @GetMapping("/activities")
    public List<Activity> findAllActivities() {
        return activityRepository.findAll();
    }


    /** Put Request for editing/updating an activity created by a user
     *  Checks all possible inputs to see if that input is there, and to be updated
     *  takes from the client only the json object of the to-be-updated inputs
     *  and the activity id through put the url mapping.
     * @param activityId the Id of the Activity to edit
     * @param activity the activity object to update
     * @param profileId the Id of the User who created the activity
     */

    @PutMapping("/profiles/{profileId}/activities/{activityId}")
    public void editActivity(@PathVariable(value = "profileId") Long profileId,
                             @PathVariable(value = "activityId") Long activityId,
                             @Validated @RequestBody Activity activity,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("Token");
        User author = userAuthenticationService.findByUserId(token, profileId);
        //get old activity to set values
        Activity oldActivity = activityRepository.findByActivityId(activityId);
        //check activity exists and user is author
        if (oldActivity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found");
        }
        if ((!oldActivity.getCreatorUserId().equals(profileId)) //check for author
                && (!userAuthenticationService.hasAdminPrivileges(author))) { //check for admin
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is forbidden from editing activity with ID:" + activityId);
        }
        ActivityValidator.validate(activity);
        oldActivity.setDescription(activity.getDescription());
        oldActivity.setName(activity.getName());
        oldActivity.setLocation(activity.getLocation());
        oldActivity.setContinuous(activity.isContinuous());
        if (!activity.isContinuous()) {
            oldActivity.setStartTime(activity.getStartTime());
            oldActivity.setEndTime(activity.getEndTime());
        }
        oldActivity.setActivityTypes(activityTypeService.getMatchingEntitiesFromRepository(activity.getActivityTypes()));
        //save this updated activity
        activityRepository.save(oldActivity);

        //Create FeedEvents for participants and creator
        feedEventService.modifyActivityEvent(oldActivity, profileId);

        response.setStatus(HttpServletResponse.SC_OK); //200
    }

    /**
     * Delete an Activity
     * @param activityId the Id of the activity to delete
     * @param request the actual request from the client, containing pertinent data
     */
    @DeleteMapping("/profiles/{profileId}/activities/{activityId}")
    public void deleteActivity(@PathVariable(value="profileId") Long profileId, @PathVariable(value="activityId") Long activityId, HttpServletRequest request) {
        //Check the activity is this specific users activity
        Activity activity = activityRepository.findByActivityId(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found");
        }
        Long authorId = activity.getCreatorUserId();
        String token = request.getHeader("Token");
        User user = userAuthenticationService.findByUserId(token, profileId); //finds user and validates they exist

        if ((!userAuthenticationService.hasAdminPrivileges(user)) && (authorId != null)) {
            /* Only run this check if the user is NOT an Admin,
               otherwise admins can delete/edit others activities.
             */
            if (!authorId.equals(user.getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is forbidden from deleting activity with ID:" + activityId);
            }
        }

        //Create FeedEvents for participants and creator
        feedEventService.deleteActivityEvent(activity, profileId);

        //Delete the activity
        activityRepository.delete(activity);
    }

    /**
     * Get all activities that a user has created or is currently following
     * @param profileId the id of the user/creator
     * @param request the actual request from the client, containing pertinent data
     */
    @GetMapping("/profiles/{profileId}/activities")
    public List<Activity> getUsersActivities(@PathVariable Long profileId, HttpServletRequest request) {
        //checking for user validation
        //attempt to find user by token, don't need to save user discovered
        String token = request.getHeader("Token");
        userAuthenticationService.viewUserById(profileId, token);
        List<Activity> activities = activityRepository.findAllByUserId(profileId);
        List<Long> followedActivityIds = this.activityParticipantRepository.findActivitiesByParticipantId(profileId);
        List<Activity> followedActivities = this.activityRepository.findActivityByActivityIdIn(followedActivityIds);
        activities.addAll(followedActivities);
        Set<Activity> distinctActivities = new HashSet<>(activities);

        return new ArrayList<Activity>(distinctActivities);
    }


    /**
     * Get a list of participants for an activity
     * @param activityId the Id of the Activity
     * @return a list of Users
     */
    @GetMapping("/activities/{activityId}/participants")
    public Set<User> getParticipantsOfActivity(@PathVariable Long activityId, HttpServletRequest request) {
        String token = request.getHeader("Token");
        userAuthenticationService.findByToken(token);
        Activity activity = activityRepository.findByActivityId(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found");
        }
        return activity.getParticipants();
    }
}
