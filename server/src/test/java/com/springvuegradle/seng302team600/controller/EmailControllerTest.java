package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.model.UserRole;
import com.springvuegradle.seng302team600.payload.UserRegisterRequest;
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

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long DEFAULT_EMAIL_ID = 1L;

    private ObjectMapper objectMapper;
    private User dummyUser1;
    private User dummyUser2; // Used when a second user is required
    private Email dummyEmail;
    private final String validToken = "valid";
    private boolean defaultAdminIsRegistered;

    @BeforeEach
    void setUp() {
        defaultAdminIsRegistered = false;

        objectMapper = new ObjectMapper();
        MockitoAnnotations.initMocks(this);
        dummyUser1 = new User();
    }

    private final String createUserJsonViewUser2 = JsonConverter.toJson(true,
            "lastname", "Cucumber",
            "firstname", "Larry",
            "primary_email", "larry@gmail.com",
            "password", "larrysPassword",
            "date_of_birth", "2002-1-20",
            "gender", "Female");

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
        when(userValidationService.findByToken(Mockito.anyString())).thenAnswer(i -> {
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
        when(userValidationService.findByUserId(Mockito.anyString(), Mockito.anyLong())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser1.getToken()) && i.getArgument(1).equals(dummyUser1.getUserId()))
                return dummyUser1;
            else if ((i.getArgument(0).equals(dummyUser1.getToken())) && !(i.getArgument(1).equals(dummyUser1.getUserId())))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            else
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        ReflectionTestUtils.setField(dummyUser1, "userId", DEFAULT_USER_ID);
        ReflectionTestUtils.setField(dummyEmail, "id", DEFAULT_EMAIL_ID);
        when(userValidationService.login(Mockito.anyString(),Mockito.anyString())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyEmail.getEmail()) && dummyUser1.checkPassword(i.getArgument(1))) return new UserResponse("ValidToken", dummyUser1.getUserId());
            else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
        Mockito.doAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser1.getToken())) dummyUser1.setToken(null);
            return null;
        }).when(userValidationService).logout(Mockito.anyString());
        dummyUser1.setToken(validToken);
        dummyUser1.setTokenTime();

        // Second user
        regReq = objectMapper.treeToValue(objectMapper.readTree(createUserJsonViewUser2), UserRegisterRequest.class);
        dummyUser2 = new User(regReq);
        Email fakeEmail = new Email(dummyUser2.getPrimaryEmail(), true, dummyUser2);
        ReflectionTestUtils.setField(dummyUser2, "userId", 10L);
        ReflectionTestUtils.setField(fakeEmail, "id", 10L);
        when(userValidationService.viewUserById(Mockito.anyLong(), Mockito.anyString())).thenAnswer(i -> {
            if ((long) i.getArgument(0) == 10L && i.getArgument(1) == validToken) return dummyUser2;
            else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });
    }



    // ----------- Tests -----------

    private final String createUser1JsonPost = JsonConverter.toJson(true,
            "lastname", "Kim",
            "firstname", "Tim",
            "primary_email", "tim@gmail.com",
            "password", "pinPwd",
            "date_of_birth", "2001-7-9",
            "gender", "Non-Binary");
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
        Long userId = dummyUser1.getUserId();
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


    private final String addEmailsJsonPostAdditionalEmails = JsonConverter.toJson(true,
            "additional_email", new Object[]{
                    "sillybilly@yahoo.com", "meanyweasley@yahoo.com"});
    @Test
    public void addAdditionalEmails_Success() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser1.getUserId();
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

    private final String addEmailsJsonPostCurrentPrimaryEmailInAdditionalEmails = JsonConverter.toJson(true,
            "additional_email", new Object[]{
                    "sillybilly@yahoo.com", "tim@gmail.com"});
    /**
     * Tests addEmails when the User submits a primary email in the list of additional emails.
     * This fails as addEmails does not have the functionality to swap primary email into additional email.
     */
    @Test
    public void addAdditionalEmails_Failure_WhenUserIsAlreadyUsingEmailAsPrimaryEmail() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser1.getUserId();
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


    private final String addEmailsJsonPostCurrentRemovedOneEmailInAdditionalEmails = JsonConverter.toJson(true,
            "additional_email", new Object[]{"sillybilly@yahoo.com"});
    /**
     * Tests addEmails when the User already has 2 existing additional emails A and B
     * and the User decides to remove B.
     * User should end up with an updated additional email list consisting of email A.
     */
    @Test
    public void addAdditionalEmails_Success_WhenUserSubmitsAnUpdatedAdditionalEmailsList() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser1.getUserId();
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


    private final String updateEmailsJsonPutPrimaryAndAdditionalEmails = JsonConverter.toJson(true,
            "primary_email", "lecousindangereux@gmail.com",
            "additional_email", new Object[]{"smellyjerry@yahoo.com"});
    /**
     * Tests updateEmails when a User with no additional emails
     * submits a new primary email and a list of additional emails.
     */
    @Test
    public void updatePrimaryAndAdditionalEmails_Success_WithNoExistingAdditionalEmails() throws Exception {
        setupMocking(createUser1JsonPost);
        Long userId = dummyUser1.getUserId();
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
        Long userId = dummyUser1.getUserId();
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
        Long userId = dummyUser1.getUserId();
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
        Long userId = dummyUser1.getUserId();
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
