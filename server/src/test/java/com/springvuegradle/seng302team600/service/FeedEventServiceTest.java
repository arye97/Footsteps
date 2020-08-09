package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.FeedEvent;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.FeedEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest(FeedEventService.class)
public class FeedEventServiceTest {

    @MockBean
    private FeedEventRepository feedEventRepository;

    @Autowired
    private FeedEventService feedEventService;

    private static final Long USER_ID_1 = 1L;
    private static final Long USER_ID_2 = 2L;
    private User dummyCreator;
    private User dummyParticipant;
    private static final Long ACTIVITY_ID_1 = 1L;
    private Activity dummyActivity;
    private Long nextFeedEventId;
    private List<FeedEvent> feedEventTable;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        dummyCreator = new User();
        ReflectionTestUtils.setField(dummyCreator, "userId", USER_ID_1);
        dummyParticipant = new User();
        ReflectionTestUtils.setField(dummyParticipant, "userId", USER_ID_2);

        // Activity starts with no participants, tests will add them if needed.
        dummyActivity = new Activity();
        ReflectionTestUtils.setField(dummyActivity, "activityId", ACTIVITY_ID_1);
        dummyActivity.setCreatorUserId(dummyCreator.getUserId());
        dummyActivity.setParticipants(new HashSet<>());

        nextFeedEventId = 1L;
        feedEventTable = new ArrayList<>();

        // Mocking FeedEventRepository
        when(feedEventRepository.save(Mockito.any())).then(i -> {
            FeedEvent feedEvent = i.getArgument(0);
            ReflectionTestUtils.setField(feedEvent, "feedEventId", nextFeedEventId);
            nextFeedEventId++;
            feedEventTable.add(feedEvent);
            return feedEvent;
        });
    }

    @Test
    public void createModifyFeedEventWithNoParticipants() {
        assertEquals(0, feedEventTable.size());
        feedEventService.modifyActivityEvent(dummyActivity, USER_ID_1);
        assertEquals(1, feedEventTable.size());
        assertEquals(ACTIVITY_ID_1, feedEventTable.get(0).getActivityId());
        assertEquals(USER_ID_1, feedEventTable.get(0).getAuthorId());
        assertEquals(USER_ID_1, feedEventTable.get(0).getViewerId());
    }

    @Test
    public void createModifyFeedEventWithParticipants() {
        assertEquals(0, feedEventTable.size());
        // Adding participant to dummy activity
        //dummyActivity.addP
        feedEventService.modifyActivityEvent(dummyActivity, USER_ID_1);
        assertEquals(1, feedEventTable.size());
        assertEquals(ACTIVITY_ID_1, feedEventTable.get(0).getActivityId());
        assertEquals(USER_ID_1, feedEventTable.get(0).getAuthorId());
        assertEquals(USER_ID_1, feedEventTable.get(0).getViewerId());
    }
}
