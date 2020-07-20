package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
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

    private UserAuthenticationService userService;

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    public EmailController(UserRepository userRepository, EmailRepository emailRepository, UserAuthenticationService userService) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.userService = userService;
    }

    /**
     * Return a users emails
     * @param request HttpServletRequest received from the front-end
     * @param response HttpServletResponse received from the front-end
     * @return JSON object with primaryEmails and additionalEmails field
     */
    @GetMapping("/profiles/{profileId}/emails")
    public Map<String, Object> findUserEmails(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "profileId") Long profileId) {
        String token = request.getHeader("Token");
        // Errors are thrown in userService
        User user = userService.findByUserId(token, profileId);
        user.setTransientEmailStrings();
        Map<String, Object> userIdAndEmails = new HashMap<>();
        userIdAndEmails.put("userId", user.getUserId());
        userIdAndEmails.put("primaryEmail", user.getPrimaryEmail());
        userIdAndEmails.put("additionalEmails", user.getAdditionalEmails());
        response.setStatus(HttpServletResponse.SC_OK); //200
        return userIdAndEmails;
    }

    /**
     * Checks if an email exists in the database
     * @param request HttpServletRequest received from the front-end
     * @param response HttpServletResponse received from the front-end
     */
    @GetMapping("/email")
    public void checkIfEmailIsInUse(HttpServletRequest request, HttpServletResponse response) {
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
     */
    @PostMapping("/profiles/{profileId}/emails")
    public void addEmails(@RequestBody String jsonString,
                          @PathVariable Long profileId,
                          HttpServletRequest request,
                          HttpServletResponse response) throws JsonProcessingException {
        String token = request.getHeader("Token");
        User user = userService.findByUserId(token, profileId);
        user.setTransientEmailStrings();

        ObjectNode node = new ObjectMapper().readValue(jsonString, ObjectNode.class);
        // Maybe get rid of this if statement as it doesn't seem to have a purpose
        if (node.has("additional_email")) {
            List<String> additionalEmails = new ArrayList<>();
            for (JsonNode email: node.get("additional_email")) {
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

    /**
     * Updates the primary email of a User with a candidate primary email
     * and replaces the additional emails with candidate additional emails
     * @param jsonString payload from the front-end
     * @param profileId id of User
     * @param request HttpServletRequest received from the front-end
     * @param response HttpServletResponse to be sent to the front-end
     * @throws IOException exception for error codes
     */
    @PutMapping("/profiles/{profileId}/emails")
    public void updateEmails(@RequestBody String jsonString,
                             @PathVariable Long profileId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        String token = request.getHeader("Token");
        User user = userService.findByUserId(token, profileId);
        user.setTransientEmailStrings();

        ObjectNode node = new ObjectMapper().readValue(jsonString, ObjectNode.class);
        String originalPrimaryEmail = user.getPrimaryEmail();
        String candidatePrimaryEmail = node.get("primary_email").asText();

        // If candidatePrimaryEmail is not in additional emails yet BUT exists in repo
        if (!user.getAdditionalEmails().contains(candidatePrimaryEmail) && emailRepository.existsEmailByEmail(candidatePrimaryEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request: candidate primary email " + candidatePrimaryEmail + " is already in use");
        }

        List<String> candidateAdditionalEmails = new ArrayList<>();
        for (JsonNode email: node.get("additional_email")) {
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
        response.setStatus(HttpServletResponse.SC_CREATED);
        userRepository.save(user);
    }
}
