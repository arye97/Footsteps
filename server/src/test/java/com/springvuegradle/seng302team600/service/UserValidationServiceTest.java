package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.UserRegisterRequest;
import com.springvuegradle.seng302team600.payload.UserResponse;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest(UserValidationService.class)
class UserValidationServiceTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private EmailRepository emailRepository;

    @Autowired
    private UserValidationService userService;

    private UserRegisterRequest userData;

    private User dummyUser;
    private User dummyUser1;
    private Email dummyEmail;
    private Email dummyEmail1;

    /**
     * A function which can be used to set up dummyUser1, the second test user
     * There are minimal test cases where this is required so it is only sometimes needed
     */
    public void additionalUser() {
        userData = new UserRegisterRequest();
        userData.setFirstName("Bob");
        userData.setLastName("Builder");
        userData.setPrimaryEmail("bobby@email.com");
        userData.setPassword("myPassword");
        userData.setDateOfBirth(new Calendar.Builder()
                .setDate(1996, 12, 26)
                .setTimeOfDay(14, 0, 0)
                .build().getTime());
        userData.setGender(User.Gender.MALE);
        dummyUser1.builder(userData);
        dummyEmail1 = new Email(dummyUser1.getPrimaryEmail(), true, dummyUser1);
        //This is a valid use of reflection, designed for scenarios like this
        ReflectionTestUtils.setField(dummyEmail1, "id", 2L);
    }

    @BeforeEach
    public void setUp() {
        dummyUser1 = new User();
        userData = new UserRegisterRequest();
        userData.setFirstName("Bill");
        userData.setLastName("Ford");
        userData.setPrimaryEmail("billford@email.com");
        userData.setPassword("password123");
        userData.setDateOfBirth(new Calendar.Builder()
                .setDate(1998, 2, 21)
                .setTimeOfDay(14, 0, 0)
                .build().getTime());
        userData.setGender(User.Gender.MALE);
        dummyUser = new User(userData);
        dummyEmail = new Email(dummyUser.getPrimaryEmail(), true, dummyUser);
        //This is a valid use of reflection, designed for scenarios like this
        ReflectionTestUtils.setField(dummyUser, "userId", 1L);
        ReflectionTestUtils.setField(dummyUser1, "userId", 2L);
        ReflectionTestUtils.setField(dummyEmail, "id", 1L);
        when(userRepository.save(Mockito.any(User.class))).then(i -> i.getArgument(0));
        when(userRepository.findByUserId(Mockito.anyLong())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser.getUserId())) return dummyUser;
            if (i.getArgument(0).equals(dummyUser1.getUserId())) return dummyUser1;
            else return null;
        });
        when(userRepository.findByToken(Mockito.anyString())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser.getToken())) return dummyUser;
            if (i.getArgument(0).equals(dummyUser1.getToken())) return dummyUser1;
            else return null;
        });
        when(emailRepository.save(Mockito.any(Email.class))).then(i -> i.getArgument(0));
        when(emailRepository.findByEmail(Mockito.anyString())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyEmail.getEmail())) return dummyEmail;
            else return null;
        });
        when(emailRepository.getOne(Mockito.anyLong())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyEmail.getId())) return dummyEmail;
            else return null;
        });
        when(emailRepository.existsEmailByEmail(Mockito.anyString())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyEmail.getEmail())) return dummyEmail;
            else return null;
        });
    }

    @Test
    public void doNotLoginUnauthorizedUsers() {
        assertThrows(ResponseStatusException.class, () -> userService.login(userData.getPrimaryEmail(), "wrongPassword"));
    }

    @Test
    public void loginAuthorizedUsers() {
        UserResponse userResponse = userService.login(userData.getPrimaryEmail(), userData.getPassword());
        assertNotNull(userResponse);
        assertEquals(dummyUser.getUserId(), userResponse.getUserId());
        assertEquals(dummyUser.getToken(), userResponse.getToken());
    }

    @Test
    public void tokenIsRemovedFromUser() {
        String token = "testToken";
        dummyUser.setToken(token);
        userService.logout(token);
        assertNull(dummyUser.getToken());
    }

    @Test
    public void userFoundByTokenAuthorized() {
        String token = "testToken";
        dummyUser.setToken(token);
        dummyUser.setTokenTime();
        User user = userService.findByToken(token);
        assertEquals(dummyUser, user);
    }

    @Test
    public void userNotFoundByTokenUnauthorized() {
        String token = "testToken";
        dummyUser.setToken(token);
        dummyUser.setTokenTime();
        assertThrows(ResponseStatusException.class, () -> userService.findByToken("wrongToken"));
    }

    @Test
    public void userFoundByIdAuthorized() {
        String token = "testToken";
        dummyUser.setToken(token);
        dummyUser.setTokenTime();
        User user = userService.findByUserId(token, dummyUser.getUserId());
        assertEquals(dummyUser, user);
    }

    @Test
    public void userNotFoundByIdUnauthorized() {
        String token = "testToken";
        dummyUser.setToken(token);
        dummyUser.setTokenTime();
        assertThrows(ResponseStatusException.class, () -> userService.findByUserId("wrongToken", dummyUser.getUserId()));
    }

    @Test
    public void adminFindsUserById() {
        String token = "testToken";
        additionalUser();
        ReflectionTestUtils.setField(dummyUser1, "role", 10);
        dummyUser1.setToken(token);
        dummyUser1.setTokenTime();
        User user = userService.findByUserId(token, dummyUser.getUserId());
        assertEquals(dummyUser, user);
    }

    @Test
    public void adminDoesntFindUserById() {
        String token = "testToken";
        additionalUser();
        dummyUser1.setToken(token);
        dummyUser1.setTokenTime();
        assertThrows(ResponseStatusException.class, () -> userService.findByUserId(token, dummyUser.getUserId()));
    }

    @Test
    public void userNotFoundByIdForbidden() {
        String token = "testToken";
        additionalUser();
        dummyUser1.setToken(token);
        dummyUser1.setTokenTime();
        // Try to find another user when this user is not an admin
        assertThrows(ResponseStatusException.class, () -> userService.findByUserId(token, dummyUser.getUserId()));
    }

    @Test
    /**Tests that if a user has an invalid code they cannot see anything and an error is thrown*/
    public void viewUserByIdInvalidToken() {
        String token = "myToken";
        dummyUser.setToken(token);
        dummyUser.setTokenTime();
        assertThrows(ResponseStatusException.class, () -> userService.viewUserById(dummyUser.getUserId(), "InvalidToken"));
    }

    @Test
    /**Tests that if a user looks for a user that doesn't exist an error is thrown*/
    public void viewUserByIdInvalidId() {
        String token = "myToken";
        dummyUser.setToken(token);
        dummyUser.setTokenTime();
        assertThrows(ResponseStatusException.class, () -> userService.viewUserById(3L, token));
    }

    @Test
    /**Tests that if a user tries to view a user that does exist and is logged in then the user is returned*/
    public void viewUserByIdSuccess() {
        String token = "myToken";
        dummyUser.setToken(token);
        dummyUser.setTokenTime();
        // Set up second user
        additionalUser();
        // Check viewing the user
        User result = userService.viewUserById(2L, token);
        assertEquals("Bob", result.getFirstName());
        assertEquals("Builder", result.getLastName());
        assertEquals("bobby@email.com", result.getPrimaryEmail());
        assertEquals(User.Gender.MALE, result.getGender());
    }
}