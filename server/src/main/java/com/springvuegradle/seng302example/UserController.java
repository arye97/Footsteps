package com.springvuegradle.seng302example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302example.exceptions.EmailAlreadyRegisteredException;
import com.springvuegradle.seng302example.exceptions.UserNotFoundException;
import com.springvuegradle.seng302example.model.User;

import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

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
    Returns a new User from the requested body
     */
    @PostMapping("/createprofile")
    public User newUser(@Validated @RequestBody User newUser) throws EmailAlreadyRegisteredException {
        // maybe using try catch would be better?
        if (repository.findByEmails(newUser.getEmails()) == null) {
            return repository.save(newUser);
        } else {
            throw new EmailAlreadyRegisteredException("Email: " + newUser.getEmails().getPrimaryEmail() + " is already registered!");
        }
    }

    /**
     Logs in a valid user with a registered email
     */
    @GetMapping("/login")
    public String logIn(@RequestBody String jsonLogInString) throws JsonProcessingException, UserNotFoundException {
        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);
        if (node.has("email") && node.has("password")) {
            String email = node.get("email").toString().replace("\"", "");;
            String password = node.get("password").toString().replace("\"", "");;

            for (User user: repository.findAll()) {
                if (user.getEmails().getPrimaryEmail().equals(email) && user.checkPassword(password)) {
                    return "Successful login with " + email;
                } else if (user.getEmails().getPrimaryEmail().equals(email) && !user.checkPassword(password)) {
                    return "Unsuccessful login with " + email + ", please enter a valid password";
                }
            }

            throw new UserNotFoundException(email);
        }
        return "Please enter an email and a password";
    }

    /**Finds a User by id and updates its atributes*/
    @PostMapping("/editprofile")
    public void editProfile(@RequestBody String jsonLogInString) throws JsonProcessingException {
        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);
        if (node.has("id")) {
            long id = node.get("id").asLong();

            // change findAll to findById
            for (User user : repository.findAll()) {
                if (user.getId() == id) {
                    //Edit user with new attributes
                    Iterator<Map.Entry<String, JsonNode>> expectedChildren = node.fields();
                    for (Map.Entry<String, JsonNode> entry; expectedChildren.hasNext(); ) {
                        entry = expectedChildren.next();
                        if (entry.getKey() == "id") {continue;}
                        PropertyAccessor fieldSetter = PropertyAccessorFactory.forBeanPropertyAccess(user);
                        fieldSetter.setPropertyValue(entry.getKey(), entry.getValue().asText());
                        //System.out.println(user.toString());
                        //System.out.println(user.getBio());
                        repository.save(user);
                    }
                }

            }

        }
    }





    /*
    Returns one User with the given primaryEmail
     */
//    @GetMapping("/searchprofile/{email}")
//    public User byPrimaryEmail(@PathVariable String email) throws UserNotFoundException {
//        User user = repository.findByEmails(email);
//        if (user == null) {
//            throw new UserNotFoundException(email);
//        } else {
//            return user;
//        }
//    }


}
