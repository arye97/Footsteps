package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.exception.MaximumEmailsException;
import com.springvuegradle.seng302team600.exception.MustHavePrimaryEmailException;
import com.springvuegradle.seng302team600.exception.UserNotFoundException;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EmailController {

    private UserValidationService userService;

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    public EmailController(UserRepository userRepository, EmailRepository emailRepository, UserValidationService userService) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.userService = userService;
    }

    /**
     * Return a users emails
     * @param request
     * @param response
     * @return JSON object with primaryEmails and additionalEmails field
     */
    @GetMapping("/emails")
    public Object findUserEmails(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Token");
        User user = userService.findByToken(token);
        user.setTransientEmailStrings();
        Map<String, Object> userIdAndEmails = new HashMap<>();
        userIdAndEmails.put("userId", user.getUserId());
        userIdAndEmails.put("primaryEmail", user.getPrimaryEmail());
        userIdAndEmails.put("additionalEmails", user.getAdditionalEmails());
        response.setStatus(HttpServletResponse.SC_OK);
        return userIdAndEmails;
    }

    /**
     * Checks if an email exists in the database
     * @param request HttpServletRequest received from the front-end
     * @param response HttpServletResponse received from the front-end
     */
    @GetMapping("/email")
    public void checkEmail(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Token");
        userService.findByToken(token);
        String email = request.getHeader("email");
        if (emailRepository.existsEmailByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request: email " + email + " is already in use");
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    /**
     * Adds a list of additional emails to a User
     * @param jsonString payload from the front-end
     * @param profileId id of User
     * @param request HttpServletRequest received from the front-end
     * @param response HttpServletResponse to be sent to the front-end
     * @throws MaximumEmailsException exception for maximum emails reached
     * @throws MustHavePrimaryEmailException exception for when primary email of a User is Null
     */
    @PostMapping("/profiles/{profileId}/emails")
    public void addEmail(@RequestBody String jsonString, @PathVariable Long profileId, HttpServletRequest request, HttpServletResponse response)
            throws JsonProcessingException, MustHavePrimaryEmailException, MaximumEmailsException {
        String token = request.getHeader("Token");
        User user = userService.findByUserId(token, profileId);
        user.setTransientEmailStrings();

        ObjectNode node = new ObjectMapper().readValue(jsonString, ObjectNode.class);
        // Maybe get rid of this if statement as it doesn't seem to have a purpose
        if (node.has("additionalEmails")) {
            List<String> additionalEmails = new ArrayList<>();
            for (JsonNode email: node.get("additionalEmails")) {
                // If additional email has not been associated with user BUT exists in repo
                if (!user.getAdditionalEmails().contains(email.asText()) && emailRepository.existsEmailByEmail(email.asText())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request: additional email " + email.asText() + " is already in use");
                } else {
                    additionalEmails.add(email.asText());
                }
            }
            user.setAdditionalEmails(additionalEmails);
            response.setStatus(HttpServletResponse.SC_CREATED);
            userRepository.save(user);
        }
    }

    //TODO: Tests for this method. Tested in postman but will update the current user thats logged in with the primary email due to unimplementation of adding a list of secondary emails in the database.
    /**
     * Updates the primary email of a User with a candidate primary email
     * and replaces the additional emails with candidate additional emails
     * @param jsonString payload from the front-end
     * @param profileId id of User
     * @param request HttpServletRequest received from the front-end
     * @param response HttpServletResponse to be sent to the front-end
     * @throws IOException exception for error codes
     * @throws MaximumEmailsException exception for maximum emails reached
     * @throws MustHavePrimaryEmailException exception for when primary email of a User is Null
     */
    @PutMapping("/profiles/{profileId}/emails")
    public void updateEmail(@RequestBody String jsonString, @PathVariable Long profileId, HttpServletRequest request, HttpServletResponse response)
            throws IOException, MaximumEmailsException, MustHavePrimaryEmailException {
        String token = request.getHeader("Token");
        User user = userService.findByUserId(token, profileId);
        user.setTransientEmailStrings();

        ObjectNode node = new ObjectMapper().readValue(jsonString, ObjectNode.class);
        String originalPrimaryEmail = node.get("originalPrimaryEmail").asText();
        String candidatePrimaryEmail = node.get("candidatePrimaryEmail").asText();

        // If candidatePrimaryEmail is not in additional emails yet BUT exists in repo
        if (!user.getAdditionalEmails().contains(candidatePrimaryEmail) && emailRepository.existsEmailByEmail(candidatePrimaryEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request: candidate primary email " + candidatePrimaryEmail + " is already in use");
        }

        List<String> candidateAdditionalEmails = new ArrayList<>();
        for (JsonNode email: node.get("additionalEmails")) {
            // If User.AdditionalEmails doesn't contain Email.asText()
            // And User.PrimaryEmail isn't Email.asText()
            // But that Email is found in the DB
            if (!user.getAdditionalEmails().contains(email.asText()) &&
                    !user.getPrimaryEmail().equals(email.asText()) &&
                    emailRepository.existsEmailByEmail(email.asText())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request: email already in use");
            } else {
                candidateAdditionalEmails.add(email.asText());
            }
        }

        // RESET Additional Emails
        user.setAdditionalEmails(new ArrayList<>());
        // ADD NEW PRIMARY TO USER ADDITIONAL EMAILS (Which is now empty)
        List<String> candidatePrimaryEmailList = new ArrayList<>();
        candidatePrimaryEmailList.add(candidatePrimaryEmail);
        user.setAdditionalEmails(candidatePrimaryEmailList);
        // SET NEW PRIMARY TO ACTUAL PRIMARY
        user.setPrimaryEmail(candidatePrimaryEmail);
        // MANUALLY REMOVE PREV PRIMARY
        user.deleteAdditionalEmail(originalPrimaryEmail);
        // SET CANDIDATE ADDITIONAL EMAILS
        user.setAdditionalEmails(candidateAdditionalEmails);

        response.setStatus(HttpServletResponse.SC_OK);
        userRepository.save(user);
    }
}
