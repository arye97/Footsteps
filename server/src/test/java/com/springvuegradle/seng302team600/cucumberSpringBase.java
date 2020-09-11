package com.springvuegradle.seng302team600;

import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.ActivityRepository;
import com.springvuegradle.seng302team600.repository.OutcomeRepository;
import com.springvuegradle.seng302team600.repository.ResultRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.mockito.Mockito.mock;

@ContextConfiguration(classes = {Application.class})
@SpringBootTest
public abstract class cucumberSpringBase {

    @Autowired
    protected UserAuthenticationService userAuthenticationService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected OutcomeRepository outcomeRepository;

    @Autowired
    protected ActivityRepository activityRepository;

    @Autowired
    protected ResultRepository resultRepository;

    protected Map<String, User> savedUsers;

    protected void setup() {
        this.userRepository = mock(UserRepository.class);
        this.outcomeRepository = mock(OutcomeRepository.class);
        this.activityRepository = mock(ActivityRepository.class);
        this.resultRepository = mock(ResultRepository.class);
        this.userAuthenticationService = mock(UserAuthenticationService.class);
    }
}
