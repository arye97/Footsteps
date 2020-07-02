package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.seng302team600.model.DefaultAdminUser;
import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.model.UserRole;
import com.springvuegradle.seng302team600.payload.RegisterRequest;
import com.springvuegradle.seng302team600.payload.UserResponse;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

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
    private UserValidationService userValidationService;
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ApplicationContext context;
    @Autowired
    public void context(ApplicationContext context) { this.context = context; }


    private ObjectMapper objectMapper;

    private User dummyUser;
    private User fakeUser; // Used when a second user is required
    private RegisterRequest regReq;
    private Email dummyEmail;
    private Email fakeEmail; // Used for the second (fake) user
    private String validToken = "valid";
    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long DEFAULT_EMAIL_ID = 1L;
    private boolean defaultAdminIsRegistered;

    @BeforeEach
    public void setUp() {
        defaultAdminIsRegistered = false;

        objectMapper = new ObjectMapper();
        MockitoAnnotations.initMocks(this);
        dummyUser = new User();
    }

    private void setupMocking(String json) throws JsonProcessingException {
        setupMockingNoEmail(json);
        when(emailRepository.existsEmailByEmail(Mockito.anyString())).thenAnswer(i -> {
            return i.getArgument(0).equals(dummyEmail.getEmail());
        });
    }
    private void setupMockingNoEmail(String json) throws JsonProcessingException {
        regReq = objectMapper.treeToValue(objectMapper.readTree(json), RegisterRequest.class);
        dummyUser = dummyUser.builder(regReq);
        dummyEmail = new Email(dummyUser.getPrimaryEmail(), true, dummyUser);
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> {
            User user = i.getArgument(0);
            if (user.getRole() == UserRole.DEFAULT_ADMIN) {
                defaultAdminIsRegistered = true;
            }
            return dummyUser;
        });
        when(emailRepository.save(Mockito.any(Email.class))).thenReturn(dummyEmail);
        when(emailRepository.findByEmail(Mockito.matches(dummyEmail.getEmail()))).thenReturn(dummyEmail);
        when(emailRepository.getOne(Mockito.anyLong())).thenReturn(dummyEmail);
        when(userValidationService.findByToken(Mockito.anyString())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser.getToken())) return dummyUser;
            else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        when(userRepository.findByUserId(Mockito.anyLong())).thenReturn(dummyUser);
        when(userRepository.existsUserByRole(Mockito.anyInt())).thenAnswer(i -> {
            if (((int)i.getArgument(0) == UserRole.DEFAULT_ADMIN) && !defaultAdminIsRegistered) {
                return false;
            }
            return true;
                });
        when(emailRepository.existsEmailByEmail(Mockito.anyString())).thenReturn(false);
        when(userValidationService.findByUserId(Mockito.anyString(), Mockito.anyLong())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser.getToken()) && i.getArgument(1).equals(dummyUser.getUserId()))
                return dummyUser;
            else if ((i.getArgument(0).equals(dummyUser.getToken())) && !(i.getArgument(1).equals(dummyUser.getUserId())))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            else
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        ReflectionTestUtils.setField(dummyUser, "userId", DEFAULT_USER_ID);
        ReflectionTestUtils.setField(dummyEmail, "id", DEFAULT_EMAIL_ID);
        when(userValidationService.login(Mockito.anyString(),Mockito.anyString())).thenAnswer(i -> {
                if (i.getArgument(0).equals(dummyEmail.getEmail()) && dummyUser.checkPassword(i.getArgument(1))) return new UserResponse("ValidToken", dummyUser.getUserId());
                else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        Mockito.doAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser.getToken())) dummyUser.setToken(null);
            return null;
        }).when(userValidationService).logout(Mockito.anyString());
        dummyUser.setToken(validToken);
        dummyUser.setTokenTime();

        // Second user
        fakeUser = new User();
        regReq = objectMapper.treeToValue(objectMapper.readTree(createUserJsonViewUser2), RegisterRequest.class);
        fakeUser = fakeUser.builder(regReq);
        fakeEmail = new Email(fakeUser.getPrimaryEmail(), true, fakeUser);
        ReflectionTestUtils.setField(fakeUser, "userId", 10L);
        ReflectionTestUtils.setField(fakeEmail, "id", 10L);
        when(userValidationService.viewUserById(Mockito.anyLong(), Mockito.anyString())).thenAnswer(i -> {
            if ((long) i.getArgument(0) == 10L && i.getArgument(1) == validToken) return fakeUser;
            else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
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
        System.out.println(newUserMissingFieldJson);
        setupMockingNoEmail(newUserMissingFieldJson);

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(newUserMissingFieldJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

         mvc.perform(httpReq)
                .andExpect(status().isBadRequest());
    }


    private final String newUserEmailForbiddenJson = "{\n" +
            "  \"lastname\": \"Smith\",\n" +
            "  \"firstname\": \"Jim\",\n" +
            "  \"primary_email\": \"jsmith@google.com\",\n" +
            "  \"password\": \"JimJamPwd\",\n" +
            "  \"date_of_birth\": \"1995-1-1\",\n" +
            "  \"gender\": \"Male\"\n" +
            "}";
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
        "password", "somepwd",
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


    private final String findUserDataJson = "{\n" +
            "  \"lastname\": \"Kim\",\n" +
            "  \"firstname\": \"Tim\",\n" +
            "  \"primary_email\": \"tim@gmail.com\",\n" +
            "  \"password\": \"pinPwd\",\n" +
            "  \"date_of_birth\": \"2001-7-9\",\n" +
            "  \"gender\": \"Non-Binary\"\n" +
            "}";

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


    private final String createUserForLoginJson = "{\n" +
            "  \"lastname\": \"Dean\",\n" +
            "  \"firstname\": \"Bob\",\n" +
            "  \"middlename\": \"Mark\",\n" +
            "  \"primary_email\": \"bobby@gmail.com\",\n" +
            "  \"password\": \"bobbyPwd\",\n" +
            "  \"date_of_birth\": \"1976-9-2\",\n" +
            "  \"gender\": \"Non-Binary\"\n" +
            "}";
    private final String doNotLoginIncorrectPasswordJson = "{\n" +
            "  \"email\": \"bobby@gmail.com\",\n" +
            "  \"password\": \"wrongPwd\"\n" +
            "}";
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


    private final String doNotLoginUserNotFoundJson = "{\n" +
            "  \"email\": \"wrong@gmail.com\",\n" +
            "  \"password\": \"bobbyPwd\"\n" +
            "}";
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


    private final String loginAuthorizedUserJson = "{\n" +
            "  \"email\": \"bobby@gmail.com\",\n" +
            "  \"password\": \"bobbyPwd\"\n" +
            "}";
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

    private final String createUserJsonPostLogout = "{\n" +
            "  \"lastname\": \"kite\",\n" +
            "  \"firstname\": \"Kate\",\n" +
            "  \"primary_email\": \"kite@gmail.com\",\n" +
            "  \"password\": \"kitPwd\",\n" +
            "  \"date_of_birth\": \"2002-1-2\",\n" +
            "  \"gender\": \"Female\"\n" +
            "}";
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


    private final String editProfileSuccessfulJson = "{\n" +
            "  \"lastname\": \"Smith\",\n" +
            "  \"firstname\": \"John\",\n" +
            "  \"primary_email\": \"jsmith@gmail.com\",\n" +
            "  \"password\": \"pass\",\n" +
            "  \"date_of_birth\": \"1980-6-4\",\n" +
            "  \"gender\": \"Male\"\n" +
            "}";
    private final String editProfileJsonPut = "{\n" +
            "  \"bio\": \"A guy\",\n" +
            "  \"date_of_birth\": \"1953-6-4\",\n" +
            "  \"lastname\": \"Doe\"\n" +
            "}";
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


    private final String editProfileFailureJson = "{\n" +
            "  \"lastname\": \"Smith\",\n" +
            "  \"firstname\": \"Jane\",\n" +
            "  \"primary_email\": \"janesmith@gmail.com\",\n" +
            "  \"password\": \"pass\",\n" +
            "  \"date_of_birth\": \"1980-6-5\",\n" +
            "  \"gender\": \"Female\"\n" +
            "}";
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


    private final String createUserJsonViewUser1 = "{\n" +
            "  \"lastname\": \"Tomato\",\n" +
            "  \"firstname\": \"Bob\",\n" +
            "  \"primary_email\": \"bob@gmail.com\",\n" +
            "  \"password\": \"bobsPassword\",\n" +
            "  \"date_of_birth\": \"2002-1-2\",\n" +
            "  \"gender\": \"Male\"\n" +
            "}";
    private final String createUserJsonViewUser2 = "{\n" +
            "  \"lastname\": \"Cucumber\",\n" +
            "  \"firstname\": \"Larry\",\n" +
            "  \"primary_email\": \"larry@gmail.com\",\n" +
            "  \"password\": \"larrysPassword\",\n" +
            "  \"date_of_birth\": \"2002-1-20\",\n" +
            "  \"gender\": \"Female\"\n" +
            "}";
    /**
     * Tests that a user can view another users profile
     */
    @Test
    public void viewProfileSuccess() throws Exception {
        // Set up two users
        setupMocking(createUserJsonViewUser1);
        // Get the id of the second user
        long userId = fakeUser.getUserId();
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


    @Test
    /**
     * Successful test to check if a token is logged in with given user id
     */
    public void checkIfUserIdMatchesTokenSuccess() throws Exception {
        setupMockingNoEmail(newUserJson);

        //Log in
        MockHttpServletRequestBuilder getRequestToLogin = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        mvc.perform(getRequestToLogin)
                .andExpect(status().isOk());

        //Check if user id matches the token logged in
        MockHttpServletRequestBuilder getRequestCheckToken = MockMvcRequestBuilders.get("/check-profile/{id}", dummyUser.getUserId())
                .header("Token", validToken);
        mvc.perform(getRequestCheckToken)
                .andExpect(status().isOk());
    }


    @Test
    /**
     * Unsuccessful test to check if a token is logged in with given user id
     * that that throws a 403 Forbidden
     */
    public void checkIfUserIdMatchesTokenForbidden() throws Exception {
        setupMockingNoEmail(newUserJson);
        long userId = fakeUser.getUserId();

        //Log in
        MockHttpServletRequestBuilder getRequestToLogin = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        mvc.perform(getRequestToLogin)
                .andExpect(status().isOk());

        //Check token
        System.out.println(userId);
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


    private final String changePasswordUserJson = "{\n" +
            "  \"lastname\": \"Doe\",\n" +
            "  \"firstname\": \"Jane\",\n" +
            "  \"primary_email\": \"janedoe@gmail.com\",\n" +
            "  \"password\": \"password1\",\n" +
            "  \"date_of_birth\": \"1980-6-5\",\n" +
            "  \"gender\": \"Female\"\n" +
            "}";
    private final String changePasswordUserLoginDetailsJson = "{\n" +
            "  \"email\": \"janedoe@gmail.com\",\n" +
            "  \"password\": \"PASSword2\"\n" +
            "}";
    private final String changePasswordSuccessJson = "{\n" +
            "  \"old_password\": \"password1\",\n" +
            "  \"new_password\": \"PASSword2\",\n" +
            "  \"repeat_password\": \"PASSword2\"\n" +
            "}";
    @Test
    /**
     * Test creating a user and editing they're password when the password and repeated password match.
     * NOTE: as of now there is no simple way to tell if a password has been updated because password
     * hashes are not returned when retrieving a user.  Though they could be tested by logging in,
     * logging out, changing password, and trying to log in again.
     */
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


    private final String changePasswordFailJson = "{\n" +
            "  \"old_password\": \"password1\",\n" +
            "  \"new_password\": \"password2\",\n" +
            "  \"repeat_password\": \"password3\"\n" +
            "}";
    @Test
    /**
     * Test creating a user and editing they're password when the password and repeated password NO NOT match.
     */
    public void changePasswordFail() throws Exception {
        // Create user
        setupMocking(changePasswordUserJson);


        // Change password
        MockHttpServletRequestBuilder editPassReq = buildUserChangePassword(changePasswordFailJson);
        mvc.perform(editPassReq)
                .andExpect(status().isBadRequest());   // Don't think there is any other way to test this than bad request

    }


    private final String changePasswordNewEqualsOldJson = "{\n" +
            "  \"old_password\": \"password1\",\n" +
            "  \"new_password\": \"password1\",\n" +
            "  \"repeat_password\": \"password1\"\n" +
            "}";
    @Test
    /**
     * Test creating a user and editing they're password when the new password is the same as the old password
     * (new passwords can't match old passwords).
     */
    public void changePasswordNewEqualsOld() throws Exception {
        // Create user
        setupMocking(changePasswordUserJson);


        // Change password
        MockHttpServletRequestBuilder editPassReq = buildUserChangePassword(changePasswordNewEqualsOldJson);
        mvc.perform(editPassReq)
                .andExpect(status().isBadRequest());   // Don't think there is any other way to test this than bad request
    }


    private final String changePasswordFailsRulesJson = "{\n" +
            "  \"old_password\": \"password1\",\n" +
            "  \"new_password\": \"pass\",\n" +
            "  \"repeat_password\": \"pass\"\n" +
            "}";
    @Test
    /**
     * Test creating a user and changing their password to a password that violates the password rules
     */
    public void changePasswordFailsRules() throws Exception {
        // Create user
        setupMocking(changePasswordUserJson);


        // Change password
        MockHttpServletRequestBuilder editPassReq = buildUserChangePassword(changePasswordFailsRulesJson);
        mvc.perform(editPassReq)
                .andExpect(status().isBadRequest());   // Don't think there is any other way to test this than bad request
    }

    @Test
    /** Tests that a DefaultAdminUser is created when a UserController is created.
     * Checks that the Default Admin was added to the database in a roundabout way
     * (not the greatest).
     */
    public void defaultAdminIsCreated() throws Exception {
        // Get the UserController bean instance
        UserController controller = context.getBean(UserController.class);

        // Get the defaultAdmin (private field) from UserController
        DefaultAdminUser defaultAdmin = (DefaultAdminUser)ReflectionTestUtils.getField(controller, "defaultAdmin");

        // Check that the default admin is not null
        assertNotNull(defaultAdmin);

        // Check that email is set
        assertNotNull(defaultAdmin.getPrimaryEmail());
        assertNotEquals(defaultAdmin.getPrimaryEmail(), "");

        // Use a private boolean flag to determine whether the default admin was added to the database
        // Can't find any other way to do it :(
        boolean defaultAdminWasAddedToDatabase = (boolean)ReflectionTestUtils.getField(controller, "_DAexists");
        assertTrue(defaultAdminWasAddedToDatabase);
    }

}