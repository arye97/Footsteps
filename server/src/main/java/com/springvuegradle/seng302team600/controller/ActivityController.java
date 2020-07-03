package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    /**
     * Gets the list of activity types
     * @return list of activity types
     */
    @GetMapping("activity-types")
    public List<ActivityType> getAllActivityTypes() {
        List<ActivityType> activityTypes = Arrays.asList(ActivityType.values());

        // Sorts the list of Enums
        Collections.sort(activityTypes, new Comparator<ActivityType>() {
            @Override
            public int compare(ActivityType o1, ActivityType o2) {
                return o1.getHumanReadable().toLowerCase().compareTo(o2.getHumanReadable().toLowerCase());
            }
        });
        return activityTypes;
    }

}
