package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.RegisterRequest;
import com.springvuegradle.seng302team600.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;
import org.mockito.Mock;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Autowired
    private UserValidationService userService;

    private RegisterRequest userData;

    private Long userId;

    @BeforeEach
    public void setUp() throws Exception {
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
        User user = userRepository.save(new User().builder(userData));
        userId = user.getUserId();
    }

    @AfterEach
    public void tear() {
        userRepository.deleteById(userId);
    }

    @Test
    public void doNotLoginUnauthorizedUsers() throws Exception {
        String token = userService.login(userData.getPrimaryEmail(), "wrongPassword");
        assertNull(token);
    }

    @Test
    public void loginAuthorizedUsers() throws Exception {
        String token = userService.login(userData.getPrimaryEmail(), userData.getPassword());
        assertNotNull(token);
        User user = userRepository.findByToken(token);
        assertEquals(token, user.getToken());
    }

    @Test
    public void tokenIsRemovedFromUser() {
        String token = "testToken";
        User user = userRepository.findByUserId(userId);
        user.setToken(token);
        userRepository.save(user);

        userService.logout(token);
        user = userRepository.findByUserId(userId);
        assertNull(user.getToken());
    }
}