package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.springvuegradle.seng302team600.Utilities.ActivityValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.service.ActivityTypeService;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller to manage activities and activity type
 */
@RestController
public class ActivityController {

    private ActivityRepository activityRepository;
    private UserValidationService userValidationService;
    private ActivityTypeService activityTypeService;
    private UserRepository userRepository;

    public ActivityController(ActivityRepository activityRepository, UserValidationService userValidationService,
                              ActivityTypeService activityTypeService, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userValidationService = userValidationService;
        this.activityTypeService = activityTypeService;
        this.userRepository = userRepository;
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
        userValidationService.findByUserId(token, profileId);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid activity id");
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
     * @param jsonActivityEditString an Activity to edit as a json string
     * @param profileId the Id of the User who created the activity
     */
    @PutMapping("/profiles/{profileId}/activities/{activityId}")
    public void editActivity(@PathVariable Long activityId, HttpServletRequest request, HttpServletResponse response,
                             @RequestBody String jsonActivityEditString,
                             @PathVariable(value = "profileId") Long profileId) throws IOException {
        String token = request.getHeader("Token"); //this is the users token
        userValidationService.findByUserId(token, profileId);
        Activity activity = activityRepository.findByActivityId(activityId);
        //ResponseStatusException thrown if user unauthorized or forbidden from accessing requested user
        ObjectMapper nodeMapper = new ObjectMapper();
        ObjectNode editedData = nodeMapper.readValue(jsonActivityEditString, ObjectNode.class);
        String newDescription;
        try {newDescription = editedData.get("description").asText();} catch(Exception NullPointerException){ newDescription = null;}
        String newLocation;
        try {newLocation = editedData.get("location").asText();} catch(Exception NullPointerException) { newLocation = null; }
        String newName;
        try {newName = editedData.get("activity_name").asText(); } catch (Exception NullPointerException) { newName = null; }
        String checkContinuous;
        try { checkContinuous= editedData.get("continuous").asText(); } catch (Exception NullPointerException) {checkContinuous = null;}
        JsonNode nodeActivityTypes;
        try { nodeActivityTypes = editedData.get("activity_type"); } catch (Exception NullPointerException) { nodeActivityTypes = null; }
        //may need to re-write the activity types conversion if breaks
        //Check if any have changed or are null
        if (newDescription != null) { activity.setDescription(newDescription); }
        if (newLocation != null) {activity.setLocation(newLocation);}
        if (newName != null) { activity.setName(newName); }
        if (nodeActivityTypes != null) {
            Set<ActivityType> activityTypes = new HashSet<>();
            for (JsonNode element : nodeActivityTypes) {
                activityTypes.add(new ActivityType(element.asText()));
            }
            activity.setActivityTypes(
                activityTypeService.getMatchingEntitiesFromRepository(activityTypes)
            );
        }
        if (checkContinuous != null) {
            Boolean newContinuous = editedData.get("continuous").asBoolean();
            if (checkContinuous != null) { activity.setContinuous(newContinuous);}
            if (newContinuous == false) {
                String newStartTime = editedData.get("start_time").asText();
                String newEndTime = editedData.get("end_time").asText();
                activity.setStartTime(Date.valueOf(newStartTime));
                activity.setEndTime(Date.valueOf(newEndTime)); //This date manipulation relies on import java.sql.Date
            }
        }
        activityRepository.save(activity);
        response.setStatus(HttpServletResponse.SC_OK); //200
    }

    /**
     * Delete an Activity
     * @param activityId the Id of the activity to delete
     * @param request the actual request from the client, containing pertinent data
     */
    @DeleteMapping("/activities/{activityId}")
    public void deleteActivity(@PathVariable Long activityId, HttpServletRequest request) {
        //Check the activity is this specific users activity
        Activity activity = activityRepository.findByActivityId(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid activity id");
        }
        Long authorId = activity.getCreatorUserId();
        String token = request.getHeader("Token");
        User user = userRepository.findByToken(token); //finds user and validates they exist

        if ((!userValidationService.hasAdminPrivileges(user)) && (authorId != null)) {
            /* Only run this check if the user is NOT an Admin,
               otherwise admins can delete/edit others activities.
             */
            if (!authorId.equals(user.getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not activity creator");
            }
        }
        //Delete the activity
        activityRepository.delete(activity);

    }

    /**
     * Get all activities by user
     * @param profileId the id of the user/creator
     * @param request the actual request from the client, containing pertinent data
     */
    @GetMapping("/profiles/{profileId}/activities")
    public List<Activity> getUsersActivities(@PathVariable Long profileId, HttpServletRequest request) {
        //checking for user validation
        try {
            //attempt to find user by token, don't need to save user discovered
            String token = request.getHeader("Token");
            userRepository.findByToken(token);
        } catch(Exception e) {
            //User wasn't found therefore the user was not logged in.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized - log in to view");
        }
        return activityRepository.findAllByUserId(profileId);
    }
}
