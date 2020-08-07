package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.Activity;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.FeedEventRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;

@WebMvcTest(FeedEventController.class)
public class FeedEventControllerTest {

    @MockBean
    private UserAuthenticationService userAuthenticationService;
    @MockBean
    private FeedEventRepository feedEventRepository;
    @MockBean
    private ActivityRepository activityRepository;
    @Autowired
    private MockMvc mvc;

    private static final Long USER_ID_1 = 1L;
    private static final Long USER_ID_2 = 2L;
    private User dummyUser1;
    private User dummyUser2;
    private final String validToken = "valid";
    private static final Long ACTIVITY_ID_1 = 1L;
    private Activity dummyActivity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        dummyUser1 = new User();
        dummyUser2 = new User();
        dummyActivity = new Activity();

        // Mocking UserAuthenticationService
        when(userAuthenticationService.findByUserId(Mockito.any(String.class), Mockito.any(Long.class))).thenAnswer(i -> {
            String token = i.getArgument(0);
            Long id = i.getArgument(1);
            if (token.equals(validToken)) {
                if (id.equals(USER_ID_1)) {
                    return dummyUser1;
                } else if (id.equals(USER_ID_2)) {
                    return dummyUser2;
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
        });
        when(userAuthenticationService.hasAdminPrivileges(Mockito.any())).thenAnswer(i ->
                ((User) i.getArgument(0)).getRole() >= 10);

        // Mocking FeedEventRepository
        //TODO Add mocking here for FeedEventRepository functions

        // Mocking ActivityRepository
        when(activityRepository.findByActivityId(Mockito.anyLong())).thenAnswer(i -> {
            Long id = i.getArgument(0);
            if (id.equals(ACTIVITY_ID_1)) {
                return dummyActivity;
            } else {
                return null;
            }
        });
    }

}
