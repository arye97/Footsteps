package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.model.UserRole;
import com.springvuegradle.seng302team600.payload.ActivityResponse;
import com.springvuegradle.seng302team600.payload.UserRegisterRequest;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.service.ActivityTypeService;
import com.springvuegradle.seng302team600.service.FeedEventService;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityController.class)
class ActivityControllerTest {

    @MockBean
    private FeedEventService feedEventService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private EmailRepository emailRepository;
    @MockBean
    private ActivityRepository activityRepository;
    @MockBean
    private UserAuthenticationService userAuthenticationService;
    @MockBean
    private ActivityTypeService activityTypeService;
    @Autowired
    private MockMvc mvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long DEFAULT_USER_ID_2 = 2L;
    private static final Long DEFAULT_USER_ID_3 = 3L;
    private static final Long DEFAULT_EMAIL_ID = 1L;
    private static final Long DEFAULT_ACTIVITY_ID = 1L;
    private static Long activityCount = 0L;

    private User dummyUser1;
    private User dummyUser2; // Used when a second user is required
    private User dummyUser3;
    private final String validToken = "valid";
    private final String forbiddenToken = "forbidden";
    private final String anotherValidToken = "alsovalid";
    private Set<Activity> activityMockTable = new HashSet<>();
    private Set<User> userMockTable = new HashSet<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        dummyUser1 = new User();
        dummyUser1.setToken(validToken);
        ReflectionTestUtils.setField(dummyUser1, "userId", DEFAULT_USER_ID);

        dummyUser2 = new User();
        dummyUser2.setToken(forbiddenToken);
        ReflectionTestUtils.setField(dummyUser2, "userId", DEFAULT_USER_ID_2);

        dummyUser3 = new User();
        dummyUser3.setToken(anotherValidToken);
        ReflectionTestUtils.setField(dummyUser3, "userId", DEFAULT_USER_ID_3);

        // Mocking ActivityTypeService
        when(activityTypeService.getMatchingEntitiesFromRepository(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        // Mocking userAuthenticationService
        when(userAuthenticationService.findByUserId(Mockito.any(String.class), Mockito.any(Long.class))).thenAnswer(i -> {
            String token = i.getArgument(0);
            Long userId = i.getArgument(1);
            switch (token) {
                case validToken:
                    if (userId.equals(DEFAULT_USER_ID)) {
                        return dummyUser1;
                    } else if (userId.equals(DEFAULT_USER_ID_2) || userId.equals(DEFAULT_USER_ID_3)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                    } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                    }
                case forbiddenToken:
                    if (userId.equals(DEFAULT_USER_ID_2)) {
                        return dummyUser2;
                    } else if (userId.equals(DEFAULT_USER_ID) || userId.equals(DEFAULT_USER_ID_3)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                    } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                    }
                case anotherValidToken:
                    if (userId.equals(DEFAULT_USER_ID_3)) {
                        return dummyUser3;
                    } else if (userId.equals(DEFAULT_USER_ID) || userId.equals(DEFAULT_USER_ID_2)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                    } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                    }
                default:
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
        });
        when(userAuthenticationService.hasAdminPrivileges(Mockito.any())).thenAnswer(i ->
                ((User) i.getArgument(0)).getRole() >= UserRole.ADMIN);
        when(userAuthenticationService.findByToken(Mockito.any())).thenAnswer(i -> {
            String token = i.getArgument(0);
            if (token.equals(validToken)) {
                return dummyUser1;
            } else if (token.equals(forbiddenToken)) {
                return dummyUser2;
            } else if (token.equals(anotherValidToken)) {
                return dummyUser3;
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
        });
    }

