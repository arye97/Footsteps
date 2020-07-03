package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ActivityControllerTest.class)
class ActivityControllerTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private EmailRepository emailRepository;
    @MockBean
    private UserValidationService userValidationService;
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ApplicationContext context;
    @Autowired
    public void context(ApplicationContext context) { this.context = context; }


    private ObjectMapper objectMapper;
}