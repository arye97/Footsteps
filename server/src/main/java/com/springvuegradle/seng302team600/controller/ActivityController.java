package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Controller to manage activities and activity type
 */
@RestController
public class ActivityController {

    @Autowired
    private UserValidationService userService;

    @Autowired
    private ActivityRepository activityRepository;


    @PostMapping("/profiles/{profileId}/activities")
    public void newActivity(@Validated @RequestBody Activity newActivity,
                            HttpServletResponse response,
                            @PathVariable(value = "profileId") Long profileId) {

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
