package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.exception.*;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.RegisterRequest;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
     * Creates and returns a new User from the requested body
     * @param newUserData payload of request, data to be registered
     * @param response HttpServletResponse received from the front-end
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
