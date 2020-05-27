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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
class EmailControllerTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private EmailRepository emailRepository;
    @MockBean
    private UserValidationService userValidationService;
    @Autowired
    private MockMvc mvc;

    private String createUser1JsonPost;
    private String addEmailsJsonPostAdditionalEmails;
    private String addEmailsJsonPostCurrentPrimaryEmailInAdditionalEmails;
    private String addEmailsJsonPostCurrentRemovedOneEmailInAdditionalEmails;
    private String updateEmailsJsonPutPrimaryAndAdditionalEmails;


    private String createUser2JsonPost;

    private ObjectMapper objectMapper;
    private User dummyUser;
    private RegisterRequest regReq;
    private Email dummyEmail;
    private String validToken = "valid";


    @BeforeEach
    public void setUp() {

        createUser1JsonPost = "{\n" +
                "  \"lastname\": \"Kim\",\n" +
                "  \"firstname\": \"Tim\",\n" +
                "  \"primary_email\": \"tim@gmail.com\",\n" +
                "  \"password\": \"pinPwd\",\n" +
                "  \"date_of_birth\": \"2001-7-9\",\n" +
                "  \"gender\": \"Non-Binary\"\n" +
                "}";

        addEmailsJsonPostAdditionalEmails = "{\n" +
                "  \"additional_email\": [\n" +
                        "  \"sillybilly@yahoo.com\",\n" +
                        "  \"meanyweasley@yahoo.com\"\n" +
                "  ] " +
                "}";

        addEmailsJsonPostCurrentPrimaryEmailInAdditionalEmails = "{\n" +
                "  \"additional_email\": [\n" +
                        "  \"sillybilly@yahoo.com\",\n" +
                        "  \"tim@gmail.com\"\n" +
                "  ] " +
                "}";

        addEmailsJsonPostCurrentRemovedOneEmailInAdditionalEmails = "{\n" +
                "  \"additional_email\": [\n" +
                        "  \"sillybilly@yahoo.com\"\n" +
                "  ] " +
                "}";

        updateEmailsJsonPutPrimaryAndAdditionalEmails = "{\n" +
                "  \"primary_email\": \"lecousindangereux@gmail.com\",\n" +
                "  \"additional_email\": [\n" +
                        "  \"smellyjerry@yahoo.com\"\n" +
                "  ] " +
                "}";


//        createUser2JsonPost = "{\n" +
//                "  \"lastname\": \"Billy\",\n" +
//                "  \"firstname\": \"Silly\",\n" +
//                "  \"primary_email\": \"sillybilly@yahoo.com\",\n" +
//                "  \"password\": \"iamASillyLittleBumbum\",\n" +
//                "  \"date_of_birth\": \"1960-7-9\",\n" +
//                "  \"gender\": \"Male\"\n" +
//                "}";


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
            if (i.getArgument(0).equals(dummyUser.getToken()) && i.getArgument(1).equals(dummyUser.getUserId()))
                return dummyUser;
            else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        // Set up dummy user id and dummy email id
        ReflectionTestUtils.setField(dummyUser, "userId", 1L);
        ReflectionTestUtils.setField(dummyEmail, "id", 1L);
        when(userValidationService.login(Mockito.anyString(), Mockito.anyString())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyEmail.getEmail()) && dummyUser.checkPassword(i.getArgument(1)))
                return "ValidToken";
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
    public void findEmailDataWhenUserIsUnauthorized_Failure() throws Exception {
        setupMocking(createUser1JsonPost);
        String token = "WrongToken"; // Tokens are 30 chars long.
        Long userId = 2L; //this is the wrong userId for this user
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", token);
        MvcResult result = mvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    public void findEmailData_WhenUserIsAuthorized_Success() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser.getUserId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        assertEquals("1", jsonNode.get("userId").asText());
        assertEquals("tim@gmail.com", jsonNode.get("primaryEmail").asText());
        assertEquals("", jsonNode.get("additionalEmails").asText());
    }


    @Test
    public void addAdditionalEmails_Success() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser.getUserId();
        MockHttpServletRequestBuilder getEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        MvcResult result = mvc.perform(getEmailsRequest)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        userId = jsonNode.get("userId").asLong();

        // Setup add emails POST request
        MockHttpServletRequestBuilder addEmailsRequest = MockMvcRequestBuilders.post("/profiles/{profileId}/emails", userId)
                .content(addEmailsJsonPostAdditionalEmails)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        // Perform POST
        mvc.perform(addEmailsRequest)
                .andExpect(status().isCreated());

        // Setup GET request to find updated emails
        getEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        // Perform GET
        result = mvc.perform(getEmailsRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Get Response as JsonNode
        jsonResponseStr = result.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(jsonResponseStr);
        List<String> additionalEmailsFromJson = new ObjectMapper().convertValue(jsonNode.get("additionalEmails"), ArrayList.class);
        List<String> expectedEmails = new ArrayList<>();
        expectedEmails.add("sillybilly@yahoo.com");
        expectedEmails.add("meanyweasley@yahoo.com");

        assertEquals("1", jsonNode.get("userId").asText());
        assertEquals("tim@gmail.com", jsonNode.get("primaryEmail").asText());
        assertEquals(expectedEmails.size(), additionalEmailsFromJson.size());
        assertEquals(expectedEmails, additionalEmailsFromJson);
    }

    /**
     * Tests addEmails when the User submits a primary email in the list of additional emails.
     * This fails as addEmails does not have the functionality to swap primary email into additional email.
     */
    @Test
    public void addAdditionalEmails_Failure_WhenUserIsAlreadyUsingEmailAsPrimaryEmail() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser.getUserId();
        MockHttpServletRequestBuilder getEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        MvcResult result = mvc.perform(getEmailsRequest)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        userId = jsonNode.get("userId").asLong();

        // Setup add emails POST request
        MockHttpServletRequestBuilder addEmailsRequest = MockMvcRequestBuilders.post("/profiles/{profileId}/emails", userId)
                .content(addEmailsJsonPostCurrentPrimaryEmailInAdditionalEmails)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        // Perform POST
        mvc.perform(addEmailsRequest)
                .andExpect(status().isBadRequest());

        // Setup GET request to affirm that emails have not been updated
        getEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        // Perform GET
        result = mvc.perform(getEmailsRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Get Response as JsonNode
        jsonResponseStr = result.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(jsonResponseStr);

        // Assert that no changes have been made
        assertEquals("1", jsonNode.get("userId").asText());
        assertEquals("tim@gmail.com", jsonNode.get("primaryEmail").asText());
        assertEquals("", jsonNode.get("additionalEmails").asText());
    }

    /**
     * Tests addEmails when the User already has 2 existing additional emails A and B
     * and the User decides to remove B.
     * User should end up with an updated additional email list consisting of email A.
     */
    @Test
    public void addAdditionalEmails_Success_WhenUserSubmitsAnUpdatedAdditionalEmailsList() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser.getUserId();
        MockHttpServletRequestBuilder getEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        MvcResult result = mvc.perform(getEmailsRequest)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        userId = jsonNode.get("userId").asLong();

        // Setup add emails POST request
        MockHttpServletRequestBuilder addEmailsRequest = MockMvcRequestBuilders.post("/profiles/{profileId}/emails", userId)
                .content(addEmailsJsonPostAdditionalEmails)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        // Perform POST
        mvc.perform(addEmailsRequest)
                .andExpect(status().isCreated());

        // Setup another add emails POST request
        addEmailsRequest = MockMvcRequestBuilders.post("/profiles/{profileId}/emails", userId)
                .content(addEmailsJsonPostCurrentRemovedOneEmailInAdditionalEmails)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        // Perform POST
        mvc.perform(addEmailsRequest)
                .andExpect(status().isCreated());


        // Setup GET request to affirm that emails have not been updated
        getEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        // Perform GET
        result = mvc.perform(getEmailsRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Get Response as JsonNode
        jsonResponseStr = result.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(jsonResponseStr);
        List<String> additionalEmailsFromJson = new ObjectMapper().convertValue(jsonNode.get("additionalEmails"), ArrayList.class);
        List<String> expectedEmails = new ArrayList<>();
        expectedEmails.add("sillybilly@yahoo.com");
        expectedEmails.add("meanyweasley@yahoo.com");

        assertEquals("1", jsonNode.get("userId").asText());
        assertEquals("tim@gmail.com", jsonNode.get("primaryEmail").asText());
        assertNotEquals(expectedEmails.size(), additionalEmailsFromJson.size());
        assertNotEquals(expectedEmails, additionalEmailsFromJson);
        assertEquals(1, additionalEmailsFromJson.size());
        expectedEmails.remove("meanyweasley@yahoo.com");
        assertEquals(expectedEmails, additionalEmailsFromJson);
    }

    /**
     * Tests updateEmails when a User with no additional emails
     * submits a new primary email and a list of additional emails.
     */
    @Test
    public void updatePrimaryAndAdditionalEmails_Success_WithNoExistingAdditionalEmails() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser.getUserId();
        MockHttpServletRequestBuilder getEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        MvcResult result = mvc.perform(getEmailsRequest)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        userId = jsonNode.get("userId").asLong();

        // Setup update emails PUT request
        MockHttpServletRequestBuilder updateEmailsRequest = MockMvcRequestBuilders.put("/profiles/{profileId}/emails", userId)
                .content(updateEmailsJsonPutPrimaryAndAdditionalEmails)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        // Perform PUT
        mvc.perform(updateEmailsRequest)
                .andExpect(status().isCreated());

        // Setup GET request to test results
        getEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        // Perform GET
        result = mvc.perform(getEmailsRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Get Response as JsonNode
        jsonResponseStr = result.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(jsonResponseStr);
        System.out.println(jsonNode);

        List<String> additionalEmailsFromJson = new ObjectMapper().convertValue(jsonNode.get("additionalEmails"), ArrayList.class);
        List<String> expectedEmails = new ArrayList<>();
        expectedEmails.add("smellyjerry@yahoo.com");

        assertEquals("1", jsonNode.get("userId").asText());
        assertEquals("lecousindangereux@gmail.com", jsonNode.get("primaryEmail").asText());
        assertEquals(expectedEmails.size(), additionalEmailsFromJson.size());
        assertEquals(expectedEmails, additionalEmailsFromJson);
    }

    /**
     * Tests updateEmails when a User with additional emails
     * submits a new primary email and a list of additional emails.
     */
    @Test
    public void updatePrimaryAndAdditionalEmails_Success_WithExistingAdditionalEmails() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser.getUserId();
        MockHttpServletRequestBuilder getEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        MvcResult result = mvc.perform(getEmailsRequest)
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
        userId = jsonNode.get("userId").asLong();

        // Setup add emails POST request
        MockHttpServletRequestBuilder addEmailsRequest = MockMvcRequestBuilders.post("/profiles/{profileId}/emails", userId)
                .content(addEmailsJsonPostAdditionalEmails)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        // Perform POST
        mvc.perform(addEmailsRequest)
                .andExpect(status().isCreated());

        // Setup update emails PUT request
        MockHttpServletRequestBuilder updateEmailsRequest = MockMvcRequestBuilders.put("/profiles/{profileId}/emails", userId)
                .content(updateEmailsJsonPutPrimaryAndAdditionalEmails)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Token", validToken);
        // Perform PUT
        mvc.perform(updateEmailsRequest)
                .andExpect(status().isCreated());

        // Setup GET request to test results
        getEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        // Perform GET
        result = mvc.perform(getEmailsRequest)
                .andExpect(status().isOk())
                .andReturn();

        // Get Response as JsonNode
        jsonResponseStr = result.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(jsonResponseStr);
        System.out.println(jsonNode);

        List<String> additionalEmailsFromJson = new ObjectMapper().convertValue(jsonNode.get("additionalEmails"), ArrayList.class);
        List<String> expectedEmails = new ArrayList<>();
        expectedEmails.add("smellyjerry@yahoo.com");

        assertEquals("1", jsonNode.get("userId").asText());
        assertEquals("lecousindangereux@gmail.com", jsonNode.get("primaryEmail").asText());
        assertEquals(expectedEmails.size(), additionalEmailsFromJson.size());
        assertEquals(expectedEmails, additionalEmailsFromJson);
    }

    @Test
    public void checkIfEmailIsInUse_Success_WhenEmailIsNotInUse() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser.getUserId();
        MockHttpServletRequestBuilder findEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        mvc.perform(findEmailsRequest)
                .andExpect(status().isOk());

        MockHttpServletRequestBuilder checkEmailRequest = MockMvcRequestBuilders.get("/email")
                .header("Token", validToken)
                .header("email", "schmeeeeeeeeeeeer@yahoo.com");
        mvc.perform(checkEmailRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void checkIfEmailIsInUse_Failure_WhenEmailIsInUse() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser.getUserId();
        MockHttpServletRequestBuilder findEmailsRequest = MockMvcRequestBuilders.get("/profiles/{profileId}/emails", userId)
                .header("Token", validToken);
        mvc.perform(findEmailsRequest)
                .andExpect(status().isOk());

        MockHttpServletRequestBuilder checkEmailRequest = MockMvcRequestBuilders.get("/email")
                .header("Token", validToken)
                .header("email", "tim@gmail.com");
        mvc.perform(checkEmailRequest)
                .andExpect(status().isBadRequest());
    }
}
