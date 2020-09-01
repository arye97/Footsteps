package com.springvuegradle.seng302team600.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeedEventTest {

    private FeedEvent feedEventTest;

    @BeforeEach
    void setup() {
        feedEventTest = new FeedEvent();
    }

    @Test
    void setTimeStampNowIsNow() {
        feedEventTest.setTimeStampNow();
        Long time1 = feedEventTest.getTimeStamp().getTime();
        Long time2 = new Date().getTime();
        assertTrue(Math.abs(time1 - time2) < 1000);
    }
}
