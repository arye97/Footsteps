package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.service.ActivityTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller to manage activities and activity type
 */
@RestController
public class ActivityController {

    private ActivityRepository activityRepository;
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
}
