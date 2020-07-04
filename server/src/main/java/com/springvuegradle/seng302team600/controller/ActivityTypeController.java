package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.ActivityTypeProperties;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.springvuegradle.seng302team600.model.ActivityType;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Controller to manage activity type
 */
@RestController
public class ActivityTypeController {

    private final ActivityTypeProperties activityTypeProperties;

    public ActivityTypeController(ActivityTypeProperties activityTypeProperties) {
        this.activityTypeProperties = activityTypeProperties;
    }

    //Will be implemented in story 8 - remove this comment when repo created
//    @Autowired
//    private ActivityRepository activityRepository;

    /**
     * Gets the list of activity types
     * @return list of activity types
     */
    @GetMapping("activity-types")
    public List<ActivityType> getAllActivityTypes() {

        return activityTypeProperties.getSortedActivityTypes();
    }

}