    /**
     * Mock ActivityType repository actions
     */
    @BeforeEach
    void setupActivityRepository() {
        activityCount = 0L;
        // Save
        when(activityRepository.save(Mockito.any(Activity.class))).thenAnswer(i -> {
            Activity newActivity = i.getArgument(0);
            ReflectionTestUtils.setField(newActivity, "creatorUserId", DEFAULT_USER_ID);
            ReflectionTestUtils.setField(newActivity, "activityId", (DEFAULT_ACTIVITY_ID + activityCount++));
            newActivity.setCreatorUserId(DEFAULT_USER_ID);
            activityMockTable.add(i.getArgument(0));
            return newActivity;
        });
        // FindByActivityId
        when(activityRepository.findByActivityId(Mockito.any(Long.class))).thenAnswer(i -> {
            for (Activity activity: activityMockTable) {
                if (activity.getActivityId() == i.getArgument(0)) {
                    return activity;
                }
            }
            return null;
        });
        when(activityRepository.findAllByUserId(Mockito.any(Long.class))).thenAnswer(i -> {
            Long userId = i.getArgument(0);
            User user;
            if (userId.equals(DEFAULT_USER_ID)) {
                user = dummyUser1;
            } else if (userId.equals(DEFAULT_USER_ID_2)) {
                user = dummyUser2;
            } else {
                user = dummyUser3;
            }
            List<Activity> activities = new ArrayList<>();
            for (Activity activity : activityMockTable) {
                if (activity.getCreatorUserId().equals(userId) ||
                        activity.getParticipants().contains(user)) {
                    activities.add(activity);
                }
            }
            return activities;
        });

    }






    private final String newUserJson1 = JsonConverter.toJson(true,
            "lastname", "Cucumber",
            "firstname", "Larry",
            "primary_email", "larry@gmail.com",
            "password", "larrysPassword",
            "date_of_birth", "2002-1-20",
            "gender", "Female");

    private final String newUserJson2 = JsonConverter.toJson(true,
            "lastname", "Pocket",
            "firstname", "Poly",
            "middlename", "Michelle",
            "nickname", "Pino",
            "primary_email", "poly@pocket.com",
            "password", "somepwd0",
            "date_of_birth", "2000-11-11",
            "gender", "Female");

