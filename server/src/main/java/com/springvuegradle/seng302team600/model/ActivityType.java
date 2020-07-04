package com.springvuegradle.seng302team600.model;

import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
public class ActivityType {

    private final String name;

    ActivityType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}