package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.model.*;
import com.springvuegradle.seng302team600.payload.ActivityResponse;
import com.springvuegradle.seng302team600.payload.UserRegisterRequest;
import com.springvuegradle.seng302team600.repository.*;
import com.springvuegradle.seng302team600.service.ActivityPinService;
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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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
    @MockBean
    private ActivityActivityTypeRepository activityActivityTypeRepository;
    @MockBean
    private ActivityTypeRepository activityTypeRepository;
    @MockBean
    private ActivityPinService activityPinService;
    @Autowired
    private MockMvc mvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long DEFAULT_USER_ID_2 = 2L;
    private static final Long DEFAULT_USER_ID_3 = 3L;
    private static final Long DEFAULT_EMAIL_ID = 1L;

    private static final Long DEFAULT_ACTIVITY_ID = 1L;
    private static final Long DEFAULT_ACTIVITY_ID_2 = 2L;
    private static final Long DEFAULT_ACTIVITY_ID_3 = 3L;
    private static final Long DEFAULT_ACTIVITY_ID_4 = 4L;
    private static final Long DEFAULT_ACTIVITY_ID_5 = 5L;
    private static final Long DEFAULT_ACTIVITY_ID_6 = 6L;
    private static final Long DEFAULT_ACTIVITY_ID_7 = 7L;

    private static final Long DEFAULT_ACTIVITY_TYPE_ID = 1L;
    private static final Long DEFAULT_ACTIVITY_TYPE_ID_2 = 2L;
    private static final Long DEFAULT_ACTIVITY_TYPE_ID_3 = 3L;
    private static final Long DEFAULT_ACTIVITY_TYPE_ID_4 = 4L;

    private final Double DUMMY_LAT = 12.345678D;
    private final Double DUMMY_LON = 12.345678D;
    private Long DUMMY_ACTIVITY_STUB_ID = 8L;
    private final Long DUMMY_USER_STUB_ID = 4L;
    private User dummyUserStub;
    private final List<Activity> dummyActivitiesTable = new ArrayList<>();
    private final int PAGE_ONE = 0;
    private final int PAGE_TWO = 1;
    private final int BLOCK_SIZE = 20;
    int INITIAL_ACTIVITIES_COUNT = 3;

    private static Long activityCount = 0L;
    private int currentPageNumber = 0;

    private User dummyUser1;
    private User dummyUser2; // Used when a second user is required
    private User dummyUser3;

    private Set<Activity> dummySearchActivitiesTable = new HashSet<>();

    private Set<ActivityType> dummyActivity1ActivityTypes = new HashSet<>();
    private Set<ActivityType> dummyActivity2ActivityTypes = new HashSet<>();
    private Set<ActivityType> dummyActivity3ActivityTypes = new HashSet<>();
    private Set<ActivityType> dummyOtherActivitiesActivityTypes = new HashSet<>();

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

        dummyUserStub = new User();
        dummyUserStub.setToken(anotherValidToken);
        ReflectionTestUtils.setField(dummyUserStub, "userId", DUMMY_USER_STUB_ID);

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
                    } else if (userId.equals(DUMMY_USER_STUB_ID)) {
                        return dummyUserStub;
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


        when(activityRepository.findAllByUserId(Mockito.any(Long.class), Mockito.any(Pageable.class))).thenAnswer(i -> {
            Long userId = i.getArgument(0);
            Pageable pageable = i.getArgument(1);
            int pageNumber = pageable.getPageNumber();
            int blockSize = pageable.getPageSize();
            List<Activity> foundActivities = new ArrayList<>();
            for (Activity activity : dummyActivitiesTable) {
                if (activity.getCreatorUserId().equals(userId)) {
                    foundActivities.add(activity);
                } else {
                    for (User participant : activity.getParticipants()) {
                        if (participant.getUserId().equals(userId)) {
                            foundActivities.add(activity);
                        }
                    }
                }
            }
            if (foundActivities.size() > 0) {
                int startIndex = pageNumber * blockSize;
                int endIndex = (pageNumber + 1) * blockSize;
                boolean hasNext = true;
                List<Activity> paginatedActivityBlocks;
                if (startIndex > foundActivities.size()) {
                    return null;
                } else if (endIndex > foundActivities.size()) {
                    hasNext = false;
                    endIndex = foundActivities.size();
                }
                paginatedActivityBlocks = foundActivities.subList(startIndex, endIndex);
                Slice<Activity> slice = new SliceImpl<>(paginatedActivityBlocks, pageable, hasNext);
                return slice;
            } else {
                return null;
            }
        });


        when(activityPinService.getPaginatedActivityList(Mockito.any(User.class), Mockito.any(Integer.class))).thenAnswer(i -> {
            User user = i.getArgument(0);
            int pageNumber = i.getArgument(1);
            int blockSize = 20;
            Pageable blockWithFiveActivities = PageRequest.of(pageNumber, blockSize);
            return activityRepository.findAllByUserId(user.getUserId(), blockWithFiveActivities);
        });
        when(activityPinService.getPins(Mockito.any(User.class), Mockito.any(List.class))).thenAnswer(i -> {
            User user = i.getArgument(0);
            List<Activity> activityList = i.getArgument(1);
            List<Pin> pinList = new ArrayList<>();
            for (Activity activity : activityList) {
                pinList.add(new Pin(activity, user.getUserId()));
            }
            return pinList;
        });
    }

    /**
     * Mock ActivityType repository actions
     */
    @BeforeEach
    void setupActivityRepository() {
        activityCount = 0L;

        ActivityType dummyActivityType1 = new ActivityType("Biking");
        ReflectionTestUtils.setField(dummyActivityType1, "activityTypeId", DEFAULT_ACTIVITY_TYPE_ID);
        ActivityType dummyActivityType2 = new ActivityType("Hiking");
        ReflectionTestUtils.setField(dummyActivityType2, "activityTypeId", DEFAULT_ACTIVITY_TYPE_ID_2);
        ActivityType dummyActivityType3 = new ActivityType("Eating");
        ReflectionTestUtils.setField(dummyActivityType3, "activityTypeId", DEFAULT_ACTIVITY_TYPE_ID_3);
        ActivityType dummyActivityType4 = new ActivityType("Smiling");
        ReflectionTestUtils.setField(dummyActivityType4, "activityTypeId", DEFAULT_ACTIVITY_TYPE_ID_4);

        Set<ActivityType> dummySearchActivityTypes = new HashSet<>();
        dummySearchActivityTypes.add(dummyActivityType1);
        dummySearchActivityTypes.add(dummyActivityType2);
        dummySearchActivityTypes.add(dummyActivityType3);
        dummySearchActivityTypes.add(dummyActivityType4);

        dummyActivity1ActivityTypes.add(dummyActivityType1);
        dummyActivity1ActivityTypes.add(dummyActivityType2);
        dummyActivity1ActivityTypes.add(dummyActivityType4);
        dummyActivity2ActivityTypes.add(dummyActivityType2);
        dummyActivity2ActivityTypes.add(dummyActivityType3);
        dummyActivity2ActivityTypes.add(dummyActivityType4);
        dummyActivity3ActivityTypes.add(dummyActivityType1);
        dummyActivity3ActivityTypes.add(dummyActivityType2);
        dummyActivity3ActivityTypes.add(dummyActivityType4);
        dummyOtherActivitiesActivityTypes.add(dummyActivityType4);

        Activity dummyActivity1 = new Activity();
        ReflectionTestUtils.setField(dummyActivity1, "activityId", DEFAULT_ACTIVITY_ID_2);
        ReflectionTestUtils.setField(dummyActivity1, "activityTypes", dummyActivity1ActivityTypes);
        Activity dummyActivity2 = new Activity();
        ReflectionTestUtils.setField(dummyActivity2, "activityId", DEFAULT_ACTIVITY_ID_3);
        ReflectionTestUtils.setField(dummyActivity2, "activityTypes", dummyActivity2ActivityTypes);
        Activity dummyActivity3 = new Activity();
        ReflectionTestUtils.setField(dummyActivity3, "activityId", DEFAULT_ACTIVITY_ID_4);
        ReflectionTestUtils.setField(dummyActivity3, "activityTypes", dummyActivity3ActivityTypes);
        Activity dummyActivity4 = new Activity();
        ReflectionTestUtils.setField(dummyActivity4, "activityId", DEFAULT_ACTIVITY_ID_5);
        ReflectionTestUtils.setField(dummyActivity4, "activityTypes", dummyOtherActivitiesActivityTypes);
        Activity dummyActivity5 = new Activity();
        ReflectionTestUtils.setField(dummyActivity5, "activityId", DEFAULT_ACTIVITY_ID_6);
        ReflectionTestUtils.setField(dummyActivity5, "activityTypes", dummyOtherActivitiesActivityTypes);
        Activity dummyActivity6 = new Activity();
        ReflectionTestUtils.setField(dummyActivity6, "activityId", DEFAULT_ACTIVITY_ID_7);
        ReflectionTestUtils.setField(dummyActivity6, "activityTypes", dummyOtherActivitiesActivityTypes);

        dummySearchActivitiesTable.add(dummyActivity1);
        dummySearchActivitiesTable.add(dummyActivity2);
        dummySearchActivitiesTable.add(dummyActivity3);
        dummySearchActivitiesTable.add(dummyActivity4);
        dummySearchActivitiesTable.add(dummyActivity5);
        dummySearchActivitiesTable.add(dummyActivity6);

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
                if (activity.getCreatorUserId().equals(userId) || activity.getParticipants().contains(user)) {
                    activities.add(activity);
                }
            }
            return activities;
        });



        // Mock ActivityType repository for each activity
        Map<String, Long> activityTypeNameToIdMap = new HashMap<>(4);
        Map<Long, ActivityType> activityTypeIdToObjectMap = new HashMap<>(4);
        for (ActivityType activityType: dummySearchActivityTypes) {
            activityTypeNameToIdMap.put(activityType.getName().toLowerCase(), activityType.getActivityTypeId());
            activityTypeIdToObjectMap.put(activityType.getActivityTypeId(), activityType);
        }

        when(activityTypeRepository.findActivityTypeIdsByNames(Mockito.anyList())).thenAnswer(i -> {
            List<String> activityTypeNames = i.getArgument(0);
            List<Long> activityTypeIds = new ArrayList<>();
            for (String name: activityTypeNames) {
                activityTypeIds.add(activityTypeNameToIdMap.get(name.toLowerCase()));
            }
            return activityTypeIds;
        });

        when(activityRepository.getActivitiesByIds(Mockito.anyList())).thenAnswer(i -> {
            List<Long> activityIds = i.getArgument(0);
            List<Activity> activities = new ArrayList<>();
            for (Activity activity: dummySearchActivitiesTable) {
                if (activityIds.contains(activity.getActivityId())) {
                    activities.add(activity);
                }
            }
            return activities;
        });

        // Mock the AND function of ActivityController
        when(activityActivityTypeRepository.findByAllActivityTypeIds(Mockito.anyList(), Mockito.anyInt(), Mockito.any(Pageable.class))).thenAnswer(i -> {
            List<Long> activityTypeIdsToMatch = i.getArgument(0);
            Pageable pageWithFiveActivities = i.getArgument(2);
            int pageNumber = pageWithFiveActivities.getPageNumber();
            int pageSize = pageWithFiveActivities.getPageSize();

            List<ActivityType> activityTypesToMatch = new ArrayList<>();
            for (Long id: activityTypeIdsToMatch) {
                activityTypesToMatch.add(activityTypeIdToObjectMap.get(id));
            }
            List<Long> activityIdsToSearch = new ArrayList<>();
            for (Activity activity: dummySearchActivitiesTable) {
                if (activity.getActivityTypes().containsAll(activityTypesToMatch)) {
                    activityIdsToSearch.add(activity.getActivityId());
                }
            }
            if (activityIdsToSearch.size() > 0) {
                int startIndex = pageNumber * pageSize;
                int endIndex = (pageNumber + 1) * pageSize;
                List<Long> paginatedActivityIds;
                if (startIndex > activityIdsToSearch.size()) {
                    return null;
                } else if (endIndex > activityIdsToSearch.size()) {
                    endIndex = activityIdsToSearch.size();
                }
                paginatedActivityIds = activityIdsToSearch.subList(startIndex, endIndex);
                return new PageImpl(paginatedActivityIds);
            } else {
                return null;
            }
        });

        //Mock the OR functionality
        when(activityActivityTypeRepository.findBySomeActivityTypeIds(Mockito.anyList(), Mockito.any(Pageable.class))).thenAnswer(i -> {
            List<Long> activityTypeIdsToMatch = i.getArgument(0);
            Pageable pageWithFiveActivities = i.getArgument(1);
            int pageNumber = pageWithFiveActivities.getPageNumber();
            int pageSize = pageWithFiveActivities.getPageSize();

            List<ActivityType> activityTypesToMatch = new ArrayList<>();
            for (Long id : activityTypeIdsToMatch) {
                activityTypesToMatch.add(activityTypeIdToObjectMap.get(id));
            }
            List<Long> activityIdsToSearch = new ArrayList<>();
            for (Activity activity : dummySearchActivitiesTable) {
                for (ActivityType type : activityTypesToMatch) {
                    if (activity.getActivityTypes().contains(type)) {
                        activityIdsToSearch.add(activity.getActivityId());
                    }
                }
            }
            if (activityIdsToSearch.size() > 0) {
                int startIndex = pageNumber * pageSize;
                int endIndex = (pageNumber + 1) * pageSize;
                List<Long> paginatedActivityIds;
                if (startIndex > activityIdsToSearch.size()) {
                    return null;
                } else if (endIndex > activityIdsToSearch.size()) {
                    endIndex = activityIdsToSearch.size();
                }
                paginatedActivityIds = activityIdsToSearch.subList(startIndex, endIndex);
                return new PageImpl(paginatedActivityIds);
            } else {
                return null;
            }
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
            "location", new HashMap<String, String>(){{
                put("longitude", "0.0");
                put("latitude", "0.0");
                put("name", "Null Island");
            }});

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
            "location", new HashMap<String, String>(){{
                put("longitude", "0.0");
                put("latitude", "0.0");
                put("name", "Null Island");
            }});




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
            "location", new HashMap<String, String>(){{
                put("longitude", "0.0");
                put("latitude", "0.0");
                put("name", "Null Island");
            }});

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
            "location", new HashMap<String, String>(){{
                put("longitude", "0.0");
                put("latitude", "0.0");
                put("name", "Null Island");
            }});

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
    void searchOneActivityByActivityTypesANDSuccessful() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/activities?activity=eating&method=and"))
                .header("Token", validToken)
                .header("Page-Number", currentPageNumber);

        MvcResult response = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseArray = objectMapper.readTree(response.getResponse().getContentAsString());
        assertEquals(1, responseArray.size());
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), ActivityResponse.class));
    }

    @Test
    void searchTwoActivitiesByActivityTypesANDSuccessful() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/activities?activity=biking%20hiking&method=and"))
                .header("Token", validToken)
                .header("Page-Number", currentPageNumber);

        MvcResult response = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseArray = objectMapper.readTree(response.getResponse().getContentAsString());
        assertEquals(2, responseArray.size());
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), ActivityResponse.class));
    }

    @Test
    void searchPageOneOfPaginatedActivitiesByActivityTypesANDSuccessful() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/activities?activity=smiling&method=and"))
                .header("Token", validToken)
                .header("Page-Number", currentPageNumber);

        MvcResult response = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseArray = objectMapper.readTree(response.getResponse().getContentAsString());
        assertEquals(5, responseArray.size());
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), ActivityResponse.class));
    }

    @Test
    void searchPageTwoOfPaginatedActivitiesByActivityTypesANDSuccessful() throws Exception {
        currentPageNumber++;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/activities?activity=smiling&method=and"))
                .header("Token", validToken)
                .header("Page-Number", currentPageNumber);

        MvcResult response = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseArray = objectMapper.readTree(response.getResponse().getContentAsString());
        assertEquals(1, responseArray.size());
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), ActivityResponse.class));
        currentPageNumber--;
    }

    @Test
    void searchPageThreeOfPaginatedActivitiesByActivityTypesANDFailNotFound() throws Exception {
        currentPageNumber++;
        currentPageNumber++;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/activities?activity=smiling&method=and"))
                .header("Token", validToken)
                .header("Page-Number", currentPageNumber);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
        currentPageNumber--;
        currentPageNumber--;
    }

    @Test
    void searchOneActivityByActivityTypesANDFailNotFound() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/activities?activity=smelling&method=and"))
                .header("Token", validToken)
                .header("Page-Number", currentPageNumber);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void searchOneActivityByActivityTypesMethodFailBadRequest() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/activities?activity=eating&method=blah"))
                .header("Token", validToken)
                .header("Page-Number", currentPageNumber);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void findSomeByOneActivityTypesSuccessful() throws Exception {
        currentPageNumber = 0;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/activities?activity=eating&method=OR"))
                .header("Token", validToken)
                .header("Page-Number", currentPageNumber);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseArray = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(1, responseArray.size());
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), ActivityResponse.class));
    }

    @Test
    void findSomeByTwoActivityTypesSuccessful() throws Exception {
        currentPageNumber = 0;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/activities?activity=hiking%20biking&method=OR"))
                .header("Token", validToken)
                .header("Page-Number", currentPageNumber);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode responseArray = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(3, responseArray.size());
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), ActivityResponse.class));
    }


    @Test
    void findSomeByOneActivityTypesFailure() throws Exception {
        currentPageNumber = 0;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/activities?activity=smile&method=OR"))
                .header("Token", validToken)
                .header("Page-Number", currentPageNumber);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        JsonNode responseArray = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(0, responseArray.size());
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), ActivityResponse.class));
    }

    @Test
    void findSomeByTwoActivityTypesFailure() throws Exception {
        currentPageNumber = 0;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/activities?activity=smile%20crying&method=OR"))
                .header("Token", validToken)
                .header("Page-Number", currentPageNumber);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        JsonNode responseArray = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(0, responseArray.size());
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), ActivityResponse.class));
    }

    @Test
    void getActivitiesByAKeyword() throws Exception {
        when(activityRepository.findAllByKeyword(Mockito.anyString(), Mockito.any())).thenAnswer(i -> {
            String keyword = i.getArgument(0);
            List<Activity> foundActivities = new ArrayList<>();
            if (keyword.contains("Climb")) {
                Activity dumActivity1 = new Activity();
                ReflectionTestUtils.setField(dumActivity1, "activityId", 1L);
                Activity dumActivity2 = new Activity();
                ReflectionTestUtils.setField(dumActivity2, "activityId", 2L);
                dumActivity1.setName("Climb Mount Fuji");
                dumActivity2.setName("Climb the Ivory Tower");
                foundActivities.add(dumActivity1);
                foundActivities.add(dumActivity2);
            }
            Page<Activity> result = new PageImpl(foundActivities);
            return result;
        });

        URI uri = new URI(
                null,
                null,
                "/activities",
                "activityKeywords=Climb",
                null);
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get(uri)
                .header("Token", validToken);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result);
        JsonNode responseString = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(2, responseString.size());
    }

    @Test
    void getActivitiesByExactSearch() throws Exception {
        when(activityRepository.findAllByKeyword(Mockito.anyString(), Mockito.any())).thenAnswer(i -> {
            String keyword = i.getArgument(0);
            List<Activity> foundActivities = new ArrayList<>();
            Activity dumActivity1 = new Activity();
            ReflectionTestUtils.setField(dumActivity1, "activityId", 1L);
            Activity dumActivity2 = new Activity();
            ReflectionTestUtils.setField(dumActivity2, "activityId", 2L);
            dumActivity1.setName("Climb Mount Fuji");
            dumActivity2.setName("Climb the Ivory Tower");
            foundActivities.add(dumActivity1);
            foundActivities.add(dumActivity2);
            List<Activity> selectedActivities = new ArrayList<>();
            if (keyword.equals("Climb Mount Fuji")) {
                for (Activity activity : foundActivities) {
                    if (activity.getName().equals(keyword)) {
                        selectedActivities.add(activity);
                    }
                }
            }
            Page<Activity> result = new PageImpl(selectedActivities);
            return result;
        });

        URI uri = new URI(
                null,
                null,
                "/activities",
                "activityKeywords=\"Climb%20Mount%20Fuji\"",
                null);
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get(uri)
                .header("Token", validToken);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result);
        JsonNode responseString = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(1, responseString.size());
    }
    @Test
    void requireKeywordToFindActivityByName() throws Exception {

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get(new URI("/activities?activityKeywords="))
                .header("Token", validToken);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();
        JsonNode responseString = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(0, responseString.size());
    }

    @Test
    void cannotFindActivitiesByKeyword() throws Exception {
        when(activityRepository.findAllByKeyword(Mockito.anyString(), Mockito.any())).thenAnswer(i -> {
            String keyword = i.getArgument(0);
            List<Activity> foundActivities = new ArrayList<>();
            if (keyword.equals("Climb") || keyword.equals("%Climb%")) {
                Activity dumActivity1 = new Activity();
                ReflectionTestUtils.setField(dumActivity1, "activityId", 1L);
                Activity dumActivity2 = new Activity();
                ReflectionTestUtils.setField(dumActivity2, "activityId", 2L);
                dumActivity1.setName("Climb Mount Fuji");
                dumActivity2.setName("Climb the Ivory Tower");
                foundActivities.add(dumActivity1);
                foundActivities.add(dumActivity2);
            }
            Page<Activity> result = new PageImpl(foundActivities);
            return result;
        });
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get(new URI("/activities?activityKeywords=keyword"))
                .header("Token", validToken);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();
        JsonNode responseString = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(0, responseString.size());
    }

    @Test
    void findActivitiesWhileExcludingKeyword() throws Exception {
        List<Activity> activities = new ArrayList<>();
        Activity dumActivity1 = new Activity();
        ReflectionTestUtils.setField(dumActivity1, "activityId", 1L);
        Activity dumActivity2 = new Activity();
        ReflectionTestUtils.setField(dumActivity2, "activityId", 2L);
        dumActivity1.setName("Climb Mount Fuji");
        dumActivity2.setName("Climb the Ivory Tower");
        activities.add(dumActivity1);
        activities.add(dumActivity2);

        when(activityRepository.findAllByKeywordExcludingTerm(Mockito.anyString(), Mockito.anyString())).thenAnswer(i -> {
            List<Activity> foundActivities = new ArrayList<>();
            String keyword = i.getArgument(0);
            String exclusion = i.getArgument(1);
            keyword = keyword.replaceAll("[^a-zA-Z0-9\\\\s+]", "");
            exclusion = exclusion.replaceAll("[^a-zA-Z0-9\\\\s+]", "");
            for (Activity activity : activities) {
                List<String> name = Arrays.asList(activity.getName().split(" "));
                if ((!name.contains(exclusion)) && (name.contains(keyword))) {
                    foundActivities.add(activity);
                }
            }
            return foundActivities;
        });

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get(new URI("/activities?activityKeywords=Climb%20-%20Fuji"))
                .header("Token", validToken);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();
        JsonNode responseString = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(1, responseString.size());

    }

    @Test
    void findAllActivitiesUsingMultipleNames() throws Exception {
        List<Activity> activities = new ArrayList<>();
        Activity dumActivity1 = new Activity();
        ReflectionTestUtils.setField(dumActivity1, "activityId", 1L);
        Activity dumActivity2 = new Activity();
        ReflectionTestUtils.setField(dumActivity2, "activityId", 2L);
        dumActivity1.setName("Climb Mount Fuji");
        dumActivity2.setName("Climb the Ivory Tower");
        activities.add(dumActivity1);
        activities.add(dumActivity2);

        when(activityRepository.findAllByKeyword(Mockito.anyString(), Mockito.any())).thenAnswer(i -> {
            List<Activity> foundActivities = new ArrayList<>();
            Page<Activity> pagedFoundActivities;
            String keyword = i.getArgument(0);
            keyword = keyword.replaceAll("[^a-zA-Z0-9\\\\s+]", "");
            for (Activity activity : activities) {
                List<String> name = Arrays.asList(activity.getName().split(" "));
                if (name.contains(keyword)) {
                    foundActivities.add(activity);
                }
            }
            pagedFoundActivities = new PageImpl<>(foundActivities);
            return pagedFoundActivities;
        });

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get(new URI("/activities?activityKeywords=Fuji%20%2b%20Tower"))
                .header("Token", validToken);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();
        JsonNode responseString = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(2, responseString.size());
    }

    @Test
    void getPageOneOfPaginatedBlockOfPinsWithNoActivitiesSuccess() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/profiles/4/activities/pins"))
                .header("Token", anotherValidToken)
                .header("Page-Number", PAGE_ONE);

        MvcResult response = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String hasNext = response.getResponse().getHeader("Has-Next");

        JsonNode responseArray = objectMapper.readTree(response.getResponse().getContentAsString());
        assertEquals(1, responseArray.size());
        assertFalse(Boolean.parseBoolean(hasNext));
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), Pin.class));
    }

    @Test
    void getPageOneOfPaginatedBlockOfPinsSuccess() throws Exception {
        populateDummyActivityList(INITIAL_ACTIVITIES_COUNT);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/profiles/4/activities/pins"))
                .header("Token", anotherValidToken)
                .header("Page-Number", PAGE_ONE);

        MvcResult response = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String hasNext = response.getResponse().getHeader("Has-Next");

        JsonNode responseArray = objectMapper.readTree(response.getResponse().getContentAsString());
        assertEquals(dummyActivitiesTable.size() + 1, responseArray.size());
        assertFalse(Boolean.parseBoolean(hasNext));
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), Pin.class));
    }

    @Test
    void getEmptyPageTwoOfPaginatedBlockOfPins() throws Exception {
        populateDummyActivityList(INITIAL_ACTIVITIES_COUNT);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/profiles/4/activities/pins"))
                .header("Token", anotherValidToken)
                .header("Page-Number", PAGE_TWO);

        MvcResult response = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        JsonNode responseArray = objectMapper.readTree(response.getResponse().getContentAsString());
        assertEquals(0, responseArray.size());
    }

    @Test
    void getPopulatedPageOneOfPaginatedBlockOfPinsSuccess() throws Exception {
        populateDummyActivityList(INITIAL_ACTIVITIES_COUNT + 20);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/profiles/4/activities/pins"))
                .header("Token", anotherValidToken)
                .header("Page-Number", PAGE_ONE);

        MvcResult response = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String hasNext = response.getResponse().getHeader("Has-Next");

        JsonNode responseArray = objectMapper.readTree(response.getResponse().getContentAsString());
        assertEquals(BLOCK_SIZE + 1, responseArray.size());
        assertTrue(Boolean.parseBoolean(hasNext));
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), Pin.class));
    }

    @Test
    void getPopulatedPageTwoOfPaginatedBlockOfPinsSuccess() throws Exception {
        populateDummyActivityList(INITIAL_ACTIVITIES_COUNT + 20);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI("/profiles/4/activities/pins"))
                .header("Token", anotherValidToken)
                .header("Page-Number", PAGE_TWO);

        MvcResult response = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String hasNext = response.getResponse().getHeader("Has-Next");

        JsonNode responseArray = objectMapper.readTree(response.getResponse().getContentAsString());
        assertEquals(dummyActivitiesTable.size() - BLOCK_SIZE, responseArray.size());
        assertFalse(Boolean.parseBoolean(hasNext));
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArray.get(0), Pin.class));
    }

    /**
     * Helper function that populates dummyActivityList with a number of dummy activities for testing pagination
     *
     * @param activitiesCount number of dummy activities
     */
    public void populateDummyActivityList(int activitiesCount) {
        for (int i = 0; i < activitiesCount; i++) {
            Activity dummyActivityStub = new Activity();
            ReflectionTestUtils.setField(dummyActivityStub, "activityId", DUMMY_ACTIVITY_STUB_ID);
            dummyActivityStub.setLocation(new Location(DUMMY_LAT, DUMMY_LON, "Qatar"));
            dummyActivityStub.setCreatorUserId(DUMMY_USER_STUB_ID);
            dummyActivitiesTable.add(dummyActivityStub);
            DUMMY_ACTIVITY_STUB_ID++;
        }
    }
}

