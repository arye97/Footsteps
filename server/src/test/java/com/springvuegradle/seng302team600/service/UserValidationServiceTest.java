package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.RegisterRequest;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserValidationServiceTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private EmailRepository emailRepository;

    @Autowired
    private UserValidationService userService;

    private RegisterRequest userData;

    private Long userId;
    private User dummyUser;
    private Email dummyEmail;

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
            System.out.printf("%d %d", i.getArgument(0), dummyUser.getUserId());
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
        userId = dummyUser.getUserId();
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
        System.out.println(userId);
        User user = userRepository.findByUserId(userId);
        System.out.println(user);
        user.setToken(token);
        userRepository.save(user);
        userService.logout(token);
        user = userRepository.findByUserId(userId);
        assertNull(user.getToken());
    }
}