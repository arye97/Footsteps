package com.springvuegradle.seng302team600.service;

import java.util.List;

public class ActivitySearchParsed {
    private List<String> inclusions;
    private List<String> exclusions;
    private List<String> general;

    public ActivitySearchParsed(List<String> inclusions, List<String> exclusions, List<String> general) {
        this.exclusions = exclusions;
        this.general = general;
        this.inclusions = inclusions;
    }

    public List<String> getExclusions() {
        return exclusions;
    }

    public List<String> getGeneral() {
        return general;
    }

    public List<String> getInclusions() {
        return inclusions;
    }
}
