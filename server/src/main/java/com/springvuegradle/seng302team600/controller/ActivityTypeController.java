package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.ActivityTypeProperties;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    private ActivityTypeRepository activityTypeRepository;

    public ActivityTypeController(ActivityTypeRepository activityTypeRepository) {
        this.activityTypeRepository = activityTypeRepository;
    }

    /**
     * Gets the list of activity types
     * @return list of activity types
     */
    @GetMapping("activity-types")
    public List<ActivityType> getAllActivityTypes() {

        return activityTypeRepository.findAll();
    }

}
