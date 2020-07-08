package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.Utilities.ActivityValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.service.ActivityTypeService;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller to manage activities and activity type
 */
@RestController
public class ActivityController {

    private ActivityRepository activityRepository;
    private UserValidationService userValidationService;
    private ActivityTypeService activityTypeService;

    public ActivityController(ActivityRepository activityRepository, UserValidationService userValidationService, ActivityTypeService activityTypeService) {
        this.activityRepository = activityRepository;
        this.userValidationService = userValidationService;
        this.activityTypeService = activityTypeService;
    }

    @PostMapping("/profiles/{profileId}/activities")
    public void newActivity(@Validated @RequestBody Activity newActivity,
                            HttpServletResponse response,
                            @PathVariable(value = "profileId") Long profileId) {

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

    @GetMapping("/activities/{activityId}")
    public Activity findUserActivity(@PathVariable Long activityId) {
        Activity activity = activityRepository.findByActivityId(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid activity id");
        }
        return activity;
    }

    @GetMapping("/activities")
    public List<Activity> findAllActivities() {
        List<Activity> allActivities = activityRepository.findAll();
        return allActivities;
    }

    @PutMapping("/activities/{activityId}")
    public void editActivity(@PathVariable Long activityId, HttpServletRequest request,
                             HttpServletResponse response, @RequestBody String jsonActivityEditString) throws IOException {
        String token = request.getHeader("Token"); //this is the users token
        Activity activity = activityRepository.findByActivityId(activityId);

        //ResponseStatusException thrown if user unauthorized or forbidden from accessing requested user
        ObjectMapper nodeMapper = new ObjectMapper();
        ObjectNode editedData = nodeMapper.readValue(jsonActivityEditString, ObjectNode.class);

        //Get the fields to be updated from editedData object
        String newDescription = editedData.get("description").asText();
        String newLocation = editedData.get("location").asText();
        String newName = editedData.get("activity_name").asText();
        String checkContinuous = editedData.get("continuous").asText();
        Boolean newContinuous = editedData.get("continuous").asBoolean();
        String newStartDate = editedData.get("start_date").asText();
        String newEndDate = editedData.get("end_date").asText();
        String newActivityTypes = editedData.get("activity_type").asText();
        //Check if any have changed or are null
        if (newDescription != null) { activity.setDescription(newDescription); }
        if (newLocation != null) {activity.setLocation(newLocation);}
        if (newName != null) { activity.setName(newName); }
        if (checkContinuous != null) { activity.setContinuous(newContinuous);}
//        if (newStartDate != null) { Date date = new Date(); date.getDate()newStartDate; activity.setStartTime(date);}
//        if (newEndDate != null) {}
//        if (newActivityTypes != null) {
//            activity.setActivityTypes(
//                activityTypeService.getMatchingEntitiesFromRepository(newActivityTypes)
//            );
//
//        }
    }

    @DeleteMapping("/activities/{activityId}")
    public void deleteActivity(@PathVariable Long activityId, HttpServletRequest request) {
        //Check the activity is this specific users activity
        Activity activity = activityRepository.findByActivityId(activityId);
//        Long authorId = activity.getCreatorUserId();
//        String token = request.getHeader("Token");
//        User user = userValidationService.findByToken(token); //finds user and validates they exist
//        if (!userValidationService.hasAdminPrivileges(user)) {
//            /* Only run this check if the user is NOT an Admin,
//               otherwise admins can delete/edit others activities.
//             */
//            if (!authorId.equals(user.getUserId())) {
//                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not activity creator");
//            }
//        }
//        if (activity == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid activity id");
//        }
        //Delete the activity
        activityRepository.delete(activity);

    }
}
