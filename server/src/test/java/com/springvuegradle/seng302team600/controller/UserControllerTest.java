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
    private String editProfileJsonPost;
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
                "  \"gender\": \"male\"\n" +
                "}";

        userForbiddenJsonPost = "{\n" +
                "  \"lastname\": \"Smith\",\n" +
                "  \"firstname\": \"Jim\",\n" +
                "  \"primary_email\": \"jsmith@google.com\",\n" +
                "  \"password\": \"JimJamPwd\",\n" +
                "  \"date_of_birth\": \"1995-1-1\",\n" +
                "  \"gender\": \"male\"\n" +
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
                "  \"gender\": \"female\",\n" +
                "  \"fitness\": 3,\n" +
                "  \"passports\": [\"Australia\", \"Antarctica\"]\n" +
                "}";

        createUserJsonPostFindUser = "{\n" +
                "  \"lastname\": \"Kim\",\n" +
                "  \"firstname\": \"Tim\",\n" +
                "  \"primary_email\": \"tim@gmail.com\",\n" +
                "  \"password\": \"pinPwd\",\n" +
                "  \"date_of_birth\": \"2001-7-9\",\n" +
                "  \"gender\": \"non_binary\"\n" +
                "}";

        createUserJsonPostLogin = "{\n" +
                "  \"lastname\": \"Dean\",\n" +
                "  \"firstname\": \"Bob\",\n" +
                "  \"middlename\": \"Mark\",\n" +
                "  \"primary_email\": \"bobby@gmail.com\",\n" +
                "  \"password\": \"bobbyPwd\",\n" +
                "  \"date_of_birth\": \"1976-9-2\",\n" +
                "  \"gender\": \"non_binary\"\n" +
                "}";

        createUserJsonPostLogout = "{\n" +
                "  \"lastname\": \"kite\",\n" +
                "  \"firstname\": \"Kate\",\n" +
                "  \"primary_email\": \"kite@gmail.com\",\n" +
                "  \"password\": \"kitPwd\",\n" +
                "  \"date_of_birth\": \"2002-1-2\",\n" +
                "  \"gender\": \"female\"\n" +
                "}";

        editProfileJsonPost = "{\n" +
                "  \"id\": \"1\",\n" +
                "  \"bio\": \"About ME\",\n" +
                "  \"firstname\": \"Ben\"\n" +
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

//    @Test
//    /**Test if a new User can be created*/
//    public void editProfileTest() throws Exception {
//
//        // Setup POST
//        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/editprofile")
//                .content(editProfileJsonPost)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON);
//
//        // Perform POST
//        MvcResult result = mvc.perform(httpReq)
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Get Response as JsonNode
//        String jsonResponseStr = result.getResponse().getContentAsString();
//        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
//
//        // Test response
//        assertEquals("About ME", jsonNode.get("bio").asText());
//        assertEquals("Ben", jsonNode.get("firstname").asText());
//    }
}