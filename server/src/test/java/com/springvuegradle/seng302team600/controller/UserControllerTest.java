package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

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

    private String createUserJsonPost;
    private String userMissJsonPost;
    private String userForbiddenJsonPost;
    private String createUserJsonPostFindUser;
    private String editProfileJsonPut;
    private String editProfileUserJson;
    private String editProfileNastyUserJson;
    private String createUserJsonPostLogin;
    private String jsonLoginDetails;
    private String jsonLoginDetailsIncorrectPass;
    private String jsonLoginDetailsUserNotFound;
    private String createUserJsonPostLogout;
    private String createUserJsonViewUser1;
    private String createUserJsonViewUser2;
    private String jsonEditPasswordUser;
    private String jsonEditPasswordLoginDetails;
    private String jsonPasswordChangeSuccess;
    private String jsonPasswordChangeFail;
    private String jsonPasswordSame;
    private String jsonPasswordFailsRules;

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
        userMissJsonPost = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"Male\"\n" +
                "}";

        userForbiddenJsonPost = "{\n" +
                "  \"lastname\": \"Smith\",\n" +
                "  \"firstname\": \"Jim\",\n" +
                "  \"primary_email\": \"jsmith@google.com\",\n" +
                "  \"password\": \"JimJamPwd\",\n" +
                "  \"date_of_birth\": \"1995-1-1\",\n" +
                "  \"gender\": \"Male\"\n" +
                "}";

        createUserJsonPost = "{\n" +
                "  \"lastname\": \"Pocket\",\n" +
                "  \"firstname\": \"Poly\",\n" +
                "  \"middlename\": \"Michelle\",\n" +
                "  \"nickname\": \"Pino\",\n" +
                "  \"primary_email\": \"poly@pocket.com\",\n" +
                "  \"password\": \"somepwd\",\n" +
                "  \"bio\": \"Poly Pocket is so tiny.\",\n" +
                "  \"date_of_birth\": \"2000-11-11\",\n" +
                "  \"gender\": \"Female\",\n" +
                "  \"fitness\": 3,\n" +
                "  \"passports\": [\"Australia\", \"Antarctica\"]\n" +
                "}";

        createUserJsonPostFindUser = "{\n" +
                "  \"lastname\": \"Kim\",\n" +
                "  \"firstname\": \"Tim\",\n" +
                "  \"primary_email\": \"tim@gmail.com\",\n" +
                "  \"password\": \"pinPwd\",\n" +
                "  \"date_of_birth\": \"2001-7-9\",\n" +
                "  \"gender\": \"Non-Binary\"\n" +
                "}";

        createUserJsonPostLogin = "{\n" +
                "  \"lastname\": \"Dean\",\n" +
                "  \"firstname\": \"Bob\",\n" +
                "  \"middlename\": \"Mark\",\n" +
                "  \"primary_email\": \"bobby@gmail.com\",\n" +
                "  \"password\": \"bobbyPwd\",\n" +
                "  \"date_of_birth\": \"1976-9-2\",\n" +
                "  \"gender\": \"Non-Binary\"\n" +
                "}";

        createUserJsonPostLogout = "{\n" +
                "  \"lastname\": \"kite\",\n" +
                "  \"firstname\": \"Kate\",\n" +
                "  \"primary_email\": \"kite@gmail.com\",\n" +
                "  \"password\": \"kitPwd\",\n" +
                "  \"date_of_birth\": \"2002-1-2\",\n" +
                "  \"gender\": \"Female\"\n" +
                "}";

        createUserJsonViewUser1 = "{\n" +
                "  \"lastname\": \"Tomato\",\n" +
                "  \"firstname\": \"Bob\",\n" +
                "  \"primary_email\": \"bob@gmail.com\",\n" +
                "  \"password\": \"bobsPassword\",\n" +
                "  \"date_of_birth\": \"2002-1-2\",\n" +
                "  \"gender\": \"Male\"\n" +
                "}";

        createUserJsonViewUser2 = "{\n" +
                "  \"lastname\": \"Cucumber\",\n" +
                "  \"firstname\": \"Larry\",\n" +
                "  \"primary_email\": \"larry@gmail.com\",\n" +
                "  \"password\": \"larrysPassword\",\n" +
                "  \"date_of_birth\": \"2002-1-20\",\n" +
                "  \"gender\": \"Female\"\n" +
                "}";

        editProfileJsonPut = "{\n" +
                "  \"bio\": \"A guy\",\n" +
                "  \"date_of_birth\": \"1953-6-4\",\n" +
                "  \"lastname\": \"Doe\"\n" +
                "}";

        editProfileUserJson = "{\n" +
                "  \"lastname\": \"Smith\",\n" +
                "  \"firstname\": \"John\",\n" +
                "  \"primary_email\": \"jsmith@gmail.com\",\n" +
                "  \"password\": \"pass\",\n" +
                "  \"date_of_birth\": \"1980-6-4\",\n" +
                "  \"gender\": \"Male\"\n" +
                "}";

        editProfileNastyUserJson = "{\n" +
                "  \"lastname\": \"Smith\",\n" +
                "  \"firstname\": \"Jane\",\n" +
                "  \"primary_email\": \"janesmith@gmail.com\",\n" +
                "  \"password\": \"pass\",\n" +
                "  \"date_of_birth\": \"1980-6-5\",\n" +
                "  \"gender\": \"Female\"\n" +
                "}";

        jsonLoginDetails = "{\n" +
                "  \"email\": \"bobby@gmail.com\",\n" +
                "  \"password\": \"bobbyPwd\"\n" +
                "}";

        jsonLoginDetailsIncorrectPass = "{\n" +
                "  \"email\": \"bobby@gmail.com\",\n" +
                "  \"password\": \"wrongPwd\"\n" +
                "}";

        jsonLoginDetailsUserNotFound = "{\n" +
                "  \"email\": \"wrong@gmail.com\",\n" +
                "  \"password\": \"bobbyPwd\"\n" +
                "}";

        jsonEditPasswordUser = "{\n" +
                "  \"lastname\": \"Doe\",\n" +
                "  \"firstname\": \"Jane\",\n" +
                "  \"primary_email\": \"janedoe@gmail.com\",\n" +
                "  \"password\": \"password1\",\n" +
                "  \"date_of_birth\": \"1980-6-5\",\n" +
                "  \"gender\": \"Female\"\n" +
                "}";

        jsonEditPasswordLoginDetails = "{\n" +
                "  \"email\": \"janedoe@gmail.com\",\n" +
                "  \"password\": \"PASSword2\"\n" +
                "}";

        jsonPasswordChangeSuccess = "{\n" +
                "  \"old_password\": \"password1\",\n" +
                "  \"new_password\": \"PASSword2\",\n" +
                "  \"repeat_password\": \"PASSword2\"\n" +
                "}";

        jsonPasswordChangeFail = "{\n" +
                "  \"old_password\": \"password1\",\n" +
                "  \"new_password\": \"password2\",\n" +
                "  \"repeat_password\": \"password3\"\n" +
                "}";

        jsonPasswordSame = "{\n" +
                "  \"old_password\": \"password1\",\n" +
                "  \"new_password\": \"password1\",\n" +
                "  \"repeat_password\": \"password1\"\n" +
                "}";

        jsonPasswordFailsRules = "{\n" +
                "  \"old_password\": \"password1\",\n" +
                "  \"new_password\": \"pass\",\n" +
                "  \"repeat_password\": \"pass\"\n" +
                "}";

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


