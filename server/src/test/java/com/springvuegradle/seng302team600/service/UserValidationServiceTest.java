package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.RegisterRequest;
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

    private RegisterRequest userData;

    private User dummyUser;
    private Email dummyEmail;

    @BeforeEach
    public void setUp() throws MaximumEmailsException {
        userData = new RegisterRequest();
        userData.setFirstName("Bill");
        userData.setLastName("Ford");
        userData.setPrimaryEmail("billford@email.com");
        userData.setPassword("password123");
        userData.setDateOfBirth(new Calendar.Builder()
                .setDate(1998, 2, 21)
                .setTimeOfDay(14, 0, 0)
                .build().getTime());
        userData.setGender(User.Gender.MALE);
        dummyUser = new User();
        dummyUser.builder(userData);
        dummyEmail = new Email(dummyUser.getPrimaryEmail(), true, dummyUser);
        //This is a valid use of reflection, designed for scenarios like this
        ReflectionTestUtils.setField(dummyUser, "userId", 1L);
        ReflectionTestUtils.setField(dummyEmail, "id", 1L);
        when(userRepository.save(Mockito.any(User.class))).then(i -> {
            dummyUser = i.getArgument(0);
            return dummyUser;
        });
        when(userRepository.findByUserId(Mockito.anyLong())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser.getUserId())) return dummyUser;
            else return null;
        });
        when(userRepository.findByToken(Mockito.anyString())).thenAnswer(i -> {
            if (i.getArgument(0).equals(dummyUser.getToken())) return dummyUser;
            else return null;
        });
        when(emailRepository.save(Mockito.any(Email.class))).then(i -> {
            dummyEmail = i.getArgument(0);
            return dummyEmail;
        });
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
        String token = userService.login(userData.getPrimaryEmail(), userData.getPassword());
        assertNotNull(token);
        assertEquals(token, dummyUser.getToken());
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
    public void userNotFoundByIdForbidden() {
        String token = "testToken";
        dummyUser.setToken(token);
        dummyUser.setTokenTime();
        assertThrows(ResponseStatusException.class, () -> userService.findByUserId(token, -1L));
    }
}