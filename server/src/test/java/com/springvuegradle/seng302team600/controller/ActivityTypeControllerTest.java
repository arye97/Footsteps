package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.model.UserRole;
import com.springvuegradle.seng302team600.payload.RegisterRequest;
import com.springvuegradle.seng302team600.payload.UserResponse;
import com.springvuegradle.seng302team600.repository.ActivityTypeRepository;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(ActivityTypeControllerTest.class)
class ActivityTypeControllerTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private EmailRepository emailRepository;
    @MockBean
    private ActivityTypeRepository activityTypeRepository;
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
    private List<ActivityType> activityTypeMockTable = new ArrayList<>();

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
        RegisterRequest regReq;
        regReq = objectMapper.treeToValue(objectMapper.readTree(json), RegisterRequest.class);
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
        regReq = objectMapper.treeToValue(objectMapper.readTree(createUserJsonViewUser2), RegisterRequest.class);
        dummyUser2 = new User(regReq);
        Email fakeEmail = new Email(dummyUser2.getPrimaryEmail(), true, dummyUser2);
        ReflectionTestUtils.setField(dummyUser2, "userId", 10L);
        ReflectionTestUtils.setField(fakeEmail, "id", 10L);
        when(userValidationService.viewUserById(Mockito.anyLong(), Mockito.anyString())).thenAnswer(i -> {
            if ((long) i.getArgument(0) == 10L && i.getArgument(1) == validToken) return dummyUser2;
            else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        });

        // ActivityType repository
        when(activityTypeRepository.save(Mockito.any(ActivityType.class))).thenAnswer(i -> {
            System.out.println("Save");
            activityTypeMockTable.add(i.getArgument(0));
            return activityTypeMockTable.get(activityTypeMockTable.size() - 1);
        });
        when(activityTypeRepository.saveAll(Mockito.any(List.class))).thenAnswer(i -> {
            System.out.println("SaveAll");
            activityTypeMockTable.addAll(i.getArgument(0));
            return true;
        });
    }



    // ----------- Tests -----------


    @Test
    void activityTypesAddedToRepositoryOnStartup() throws Exception {

        System.out.println(activityTypeMockTable);
    }
}