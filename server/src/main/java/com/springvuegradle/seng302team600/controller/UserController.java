package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.model.LoggedUser;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.exception.*;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
public class UserController {

    private ArrayList<Long> activeUsers = new ArrayList<Long>();

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    public UserController(UserRepository repository, EmailRepository emailRepository) {
        this.userRepository = repository;
        this.emailRepository = emailRepository;
    }

    /**
    For testing
    Return a list of Users saved in the repository
     */
    @GetMapping("/listprofile")
    public List<User> all() {
        // Use this to see the location in memory of each User
//        List<User> users = repository.findAll();
//        for (User user : users) {
//            System.out.println(user.toString());
//        }
//        return users;

        // create email
        return userRepository.findAll();
    }

    /**
     * Return a User saved in the repository via userId
     * @param request
     * @param response
     * @return User requested or null
     */
    @GetMapping("/profiles")
    public User findUserData(HttpServletRequest request, HttpServletResponse response) {
        //getSession(false) ensures that a session is not created
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            //Gets userId from client session
            Long userId = ((LoggedUser) session.getAttribute("loggedUser")).getUserId();
            response.setStatus(HttpServletResponse.SC_OK);

            User user = userRepository.findByUserId(userId);

            user.setPassword(null);
            return user;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }


    /**
     * Creates and returns a new User from the requested body
     * @param newUser
     * @param request
     * @param response
     * @throws EmailAlreadyRegisteredException
     * @throws InvalidDateOfBirthException
     * @throws UserTooYoungException
     * @throws InvalidUserNameException
     */
    @PostMapping("/profiles")
    public void newUser(@Validated @RequestBody User newUser, HttpServletRequest request, HttpServletResponse response)
            throws EmailAlreadyRegisteredException, InvalidDateOfBirthException, UserTooYoungException, InvalidUserNameException {
        HttpSession session = request.getSession();
        if (session.getAttribute("loggedUser") != null) { //Check if already logged in
            //Removes this user's ID from session
            session.removeAttribute("loggedUser");
        }
        if (emailRepository.findByEmail(newUser.getPrimaryEmail()) != null) {
            throw new EmailAlreadyRegisteredException(newUser.getPrimaryEmail());
        }

        //Throws errors if user is erroneous
        newUser.isValid();

        //Saving generates user id
        //If mandatory fields not given, exception in UserRepository.save ends function execution and makes response body
        //Gives request status:400 and specifies needed field if null in required field
        User user = userRepository.save(newUser);
        //Sets this user's ID to session userId
        session.setAttribute("loggedUser", new LoggedUser(user.getUserId(), activeUsers));
        response.setStatus(HttpServletResponse.SC_CREATED); //201
    }

    /**
     * Logs in a valid user with a registered email and password
     * @param jsonLogInString
     * @param request
     * @param response
     * @throws JsonProcessingException
     * @throws UserNotFoundException
     * @throws IncorrectPasswordException
     */
    @PostMapping("/login")
    public void logIn(@RequestBody String jsonLogInString, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, UserNotFoundException, IncorrectPasswordException {
        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);
        HttpSession session = request.getSession();

        if (session.getAttribute("loggedUser") != null) { //Check if already logged in
            //Removes this user's ID from session
            session.removeAttribute("loggedUser");
        }

        if (node.has("email") && node.has("password")) {
            String email = node.get("email").toString().replace("\"", "");
            String password = node.get("password").toString().replace("\"", "");

            Email userEmail = emailRepository.findByEmail(email);
            if (userEmail == null) {
                throw new UserNotFoundException(email);
            }
            User user = userEmail.getUser();
            if (user.checkPassword(password)) {
                //Client session will store the ID of currently logged in user
                session.setAttribute("loggedUser", new LoggedUser(user.getUserId(), activeUsers));
                response.setStatus(HttpServletResponse.SC_CREATED);
                return;
            } else {
                throw new IncorrectPasswordException(email);
            }
        }
        //email and/or password fields not given
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Logs out a user if they are not already logged out.
     * @param request
     * @param response
     * @return String, Success status.
     */
    @PostMapping("/logout")
    public String logOut(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            //Remove userId, associated with user, in client session
            session.removeAttribute("loggedUser");
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
