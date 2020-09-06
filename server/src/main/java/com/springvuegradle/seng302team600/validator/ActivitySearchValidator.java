package com.springvuegradle.seng302team600.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class ActivitySearchValidator {

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

    public static List<String> handlePlusSpecialCaseString(String searchString) {
        List<String> name = Arrays.asList(searchString.split("plus"));
        List<String> queries = new ArrayList<>();
        for (String terms : name) {
            List<String> total_terms = Arrays.asList(terms.split(" "));
            String newQuery = "";
            for (String term : total_terms) {
                newQuery = String.format("%s%s%s", newQuery, term, "%");
            }
            queries.add(newQuery);
        }
        return queries;
    }

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
            String newQuery = "";
            List<String> searchTerms =  Arrays.asList(searchString.split(" "));
            for (String term : searchTerms) {
                newQuery = String.format("%s%s%s", newQuery, term, "%");
            }
            searchString = newQuery;
        }
        return searchString;
    }
}
