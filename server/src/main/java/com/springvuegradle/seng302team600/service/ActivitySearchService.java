package com.springvuegradle.seng302team600.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class ActivitySearchService {

    /**
     * This function handles the case when multiple separate search queries are to be linked together
     * which is denoted by the inclusion of a 'AND' operator in the search string
     * @param searchString the string query to parse
     * @return a list of two strings, one is the query and one is the term to be excluded from the search
     */
    public static List<String> handleANDSpecialCaseString(String searchString) {
        List<String> name = Arrays.asList(searchString.split("AND"));
        String newQuery = "%";
        List<String> queryTerms = new ArrayList<>();
        for (String terms : name) {
            terms = terms.trim();
            String newTerm = "%";
            newTerm = newTerm + terms + "%";
            queryTerms.add(newTerm);
        }
        return queryTerms;
    }

    /**
     * This function handles generic searches, and exact string matching.
     * Exact matches are denoted by double quotes encompassing the ENTIRE query, not just a single word/term
     * @param searchString the string query to parse
     * @return a query string to be used in an SQL query
     */
    public static String getSearchQuery(String searchString) {
        if (searchString.startsWith("\"") && searchString.endsWith("\"")){
            //then the user has chosen exact match!
            searchString = searchString.substring(1, searchString.length() - 1);
            if (searchString.contains("%20")) {
                List<String> searchTerms =  Arrays.asList(searchString.split(" "));
                searchString = "";
                for (String term : searchTerms) {
                    searchString = String.format("%s%s%s", searchString, term, " ");
                }
                searchString = searchString.trim();
            }
        } else {
            String newQuery = "%";
            List<String> searchTerms =  Arrays.asList(searchString.split(" "));
            for (String term : searchTerms) {
                newQuery = String.format("%s%s%s", newQuery, term, "%");
            }
            searchString = newQuery;
        }
        return searchString;
    }
}
