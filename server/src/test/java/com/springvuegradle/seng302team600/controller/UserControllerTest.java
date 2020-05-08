package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.RegisterRequest;
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

    private ObjectMapper objectMapper;

    private User dummyUser;
    private RegisterRequest regReq;
    private Email dummyEmail;
    private String validToken = "valid";

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
        when(userRepository.save(Mockito.any(User.class))).thenReturn(dummyUser);
        when(emailRepository.save(Mockito.any(Email.class))).thenReturn(dummyEmail);
        when(emailRepository.findByEmail(Mockito.matches(dummyEmail.getEmail()))).thenReturn(dummyEmail);
        when(emailRepository.getOne(Mockito.anyLong())).thenReturn(dummyEmail);
        when(userValidationService.findByToken(Mockito.anyString())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser.getToken())) return dummyUser;
            else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        when(userRepository.findByUserId(Mockito.anyLong())).thenReturn(dummyUser);
        when(emailRepository.existsEmailByEmail(Mockito.anyString())).thenReturn(false);
        when(userValidationService.findByUserId(Mockito.anyString(), Mockito.anyLong())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser.getToken()) && i.getArgument(1).equals(dummyUser.getUserId())) return dummyUser;
            else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        ReflectionTestUtils.setField(dummyUser, "userId", 1L);
        ReflectionTestUtils.setField(dummyEmail, "id", 1L);
        when(userValidationService.login(Mockito.anyString(),Mockito.anyString())).thenAnswer(i -> {
                if (i.getArgument(0).equals(dummyEmail.getEmail()) && dummyUser.checkPassword(i.getArgument(1))) return "ValidToken";
                else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        Mockito.doAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser.getToken())) dummyUser.setToken(null);
            return null;
        }).when(userValidationService).logout(Mockito.anyString());
        dummyUser.setToken(validToken);
        dummyUser.setTokenTime();
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

    @Test
    public void doNotLoginIncorrectPassword() throws Exception {
        setupMocking(createUserJsonPostLogin);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/login")
                .content(jsonLoginDetailsIncorrectPass)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void doNotLoginUserNotFound() throws Exception {
        setupMocking(createUserJsonPostLogin);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/login")
                .content(jsonLoginDetailsUserNotFound)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginAuthorizedUser() throws Exception {
        setupMocking(createUserJsonPostLogin);
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

        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/profiles")
                .header("Token", validToken);
        mvc.perform(editRequest)
                .andExpect(status().isUnauthorized());
    }
}