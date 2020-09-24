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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
     *
     * @param pageNumber     the page number, starting at zero
     * @param strCoordinates a string to be converted into a Coordinates object containing latitude and longitude
     * @param activityTypes  a list of activity types
     * @param cutoffDistance the max distance to search by
     * @param method         the type of activity type filtering
     * @param blockSize      the size of the blocks for the repo to page by
     * @return A list of activity activities
     */
    public Slice<Activity> getActivitiesByLocation(String strCoordinates, String activityTypes,
                                                   Double cutoffDistance, String method,
                                                   int blockSize, int pageNumber)
            throws JsonProcessingException {
        if (pageNumber < 0) {
            pageNumber = 0;
        }
        Pageable activitiesBlock = PageRequest.of(pageNumber, blockSize);

        Coordinates coordinates = validateCoordinates(strCoordinates);

        if (cutoffDistance < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cutoff distance must be 0 or greater");
        } else  if (cutoffDistance >= MAX_CUTOFF_DISTANCE) {
            cutoffDistance = MAX_DISTANCE;
        }

        Slice<Activity> paginatedActivities;
        if (activityTypes.length() >= 1) {
            List<Long> activityTypeIds = getActivityTypeIds(activityTypes);
            paginatedActivities = getPaginatedActivities(method, coordinates,
                    cutoffDistance, activityTypeIds, activitiesBlock);
        } else {
            paginatedActivities = activityRepository.findAllWithinDistance(
                    coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance, activitiesBlock);
        }
        return paginatedActivities;
    }

    /**
     * Parses the string coordinates to a Coordinate object and validates the coordinates
     * @param strCoordinates    The string representation of the coordinates from the request
     * @return coordinates      The Coordinate object made from the string
     * @throws JsonProcessingException
     */
    private Coordinates validateCoordinates(String strCoordinates) throws JsonProcessingException {
        Coordinates coordinates = new ObjectMapper().readValue(strCoordinates, Coordinates.class);
        if (coordinates.getLatitude() > 90 || coordinates.getLatitude() < -90) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Latitude must exist (be between -90 and 90 degrees)");
        }
        if (coordinates.getLongitude() > 180 || coordinates.getLongitude() < -180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Longitude must exist (be between -180 and 180 degrees)");
        }
        return coordinates;
    }

    /**
     * Retrieves the list of activity type ids from the repo
     * @param activityTypes     A space delimited string of the activity types
     * @return activityTypeIds  The list of activity type ids for the provided activity types
     */
    private List<Long> getActivityTypeIds(String activityTypes) {
        List<String> typesWithDashes = Arrays.asList(activityTypes.split(" "));
        List<String> types = typesWithDashes.stream()
                .map(a -> a.replace('-', ' '))
                .collect(Collectors.toList());
        List<Long> activityTypeIds = activityTypeRepository.findActivityTypeIdsByNames(types);

        return activityTypeIds;
    }

    /**
     * Retrieves a paginated slice of activities from the repo
     * Uses slightly differing repo functions depending on method
     * @param method            Method of search (strictly "and" or "or"
     * @param coordinates       Coordinates to center the search on
     * @param cutoffDistance    Radius of search area
     * @param activityTypeIds   Ids of activity types to search for
     * @param activitiesBlock   Pageable object containing the page number and max items per page
     * @return paginatedActivities  Paginated Slice of activities retried based on search parameters
     */
    private Slice<Activity> getPaginatedActivities(String method, Coordinates coordinates, Double cutoffDistance,
                           List<Long> activityTypeIds, Pageable activitiesBlock) {
        Slice<Activity> paginatedActivities;
        if (method.equalsIgnoreCase("and")) {
            int numActivityTypes = activityTypeIds.size();
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
        return paginatedActivities;
    }

    /**
     * Takes some properties to search for activities by location.
     * Gets the length of the search results.
     *
     * @param strCoordinates a string to be converted into a Coordinates object containing latitude and longitude
     * @param activityTypes  a list of activity types
     * @param cutoffDistance the max distance to search by
     * @param method         the type of activity type filtering
     * @return the count of activities searched for
     * @throws JsonProcessingException thrown if error occurs when converting strCoordinates to coordinates
     */
    public int getRowsForActivityByLocation(String strCoordinates, String activityTypes,
                                 Double cutoffDistance, String method) throws JsonProcessingException {
        Coordinates coordinates = validateCoordinates(strCoordinates);
        if (cutoffDistance >= MAX_CUTOFF_DISTANCE) {
            cutoffDistance = MAX_DISTANCE;
        }
        Integer numberOfRows;
        if (activityTypes.length() >= 1) {
            List<String> typesWithDashes = Arrays.asList(activityTypes.split(" "));
            List<String> types = typesWithDashes.stream()
                    .map(a -> a.replace('-', ' '))
                    .collect(Collectors.toList());
            List<Long> activityTypeIds = activityTypeRepository.findActivityTypeIdsByNames(types);
            int numActivityTypes = activityTypeIds.size();
            if (method.equalsIgnoreCase("and")) {
                numberOfRows = activityRepository.countAllWithinDistanceByAllActivityTypeIds(
                        coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance,
                        activityTypeIds, numActivityTypes);
            } else if (method.equalsIgnoreCase("or")) {
                numberOfRows = activityRepository.countAllWithinDistanceBySomeActivityTypeIds(
                        coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance,
                        activityTypeIds);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Method must be specified as either (AND, OR)");
            }
        } else {
            numberOfRows = activityRepository.countAllWithinDistance(
                    coordinates.getLatitude(), coordinates.getLongitude(), cutoffDistance);
        }
        // Empty result list
        return Objects.requireNonNullElse(numberOfRows, 0);
    }
}
