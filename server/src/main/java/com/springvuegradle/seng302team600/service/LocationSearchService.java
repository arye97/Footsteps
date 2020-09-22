package com.springvuegradle.seng302team600.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.payload.request.Coordinates;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("locationSearchService")
public class LocationSearchService {

    private static final Double MAX_CUTOFF_DISTANCE = 10000.0;  // Max distance from front end sliders
    private static final Double MAX_DISTANCE = 45000.0;         // If above the max distance search entire world, distance of 45000 covers whole world

    private final ActivityTypeRepository activityTypeRepository;
    private final ActivityRepository activityRepository;

    public LocationSearchService(ActivityTypeRepository activityTypeRepository, ActivityRepository activityRepository) {
        this.activityTypeRepository = activityTypeRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Takes some properties to search for activities by location.
     * Pagination is preformed on the repo in blocks/pages of blockSize.
     * @param request the http request
     * @param strCoordinates a string to be converted into a Coordinates object containing latitude and longitude
     * @param activityTypes a list of activity types
     * @param cutoffDistance the max distance to search by
     * @param method the type of activity type filtering
     * @param blockSize the size of the blocks for the repo to page by
     * @return A list of activity activities
     */
    public Slice<Activity> getLocationsHelper(String strCoordinates, String activityTypes,
                                              Double cutoffDistance, String method,
                                              int blockSize, HttpServletRequest request)
            throws JsonProcessingException {
        Coordinates coordinates = new ObjectMapper().readValue(strCoordinates, Coordinates.class);
        int pageNumber;
        try {
            pageNumber = request.getIntHeader("Page-Number");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Page-Number must be an integer");
        }
        if (pageNumber < 0) {
            pageNumber = 0;
        }

        Pageable activitiesBlock = PageRequest.of(pageNumber, blockSize);
        if (coordinates.getLatitude() > 90 || coordinates.getLatitude() < -90) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Latitude must exist (be between -90 and 90 degrees)");
        }
        if (coordinates.getLongitude() > 180 || coordinates.getLongitude() < -180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Longitude must exist (be between -180 and 180 degrees)");
        }
        if (cutoffDistance >= MAX_CUTOFF_DISTANCE) {
            cutoffDistance = MAX_DISTANCE;
        }
        Slice<Activity> paginatedActivities;
        if (activityTypes.length() >= 1) {
            List<String> typesWithDashes = Arrays.asList(activityTypes.split(" "));
            List<String> types = typesWithDashes.stream()
                    .map(a -> a.replace('-', ' '))
                    .collect(Collectors.toList());
            List<Long> activityTypeIds = activityTypeRepository.findActivityTypeIdsByNames(types);
            int numActivityTypes = activityTypeIds.size();
            if (method.equalsIgnoreCase("and")) {
                paginatedActivities = activityRepository.findAllWithinDistanceByAllActivityTypeIds(
                        coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance,
                        activityTypeIds, numActivityTypes, activitiesBlock);
            } else if (method.equalsIgnoreCase("or")) {
                paginatedActivities = activityRepository.findAllWithinDistanceBySomeActivityTypeIds(
                        coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance,
                        activityTypeIds, activitiesBlock);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Method must be specified as either (AND, OR)");
            }
        } else {
            paginatedActivities = activityRepository.findAllWithinDistance(
                    coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance, activitiesBlock);
        }
        return paginatedActivities;
    }
}
