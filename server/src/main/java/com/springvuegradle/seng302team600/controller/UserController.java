package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.model.LoggedUser;
import com.springvuegradle.seng302team600.payload.RegisterRequest;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.exception.*;
import com.springvuegradle.seng302team600.service.UserValidationService;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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
        String token = "token1"; //TODO retrieve token from header
        User user = userRepository.findByToken(token);
        if (user != null) {
            //Security breach if password sent to client
            user.setPassword(null);
            response.setStatus(HttpServletResponse.SC_OK);
            return user;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }


    /**
     * Creates and returns a new User from the requested body
     * @param newUserData payload of request, data to be registered
     * @param request the http request to the
     * @param response the http response
     * @throws EmailAlreadyRegisteredException thrown if provided email already used
     * @throws InvalidDateOfBirthException thrown if provided DateOfBirth is invalid
     * @throws UserTooYoungException thrown if provided DateOfBirth is to recent, young
     * @throws InvalidUserNameException thrown if user's name is invalid
     */
    @PostMapping("/profiles")
    public void newUser(@Validated @RequestBody RegisterRequest newUserData, HttpServletRequest request, HttpServletResponse response)
            throws EmailAlreadyRegisteredException, InvalidDateOfBirthException, UserTooYoungException, InvalidUserNameException {
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
        User user = userRepository.save(newUser);
        String token = userService.login(newUserData.getPrimaryEmail(), newUserData.getPassword());
        //TODO add token to header
        response.setStatus(HttpServletResponse.SC_CREATED); //201
    }

    /**
     * Logs in a valid user with a registered email and password
     * @param jsonLogInString the json body of the request as a string
     * @param request the http request to the endpoint
     * @param response the http response
     * @throws JsonProcessingException thrown if there is an issue when converting the body to an object node
     * @throws UserNotFoundException thrown if user email not found in repository
     * @throws IncorrectPasswordException thrown if password was incorrect for given user
     */
    @PostMapping("/login")
    public void logIn(@RequestBody String jsonLogInString, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, UserNotFoundException, IncorrectPasswordException {
        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);

        if (node.has("email") && node.has("password")) {
            String email = node.get("email").toString().replace("\"", "");
            String password = node.get("password").toString().replace("\"", "");

            String token = userService.login(email, password);

            if (token.isEmpty()) {
                //Token was empty, either email not found or password incorrect
                if (emailRepository.existsEmailByEmail(email)) {
                    throw new IncorrectPasswordException(email);
                } else {
                    throw new UserNotFoundException(email);
                }
            }
            response.setStatus(HttpServletResponse.SC_CREATED);
            //TODO add token to header
            return;
        }
        //email and/or password fields not given
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Logs out a user if they are not already logged out.
     * @param request the http request to the endpoint
     * @param response the http response
     * @return String, Success status.
     */
    @PostMapping("/logout")
    public String logOut(HttpServletRequest request, HttpServletResponse response) {
        String token = ""; //TODO retrieve token from header
        if (!token.isEmpty()) {
            userService.logout(token);
            //TODO Remove token from header
            response.setStatus(HttpServletResponse.SC_OK);
            return "Logout successful";
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return "Already logged out";
    }

    /**
     *
     * @param jsonEditProfileString the json body of the request as a string
     * @param request the http request to the endpoint
     * @param response the http response
     * @param profileId user id obtained from the request url
     * @throws JsonProcessingException thrown if there is an issue when converting the body to an object node
     */
    @PutMapping("/profiles/{profileId}")
    public void editProfile(@RequestBody String jsonEditProfileString, HttpServletRequest request,
                            HttpServletResponse response, @PathVariable(value = "profileId") Long profileId) throws IOException {
        HttpSession session = request.getSession();
        if (session != null && session.getAttribute("loggedUser") != null) {
            String sessionId = session.getId();
            Long userId = ((LoggedUser)session.getAttribute("loggedUser")).getUserId();
            if (validUser(userId, sessionId, profileId)) {
                ObjectMapper nodeMapper = new ObjectMapper();
                User user = userRepository.findById(profileId).get();
                //Remove fields that should not be modified here
                ObjectNode modData = nodeMapper.readValue(jsonEditProfileString, ObjectNode.class);
                modData.remove("date_of_birth");
                modData.remove("primary_email");
                modData.remove("additional_email");
                modData.remove("password");

                ObjectReader userReader = nodeMapper.readerForUpdating(user);
                User modUser = userReader.readValue(modData);
                userRepository.save(modUser);
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * Will need to be changed later when the database is fully implemented
     * Checks that the session gives the request access to modify the user with a given id
     * This will allow for checking both admins and users
     * @param userId the id of the user linked to the session
     * @param sessionId the session id/token
     * @param profileId the id of the user to access the profile of
     * @return true if the session has permission to modify the user; false otherwise
     */
    private boolean validUser(long userId, String sessionId, long profileId) {
        if (userId == profileId) {
            return true;
        } else {
            return false;
        }
    }



}
