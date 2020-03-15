package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.exception.EmailAlreadyRegisteredException;
import com.springvuegradle.seng302team600.model.User;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

//    /**
//    Return a list of Users saved in the repository
//     */
//    @GetMapping("/listprofile")
//    public List<User> all() {
//        // Use this to see the location in memory of each User
////        List<User> users = repository.findAll();
////        for (User user : users) {
////            System.out.println(user.toString());
////        }
////        return users;
//        return repository.findAll();
//    }


    /**
     Creates and returns a new User from the requested body
     */
    @PostMapping("/profiles")
    public User newUser(@Validated @RequestBody User newUser) throws EmailAlreadyRegisteredException, JsonProcessingException {
//        List<Emails> emails = new ArrayList<Emails>();
//        Emails primaryEmail = new Emails(body.get("primary_email").toString(), true);
//        emails.add(primaryEmail);
//        body.put("primary_email", emails);
//
//        ObjectMapper mapper = new ObjectMapper();
//        User obj = mapper.readValue("{\"middlename\":\"Jack\",\"lastname\":\"DADSSA\",\"nickname\":\"Jacky\",\"primary_email\":[{\"email\":\"mauricejacask@yahoo.com\",\"primary\":true,\"id\":null}],\"password\":\"jack\",\"bio\":\"Jacky loves to ride his bike on crazy mountains.\",\"date_of_birth\":\"1985-12-20\",\"gender\":\"male\"}", User.class);

        //JSONObject jsonObject=new JSONObject(body);

        //make function for this
        newUser.getEmails().get(0).setPrimary(true);
        repository.save(newUser);

        return newUser;

        // maybe using try catch would be better?
//        if (repository.findByEmails(newUser.getEmails()) == null) {
//        return repository.save(newUser);
//        } else {
//            throw new EmailAlreadyRegisteredException("Email: " + newUser.getEmails().getPrimaryEmail() + " is already registered!");
//        }
    }

//    /**
//    Creates and returns a new User from the requested body
//     */
//    @PostMapping("/profiles")
//    public User newUser(@Validated @RequestBody User newUser) throws EmailAlreadyRegisteredException {
        // maybe using try catch would be better?
//        if (repository.findByEmails(newUser.getEmails()) == null) {
//            return repository.save(newUser);
//        } else {
//            throw new EmailAlreadyRegisteredException("Email: " + newUser.getEmails().getPrimaryEmail() + " is already registered!");
//        }
//    }

//    /**
//     Logs in a valid user with a registered email
//     */
//    @PostMapping("/login")
//    public String logIn(@RequestBody String jsonLogInString) throws JsonProcessingException, UserNotFoundException {
//        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);
//        if (node.has("email") && node.has("password")) {
//            String email = node.get("email").toString().replace("\"", "");
//            String password = node.get("password").toString().replace("\"", "");
//
//            for (User user: repository.findAll()) {
//                if (user.getEmails().getPrimaryEmail().equals(email) && user.checkPassword(password)) {
//                    return "Successful login with " + email;
//                } else if (user.getEmails().getPrimaryEmail().equals(email) && !user.checkPassword(password)) {
//                    return "Unsuccessful login with " + email + ", please enter a valid password";
//                }
//            }
//
//            throw new UserNotFoundException(email);
//        }
//        return "Please enter an email and a password";
//    }
//
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
