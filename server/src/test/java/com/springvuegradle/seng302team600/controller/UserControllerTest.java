package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.springvuegradle.seng302team600.model.LoggedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    private MockHttpSession session;

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

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
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
                "  \"gender\": \"male\"\n" +
                "}";

        editProfileNastyUserJson = "{\n" +
                "  \"lastname\": \"Smith\",\n" +
                "  \"firstname\": \"Jane\",\n" +
                "  \"primary_email\": \"janesmith@gmail.com\",\n" +
                "  \"password\": \"pass\",\n" +
                "  \"date_of_birth\": \"1980-6-5\",\n" +
                "  \"gender\": \"female\"\n" +
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

        session = new MockHttpSession();
        objectMapper = new ObjectMapper();
    }

    @Test
    /**Test if newUser catches missing field*/
    public void newUserMissingFieldTest() throws Exception {

        // Setup POST
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(userMissJsonPost)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Perform POST
         mvc.perform(httpReq)
                .andExpect(status().isBadRequest());
    }

    @Test
    /**Test if newUser catches EmailAlreadyRegisteredException*/
    public void newUserEmailForbidden() throws Exception {
        // Setup POST
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(userForbiddenJsonPost)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Perform POST
        mvc.perform(httpReq)
                .andExpect(status().isCreated());

        // Setup POST
        httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(userForbiddenJsonPost)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Perform POST
        mvc.perform(httpReq)
                .andExpect(status().isForbidden());
    }

    @Test
    /**Test if a new User can be created*/
    public void newUserTest() throws Exception {

        // Setup POST
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(createUserJsonPost)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        // Perform POST
        mvc.perform(httpReq)
                .andExpect(status().isCreated());

        // Test session
        assertNotNull(((LoggedUser) session.getAttribute("loggedUser")).getUserId());
    }

    @Test
    /**Will be removed in future development*/
    public void allTest() throws Exception {
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get("/listprofile")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(httpReq)
                .andExpect(status().isOk());

    }

    @Test
    /**Test findUserData, authorized and unauthorized conditions*/
    public void findUserDataTest() throws Exception {
        // Get profile (Unauthorized)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/profiles")
                .session(session);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Test result (null)
        assertEquals("", result.getResponse().getContentAsString());

        // Register profile
        request = MockMvcRequestBuilders.post("/profiles")
                .content(createUserJsonPostFindUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        mvc.perform(request)
                .andExpect(status().isCreated());

        // Get profile (Authorized)
        request = MockMvcRequestBuilders.get("/profiles")
                .session(session);

        result = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        // Get Response as JsonNode
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);

        // Test response
        assertEquals("Tim", jsonNode.get("firstname").asText());
    }

    @Test
    /**Tests login conditions, successful and unsuccessful*/
    public void logInTest() throws Exception {
        // Register profile
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/profiles")
                .content(createUserJsonPostLogin)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        mvc.perform(request)
                .andExpect(status().isCreated());

        // Logout profile
        request = MockMvcRequestBuilders.post("/logout")
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        mvc.perform(request)
                .andExpect(status().isOk());

        // Login profile (Incorrect Password)
        request = MockMvcRequestBuilders.post("/login")
                .content(jsonLoginDetailsIncorrectPass)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        mvc.perform(request)
                .andExpect(status().isUnauthorized());

        // Login profile (User not found)
        request = MockMvcRequestBuilders.post("/login")
                .content(jsonLoginDetailsUserNotFound)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        mvc.perform(request)
                .andExpect(status().isUnauthorized());

        // Login profile (Success)
        request = MockMvcRequestBuilders.post("/login")
                .content(jsonLoginDetails)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        // Perform POST
        mvc.perform(request)
                .andExpect(status().isCreated());

        // Test session
        assertNotNull(((LoggedUser) session.getAttribute("loggedUser")).getUserId());
    }

    @Test
    /**Tests logout conditions, successful and forbidden*/
    public void logOutTest() throws Exception {
        // Logout profile (Already logged out)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/logout")
                .session(session)
                .accept(MediaType.APPLICATION_JSON);

        // Perform POST
        MvcResult result = mvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

        // Test response
        assertEquals("Already logged out", result.getResponse().getContentAsString());

        // Register profile
        request = MockMvcRequestBuilders.post("/profiles")
                .content(createUserJsonPostLogout)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        mvc.perform(request);

        // Logout profile (Logout successful)
        request = MockMvcRequestBuilders.post("/logout")
                .session(session)
                .accept(MediaType.APPLICATION_JSON);

        // Perform POST
        result = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        // Test response
        assertEquals("Logout successful", result.getResponse().getContentAsString());
    }

    @Test
    /**Test if a user can be edited successfully*/
    public void editProfileSuccessfulTest() throws Exception {
        // Register a new user to edit the profile of
        MockHttpServletRequestBuilder registerRequest = MockMvcRequestBuilders.post("/profiles")
                .content(editProfileUserJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        mvc.perform(registerRequest)
                .andExpect(status().isCreated());

        long userId = ((LoggedUser)session.getAttribute("loggedUser")).getUserId();

        // Setup edit profile PUT request and GET request
        MockHttpServletRequestBuilder editRequest = MockMvcRequestBuilders.put("/profiles/{id}", userId)
                .content(editProfileJsonPut)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/profiles")
                .session(session);

        // Perform PUT
        mvc.perform(editRequest)
                .andExpect(status().isOk());

        MvcResult result = mvc.perform(getRequest)
                              .andExpect(status().isOk())
                              .andReturn();

        // Get Response as JsonNode
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);

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
        // Register a new user to not edit the profile of
        MockHttpServletRequestBuilder registerRequest = MockMvcRequestBuilders.post("/profiles")
                .content(editProfileNastyUserJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        mvc.perform(registerRequest)
                .andExpect(status().isCreated());

        long userId = ((LoggedUser)session.getAttribute("loggedUser")).getUserId();

        // Setup bad edit profile PUT request
        MockHttpServletRequestBuilder editRequest = MockMvcRequestBuilders.put("/profiles/{id}", userId - 1)
                .content(editProfileJsonPut)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session);

        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/profiles")
                .session(session);

        // Perform PUT
        mvc.perform(editRequest)
                .andExpect(status().isForbidden());
    }
}