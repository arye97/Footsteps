package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.springvuegradle.seng302team600.model.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.payload.RegisterRequest;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.exception.*;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@RestController
public class UserController {

    private UserValidationService userService;

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    public UserController(UserRepository userRepository, EmailRepository emailRepository, UserValidationService userService) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.userService = userService;
    }

    /**
     * Return a User saved in the repository if authorized
     * @param request the http request to the
     * @param response the http response
     * @return User requested or null
     */
    @GetMapping("/profiles")
    public User findUserData(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Token");
        User user = userService.findByToken(token);
        if (user != null) {
            user.setTransientEmailStrings();
            //Security breach if password sent to client
            user.setPassword(null);
            response.setStatus(HttpServletResponse.SC_OK); //200
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
        }
        return user;
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
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        } else {
            user.setTransientEmailStrings();
            Map<String, Object> userIdAndEmails = new HashMap<>();
            userIdAndEmails.put("userId", user.getUserId());
            userIdAndEmails.put("primaryEmail", user.getPrimaryEmail());
            userIdAndEmails.put("additionalEmails", user.getAdditionalEmails());
            response.setStatus(HttpServletResponse.SC_OK);
            return userIdAndEmails;
        }
    }

    /**
     * Creates and returns a new User from the requested body
     * @param newUserData payload of request, data to be registered
     * @param response the http response
     * @throws EmailAlreadyRegisteredException thrown if provided email already used
     * @throws InvalidDateOfBirthException thrown if provided DateOfBirth is invalid
     * @throws UserTooYoungException thrown if provided DateOfBirth is to recent, young
     * @throws InvalidUserNameException thrown if user's name is invalid
     */
    @PostMapping("/profiles")
    public String newUser(@Validated @RequestBody RegisterRequest newUserData, HttpServletResponse response)
            throws MaximumEmailsException, EmailAlreadyRegisteredException, InvalidDateOfBirthException, UserTooYoungException, InvalidUserNameException {
        if (emailRepository.existsEmailByEmail(newUserData.getPrimaryEmail())) {
            throw new EmailAlreadyRegisteredException(newUserData.getPrimaryEmail());
        }
        User newUser = new User();
        newUser.builder(newUserData);
        //Throws errors if user is erroneous
        newUser.isValid();

        //Saving generates user id
        //If mandatory fields not given, exception in UserRepository.save ends function execution and makes response body
        //Gives request status:400 and specifies needed field if null in required field
        userRepository.save(newUser);
        String token = userService.login(newUserData.getPrimaryEmail(), newUserData.getPassword());
        response.setStatus(HttpServletResponse.SC_CREATED); //201
        return token;
    }

    /**
     * Adds a list of additional emails to a User
     * @param jsonString payload from the front-end
     * @param profileId id of User
     * @param request HttpServletRequest received from the front-end
     * @param response HttpServletResponse to be sent to the front-end
     * @throws IOException exception for error codes
     * @throws UserNotFoundException exception for when User is not found in the database
     * @throws MaximumEmailsException exception for maximum emails reached
     * @throws MustHavePrimaryEmailException exception for when primary email of a User is Null
     */
    @PostMapping("/profiles/{profileId}/emails")
    public void addEmail(@RequestBody String jsonString, @PathVariable Long profileId, HttpServletRequest request, HttpServletResponse response)
            throws IOException, UserNotFoundException, MaximumEmailsException, MustHavePrimaryEmailException {
        String token = request.getHeader("Token");
        User user = userService.findByUserId(token, profileId);
        if (user == null) {
            // A User is signed in but does not match token provided
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User Id does not match token");
            return;
        }
        user.setTransientEmailStrings();

        // TODO do all this in Service I guess?: like WRITE AN EMAIL IS VALID VALIDATOR
        ObjectNode node = new ObjectMapper().readValue(jsonString, ObjectNode.class);
        // Maybe get rid of this if statement as it doesn't seem to have a purpose
        if (node.has("additionalEmails")) {
            List<String> additionalEmails = new ArrayList<>();
            for (JsonNode email: node.get("additionalEmails")) {
                // If additional email has not been associated with user BUT exists in repo
                if (!user.getAdditionalEmails().contains(email.asText()) && emailRepository.existsEmailByEmail(email.asText())) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request: additional email " + email.asText() + " is already in use");
                    return;
                } else {
                    additionalEmails.add(email.asText());
                }
            }
            user.setAdditionalEmails(additionalEmails);
            // Have to delete Emails from db as well FOr STORY 4 (EMAILS THAT ARE DELETED FROM ADDITIONAL EMAILS)
            // Might implement sth from front end that has list of emails
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
     * @throws UserNotFoundException exception for when User is not found in the database
     * @throws MaximumEmailsException exception for maximum emails reached
     * @throws MustHavePrimaryEmailException exception for when primary email of a User is Null
     */
    @PutMapping("/profiles/{profileId}/emails")
    public void updateEmail(@RequestBody String jsonString, @PathVariable Long profileId, HttpServletRequest request, HttpServletResponse response)
            throws IOException, UserNotFoundException, MaximumEmailsException, MustHavePrimaryEmailException {
        String token = request.getHeader("Token");
        User user = userService.findByUserId(token, profileId);
        if (user == null) {
            // A User is signed in but does not match token provided
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User Id does not match token");
            return;
        }
        user.setTransientEmailStrings();

        // TODO do all this in Service I guess?: like WRITE AN EMAIL IS VALID VALIDATOR
        ObjectNode node = new ObjectMapper().readValue(jsonString, ObjectNode.class);
        String originalPrimaryEmail = node.get("originalPrimaryEmail").asText();
        String candidatePrimaryEmail = node.get("candidatePrimaryEmail").asText();

        // If candidatePrimaryEmail is not in additional emails yet BUT exists in repo
        if (!user.getAdditionalEmails().contains(candidatePrimaryEmail) && emailRepository.existsEmailByEmail(candidatePrimaryEmail)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request: candidate primary email " + candidatePrimaryEmail + " is already in use");
            return;
        }

        List<String> candidateAdditionalEmails = new ArrayList<>();
        for (JsonNode email: node.get("additionalEmails")) {
            // If User.AdditionalEmails doesn't contain Email.asText()
            // And User.PrimaryEmail isn't Email.asText()
            // But that Email is found in the DB
            if (!user.getAdditionalEmails().contains(email.asText()) &&
                      !user.getPrimaryEmail().equals(email.asText()) &&
                 emailRepository.existsEmailByEmail(email.asText())) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request: email already in use");
                return;
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

    /**
     * Logs in a valid user with a registered email and password
     * @param jsonLogInString the json body of the request as a string
     * @param response the http response
     * @throws JsonProcessingException thrown if there is an issue when converting the body to an object node
     * @throws UserNotFoundException thrown if user email not found in repository
     * @throws IncorrectPasswordException thrown if password was incorrect for given user
     * @return token to be stored by the client.
     */
    @PostMapping("/login")
    public String logIn(@RequestBody String jsonLogInString, HttpServletResponse response) throws JsonProcessingException, UserNotFoundException, IncorrectPasswordException {
        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);

        if (node.has("email") && node.has("password")) {
            String email = node.get("email").toString().replace("\"", "");
            String password = node.get("password").toString().replace("\"", "");

            String token = userService.login(email, password);

            if (token == null) {
                //Token was null, either email not found or password incorrect
                if (emailRepository.existsEmailByEmail(email)) {
                    throw new IncorrectPasswordException(email);
                } else {
                    throw new UserNotFoundException(email);
                }
            }
            response.setStatus(HttpServletResponse.SC_CREATED); //201
            return token;
        }
        //email and/or password fields not given
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
        return null;
    }

    /**
     * Logs out a user if they are not already logged out.
     * @param request the http request to the endpoint
     * @param response the http response.
     */
    @PostMapping("/logout")
    public void logOut(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Token");
        if (token != null) {
            userService.logout(token);
            response.setStatus(HttpServletResponse.SC_OK); //200
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); //403
    }

    /**
     * If the current user has authorization edit user with given id.
     * @param jsonEditProfileString the json body of the request as a string
     * @param request the http request to the endpoint
     * @param response the http response
     * @param profileId user id obtained from the request url
     * @throws JsonProcessingException thrown if there is an issue when converting the body to an object node
     * @throws UserNotFoundException throws if requested user not found.
     */
    @PutMapping("/profiles/{profileId}")
    public void editProfile(@RequestBody String jsonEditProfileString, HttpServletRequest request,
                            HttpServletResponse response, @PathVariable(value = "profileId") Long profileId) throws UserNotFoundException, IOException {
        String token = request.getHeader("Token");
        ObjectMapper nodeMapper = new ObjectMapper();
        User user = userService.findByUserId(token, profileId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
            return;
        }
        //Remove fields that should not be modified here
        ObjectNode modData = nodeMapper.readValue(jsonEditProfileString, ObjectNode.class);
        modData.remove("date_of_birth");
        modData.remove("primary_email");
        modData.remove("additional_email");
        modData.remove("password");

        ObjectReader userReader = nodeMapper.readerForUpdating(user);
        User modUser = userReader.readValue(modData);
        userRepository.save(modUser);
        response.setStatus(HttpServletResponse.SC_OK); //200
    }

}
