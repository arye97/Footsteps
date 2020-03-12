package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.exception.IncorrectPasswordException;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.exception.EmailAlreadyRegisteredException;
import com.springvuegradle.seng302team600.exception.UserNotFoundException;
import com.springvuegradle.seng302team600.model.User;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;

@RestController
public class UserController {

    private ArrayList<Long> activeUsers = new ArrayList<Long>();

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
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
        return repository.findAll();
    }

    /**
     Return a User saved in the repository via userId
     */
    @GetMapping("/profiles")
    public User findUserData(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            //Gets userId from client session
            Long userId = (Long) session.getAttribute("userId");
            response.setStatus(HttpServletResponse.SC_OK);
            return repository.findByUserId(userId);
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return null;
    }


    /**
    Creates and returns a new User from the requested body
     */
    @PostMapping("/profiles")
    public User newUser(@Validated @RequestBody User newUser, HttpServletRequest request, HttpServletResponse response) throws EmailAlreadyRegisteredException {
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") != null) { //Check if already logged in
            //Removes user ID from activeUsers
            activeUsers.remove((Long) session.getAttribute("userId"));
            //Removes this user's ID from session
            session.removeAttribute("userId");
        }
        if (repository.findByEmails(newUser.getEmails()) == null) {
            //If mandatory fields not given, exception in UserRepository.save ends function execution and makes response body
            User user = repository.save(newUser);
            //Adds user ID to activeUsers
            activeUsers.add(user.getUserId());
            //Sets this user's ID to session userId
            session.setAttribute("userId", user.getUserId());
            response.setStatus(HttpServletResponse.SC_CREATED);
            return user;
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            throw new EmailAlreadyRegisteredException("Email: " + newUser.getEmails().getPrimaryEmail() + " is already registered!");
        }
    }

    /**
     Logs in a valid user with a registered email
     */
    @PostMapping("/login")
    public User logIn(@RequestBody String jsonLogInString, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, UserNotFoundException, IncorrectPasswordException {
        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);
        HttpSession session = request.getSession();

        if (session.getAttribute("userId") != null) { //Check if already logged in
            //Removes user ID from activeUsers
            activeUsers.remove((Long) session.getAttribute("userId"));
            //Removes this user's ID from session
            session.removeAttribute("userId");
        }

        if (node.has("email") && node.has("password")) {
            String email = node.get("email").toString().replace("\"", "");
            String password = node.get("password").toString().replace("\"", "");

            for (User user: repository.findAll()) {
                if (user.getEmails().contains(email)) {
                    if (user.checkPassword(password)) {
                        //Client session will store the ID of currently logged in user
                        session.setAttribute("userId", user.getUserId());
                        //Server adds new active user ID to activeUsers
                        activeUsers.add(user.getUserId());
                        response.setStatus(HttpServletResponse.SC_CREATED);
                        return user;
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        throw new IncorrectPasswordException(email);
                    }
                }
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new UserNotFoundException(email);
        }
        //email and/or password fields not given
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return null;
    }

    /**
     Logs out a user if they are not already logged out.
     */
    @PostMapping("/logout")
    public String logOut(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            //Remove userId from activeUsers on server
            Long userId = (Long) session.getAttribute("userId");
            activeUsers.remove(userId);
            //Remove userId, associated with user, in client session
            session.removeAttribute("userId");
            response.setStatus(HttpServletResponse.SC_OK);
            return "Logged out successful";
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return "Already logged out";
    }

    //TODO: change this to PUT /profiles/{profileId} for story 4
//    /**Finds a User by id and updates its atributes*/
//    @PostMapping("/editprofile")
//    public User editProfile(@RequestBody String jsonLogInString) throws JsonProcessingException {
//        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);
//        long id = node.get("id").asLong();
//
//        User user = repository.findById(id).get();
//        //Edit user with new attributes
//        Iterator<Map.Entry<String, JsonNode>> expectedChildren = node.fields();
//        for (Map.Entry<String, JsonNode> entry; expectedChildren.hasNext(); ) {
//            entry = expectedChildren.next();
//            if (entry.getKey() == "id") {continue;}
//            PropertyAccessor fieldSetter = PropertyAccessorFactory.forBeanPropertyAccess(user);
//            fieldSetter.setPropertyValue(entry.getKey(), entry.getValue().asText());
//
//            repository.save(user);
//        }
//        return user;
//    }



}
