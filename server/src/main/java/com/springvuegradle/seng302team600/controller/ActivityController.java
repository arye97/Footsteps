package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.service.ActivityTypeService;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.CascadeType;
import javax.persistence.OneToOne;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

/**
 * Controller to manage activities and activity type
 */
@RestController
public class ActivityController {

    private ActivityRepository activityRepository;
    @Autowired private UserValidationService userValidationService;
    private ActivityTypeService activityTypeService;

    public ActivityController(ActivityRepository activityRepository, ActivityTypeService activityTypeService) {
        this.activityRepository = activityRepository;
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
        activityRepository.save(newActivity);

        response.setStatus(HttpServletResponse.SC_CREATED); //201
    }

    @GetMapping("/activities/{activityId}")
    public Activity findUserActivity(@PathVariable Long activityId) {
        Activity activity = activityRepository.findByActivityId(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid activity id");
        }
        System.out.println(activity);
        return activity;
    }

    @GetMapping("/activities")
    public List<Activity> findAllActivities() {
        List<Activity> allActivities = activityRepository.findAll();
        return allActivities;
    }

    @Transactional
    @DeleteMapping("/activities/{activityId}")
    public void deleteActivity(@PathVariable Long activityId, HttpServletRequest request) {
        //Check the activity is this specific users activity
        Activity activity = activityRepository.findByActivityId(activityId);
        Long authorId = activity.getCreatorUserId();
        String token = request.getHeader("Token");
        User user = userValidationService.findByToken(token); //finds user and validates they exist
        if (!userValidationService.hasAdminPrivileges(user)) {
            /* Only run this check if the user is NOT an Admin,
               otherwise admins can delete/edit others activities.
             */
            if (!authorId.equals(user.getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not activity creator");
            }
        }
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid activity id");
        }
        //Delete the activity
        activityRepository.delete(activity);

    }
}
