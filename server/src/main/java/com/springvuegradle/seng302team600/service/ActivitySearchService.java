package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.Location;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.ActivityResponse;

import java.util.*;

public class ActivitySearchService {

    /**
     * This function handles the case when a search query needs to have an
     * excluded term seperated from the main body, which is denoted by a '-' character in the search string
     *
     * @param searchString the string query to parse
     * @return a list of two strings, one is the query and one is the term to be excluded from the search
     */
    public static List<String> handleMinusSpecialCaseString(String searchString) {
        String searchQuery = "%";
        String exclusion = "%";
        List<String> queries = new ArrayList<>();
        List<String> name = Arrays.asList(searchString.split(" "));
        int i = 0;
        while (!name.get(i).equals("-")) {
            searchQuery = String.format("%s%s%s", searchQuery, name.get(i), "%");
            i++;
        }
        i++; //get past the - sign
        while (name.size() > i) {
            exclusion = String.format("%s%s%s", exclusion, name.get(i), "%");
            i++;
        }
        queries.add(searchQuery);
        queries.add(exclusion);
        return queries;
    }

    /**
     * This function handles the case when multiple separate search queries are to be linked together
     * which is denoted by the inclusion of a '+' character in the search string
     *
     * @param searchString the string query to parse
     * @return a list of two strings, one is the query and one is the term to be excluded from the search
     */
    public static List<String> handlePlusSpecialCaseString(String searchString) {
        List<String> name = Arrays.asList(searchString.split("\\+"));
        List<String> queries = new ArrayList<>();
        for (String terms : name) {
            List<String> totalTerms = Arrays.asList(terms.split(" "));
            String newQuery = "";
            for (String term : totalTerms) {
                newQuery = String.format("%s%s%s", newQuery, term, "%");
            }
            queries.add(newQuery);
        }
        return queries;
    }

    /**
     * This function handles generic searches, and exact string matching.
     * Exact matches are denoted by double quotes encompassing the ENTIRE query, not just a single word/term
     *
     * @param searchString the string query to parse
     * @return a query string to be used in an SQL query
     */
    public static String getSearchQuery(String searchString) {
        if (searchString.startsWith("\"") && searchString.endsWith("\"")) {
            //then the user has chosen exact match!
            searchString = searchString.substring(1, searchString.length() - 1);
            if (searchString.contains("%20")) {
                List<String> searchTerms = Arrays.asList(searchString.split("%20")); //underscore is our space char
                searchString = "";
                for (String term : searchTerms) {
                    searchString = String.format("%s%s%s", searchString, term, " ");
                }
                searchString = searchString.trim();
            }
        } else {
            String newQuery = "%";
            List<String> searchTerms = Arrays.asList(searchString.split(" "));
            for (String term : searchTerms) {
                newQuery = String.format("%s%s%s", newQuery, term, "%");
            }
            searchString = newQuery;
        }
        return searchString;
    }

    /**
     * Finds the euclidean distance between the user and the activity to be checked
     *
     * @param activityLocation the lat and long coordinates of the activity
     * @param userLocation     the lat and long coordinates of the user
     * @return the euclidean distance between the activity and the user
     */
    private static double getEuclideanDistance(Location activityLocation, Location userLocation) {
        double activityLat = activityLocation.getLatitude();
        double userLat = userLocation.getLatitude();
        double activityLong = activityLocation.getLongitude();
        double userLong = userLocation.getLongitude();

        return Math.sqrt(
                (userLat - activityLat) * (userLat - activityLat) + (userLong - activityLong) * (userLong - activityLong));
    }

    /**
     * Sorts the activities by their euclidean distance to the user
     *
     * Note: This function is O(n2), if there is time, revise this to be
     * at most O(nlogn) time complexity.
     *
     * @param activities the activities to sort through
     * @param user       the user whos distance we are measuring from
     * @return the sorted list of activities
     */
    public static List<Activity> sortActivitiesByNearestLocationToUser(
            List<Activity> activities, User user) {

        Location userLocation = user.getPrivateLocation();
        if (userLocation == null) {
            userLocation = user.getPublicLocation();
            if (userLocation == null) {
                return activities;
            }
        }

        List<Activity> closeActivities = new ArrayList<>();
        double minDistance = Double.POSITIVE_INFINITY;
        for (Activity activity : activities) {
            Location activityLocation = activity.getLocation();
            if (activityLocation == null) {
                //then cant compare so just add it to the end
                closeActivities.add(activity);
            } else {
                double distance = getEuclideanDistance(activityLocation, userLocation);
                if (distance < minDistance) {
                    minDistance = distance;
                }
                if ((closeActivities.isEmpty())) {
                    closeActivities.add(activity);
                } else {
                    for (int i = 0; i < closeActivities.size(); i++) {
                        if (closeActivities.get(i).getLocation() == null) {
                            closeActivities.add(i, activity);
                        } else {
                            distance = getEuclideanDistance(closeActivities.get(i).getLocation(), userLocation);
                            if (distance < minDistance) {
                                closeActivities.add(i, activity);
                            }
                        }
                    }
                }
            }

        }
        return closeActivities;
    }
}
