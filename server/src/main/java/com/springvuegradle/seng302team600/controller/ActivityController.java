package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.springvuegradle.seng302team600.model.ActivityType;

import java.util.*;

/**
 * Controller to manage activities and activity type
 */
@RestController
public class ActivityController {

    @Autowired
    private UserValidationService userService;

    //Will be implemented in story 8 - remove this comment when repo created
//    @Autowired
//    private ActivityRepository activityRepository;

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
