package com.springvuegradle.seng302team600;

import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    protected Map<String, User> savedUsers;

    protected void setup() {
        this.userRepository = mock(UserRepository.class);
        this.userAuthenticationService = mock(UserAuthenticationService.class);
    }
}
