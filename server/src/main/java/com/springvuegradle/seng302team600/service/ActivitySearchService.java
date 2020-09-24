package com.springvuegradle.seng302team600.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivitySearchService {

    public static List<String> extractExactMatches(String searchString) {
        List<String> result = new ArrayList<>();
        Pattern p = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
        Matcher m = p.matcher(searchString);
        while (m.find()) {
            String matched = m.group(1);
            if (matched.equalsIgnoreCase("OR")) {
                result.add("\"+\"");
            } else if (matched.equalsIgnoreCase("AND")) {
                //Pass, no need for this since it is assumed that blank space is an AND
            } else if (matched.startsWith("\"")) {
                result.add(matched.substring(1, matched.length() - 1));
            } else {
                result.add(matched);
            }
        }
        return result;
    }
}
