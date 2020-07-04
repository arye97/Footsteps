package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.model.UserRole;
import com.springvuegradle.seng302team600.payload.UserRegisterRequest;
import com.springvuegradle.seng302team600.payload.UserResponse;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityController.class)
class ActivityControllerTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private EmailRepository emailRepository;
    @MockBean
    private ActivityRepository activityRepository;
    @MockBean
    private UserValidationService userValidationService;
    @Autowired
    private MockMvc mvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long DEFAULT_EMAIL_ID = 1L;

    private User dummyUser1;
    private User dummyUser2; // Used when a second user is required
    private Email dummyEmail;
    private final String validToken = "valid";
    private boolean defaultAdminIsRegistered;

    @BeforeEach
    void setUp() {
        defaultAdminIsRegistered = false;

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

    private final String newActivityJson = JsonConverter.toJson(true,
            "activity_name", "Kaikoura Coast Track race",
            "description", "A big and nice race on a lovely peninsula",
            "activity_type", new Object[]{
                    "tramping", "hiking"
            },
            "continuous", false,
            "start_time", "2020-02-20T08:00:00+1300",
            "end_time", "2020-02-20T08:00:00+1300",
            "location", "Kaikoura, NZ");

    /**
     * Test successful createion of new activity.
     */
    @Test
    void newActivity() throws Exception {

        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles/{profileId}/activities", DEFAULT_USER_ID)
                .content(newActivityJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isCreated())
                .andReturn();
        assertNotNull(result.getResponse());
    }


    private final String newActivityWrongDateFormatJson = JsonConverter.toJson(true,
            "activity_name", "Port Hills Rock Climbing",
            "description", "Cattlestop Crag lead climbing",
            "activity_type", new Object[]{
                    "climbing", "abseiling"
            },
            "continuous", false,
            "start_time", "2020-02-20T08:00:00Z",
            "end_time", "2020-02-20T08:00:00Z",
            "location", "Christchurch, NZ");

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
}