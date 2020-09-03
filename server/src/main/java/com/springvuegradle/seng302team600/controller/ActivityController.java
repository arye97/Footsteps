package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.FeedEvent;
import com.springvuegradle.seng302team600.validator.ActivityValidator;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.ActivityResponse;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.service.ActivityTypeService;
import com.springvuegradle.seng302team600.service.FeedEventService;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.springvuegradle.seng302team600.payload.ParticipantResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Controller to manage activities and activity type
 */
@RestController
public class ActivityController {

    private final ActivityRepository activityRepository;
    private final UserAuthenticationService userAuthenticationService;
    private final ActivityTypeService activityTypeService;
    private final FeedEventService feedEventService;

    private static final int PAGE_SIZE = 5;
    private static final String CONTINUOUS = "CONTINUOUS";
    private static final String DURATION = "DURATION";

    public ActivityController(ActivityRepository activityRepository, UserAuthenticationService userAuthenticationService,
                              ActivityTypeService activityTypeService, FeedEventService feedEventService) {
        this.activityRepository = activityRepository;
        this.userAuthenticationService = userAuthenticationService;
        this.activityTypeService = activityTypeService;
        this.feedEventService = feedEventService;
    }

    /**
     * Create a new Activity.
     *
     * @param newActivity the new Activity
     * @param response    Used to set status of operation
     * @param profileId   the Id of the User who created the activity
     * @return the id of the activity
     */
    @PostMapping("/profiles/{profileId}/activities")
    public Long newActivity(@Validated @RequestBody Activity newActivity,
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
        Activity createdActivity = activityRepository.save(newActivity);
        response.setStatus(HttpServletResponse.SC_CREATED); //201

        return createdActivity != null ? createdActivity.getActivityId() : null;
    }

    /**
     * Get an activity by Id
     *
     * @param activityId the Id of the Activity
     * @return the found activity
     */
    @GetMapping("/activities/{activityId}")
    public ActivityResponse findUserActivity(@PathVariable Long activityId) {
        Activity activity = activityRepository.findByActivityId(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity is not found. This activity may have been deleted.");
        }
        return new ActivityResponse(activity);
    }

    /**
     * Get all the Activities in the Database
     *
     * @return List of all Activities
     */
    @GetMapping("/activities")
    public List<ActivityResponse> findAllActivities() {
        List<Activity> activities = activityRepository.findAll();
        List<ActivityResponse> activityResponses = new ArrayList<>();
        activities.forEach(i -> activityResponses.add(new ActivityResponse(i)));
        return activityResponses;
    }


    /**
     * Put Request for editing/updating an activity created by a user
     * Checks all possible inputs to see if that input is there, and to be updated
     * takes from the client only the json object of the to-be-updated inputs
     * and the activity id through put the url mapping.
     *
     * @param activityId the Id of the Activity to edit
     * @param activity   the activity object to update
     * @param profileId  the Id of the User who created the activity
     */