    private void setupMockingNoEmailDummyUser1(String json) throws JsonProcessingException {
        UserRegisterRequest regReq;
        regReq = objectMapper.treeToValue(objectMapper.readTree(json), UserRegisterRequest.class);
        dummyUser1 = dummyUser1.builder(regReq);
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> dummyUser1);
    }


    private void setupMockingNoEmailDummyUser3(String json) throws JsonProcessingException {
        UserRegisterRequest regReq;
        regReq = objectMapper.treeToValue(objectMapper.readTree(json), UserRegisterRequest.class);
        dummyUser3 = dummyUser3.builder(regReq);
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> dummyUser3);
    }






    // ----------- Tests -----------

    private final String newActivity1Json = JsonConverter.toJson(true,
            "activity_name", "Kaikoura Coast Track race",
            "description", "A big and nice race on a lovely peninsula",
            "activity_type", new Object[]{
                    "Astronomy", "Hiking"
            },
            "continuous", false,
            "start_time", "2020-02-20T08:00:00+1300",
            "end_time", "2020-02-20T08:00:00+1300",
            "location", "Kaikoura, NZ");

    /**
     * Test successful creation of new activity.
     */
    @Test
    void newActivity() throws Exception {

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles/{profileId}/activities", DEFAULT_USER_ID)
                .content(newActivity1Json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isCreated())
                .andReturn();
        assertNotNull(result.getResponse());
        assertDoesNotThrow(() -> {Long.valueOf(result.getResponse().getContentAsString());});
    }

    /**
     * Test successful deletion of an activity.
     */
    @Test
    void deleteActivity() throws Exception {
        Activity activityInRepo = objectMapper.readValue(newActivity1Json, Activity.class);
        activityRepository.save(activityInRepo);

        MockHttpServletRequestBuilder httpReqDelete = MockMvcRequestBuilders.delete("/profiles/{profileId}/activities/{activityId}", DEFAULT_USER_ID, DEFAULT_ACTIVITY_ID)
                .header("Token", validToken);
        MvcResult result = mvc.perform(httpReqDelete).andExpect(status().isOk()).andReturn();
        assertNotNull(result.getResponse());
    }

    private final String newActivityEditJson = JsonConverter.toJson(true,
            "activity_name", "Kaikoura Coast Track race",
            "description", "A big and nice race on a lovely peninsula",
            "activity_type", new Object[]{
                    "Gymnastics",
                    "Hiking"
            },
            "continuous", false,
            "start_time", "2020-02-20T08:00:00+1300",
            "end_time", "2020-02-20T08:00:00+1300",
            "location", "Kaikoura, NZ");




    /**
     * Test successful edit/update of an activity details
     */
    @Test
    void editActivity() throws Exception {
        Activity activityInRepo = objectMapper.readValue(newActivity1Json, Activity.class);
        activityRepository.save(activityInRepo);

        //Activity newActivity = objectMapper.readValue(newActivityEditJson, Activity.class);
        MockHttpServletRequestBuilder httpReqEdit = MockMvcRequestBuilders.put("/profiles/{profileId}/activities/{activityId}", DEFAULT_USER_ID, DEFAULT_ACTIVITY_ID)
                .header("Token", validToken)
                .content(newActivityEditJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(httpReqEdit).andExpect(status().isOk()).andReturn();

        assertNotNull(result.getResponse());
    }

    private final String newActivityWrongDateFormatJson = JsonConverter.toJson(true,
            "activity_name", "Port Hills Rock Climbing",
            "description", "Cattlestop Crag lead climbing",
            "activity_type", new Object[]{
                    "Rock Climbing", "Mountaineering"
            },
            "continuous", false,
            "start_time", "2020-02-20T08:00:00Z",
            "end_time", "2020-02-20T08:00:00Z",
            "location", "Christchurch, NZ");

    /**
     * Tests that Bad Request is returned when the date format is not correct, in this case representing time zone
     * with a 'Z'.  It must be represented with +/- time.  See Java SimpleDateFormat and @JsonFormat in Activity.
     */
    @Test
    void newActivityWrongDateFormat() throws Exception {

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles/{profileId}/activities", DEFAULT_USER_ID)
                .content(newActivityWrongDateFormatJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isBadRequest())
                .andReturn();
        assertNotNull(result.getResponse());
    }

    private final String newActivity2Json = JsonConverter.toJson(true,
            "activity_name", "Kaikoura Coast Track race",
            "description", "A big and nice race on a lovely peninsula",
            "activity_type", new Object[]{
                    "Astronomy", "Hiking"
            },
            "continuous", false,
            "start_time", "2020-02-20T08:00:00+1300",
            "end_time", "2020-02-20T08:00:00+1300",
            "location", "Kaikoura, NZ");


    /**
     * Test successful get request of activities created by specific user
     */
    @Test
    void getUserActivities() throws Exception {
        //Save some activities to get
        Activity activity = objectMapper.readValue(newActivity1Json, Activity.class);
        activityRepository.save(activity);

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get("/profiles/{profileId}/activities", DEFAULT_USER_ID)
                .header("Token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();

        String strContentLength = result.getResponse().getHeader("Total-Rows");
        assertNotNull(strContentLength);
        int contentLength = Integer.parseInt(strContentLength);
        assertEquals(1, contentLength);
    }


    /**
     * Test successful get request of activity.
     */
    @Test
    void getActivity() throws Exception {
        Activity activityInRepo = objectMapper.readValue(newActivity2Json, Activity.class);
        activityRepository.save(activityInRepo);
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get("/activities/{activityId}", DEFAULT_ACTIVITY_ID)
                .content(newActivity2Json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        Activity activityReceived = objectMapper.readValue(jsonResponseStr, Activity.class);  // Convert JSON to Activity obj

        assertEquals(activityRepository.findByActivityId(DEFAULT_ACTIVITY_ID), activityReceived);
    }


    @Test
    void checkActivityIsEditable() throws Exception {
        Activity activityInRepo = objectMapper.readValue(newActivity2Json, Activity.class);
        activityRepository.save(activityInRepo);
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get("/check-activity/{activityId}", DEFAULT_ACTIVITY_ID)
                .header("Token", validToken)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(httpReq)
                .andExpect(status().isOk());
    }


    @Test
    void checkActivityIsNotEditable() throws Exception {
        Activity activityInRepo = objectMapper.readValue(newActivity2Json, Activity.class);
        activityRepository.save(activityInRepo);
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get("/check-activity/{activityId}", DEFAULT_ACTIVITY_ID)
                .header("Token", forbiddenToken)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(httpReq)
                .andExpect(status().isForbidden());
    }


    @Test
    void getParticipantsOfActivitySuccess() throws Exception {
        Activity activityInRepo = objectMapper.readValue(newActivity2Json, Activity.class);
        setupMockingNoEmailDummyUser1(newUserJson1);
        setupMockingNoEmailDummyUser3(newUserJson2);
        activityInRepo.addParticipant(dummyUser1);
        activityInRepo.addParticipant(dummyUser3);
        activityRepository.save(activityInRepo);


        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get("/check-activity/{activityId}", DEFAULT_ACTIVITY_ID)
                .header("Token", validToken)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        // Convert set to list for indexing each user
        List<User> expectedParticipants = new ArrayList<>(activityRepository.findByActivityId(DEFAULT_ACTIVITY_ID).getParticipants());
        for (int i = 0; i < jsonNode.size(); i++) {
            // Removes "timedOut" property from User json received as timedOut is not a property within the User object
            ((ObjectNode) jsonNode.get(i)).remove("timedOut");
            // Converts user JsonNode node into User object
            User participant = objectMapper.treeToValue(jsonNode.get(i), User.class);
            assertEquals(participant.toString(), expectedParticipants.get(i).toString());
        }
    }

    @Test
    void getActivitiesByAKeyword() throws Exception {
        when(activityRepository.findAllByKeyword(Mockito.anyString())).thenAnswer(i -> {
            String keyword = i.getArgument(0);
            List<Activity> foundActivities = new ArrayList<>();
            if (keyword.equals("Climb") || keyword.equals("%Climb%") || keyword.equals("climb")) {
                Activity dumActivity1 = new Activity();
                ReflectionTestUtils.setField(dumActivity1, "activityId", 1L);
                Activity dumActivity2 = new Activity();
                ReflectionTestUtils.setField(dumActivity2, "activityId", 2L);
                dumActivity1.setName("Climb Mount Fuji");
                dumActivity2.setName("Climb the Ivory Tower");
                foundActivities.add(dumActivity1);
                foundActivities.add(dumActivity2);
            }
            return foundActivities;
        });

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get(new URI("/activities?activityName=Climb"))
                .header("Token", validToken);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result);
        JsonNode responseString = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(2, responseString.size());
    }

    @Test
    void requireKeywordToFindActivityByName() throws Exception {

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get(new URI("/activities?activityName="))
                .header("Token", validToken);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();
        JsonNode responseString = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(0, responseString.size());
    }

    @Test
    void cannotFindActivitiesByKeyword() throws Exception {
        when(activityRepository.findAllByKeyword(Mockito.anyString())).thenAnswer(i -> {
            String keyword = i.getArgument(0);
            List<Activity> foundActivities = new ArrayList<>();
            if (keyword.equals("Climb") || keyword.equals("%Climb%") || keyword.equals("climb")) {
                Activity dumActivity1 = new Activity();
                ReflectionTestUtils.setField(dumActivity1, "activityId", 1L);
                Activity dumActivity2 = new Activity();
                ReflectionTestUtils.setField(dumActivity2, "activityId", 2L);
                dumActivity1.setName("Climb Mount Fuji");
                dumActivity2.setName("Climb the Ivory Tower");
                foundActivities.add(dumActivity1);
                foundActivities.add(dumActivity2);
            }
            return foundActivities;
        });
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get(new URI("/activities?activityName=keyword"))
                .header("Token", validToken);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();
        JsonNode responseString = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(0, responseString.size());
    }
}
