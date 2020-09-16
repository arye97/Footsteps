package com.springvuegradle.seng302team600.service;


import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.Pin;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("activityPinService")
public class ActivityPinService {

    private final ActivityRepository activityRepository;

    public ActivityPinService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * Finds activity geo-map pins relevant to this specific user and also the users geo-map pin
     *
     * @param user the user to gather pin data from
     * @return a list of pins with the user's pin at the very front of the list followed by all activity pins
     */
    public List<Pin> getPins(User user) {
        if (user.getUserId() == null) {
            return new ArrayList<>();
        }
        List<Activity> activityList = activityRepository.findAllByUserId(user.getUserId());
        List<Pin> pins;
        pins = activitiesToPins(activityList, user.getUserId());
        pins.add(0, new Pin(user));
        return pins;
    }

    /**
     * Converts a list of activity object into a list of Pin objects
     * for use with location pins on a front end map
     *
     * @param activityList the list of activities which include locations
     * @param userId       the id of the current logged in user
     * @return a list of map pins
     */
    private List<Pin> activitiesToPins(List<Activity> activityList, Long userId) {
        List<Pin> pinList = new ArrayList<>();
        for (Activity activity : activityList) {
            pinList.add(new Pin(activity, userId));
        }
        return pinList;
    }

}
