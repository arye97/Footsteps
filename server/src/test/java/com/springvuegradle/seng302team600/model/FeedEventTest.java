package com.springvuegradle.seng302team600.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FeedEventTest {

    private FeedEvent feedEventTest;

    @BeforeEach
    void setup() {
        feedEventTest = new FeedEvent();
    }

    @Test
    void setTimeStampNowIsNow() {
        feedEventTest.setTimeStampNow();
        assertEquals(new Date(), feedEventTest.getTimeStamp());
    }
}
