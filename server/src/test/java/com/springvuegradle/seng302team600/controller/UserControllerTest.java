package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.springvuegradle.seng302team600.advice.ExceptionAdvice;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
//@AutoConfigureMockMvc
class UserControllerTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailRepository emailRepository;
    @InjectMocks
    private UserValidationService userValidationService;
    @InjectMocks
    private UserController userController;

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

    private ObjectMapper objectMapper;

    @BeforeClass
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
    }

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        mvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new ExceptionAdvice())
                .build();
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
                .accept(MediaType.APPLICATION_JSON);

        // Perform POST
        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isCreated())
                .andReturn();

        // Test Token
        assertNotNull(result.getResponse());
    }

    @Test
    public void findUserDataUnauthorized() throws Exception {
        String token = "WrongToken"; // Tokens are 30 chars long.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/profiles")
                .header("Token", token);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertEquals(null, result.getResponse());
    }

    @Test
    public void findUserDataAuthorized() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/profiles")
                .content(createUserJsonPostFindUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        String token = result.getResponse().getContentAsString();

        request = MockMvcRequestBuilders.get("/profiles")
                .header("Token", token);
        result = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        assertEquals("Tim", jsonNode.get("firstname").asText());
    }

    /**
     * Helper function which registers a person to the mock database
     * @param personData string to be processed and registered as user
     * @return the token gained from registering
     */
    public String registerPersonHelper(String personData) throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/profiles")
                .content(personData)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(request)
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    /**
     * Helper function which will logout person from mock database
     * @param token from person to be logged out
     */
    public void logoutPersonHelper(String token) throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", token);

        mvc.perform(request);
    }

    @Test
    public void doNotLoginIncorrectPassword() throws Exception {
        String token = registerPersonHelper(createUserJsonPostLogin);
        logoutPersonHelper(token);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/login")
                .content(jsonLoginDetailsIncorrectPass)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void doNotLoginUserNotFound() throws Exception {
        String token = registerPersonHelper(createUserJsonPostLogin);
        logoutPersonHelper(token);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/login")
                .content(jsonLoginDetailsUserNotFound)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginAuthorizedUser() throws Exception {
        String token = registerPersonHelper(createUserJsonPostLogin);
        logoutPersonHelper(token);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/login")
                .content(jsonLoginDetails)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    public void forbiddenLogoutIfTokenNotFound() throws Exception {
        String token = "WrongToken"; // Tokens are 30 chars long.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", token);
        mvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void successfulLogout() throws Exception {
        String token = registerPersonHelper(createUserJsonPostLogout);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", token);
        mvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    /**Test if a user can be edited successfully*/
    public void editProfileSuccessfulTest() throws Exception {
        String token = registerPersonHelper(editProfileUserJson);
        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/profiles")
                .header("Token", token);
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
                .header("Token", token);
        // Perform PUT
        mvc.perform(editRequest)
                .andExpect(status().isOk());

        getRequest = MockMvcRequestBuilders.get("/profiles")
                .header("Token", token);
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
        String token = registerPersonHelper(editProfileNastyUserJson);
        // Setup bad edit profile PUT request, userId will never be -1
        MockHttpServletRequestBuilder editRequest = MockMvcRequestBuilders.put("/profiles/{id}", -1)
                .content(editProfileJsonPut)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", token);

        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/profiles")
                .header("Token", token);
        mvc.perform(editRequest)
                .andExpect(status().isForbidden());
    }
}