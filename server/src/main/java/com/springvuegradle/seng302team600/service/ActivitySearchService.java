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
    private static List<String> fetchLeadingCharacter(String searchString, String regex, String remove) {
        List<String> terms = new ArrayList<>();
        List<String> allKeywords = Arrays.asList(searchString.split(regex));
        List<String> termKeywords = new ArrayList<>();

        termKeywords.addAll(allKeywords);
        termKeywords.remove(0);

        for (String term : termKeywords) {
            term = term.trim();
            List<String> temp = Arrays.asList(term.split(remove));
            if (temp.size() == 0 || remove.isEmpty()) {
                terms.add(Arrays.asList(term.split(" ")).get(0));
            }
        }
        return terms;
    }

    public static ActivitySearchParsed parseSearchQuery(String query) {
        List<String> inclusions = fetchLeadingCharacter(query, "\\+", "");
        List<String> exclusions = fetchLeadingCharacter(query, "-", "");
        List<String> general    = fetchLeadingCharacter(" " + query, " ", "[^-\\+]");
        ActivitySearchParsed response = new ActivitySearchParsed(inclusions, exclusions, general);
        System.out.println(inclusions);
        System.out.println(exclusions);
        System.out.println(general);
        return response;
    }
}