//        when(userRepository.findByUserId(Mockito.anyLong())).thenReturn(fakeUser);
//        when(userValidationService.findByUserId(Mockito.anyString(), Mockito.anyLong())).thenAnswer(i -> {
//            if (i.getArgument(0).equals(fakeUser.getToken()) && i.getArgument(1).equals(fakeUser.getUserId()))
//                return fakeUser;
//            else if ((i.getArgument(0).equals(fakeUser.getToken())) && !(i.getArgument(1).equals(fakeUser.getUserId())))
//                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
//            else
//                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
//        });

    }

    @Test
    public void newUserMissingFieldTest() throws Exception {
        setupMockingNoEmail(userMissJsonPost);

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(userMissJsonPost)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

         mvc.perform(httpReq)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void newUserEmailForbidden() throws Exception {
        setupMocking(userForbiddenJsonPost);

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(userForbiddenJsonPost)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(httpReq)
                .andExpect(status().isConflict());
    }

    @Test
    public void newUserTest() throws Exception {
        setupMockingNoEmail(createUserJsonPost);

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(createUserJsonPost)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isCreated())
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    public void findUserDataUnauthorized() throws Exception {
        setupMocking(createUserJsonPostFindUser);
        String token = "WrongToken"; // Tokens are 30 chars long.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/profiles")
                .header("Token", token);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    public void findUserDataAuthorized() throws Exception {
        setupMocking(createUserJsonPostFindUser);
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

    @Test
    public void doNotLoginIncorrectPassword() throws Exception {
        setupMocking(createUserJsonPostLogin);
        MockHttpServletRequestBuilder request = buildLoginRequest(jsonLoginDetailsIncorrectPass);

        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void doNotLoginUserNotFound() throws Exception {
        setupMocking(createUserJsonPostLogin);
        MockHttpServletRequestBuilder request = buildLoginRequest(jsonLoginDetailsUserNotFound);

        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginAuthorizedUser() throws Exception {
        setupMocking(createUserJsonPostLogin);
        MockHttpServletRequestBuilder request = buildLoginRequest(jsonLoginDetails);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    public void forbiddenLogoutIfTokenNotFound() throws Exception {
        //System won't care if the token is wrong, as long as it isn't null
        //String token = "WrongToken"; // Tokens are 30 chars long.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/logout")
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void successfulLogout() throws Exception {
        setupMocking(createUserJsonPostLogout);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        mvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    /**Test if a user can be edited successfully*/
    public void editProfileSuccessfulTest() throws Exception {
        setupMocking(editProfileUserJson);
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

    @Test
    /** Tests that a user cannot edit another user's profile */
    public void editProfileFailureTest() throws Exception {
        setupMocking(editProfileNastyUserJson);
        // Setup bad edit profile PUT request, userId will never be -1
        MockHttpServletRequestBuilder editRequest = MockMvcRequestBuilders.put("/profiles/{id}", -1)
                .content(editProfileJsonPut)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);

        mvc.perform(editRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    /** Tests that a user can view another users profile */
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
        setupMockingNoEmail(createUserJsonPost);

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
        setupMockingNoEmail(createUserJsonPost);
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
     * @throws Exception
     */
    private MockHttpServletRequestBuilder buildUserChangePassword(String jsonPasswordChange) throws Exception{

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

    @Test
    /**
     * Test creating a user and editing they're password when the password and repeated password match.
     * NOTE: as of now there is no simple way to tell if a password has been updated because password
     * hashes are not returned when retrieving a user.  Though they could be tested by logging in,
     * logging out, changing password, and trying to log in again.
     */
    public void changePasswordSuccessTest() throws Exception {

        // Create user
        setupMockingNoEmail(jsonEditPasswordUser);


        // Change password
        MockHttpServletRequestBuilder editPassReq = buildUserChangePassword(jsonPasswordChangeSuccess);
        mvc.perform(editPassReq)
                .andExpect(status().isOk());

        // Logout
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        mvc.perform(request)
                .andExpect(status().isOk());

        // Login with new password
        MockHttpServletRequestBuilder loginRequest2 = buildLoginRequest(jsonEditPasswordLoginDetails);
        mvc.perform(loginRequest2)
                .andExpect(status().isCreated());
    }

    @Test
    /**
     * Test creating a user and editing they're password when the password and repeated password NO NOT match.
     */
    public void changePasswordFailTest() throws Exception {
        // Create user
        setupMocking(jsonEditPasswordUser);


        // Change password
        MockHttpServletRequestBuilder editPassReq = buildUserChangePassword(jsonPasswordChangeFail);
        mvc.perform(editPassReq)
                .andExpect(status().isBadRequest());   // Don't think there is any other way to test this than bad request

    }

    @Test
    /**
     * Test creating a user and editing they're password when the new password is the same as the old password
     * (new passwords can't match old passwords).
     */
    public void changePasswordNewEqualsOldTest() throws Exception {
        // Create user
        setupMocking(jsonEditPasswordUser);


        // Change password
        MockHttpServletRequestBuilder editPassReq = buildUserChangePassword(jsonPasswordSame);
        mvc.perform(editPassReq)
                .andExpect(status().isBadRequest());   // Don't think there is any other way to test this than bad request
    }

    @Test
    /**
     * Test creating a user and changing their password to a password that violates the password rules
     */
    public void changePasswordFailsRules() throws Exception {
        // Create user
        setupMocking(jsonEditPasswordUser);


        // Change password
        MockHttpServletRequestBuilder editPassReq = buildUserChangePassword(jsonPasswordFailsRules);
        mvc.perform(editPassReq)
                .andExpect(status().isBadRequest());   // Don't think there is any other way to test this than bad request
    }
}