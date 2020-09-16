package com.springvuegradle.seng302team600.service;


import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.Pin;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.ActivityResponse;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service("activityPinService")
public class ActivityPinService {

    private final ActivityRepository activityRepository;

    private static final int BLOCK_SIZE = 20;

    public ActivityPinService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * Takes a list of activities and returns geo-map pins for the specified activities.
     *
     * @param activityList list of activities to derive pins from
     * @return a list of activity pins
     */
    public List<Pin> getPins(User user, List<Activity> activityList) {
        List<Pin> pins;
        pins = activitiesToPins(activityList, user.getUserId());
        return pins;
    }

    /**
     * Obtains a paginated block of activities associated with a user.
     *
     * @param user creator/follower of activities
     * @param pageNumber page number requested
     * @return a paginated block of activities
     */
    public Slice<Activity> getPaginatedActivityList(User user, int pageNumber) {
//        if (user.getUserId() == null) {
//            return new ArrayList<>();
//        }
        Slice<Activity> paginatedBlockOfActivities;
        Pageable blockWithFiveActivities = PageRequest.of(pageNumber, BLOCK_SIZE);
        paginatedBlockOfActivities = activityRepository.findAllByUserId(user.getUserId(), blockWithFiveActivities);
        if (paginatedBlockOfActivities == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No activities have been found for this user");
        }
        return paginatedBlockOfActivities;
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
