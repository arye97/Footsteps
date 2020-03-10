package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.exception.EmailAlreadyRegisteredException;
import com.springvuegradle.seng302team600.exception.UserNotFoundException;
import com.springvuegradle.seng302team600.model.User;

import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
public class UserController {

    private ArrayList<Long> activeUsers = new ArrayList<Long>();

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    /**
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
    Creates and returns a new User from the requested body
     */
    @PostMapping("/profiles")
    public User newUser(@Validated @RequestBody User newUser, HttpServletRequest request, HttpServletResponse response) throws EmailAlreadyRegisteredException {
        // maybe using try catch would be better?
        if (repository.findByEmails(newUser.getEmails()) == null) {
            //Adds user ID to activeUsers
            activeUsers.add(newUser.getId());
            HttpSession session = request.getSession();
            //Sets this user's ID to session userId
            session.setAttribute("userId", newUser.getId());
            response.setStatus(201);
            return repository.save(newUser);
        } else {
            throw new EmailAlreadyRegisteredException("Email: " + newUser.getEmails().getPrimaryEmail() + " is already registered!");
        }
    }

    /**
     Logs in a valid user with a registered email
     */
    @PostMapping("/login")
    public String logIn(@RequestBody String jsonLogInString, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, UserNotFoundException {
        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);
        HttpSession session = request.getSession();

        if (node.has("email") && node.has("password")) {
            String email = node.get("email").toString().replace("\"", "");
            String password = node.get("password").toString().replace("\"", "");

            for (User user: repository.findAll()) {
                if (user.getEmails().contains(email) && user.checkPassword(password)) {
                    //Client session will store the ID of currently logged in user
                    session.setAttribute("userId", user.getId());
                    //Server adds new active user ID to activeUsers
                    activeUsers.add(user.getId());
                    response.setStatus(200);
                    return "Successful login with " + email;

                } else if (user.getEmails().getPrimaryEmail().equals(email) && !user.checkPassword(password)) {
                    return "Unsuccessful login with " + email + ", please enter a valid password";
                }
            }
            //response.setStatus();
            throw new UserNotFoundException(email);
        }
        return "Please enter an email and a password";
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
            response.setStatus(200);
            return "Logged out successful";
        }
        return "Already logged out";
    }

    /**Finds a User by id and updates its atributes*/
    @PostMapping("/editprofile")
    public User editProfile(@RequestBody String jsonLogInString) throws JsonProcessingException {
        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);
        long id = node.get("id").asLong();

        User user = repository.findById(id).get();
        //Edit user with new attributes
        Iterator<Map.Entry<String, JsonNode>> expectedChildren = node.fields();
        for (Map.Entry<String, JsonNode> entry; expectedChildren.hasNext(); ) {
            entry = expectedChildren.next();
            if (entry.getKey() == "id") {continue;}
            PropertyAccessor fieldSetter = PropertyAccessorFactory.forBeanPropertyAccess(user);
            fieldSetter.setPropertyValue(entry.getKey(), entry.getValue().asText());

            repository.save(user);
        }
        return user;
    }



}
