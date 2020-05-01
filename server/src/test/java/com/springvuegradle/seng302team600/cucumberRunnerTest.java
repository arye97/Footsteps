package com.springvuegradle.seng302team600;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty"}, // How to format test report, "pretty" is good for human eyes
        glue = {"com.springvuegradle.seng302team600.cucumberSteps"}, // Where to look for your tests' steps
        features = {"classpath:/featureFiles/"}, // Where to look for your features
        strict = true // Causes cucumber to fail if any step definitions are still undefined
)
public class cucumberRunnerTest { }