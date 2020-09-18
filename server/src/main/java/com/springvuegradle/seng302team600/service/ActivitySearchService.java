package com.springvuegradle.seng302team600.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class ActivitySearchService {

    /**
     * This function handles the case when a search query needs to have an
     * excluded term separated from the main body, which is denoted by a '-' character in the search string
     * @param searchString the string query to parse
     * @return a list of two strings, one is the query and one is the term to be excluded from the search
     */
    public static List<String> handleMinusSpecialCaseString(String searchString) {

        String searchQuery;
        String exclusions = "%";
        List<String> queries = new ArrayList<>();

        List<String> allKeywords = Arrays.asList(searchString.split("-"));
        List<String> exclusionKeywords = new ArrayList<>();

        searchQuery = allKeywords.get(0);
        searchQuery = searchQuery.trim();

        exclusionKeywords.addAll(allKeywords);
        exclusionKeywords.remove(0);

        for (String exclusion : exclusionKeywords) {
            exclusion = exclusion.trim();
            exclusions = String.format("%s%s%s", exclusions, exclusion, "%");
        }

        List<String> searchQueryList = Arrays.asList(searchQuery.split(" "));
        searchQuery = "%";

        for (String searchWord : searchQueryList) {
            searchQuery = String.format("%s%s%s", searchQuery, searchWord, "%");
        }

        if (searchQuery.equals("%")) {
            searchQuery = "";
        }

        if (exclusions.equals("%")) {
            searchQuery = "";
        }

        queries.add(searchQuery);
        queries.add(exclusions);

        return queries;
    }

    /**
     * This function handles the case when multiple separate search queries are to be linked together
     * which is denoted by the inclusion of a '+' character in the search string
     * @param searchString the string query to parse
     * @return a list of two strings, one is the query and one is the term to be excluded from the search
     */
    public static String handlePlusSpecialCaseString(String searchString) {
        List<String> name = Arrays.asList(searchString.split("\\+"));
        List<String> queries = new ArrayList<>();
        String newQuery = "%";
        for (String terms : name) {
            terms = terms.trim();
            List<String> totalTerms = Arrays.asList(terms.split(" "));
            for (String term : totalTerms) {
                term = term.trim();
                newQuery = String.format("%s%s%s", newQuery, term, "%");
            }
        }
        System.out.println(newQuery);
        return newQuery;
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
                List<String> searchTerms =  Arrays.asList(searchString.split("%20")); //underscore is our space char
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
