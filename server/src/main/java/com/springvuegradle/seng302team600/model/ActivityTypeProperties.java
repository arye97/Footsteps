package com.springvuegradle.seng302team600.model;

import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * A class to retrieves a List of ActivityTypes from activity_types.yml.
 * Populates the activity_type table on application start.
 */
@Component
@ConfigurationProperties("static")
public class ActivityTypeProperties implements CommandLineRunner {

    @Autowired
    private ActivityTypeRepository activityTypeRepository;

    private final List<ActivityType> activityTypes;

    /** Compares ActivityTypes alphabetically, case insensitive */
    private static final Comparator<ActivityType> alphaComparator = Comparator.comparing(o -> o.getName().toLowerCase());


    public ActivityTypeProperties(List<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public List<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public List<ActivityType> getSortedActivityTypes() {
        activityTypes.sort(alphaComparator);
        return activityTypes;
    }


    /**
     * Populates the activity_type table on application start.  Only adds an ActivityType if it doesn't already exist.
     */
    @Override
    public void run(String...args) {
        // Should find a better way to do this, like making ActivityType.name a primary key?
        Set<ActivityType> activityTypesInRepo = new HashSet<>(activityTypeRepository.findAll());
        for (ActivityType activityType: getSortedActivityTypes()) {
            if (!activityTypesInRepo.contains(activityType)) {
                activityTypeRepository.save(activityType);
            }
        }
    }

}