    @PutMapping("/profiles/{profileId}/activities/{activityId}")
    public void editActivity(@PathVariable(value = "profileId") Long profileId,
                             @PathVariable(value = "activityId") Long activityId,
                             @Validated @RequestBody Activity activity,
                             HttpServletRequest request, HttpServletResponse response) {
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
     *
     * @param activityId the Id of the activity to delete
     * @param request    the actual request from the client, containing pertinent data
     */
    @DeleteMapping("/profiles/{profileId}/activities/{activityId}")
    public void deleteActivity(@PathVariable(value = "profileId") Long profileId, @PathVariable(value = "activityId") Long activityId, HttpServletRequest request) {
        //Check the activity is this specific users activity
        Activity activity = activityRepository.findByActivityId(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid activity id");
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
     * Get all activities that a user has created or is currently following.
     * This list can be limited/filtered by just continuous or duration activities.
     * Accepts extra headers
     * - Page-Number which has a of default 0 (first page),
     * - Search-Filter which can be either CONTINUOUS or DURATION.
     * A successful response returns the header Total-Rows.
     *
     * @param profileId the id of the user/creator
     * @param request   the actual request from the client, containing pertinent data
     */
    @GetMapping("/profiles/{profileId}/activities")
    public List<ActivityResponse> getUsersActivities(@PathVariable Long profileId,
                                                     HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Token");
        userAuthenticationService.viewUserById(profileId, token);

        int pageNumber = request.getIntHeader("Page-Number");
        if (pageNumber < 0) pageNumber = 0;

        String searchFilter = request.getHeader("Search-Filter");
        List<Activity> activities;
        if (searchFilter != null && searchFilter.equals(CONTINUOUS)) {
            activities = activityRepository.findAllContinuousByUserId(profileId);
        } else if (searchFilter != null && searchFilter.equals(DURATION)) {
            activities = activityRepository.findAllDurationByUserId(profileId);
        } else {
            activities = activityRepository.findAllByUserId(profileId);
        }

        if (activities == null) {
            return new ArrayList<>();
        }
        Set<Activity> distinctActivities = new HashSet<>(activities);
        int fromIndex = pageNumber * PAGE_SIZE;
        int toIndex = pageNumber * PAGE_SIZE + PAGE_SIZE;
        int totalElements = distinctActivities.size();
        if (fromIndex >= totalElements) {
            return new ArrayList<>();
        }
        if (toIndex > totalElements) toIndex = totalElements;
        List<Activity> activitiesOnPage = new ArrayList<>(distinctActivities)
                .subList(fromIndex, toIndex);

        response.setIntHeader("Total-Rows", totalElements);
        List<ActivityResponse> activityResponses = new ArrayList<>();
        activitiesOnPage.forEach(i ->
                activityResponses.add(new ActivityResponse(i)));
        return activityResponses;
    }

    /**
     * Get a list of participants for an activity
     *
     * @param activityId the Id of the Activity
     * @return a HashSet of Users with firstname, lastname and id
     */
    @GetMapping("/activities/{activityId}/participants")
    public Set<ParticipantResponse> getParticipantsOfActivity(@PathVariable Long activityId, HttpServletRequest request) {
        Set<User> participantList;
        Set<ParticipantResponse> returnedParticipantData = new HashSet<>();
        String token = request.getHeader("Token");
        userAuthenticationService.findByToken(token);
        Activity activity = activityRepository.findByActivityId(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found");
        }
        participantList = activity.getParticipants();
        for (User user : participantList) {
            returnedParticipantData.add(new ParticipantResponse(user));
        }
        return returnedParticipantData;
    }


    /**
     * Check if the user assigned to this session token
     * is allowed to edit the given activity.
     * - 200 OK
     * - 401 UNAUTHORIZED
     * - 403 FORBIDDEN
     *
     * @param activityId the activity's ID which is being checked
     * @param request    the actual request from the client, containing pertinent data
     */
    @GetMapping("/check-activity/{activityId}")
    public void isActivityEditableByUser(@PathVariable(value = "activityId") Long activityId, HttpServletRequest request) {
        String token = request.getHeader("Token");
        // Checks the authentication of the user, are they logged in, have they timed out, do they exist.
        // If an error is found this service throws an UNAUTHORIZED error (401)
        User user = userAuthenticationService.findByToken(token);
        Activity activity = activityRepository.findByActivityId(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found");
        }
        // Check if the user is the creator or an admin, if not throw a FORBIDDEN error (403).
        if (!user.getUserId().equals(activity.getCreatorUserId()) && !userAuthenticationService.hasAdminPrivileges(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is forbidden from editing activity with ID:" + activityId);
        }
        // If this point is reached status code is OK (200)
    }

    /**
     * Takes a string from url params which we will use for matching
     * This string must have % character in place of spaces provided from front end
     * eg Climb%20Mount%20Fuji
     * so the url would look like => /activities?activityName=Climb%20Mount%20Fuji
     * or if we want to exact match it would be /activities?activityName="Climb%20Mount%20Fuji"
     * where we check all activities if they contain any of these words
     * @param request the http request with the user token we need
     * @param activityName the word/sentence we need to search for
     * @return a list containing all activities found
     */
    @RequestMapping(
            value = "/activities",
            params = { "activityName"},
            method = RequestMethod.GET
    )
    public List<ActivityResponse> getActivitiesByName(HttpServletRequest request,
                                                      @RequestParam(value="activityName") String activityName) {
        List<ActivityResponse> activitiesFound = new ArrayList<>();
        if (activityName.length() == 0) {
            return activitiesFound;
        }
        String token = request.getHeader("Token");
        userAuthenticationService.findByToken(token);
        //check for multiple words in the search query
        if (activityName.startsWith("\"") && activityName.endsWith("\"")){
            //then the user has chosen exact match!
            activityName = activityName.substring(1, activityName.length() - 1);
            if (activityName.contains("%20")) {
                List<String> searchTerms =  Arrays.asList(activityName.split("%20")); //underscore is our space char
                activityName = "";
                for (String term : searchTerms) {
                    activityName = activityName + term + " ";
                }
                activityName = activityName.trim();
            }
        } else {
            activityName = "%" + activityName + "%";
        }
        List<Activity> activities = activityRepository.findAllByKeyword(activityName);
        for (Activity activity : activities) {
            activitiesFound.add(new ActivityResponse(activity));
        }
        return activitiesFound;
    }
}
