package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.RegisterRequest;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class UserController {

    private UserValidationService userService;

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;


    // json Field names for Editing a password U5
    private static final String OLD_PASSWORD_FIELD = "old_password";
    private static final String NEW_PASSWORD_FIELD = "new_password";
    private static final String REPEAT_PASSWORD_FIELD = "repeat_password";
    private static final String PASSWORD_RULES_REGEX = "(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}";


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
        user.setTransientEmailStrings();
        //Security breach if password sent to client
        user.setPassword(null);
        response.setStatus(HttpServletResponse.SC_OK); //200
        return user;
    }

    /**
     * Creates and returns a new User from the requested body
     * @param newUserData payload of request, data to be registered
     * @param response the http response
     */
    @PostMapping("/profiles")
    public String newUser(@Validated @RequestBody RegisterRequest newUserData, HttpServletResponse response) {
        if (emailRepository.existsEmailByEmail(newUserData.getPrimaryEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email: " + newUserData.getPrimaryEmail() + " is already registered"); //409. It may be worth consider to a 200 error for security reasons
        }


        User newUser = new User();
        //TODO remove println below
        System.out.println(newUserData.getFirstName());
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
     * Logs in a valid user with a registered email and password
     * @param jsonLogInString the json body of the request as a string
     * @param response the http response
     * @throws JsonProcessingException thrown if there is an issue when converting the body to an object node
     * @return token to be stored by the client.
     */
    @PostMapping("/login")
    public String logIn(@RequestBody String jsonLogInString, HttpServletResponse response) throws JsonProcessingException {
        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);

        if (node.has("email") && node.has("password")) {
            String email = node.get("email").toString().replace("\"", "");
            String password = node.get("password").toString().replace("\"", "");
            //ResponseStatusException thrown if email or password incorrect
            String token = userService.login(email, password);
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
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); //403
        }
    }

    /**
     * If the current user has authorization edit user with given id.
     * @param jsonEditProfileString the json body of the request as a string
     * @param request the http request to the endpoint
     * @param response the http response
     * @param profileId user id obtained from the request url
     * @throws JsonProcessingException thrown if there is an issue when converting the body to an object node
     */
    @PutMapping("/profiles/{profileId}")
    public void editProfile(@RequestBody String jsonEditProfileString, HttpServletRequest request,
                            HttpServletResponse response, @PathVariable(value = "profileId") Long profileId) throws IOException {
        String token = request.getHeader("Token");
        ObjectMapper nodeMapper = new ObjectMapper();
        //ResponseStatusException thrown if user unauthorized or forbidden from accessing requested user
        User user = userService.findByUserId(token, profileId);   // Get the user to modify
        //Remove fields that should not be modified here
        ObjectNode modData = nodeMapper.readValue(jsonEditProfileString, ObjectNode.class);
        modData.remove("primary_email");
        modData.remove("additional_email");
        modData.remove("password");

        ObjectReader userReader = nodeMapper.readerForUpdating(user);
        User modUser = userReader.readValue(modData);   // Create the modified user
        //Throws errors if user is erroneous
        modUser.isValid();   // If this user has authorization
        userRepository.save(modUser);
        response.setStatus(HttpServletResponse.SC_OK); //200
    }

    /**
     * If the current user has authorization edit the users password.  Only edits the password
     * if the old password given in the request body matches the current password.
     * @param jsonEditPasswordString the json body of the request as a string of the form
     *                              old_password, new_password, repeat_password
     * @param request the http request to the endpoint
     * @param response the http response
     * @param profileId user id obtained from the request url
     * @throws JsonProcessingException thrown if there is an issue when converting the body to an object node
     */
    @PutMapping("/profiles/{profileId}/password")
    public void editPassword(@RequestBody String jsonEditPasswordString, HttpServletRequest request,
                            HttpServletResponse response, @PathVariable(value = "profileId") Long profileId) throws IOException {
        System.out.println("Got request to edit password");
        String token = request.getHeader("Token");
        ObjectMapper nodeMapper = new ObjectMapper();
        //ResponseStatusException thrown if user unauthorized or forbidden from accessing requested user
        User user = userService.findByUserId(token, profileId);   // Get the user to modify
        System.out.println("User " + user.toString() + " " + user.getUserId() + ' ');
        System.out.println("JSON: " + jsonEditPasswordString);
        ObjectNode modData = nodeMapper.readValue(jsonEditPasswordString, ObjectNode.class);
        System.out.println(modData);

        String oldPassword = modData.get(OLD_PASSWORD_FIELD).asText();
        String newPassword = modData.get(NEW_PASSWORD_FIELD).asText();
        String repeatPassword = modData.get(REPEAT_PASSWORD_FIELD).asText();

        // Old Password doesn't match current password (invalid password)
        System.out.println("Old password:" + oldPassword);
        if (!user.checkPassword(oldPassword)) {
            System.out.println("Wrong pass");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
        // New Password and Repeated Password don't match
        } else if (!newPassword.equals(repeatPassword)) {
            System.out.println("Don't match");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
        // New Password matches old password
        } else if (newPassword.equals(oldPassword)) {
            System.out.println("You need to choose a different new password");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
        // Password violates password rules
        } else if (!passwordPassesRules(newPassword)) {
            System.out.println("Wrong rules");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
        } else {
            user.setPassword(newPassword);
            user.isValid();   // If this user has authorization
            userRepository.save(user);
            response.setStatus(HttpServletResponse.SC_OK); //200
        }
    }


    /**
     * Checks is a password complies with the password rules.
     * i.e. has to be a certain length or have certain characters.
     * @param password plaintext password
     * @return true if it passes the rules
     */
    private Boolean passwordPassesRules(String password) {
        return password.matches(PASSWORD_RULES_REGEX);
    }

}
