package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.FeedEvent;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.IsFollowingResponse;
import com.springvuegradle.seng302team600.repository.ActivityParticipantRepository;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.FeedEventRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedEventController.class)
public class FeedEventControllerTest {

    @MockBean
    private UserAuthenticationService userAuthenticationService;
    @MockBean
    private FeedEventRepository feedEventRepository;
    @MockBean
    private ActivityRepository activityRepository;
    @MockBean
    private ActivityParticipantRepository activityParticipantRepository;
    @Autowired
    private MockMvc mvc;

    private static final Long USER_ID_1 = 1L;
    private static final Long USER_ID_2 = 2L;
    private User dummyUser1;
    private User dummyUser2;
    private final String validToken = "valid";
    private static final Long ACTIVITY_ID_1 = 1L;
    private Activity dummyActivity;
    private List<FeedEvent> feedEventTable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        dummyUser1 = new User();
        dummyUser2 = new User();
        dummyActivity = new Activity();
        feedEventTable = new ArrayList<>();

        // Mocking UserAuthenticationService
        when(userAuthenticationService.findByUserId(Mockito.any(String.class), Mockito.any(Long.class))).thenAnswer(i -> {
            String token = i.getArgument(0);
            Long id = i.getArgument(1);
            if (token.equals(validToken)) {
                if (id.equals(USER_ID_1)) {
                    return dummyUser1;
                } else if (id.equals(USER_ID_2)) {
                    return dummyUser2;
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
        });
        when(userAuthenticationService.hasAdminPrivileges(Mockito.any())).thenAnswer(i ->
                ((User) i.getArgument(0)).getRole() >= 10);

        // Mocking FeedEventRepository
        when(feedEventRepository.findByViewerIdOrderByTimeStamp(Mockito.anyLong())).thenAnswer(i -> {
            Long id = i.getArgument(0);
            List<FeedEvent> result = new ArrayList<>();
            for (FeedEvent feedEvent : feedEventTable) {
                if (feedEvent.getViewerId().equals(id)) {
                    result.add(feedEvent);
                }
            }
            return result.isEmpty() ? null : result;
        });

        // Mocking ActivityRepository
        when(activityRepository.findByActivityId(Mockito.anyLong())).thenAnswer(i -> {
            Long id = i.getArgument(0);
            if (id.equals(ACTIVITY_ID_1)) {
                return dummyActivity;
            } else {
                return null;
            }
        });
        // Mocking ActivityParticipantRepository
        when(activityParticipantRepository.existsByActivityIdAndUserId(Mockito.anyLong(),Mockito.anyLong())).thenAnswer(i -> {
            Long activityId = i.getArgument(0);
            Long userId = i.getArgument(1);
            if (activityId.equals(USER_ID_1)) {
                return true;
            } else {
                return false;
            }
        });
    }

    @Test
    public void userIsFollowing() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder userFollowingRequest = MockMvcRequestBuilders
                .get("/profiles/{profileId}/subscriptions/activities/{activityId}", USER_ID_1, ACTIVITY_ID_1)
                .header("Token", validToken);
        MvcResult result = mvc.perform(userFollowingRequest)
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        assertTrue(jsonNode.get("subscribed").asBoolean());
    }

    @Test
    public void userIsNotFollowing() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder userFollowingRequest = MockMvcRequestBuilders
                .get("/profiles/{profileId}/subscriptions/activities/{activityId}", USER_ID_2, ACTIVITY_ID_1)
                .header("Token", validToken);
        MvcResult result = mvc.perform(userFollowingRequest)
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        assertTrue(!jsonNode.get("subscribed").asBoolean());
    }

    @Test
    public void userIsFollowingInvalidToken() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder userFollowingRequest = MockMvcRequestBuilders
                .get("/profiles/{profileId}/subscriptions/activities/{activityId}", USER_ID_1, ACTIVITY_ID_1)
                .header("Token", "NotAValidToken");
        MvcResult result = mvc.perform(userFollowingRequest)
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        assertTrue(!jsonNode.get("subscribed").asBoolean());
    }

}
