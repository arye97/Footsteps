package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.seng302team600.validator.UserValidator;
import com.springvuegradle.seng302team600.model.*;
import com.springvuegradle.seng302team600.payload.LoginResponse;
import com.springvuegradle.seng302team600.payload.UserRegisterRequest;
import com.springvuegradle.seng302team600.payload.UserResponse;
import com.springvuegradle.seng302team600.repository.*;
import com.springvuegradle.seng302team600.service.ActivityTypeService;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private EmailRepository emailRepository;
    @MockBean
    private ActivityActivityTypeRepository activityActivityTypeRepository;
    @MockBean
    private ActivityTypeRepository activityTypeRepository;
    @MockBean
    private UserActivityTypeRepository userActivityTypeRepository;
    @MockBean
    private UserAuthenticationService userAuthenticationService;
    @MockBean
    private ActivityTypeService activityTypeService;
    @MockBean
    private UserValidator validator;
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ApplicationContext context;
    @Autowired
    public void context(ApplicationContext context) { this.context = context; }


    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long DEFAULT_EMAIL_ID = 1L;

    private int pageNumber;

    private User dummyUser1;
    private User dummyUser2; // Used when a second user is required
    private Email dummyEmail;
    private String validToken = "valid";
    private boolean defaultAdminIsRegistered;

    private final Location dummyPublicLocation = new Location(6.0, 9.0, "Puerto Rico");
    private final Location dummyPrivateLocation = new Location(5.0, 2.0, "Paris");


    @BeforeEach
    void setUp() {
        defaultAdminIsRegistered = false;
        MockitoAnnotations.initMocks(this);
        dummyUser1 = new User();
        pageNumber = 0;
    }

    private void setupMocking(String json) throws JsonProcessingException {
        setupMockingNoEmail(json);
        when(emailRepository.existsEmailByEmail(Mockito.anyString())).thenAnswer(i -> {
            return i.getArgument(0).equals(dummyEmail.getEmail());
        });
    }

    private void setupMockingNoEmail(String json) throws JsonProcessingException {
        UserRegisterRequest regReq;
        regReq = objectMapper.treeToValue(objectMapper.readTree(json), UserRegisterRequest.class);
        dummyUser1 = dummyUser1.builder(regReq);
        dummyEmail = new Email(dummyUser1.getPrimaryEmail(), true, dummyUser1);
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> {
            User user = i.getArgument(0);
            if (user.getRole() == UserRole.DEFAULT_ADMIN) {
                defaultAdminIsRegistered = true;
            }
            return dummyUser1;
        });
        when(emailRepository.save(Mockito.any(Email.class))).thenReturn(dummyEmail);
        when(emailRepository.findByEmail(Mockito.matches(dummyEmail.getEmail()))).thenReturn(dummyEmail);
        when(emailRepository.getOne(Mockito.anyLong())).thenReturn(dummyEmail);
        when(userAuthenticationService.findByToken(Mockito.anyString())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser1.getToken())) return dummyUser1;
            else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        when(userRepository.findByUserId(Mockito.anyLong())).thenReturn(dummyUser1);
        when(userRepository.existsUserByRole(Mockito.anyInt())).thenAnswer(i -> {
            if (((int)i.getArgument(0) == UserRole.DEFAULT_ADMIN) && !defaultAdminIsRegistered) {
                return false;
            }
            return true;
                });
        when(emailRepository.existsEmailByEmail(Mockito.anyString())).thenReturn(false);
        when(userAuthenticationService.findByUserId(Mockito.anyString(), Mockito.anyLong())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser1.getToken()) && i.getArgument(1).equals(dummyUser1.getUserId()))
                return dummyUser1;
            else if ((i.getArgument(0).equals(dummyUser1.getToken())) && !(i.getArgument(1).equals(dummyUser1.getUserId())))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            else
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        ReflectionTestUtils.setField(dummyUser1, "userId", DEFAULT_USER_ID);
        ReflectionTestUtils.setField(dummyEmail, "emailId", DEFAULT_EMAIL_ID);

        when(userAuthenticationService.login(Mockito.anyString(),Mockito.anyString())).thenAnswer(i -> {
                if (i.getArgument(0).equals(dummyEmail.getEmail()) && dummyUser1.checkPassword(i.getArgument(1))) return new LoginResponse("ValidToken", dummyUser1.getUserId());
                else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        Mockito.doAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser1.getToken())) dummyUser1.setToken(null);
            return null;
        }).when(userAuthenticationService).logout(Mockito.anyString());
        dummyUser1.setToken(validToken);
        dummyUser1.setTokenTime();

        // Second user
        regReq = objectMapper.treeToValue(objectMapper.readTree(createUserJsonViewUser2), UserRegisterRequest.class);
        dummyUser2 = new User(regReq);
        Email fakeEmail = new Email(dummyUser2.getPrimaryEmail(), true, dummyUser2);
        ReflectionTestUtils.setField(dummyUser2, "userId", 10L);
        ReflectionTestUtils.setField(fakeEmail, "emailId", 10L);
        when(userAuthenticationService.viewUserById(Mockito.anyLong(), Mockito.anyString())).thenAnswer(i -> {
            if ((long) i.getArgument(0) == 10L && i.getArgument(1) == validToken) return dummyUser2;
            else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });

        // Mock ActivityType repository for only dummyUser1
        Map<String, Long> activityNameToIdMap = new HashMap<>(4);
        Collection<ActivityType> activityTypes = dummyUser1.getActivityTypes();  // Get ActivityTypes from user
        activityTypes = activityTypes == null ? new HashSet<>() : activityTypes;  // If null set to empty set
        long id = 1;
        for (ActivityType activityType: activityTypes) {
            activityNameToIdMap.put(activityType.getName().toLowerCase(), id);
            id++;
        }
        /**
         * Mock the AND function of UserController
         */
        when(userActivityTypeRepository.findByAllActivityTypeIds(Mockito.anyList(), Mockito.anyInt(), Mockito.any(Pageable.class))).thenAnswer(i -> {
            List<Long> activityTypeIdsToMatch = i.getArgument(0);

            Collection<Long> allActivityTypeIds = activityNameToIdMap.values();
            if (allActivityTypeIds.containsAll(activityTypeIdsToMatch)) {   // Contains all
                Page<Long> result = new PageImpl(Arrays.asList(dummyUser1.getUserId()));
                return result;
            } else {
                return null;
            }
        });
        /**
         * Mock the OR function of UserController
         */
        when(userActivityTypeRepository.findBySomeActivityTypeIds(Mockito.anyList(), Mockito.any(Pageable.class))).thenAnswer(i -> {
            List<Long> activityTypeIdsToMatch = i.getArgument(0);
            boolean matched = false;

            Collection<Long> allActivityTypeIds = activityNameToIdMap.values();
            if (!Collections.disjoint(allActivityTypeIds, activityTypeIdsToMatch)) {  // Contains any
                Page<Long> result = new PageImpl(Arrays.asList(dummyUser1.getUserId()));
                return result;
            } else {
                return null;
            }
        });
        /**
         * Use a hash map to convert ActivityType names to mocked ids
         */
        when(activityTypeRepository.findActivityTypeIdsByNames(Mockito.anyList())).thenAnswer(i -> {
            List<String> activityTypeNames = i.getArgument(0);
            List<Long> activityTypeIdList = new ArrayList<>();
            for (String name: activityTypeNames) {
                activityTypeIdList.add(activityNameToIdMap.get(name.toLowerCase()));
            }
            return activityTypeIdList;
        });
        when(userRepository.getUsersByIds(Mockito.anyList())).thenAnswer(i -> {
            List<Long> checkId = new ArrayList<>();
            checkId.add(dummyUser1.getUserId());
            if (i.getArguments()[0].equals(checkId)) {
                List<User> users = new ArrayList<>();
                users.add(dummyUser1);
                return users;
            } else {
                return null;
            }

        });
    }



    // ----------- Tests -----------

    private final String newUserMissingFieldJson = JsonConverter.toJson(true,
            "lastname", "Benson",
            "middlename", "Jack",
            "nickname", "Jackie",
            "primary_email", "jacky@google.com",
            "password", "jacky'sSecuredPwd",
            "bio", "Jacky loves to ride his bike on crazy mountains.",
            "date_of_birth", "1985-12-20",
            "gender", "Male");

    /**
     *Try to create a user with missing field
     */
    @Test
    public void newUserMissingField() throws Exception {
        setupMockingNoEmail(newUserJson);

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(newUserMissingFieldJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

         mvc.perform(httpReq)
                .andExpect(status().isBadRequest());
    }


    private final String newUserEmailForbiddenJson = JsonConverter.toJson(true,
            "lastname", "Smith",
            "firstname", "Jim",
            "primary_email", "jsmith@google.com",
            "password", "JimJamPwd",
            "date_of_birth", "1995-1-1",
            "gender", "Male");
    @Test
    public void newUserEmailForbidden() throws Exception {
        setupMocking(newUserEmailForbiddenJson);

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(newUserEmailForbiddenJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(httpReq)
                .andExpect(status().isConflict());
    }



    private final String newUserJson = JsonConverter.toJson(
        "lastname", "Pocket",
        "firstname", "Poly",
        "middlename", "Michelle",
        "nickname", "Pino",
        "primary_email", "poly@pocket.com",
        "password", "somepwd0",
        "bio", "Poly Pocket is so tiny.",
        "date_of_birth", "2000-11-11",
        "gender", "Female",
        "fitness", 3,
        "passports", new Object[]{"Australia", "Antarctica"});
    @Test
    public void newUser() throws Exception {
        setupMockingNoEmail(newUserJson);

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(newUserJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isCreated())
                .andReturn();
        assertNotNull(result.getResponse());
    }


    private final String findUserDataJson = JsonConverter.toJson(true,
            "lastname", "Kim",
            "firstname", "Tim",
            "primary_email", "tim@gmail.com",
            "password", "pinPwd00",
            "date_of_birth", "2001-7-9",
            "gender", "Non-Binary");
    /**
     * Test wrong token
     */
    @Test
    public void findUserDataUnauthorized() throws Exception {
        setupMocking(findUserDataJson);
        String token = "WrongToken"; // Tokens are 30 chars long.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/profiles")
                .header("Token", token);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().isEmpty());
    }


    /**
     * Test get user data with correct token
     */
    @Test
    public void findUserDataAuthorized() throws Exception {
        setupMocking(findUserDataJson);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        assertEquals("Tim", jsonNode.get("firstname").asText());
    }


    /**
     * Helper function that creates a mock request to login a user
     * @param jsonLoginDetails a json string of login details with keys email: password:
     * @return the created request
     */
    private MockHttpServletRequestBuilder buildLoginRequest(String jsonLoginDetails) {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/login")
                .content(jsonLoginDetails)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        return request;
    }


    private final String createUserForLoginJson = JsonConverter.toJson(true,
            "lastname", "Dean",
            "firstname", "Bob",
            "middlename", "Mark",
            "primary_email", "bobby@gmail.com",
            "password", "bobbyPwd0",
            "date_of_birth", "1976-9-2",
            "gender", "Non-Binary");
    private final String doNotLoginIncorrectPasswordJson = JsonConverter.toJson(true,
            "email", "bobby@gmail.com",
            "password", "wrongPwd");
    /**
     * Test unauthorized when incorrect password
     */
    @Test
    public void doNotLoginIncorrectPassword() throws Exception {
        setupMocking(createUserForLoginJson);
        MockHttpServletRequestBuilder request = buildLoginRequest(doNotLoginIncorrectPasswordJson);

        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }


    private final String doNotLoginUserNotFoundJson = JsonConverter.toJson(true,
            "email", "wrong@gmail.com",
            "password", "bobbyPwd0");
    /**
     * Test unauthorized when User not found
     */
    @Test
    public void doNotLoginUserNotFound() throws Exception {
        setupMocking(createUserForLoginJson);
        MockHttpServletRequestBuilder request = buildLoginRequest(doNotLoginUserNotFoundJson);

        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }


    private final String loginAuthorizedUserJson = JsonConverter.toJson(true,
            "email", "bobby@gmail.com",
            "password", "bobbyPwd0");
    /**
     * Test successful login
     */
    @Test
    public void loginAuthorizedUser() throws Exception {
        setupMocking(createUserForLoginJson);
        MockHttpServletRequestBuilder request = buildLoginRequest(loginAuthorizedUserJson);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        assertNotNull(result.getResponse().getContentAsString());
    }


    /**
     * Logout fails when token not found (is null)
     */
    @Test
    public void forbiddenLogoutIfTokenNotFound() throws Exception {
        //System won't care if the token is wrong, as long as it isn't null
        //String token = "WrongToken"; // Tokens are 30 chars long.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/logout")
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(status().isForbidden());
    }

    private final String createUserJsonPostLogout = JsonConverter.toJson(true,
            "lastname", "kite",
            "firstname", "Kate",
            "primary_email", "kite@gmail.com",
            "password", "kitPwd00",
            "date_of_birth", "2002-1-2",
            "gender", "Female");
    /**
     * Test successful logout
     */
    @Test
    public void successfulLogout() throws Exception {
        setupMocking(createUserJsonPostLogout);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        mvc.perform(request)
                .andExpect(status().isOk());
    }


    private final String editProfileSuccessfulJson = JsonConverter.toJson(true,
            "lastname", "Smith",
            "firstname", "John",
            "primary_email", "jsmith@gmail.com",
            "password", "passWord0",
            "date_of_birth", "1980-6-4",
            "gender", "Male");
    private final String editProfileJsonPut = JsonConverter.toJson(true,
            "bio", "A guy",
            "date_of_birth", "1953-6-4",
            "lastname", "Doe");
    /**
     * Test if a user can be edited successfully
     */
    @Test
    public void editProfileSuccessful() throws Exception {
        setupMocking(editProfileSuccessfulJson);
        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        MvcResult result = mvc.perform(getRequest)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        Long userId = jsonNode.get("id").asLong();

        // Setup edit profile PUT request and GET request
        MockHttpServletRequestBuilder editRequest = MockMvcRequestBuilders.put("/profiles/{id}", userId)
                .content(editProfileJsonPut)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        // Perform PUT
        mvc.perform(editRequest)
                .andExpect(status().isOk());

        getRequest = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        result = mvc.perform(getRequest)
                              .andExpect(status().isOk())
                              .andReturn();
        // Get Response as JsonNode
        jsonResponseStr = result.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(jsonResponseStr);
        // Check that fields have been updated
        assertEquals("A guy", jsonNode.get("bio").asText());
        assertEquals("Doe", jsonNode.get("lastname").asText());
        // Check that protected fields have not been updated
        assertNotEquals("1953-1-1", jsonNode.get("date_of_birth").asText());
        assertNotEquals("1980-6-4", jsonNode.get("date_of_birth").asText());
    }


    private final String editProfileFailureJson = JsonConverter.toJson(true,
            "lastname", "Smith",
            "firstname", "Jane",
            "primary_email", "janesmith@gmail.com",
            "password", "passWord0",
            "date_of_birth", "1980-6-5",
            "gender", "Female");
    /**
     * Tests that a user cannot edit another user's profile
     */
    @Test
    public void editProfileFailure() throws Exception {
        setupMocking(editProfileFailureJson);
        // Setup bad edit profile PUT request, userId will never be -1
        MockHttpServletRequestBuilder editRequest = MockMvcRequestBuilders.put("/profiles/{id}", -1)
                .content(editProfileJsonPut)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);

        mvc.perform(editRequest)
                .andExpect(status().isForbidden());
    }


    private final String createUserJsonViewUser1 = JsonConverter.toJson(true,
            "lastname", "Tomato",
            "firstname", "Bob",
            "primary_email", "bob@gmail.com",
            "password", "bobsPassword0",
            "date_of_birth", "2002-1-2",
            "activity_types", new Object[]{
                    "Rock Climbing", "Mountaineering"
            },
            "gender", "Male");
    private final String createUserJsonViewUser2 = JsonConverter.toJson(true,
            "lastname", "Cucumber",
            "firstname", "Larry",
            "primary_email", "larry@gmail.com",
            "password", "larrysPassword0",
            "date_of_birth", "2002-1-20",
            "gender", "Female");
    /**
     * Tests that a user can view another users profile
     */
    @Test
    public void viewProfileSuccess() throws Exception {
        // Set up two users
        setupMocking(createUserJsonViewUser1);
        // Get the id of the second user
        long userId = dummyUser2.getUserId();
        // Request the second user
        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/profiles/{id}", userId)
                .header("Token", validToken);
        MvcResult result = mvc.perform(getRequest)
                .andExpect(status().isOk())
                .andReturn();
        // Get Response as JsonNode
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        // Check that the right user has been retrieved
        assertEquals("Larry", jsonNode.get("firstname").asText());
        assertEquals("Cucumber", jsonNode.get("lastname").asText());
        assertEquals("larry@gmail.com", jsonNode.get("primary_email").asText());
        assertEquals("Female", jsonNode.get("gender").asText());
        assertEquals("2002-01-20", jsonNode.get("date_of_birth").asText());
    }


    /**
     * Successful test to check if a token is logged in with given user id
     */
    @Test
    public void checkIfUserIdMatchesTokenSuccess() throws Exception {
        setupMockingNoEmail(newUserJson);

        //Log in
        MockHttpServletRequestBuilder getRequestToLogin = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        mvc.perform(getRequestToLogin)
                .andExpect(status().isOk());

        //Check if user id matches the token logged in
        MockHttpServletRequestBuilder getRequestCheckToken = MockMvcRequestBuilders.get("/check-profile/{id}", dummyUser1.getUserId())
                .header("Token", validToken);
        mvc.perform(getRequestCheckToken)
                .andExpect(status().isOk());
    }


    /**
     * Unsuccessful test to check if a token is logged in with given user id
     * that that throws a 403 Forbidden
     */
    @Test
    public void checkIfUserIdMatchesTokenForbidden() throws Exception {
        setupMockingNoEmail(newUserJson);
        long userId = dummyUser2.getUserId();

        //Log in
        MockHttpServletRequestBuilder getRequestToLogin = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        mvc.perform(getRequestToLogin)
                .andExpect(status().isOk());

        //Check token
        MockHttpServletRequestBuilder getRequestCheckToken = MockMvcRequestBuilders.get("/check-profile/{id}", userId)
                .header("Token", validToken);
        mvc.perform(getRequestCheckToken)
                .andExpect(status().isForbidden());
    }


    /**
     * Helper method to build a request to change the password of a user.  Gets the UserID from the current user
     * (might need to be changed when we get users by ID)
     * and change the user's password by their ID.
     * @param jsonPasswordChange a json put request to change the password
     * @return the request that is built.
     */
    private MockHttpServletRequestBuilder buildUserChangePassword(String jsonPasswordChange) {

        // Getting the user Id by using /profiles sets the password in the User to null, which breaks the tests.
        // So for now its set explicitly
        Long userId = DEFAULT_USER_ID;

        // Edit their password
        MockHttpServletRequestBuilder editPassReq = MockMvcRequestBuilders.put("/profiles/{id}/password", userId)
                .content(jsonPasswordChange)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        return editPassReq;
    }


    private final String changePasswordUserJson = JsonConverter.toJson(true,
            "lastname", "Doe",
            "firstname", "Jane",
            "primary_email", "janedoe@gmail.com",
            "password", "password1",
            "date_of_birth", "1980-6-5",
            "gender", "Female");
    private final String changePasswordUserLoginDetailsJson = JsonConverter.toJson(true,
            "email", "janedoe@gmail.com",
            "password", "PASSword2");
    private final String changePasswordSuccessJson = JsonConverter.toJson(true,
            "old_password", "password1",
            "new_password", "PASSword2",
            "repeat_password", "PASSword2");
    /**
     * Test creating a user and editing they're password when the password and repeated password match.
     * NOTE: as of now there is no simple way to tell if a password has been updated because password
     * hashes are not returned when retrieving a user.  Though they could be tested by logging in,
     * logging out, changing password, and trying to log in again.
     */
    @Test
    public void changePasswordSuccess() throws Exception {

        // Create user
        setupMockingNoEmail(changePasswordUserJson);


        // Change password
        MockHttpServletRequestBuilder editPassReq = buildUserChangePassword(changePasswordSuccessJson);
        mvc.perform(editPassReq)
                .andExpect(status().isOk());

        // Logout
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        mvc.perform(request)
                .andExpect(status().isOk());

        // Login with new password
        MockHttpServletRequestBuilder loginRequest2 = buildLoginRequest(changePasswordUserLoginDetailsJson);
        mvc.perform(loginRequest2)
                .andExpect(status().isCreated());
    }


    private final String changePasswordFailJson = JsonConverter.toJson(true,
            "old_password", "password1",
            "new_password", "password2",
            "repeat_password", "password3");
    /**
     * Test creating a user and editing they're password when the password and repeated password NO NOT match.
     */
    @Test
    public void changePasswordFail() throws Exception {
        // Create user
        setupMocking(changePasswordUserJson);


        // Change password
        MockHttpServletRequestBuilder editPassReq = buildUserChangePassword(changePasswordFailJson);
        mvc.perform(editPassReq)
                .andExpect(status().isBadRequest());   // Don't think there is any other way to test this than bad request

    }


    private final String changePasswordNewEqualsOldJson = JsonConverter.toJson(true,
            "old_password", "password1",
            "new_password", "password1",
            "repeat_password", "password1");

    /**
     * Test creating a user and editing they're password when the new password is the same as the old password
     * (new passwords can't match old passwords).
     */
    @Test
    public void changePasswordNewEqualsOld() throws Exception {
        // Create user
        setupMocking(changePasswordUserJson);


        // Change password
        MockHttpServletRequestBuilder editPassReq = buildUserChangePassword(changePasswordNewEqualsOldJson);
        mvc.perform(editPassReq)
                .andExpect(status().isBadRequest());   // Don't think there is any other way to test this than bad request
    }


    private final String changePasswordFailsRulesJson = JsonConverter.toJson(true,
            "old_password", "password1",
            "new_password", "pass",
            "repeat_password", "pass");
    /**
     * Test creating a user and changing their password to a password that violates the password rules
     */
    @Test
    public void changePasswordFailsRules() throws Exception {
        // Create user
        setupMocking(changePasswordUserJson);

        // Change password
        MockHttpServletRequestBuilder editPassReq = buildUserChangePassword(changePasswordFailsRulesJson);
        mvc.perform(editPassReq)
                .andExpect(status().isBadRequest());   // Don't think there is any other way to test this than bad request
    }

    /** Tests that a DefaultAdminUser is created when a UserController is created.
     * Checks that the Default Admin was added to the database in a roundabout way
     * (not the greatest).
     */
    @Test
    public void defaultAdminIsCreated() throws Exception {
        // Get the UserController bean instance
        UserController controller = context.getBean(UserController.class);

        // Get the defaultAdmin (private field) from UserController
        DefaultAdminUser defaultAdmin = (DefaultAdminUser)ReflectionTestUtils.getField(controller, "defaultAdmin");

        // Check that the default admin is not null
        assertNotNull(defaultAdmin);

        // Check that email is set
        assertNotNull(defaultAdmin.getPrimaryEmail());
        assertNotEquals("", defaultAdmin.getPrimaryEmail());

        // Use a private boolean flag to determine whether the default admin was added to the database
        // Can't find any other way to do it :(
        boolean defaultAdminWasAddedToDatabase = (boolean)ReflectionTestUtils.getField(controller, "_DAexists");
        assertTrue(defaultAdminWasAddedToDatabase);
    }

    private final String newActivityTypesString = JsonConverter.toJson(true,
            "activities", new Object[]{
                    "Rock Climbing", "Mountaineering"
            });

    /**
     *  Tests that the PUT endpoint updates the users activity types
     */
    @Test
    public void updateUserActivityTypes() throws Exception {
        setupMockingNoEmail(createUserJsonViewUser1);

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(createUserJsonViewUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(httpReq)
                .andExpect(status().isCreated())
                .andReturn();

        MockHttpServletRequestBuilder httpReqPut = MockMvcRequestBuilders.put(
                "/profiles/{profileId}/activity-types", dummyUser1.getUserId())
                .header("Token", validToken)
                .content(newActivityTypesString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(httpReqPut)
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result);
    }

    /** Tests the response of getting a users role
     */
    @Test
    void getUserRole() throws Exception {
        setupMocking(newUserJson);
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get("/profiles/{profileId}/role", DEFAULT_USER_ID)
                .header("Token", validToken);
        MvcResult request = mvc.perform(httpReq).andExpect(status().isOk()).andReturn();
        assertNotNull(request);
        assertEquals("0", request.getResponse().getContentAsString()); //as we are getting content as string we check it to a string of 0
    }

    private final String newUserWithActivityTypes = JsonConverter.toJson(
            "lastname", "Pocket",
            "firstname", "Poly",
            "middlename", "Michelle",
            "nickname", "Pino",
            "primary_email", "poly@pocket.com",
            "password", "somepwd0",
            "bio", "Poly Pocket is so tiny.",
            "activity_types", new Object[]{"Hiking"},
            "date_of_birth", "2000-11-11",
            "gender", "Female",
            "fitness", 3,
            "passports", new Object[]{"Australia", "Antarctica"});

    /**
     * Tests the response of getting the user by activity types using OR method
     * using the activity types set in the user
     */
    @Test
    void getUserByActivityTypesOR() throws Exception {
        setupMocking(newUserWithActivityTypes);
                                                                        // Passing path in as a URI prevents it from being re-encoded
        MockHttpServletRequestBuilder httpReqOR = MockMvcRequestBuilders.get(new URI("/profiles?activity=Hiking%20biking&method=or"))
                .header("Token", validToken)
                .header("Page-Number", pageNumber);

        MvcResult requestOR = mvc.perform(httpReqOR).andExpect(status().isOk()).andReturn();

        // Deserialize into an JsonNode, and test that the array contains one value
        JsonNode responseArrayOR = objectMapper.readTree(requestOR.getResponse().getContentAsString());
        assertEquals(1, responseArrayOR.size());

        // Test that response can be deserialized into UserResponse
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArrayOR.get(0), UserResponse.class));
    }

    /**
     * Tests the response of getting the user by activity types using AND method
     * using the activity types set in the user
     */
    @Test
    void getUserByActivityTypesAND() throws Exception {
        setupMocking(newUserWithActivityTypes);

        MockHttpServletRequestBuilder httpReqAND = MockMvcRequestBuilders.get(new URI("/profiles?activity=hiking%20biking&method=and"))
                .header("Token", validToken)
                .header("Page-Number", pageNumber);

        MvcResult requestAND = mvc.perform(httpReqAND).andExpect(status().is4xxClientError()).andReturn();

        assertNull(requestAND.getResponse().getContentType()); //should be equal as the user will not have BOTH hiking AND biking

    }

    private final String newUserWithSpacedActivityTypes = JsonConverter.toJson(true,
            "lastname", "Pocket",
            "firstname", "Poly",
            "middlename", "Michelle",
            "nickname", "Pino",
            "primary_email", "poly@pocket.com",
            "password", "somepwd0",
            "bio", "Poly Pocket is so tiny.",
            "activity_types", new Object[]{"Rock Climbing", "Baseball and Softball"},
            "date_of_birth", "2000-11-11",
            "gender", "Female",
            "fitness", 3,
            "passports", new Object[]{"Australia", "Antarctica"});


    /**
     * Tests getting a user by activity type with a dash in the name using OR method
     */
    @Test
    void getUserByActivityTypesWithSpacesOR() throws Exception {
        setupMocking(newUserWithSpacedActivityTypes);

        MockHttpServletRequestBuilder httpReqOR = MockMvcRequestBuilders.get(new URI("/profiles?activity=rock-climbing%20Hiking&method=or"))
                .header("Token", validToken)
                .header("Page-Number", pageNumber);


        //---Test-OR-Response----
        MvcResult requestOR = mvc.perform(httpReqOR).andExpect(status().isOk()).andReturn();

        // Deserialize into an JsonNode, and test that the array contains one value
        JsonNode responseArrayOR = objectMapper.readTree(requestOR.getResponse().getContentAsString());
        assertEquals(1, responseArrayOR.size());

        // Test that response can be deserialized into UserResponse
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArrayOR.get(0), UserResponse.class));

    }

    /**
     * Tests getting a user by activity type with a dash in the name using AND method
     */
    @Test
    void getUserByActivityTypesWithSpacesAND() throws Exception {
        setupMocking(newUserWithSpacedActivityTypes);

        MockHttpServletRequestBuilder httpReqAND = MockMvcRequestBuilders.get(new URI("/profiles?activity=baseball-and-Softball%20Rock-Climbing&method=and"))
                .header("Token", validToken)
                .header("Page-Number", pageNumber);

        //---Test-AND-Response----
        MvcResult requestAND = mvc.perform(httpReqAND).andExpect(status().isOk()).andReturn();

        // Deserialize into an JsonNode, and test that the array contains one value
        JsonNode responseArrayAND = objectMapper.readTree(requestAND.getResponse().getContentAsString());
        assertEquals(1, responseArrayAND.size());

        // Test that response can be deserialized into UserResponse
        assertDoesNotThrow(() -> objectMapper.treeToValue(responseArrayAND.get(0), UserResponse.class));
    }

    private final String newUserWithOneSpacedActivityType = JsonConverter.toJson(true,
            "lastname", "Someone",
            "firstname", "Lester",
            "middlename", "Michelle",
            "nickname", "Pino",
            "primary_email", "lester@hotmail.com",
            "password", "somepwd0",
            "bio", "Poly Pocket is so tiny.",
            "activity_types", new Object[]{"Baseball and Softball"},
            "date_of_birth", "2000-11-11",
            "gender", "Female",
            "fitness", 3,
            "passports", new Object[]{"Australia", "Antarctica"});

    /**
     * Tests getting a user by activity type with a dash in the name using AND method
     */
    @Test
    void failToGetUserByActivityTypesWithSpacesAND() throws Exception {
        setupMocking(newUserWithOneSpacedActivityType);

        MockHttpServletRequestBuilder httpReqAND = MockMvcRequestBuilders.get(new URI("/profiles?activity=baseball-and-Softball%20Rock-Climbing&method=and"))
                .header("Token", validToken)
                .header("Page-Number", pageNumber);


        //---Test-AND-Response----
        MvcResult requestAND = mvc.perform(httpReqAND).andExpect(status().is4xxClientError()).andReturn();

        // Should be null because this user has ONLY Baseball and Softball, not Rock Climbing
        assertNull(requestAND.getResponse().getContentType());
    }

    private final String userWithNoLocations = JsonConverter.toJson(true,
            "lastname", "Bobman",
            "middlename", "Cindy",
            "firstname", "Papu",
            "nickname", "Patty",
            "primary_email", "patty@google.com",
            "password", "QwErRTY1234",
            "bio", "Cindy loves to ride my bike on crazy rivers!",
            "date_of_birth", "1999-12-20",
            "gender", "Female"
    );

    private final String publicAndPrivateLocations = JsonConverter.toJson(true,
            "public_location", dummyPublicLocation,
            "private_location", dummyPrivateLocation
    );

    @Test
    public void editLocationOfUserWithNoLocationsSuccess() throws Exception {
        setupMocking(userWithNoLocations);
        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        MvcResult result = mvc.perform(getRequest)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        Long userId = jsonNode.get("id").asLong();

        MockHttpServletRequestBuilder editLocationRequest = MockMvcRequestBuilders.put("/profiles/{profileId}/location", userId)
                .header("Token", validToken)
                .content(publicAndPrivateLocations)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(editLocationRequest)
                .andExpect(status().isOk());


        getRequest = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        result = mvc.perform(getRequest)
                .andExpect(status().isOk())
                .andReturn();

        jsonResponseStr = result.getResponse().getContentAsString();
        UserResponse user = objectMapper.readValue(jsonResponseStr, UserResponse.class);

        assertEquals(dummyPublicLocation.getLocationName(), user.getPublicLocation().getLocationName());
        assertEquals(dummyPrivateLocation.getLocationName(), user.getPrivateLocation().getLocationName());
        assertEquals(dummyPublicLocation.getLatitude(), user.getPublicLocation().getLatitude());
        assertEquals(dummyPrivateLocation.getLatitude(), user.getPrivateLocation().getLatitude());
        assertEquals(dummyPublicLocation.getLongitude(), user.getPublicLocation().getLongitude());
        assertEquals(dummyPrivateLocation.getLongitude(), user.getPrivateLocation().getLongitude());
    }


    private final String nullLocations = JsonConverter.toJson(true,
            "public_location", null,
            "private_location", null
    );

    /**
     * Test setting a user's locations to null.
     * @throws Exception
     */
    @Test
    public void removeLocationOfUserWithLocationsSuccess() throws Exception {
        setupMocking(userWithNoLocations);
        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        MvcResult result = mvc.perform(getRequest)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        Long userId = jsonNode.get("id").asLong();

        MockHttpServletRequestBuilder editLocationRequest = MockMvcRequestBuilders.put("/profiles/{profileId}/location", userId)
                .header("Token", validToken)
                .content(publicAndPrivateLocations)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(editLocationRequest)
                .andExpect(status().isOk());

        editLocationRequest = MockMvcRequestBuilders.put("/profiles/{profileId}/location", userId)
                .header("Token", validToken)
                .content(nullLocations)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(editLocationRequest)
                .andExpect(status().isOk());


        getRequest = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        result = mvc.perform(getRequest)
                .andExpect(status().isOk())
                .andReturn();

        jsonResponseStr = result.getResponse().getContentAsString();
        UserResponse user = objectMapper.readValue(jsonResponseStr, UserResponse.class);

        assertNull(user.getPublicLocation());
        assertNull(user.getPrivateLocation());
    }
}