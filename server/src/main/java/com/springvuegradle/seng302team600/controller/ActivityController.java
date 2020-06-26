package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller to manage activities and activity type
 */
@RestController
public class ActivityController {


    //******* Place holder for ActivityType Enum - remove when implemented ********
    public enum ActivityType {
        @JsonProperty("Swimming with Elephants")
        SWIMMING_WITH_ELEPHANTS,
        @JsonProperty("Running")
        RUNNING
    }
    //******************************************************************************

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
        return List.of(ActivityType.values());
    }

}
