package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.FeedEvent;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.FeedEventRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
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
    @MockBean
    private UserRepository userRepository;

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
    private final String outcomeTitle1 = "Run 10 km";
    private final String outcomeTitle2 = "Checkpoints";

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

        // Mocking UserRepository
        when(userRepository.findByUserId(Mockito.anyLong())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyCreator.getUserId())) return dummyCreator;
            if (i.getArgument(0).equals(dummyParticipant.getUserId())) return dummyParticipant;
            else return null;
        });
    }

    @Test
    public void createModifyFeedEventWithoutParticipants() {
        assertEquals(0, feedEventTable.size());
        feedEventService.modifyActivityEvent(dummyActivity, USER_ID_1);
        assertEquals(1, feedEventTable.size());
        assertEquals(ACTIVITY_ID_1, feedEventTable.get(0).getActivityId());
        assertEquals(USER_ID_1, feedEventTable.get(0).getAuthorId());
        assertTrue(feedEventTable.get(0).getViewers().contains(dummyCreator));
    }

    @Test
    public void createModifyFeedEventWithParticipants() {
        assertEquals(0, feedEventTable.size());
        // Adding participant to dummy activity
        dummyActivity.addParticipant(dummyParticipant);
        feedEventService.modifyActivityEvent(dummyActivity, USER_ID_1);
        assertEquals(1, feedEventTable.size());
        FeedEvent event = feedEventTable.get(0);
        assertEquals(ACTIVITY_ID_1, event.getActivityId());
        assertEquals(USER_ID_1, event.getAuthorId());
        assertTrue(event.getViewers().contains(dummyCreator));
        assertTrue(event.getViewers().contains(dummyParticipant));
    }

    @Test
    public void createDeleteFeedEventWithoutParticipants() {
        assertEquals(0, feedEventTable.size());
        feedEventService.deleteActivityEvent(dummyActivity, USER_ID_1);
        assertEquals(1, feedEventTable.size());
        FeedEvent event = feedEventTable.get(0);
        assertEquals(ACTIVITY_ID_1, event.getActivityId());
        assertEquals(USER_ID_1, event.getAuthorId());
        assertTrue(event.getViewers().contains(dummyCreator));
    }

    @Test
    public void createDeleteFeedEventWithParticipants() {
        assertEquals(0, feedEventTable.size());
        // Adding participant to dummy activity
        dummyActivity.addParticipant(dummyParticipant);
        feedEventService.deleteActivityEvent(dummyActivity, USER_ID_1);
        assertEquals(1, feedEventTable.size());
        FeedEvent event = feedEventTable.get(0);
        assertEquals(ACTIVITY_ID_1, event.getActivityId());
        assertEquals(USER_ID_1, event.getAuthorId());
        assertTrue(event.getViewers().contains(dummyCreator));
        assertTrue(event.getViewers().contains(dummyParticipant));
    }

    @Test // Assuming creator can add their own results to an event
    public void createAddResultFeedEventByActivityCreator() {
        assertEquals(0, feedEventTable.size());
        feedEventService.addResultToActivityEvent(dummyActivity, USER_ID_1, outcomeTitle1);
        assertEquals(1, feedEventTable.size());
        FeedEvent event = feedEventTable.get(0);
        assertEquals(ACTIVITY_ID_1, event.getActivityId());
        assertEquals(USER_ID_1, event.getAuthorId());
        assertTrue(event.getViewers().contains(dummyCreator));
        assertEquals(outcomeTitle1, event.getOutcomeTitle());
    }

    @Test // Assuming creator can add their own results to an event
    public void createMultipleAddResultFeedEventByActivityCreator() {
        assertEquals(0, feedEventTable.size());
        feedEventService.addResultToActivityEvent(dummyActivity, USER_ID_1, outcomeTitle1);
        feedEventService.addResultToActivityEvent(dummyActivity, USER_ID_1, outcomeTitle2);
        assertEquals(2, feedEventTable.size());

        FeedEvent event = feedEventTable.get(0);
        assertEquals(ACTIVITY_ID_1, event.getActivityId());
        assertEquals(USER_ID_1, event.getAuthorId());
        assertTrue(event.getViewers().contains(dummyCreator));
        assertEquals(outcomeTitle1, event.getOutcomeTitle());

        event = feedEventTable.get(1);
        assertEquals(ACTIVITY_ID_1, event.getActivityId());
        assertEquals(USER_ID_1, event.getAuthorId());
        assertTrue(event.getViewers().contains(dummyCreator));
        assertEquals(outcomeTitle2, event.getOutcomeTitle());
    }

    @Test
    public void createAddResultFeedEventByActivityParticipant() {
        assertEquals(0, feedEventTable.size());
        // Adding participant to dummy activity
        dummyActivity.addParticipant(dummyParticipant);
        feedEventService.addResultToActivityEvent(dummyActivity, USER_ID_1, outcomeTitle1);
        assertEquals(1, feedEventTable.size());
        FeedEvent event = feedEventTable.get(0);
        assertEquals(ACTIVITY_ID_1, event.getActivityId());
        assertEquals(USER_ID_1, event.getAuthorId());
        assertTrue(event.getViewers().contains(dummyCreator));
        assertTrue(event.getViewers().contains(dummyParticipant));
        assertEquals(outcomeTitle1, event.getOutcomeTitle());
    }
}
