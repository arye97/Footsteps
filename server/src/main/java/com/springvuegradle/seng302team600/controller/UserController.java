package com.springvuegradle.seng302team600.controller;

import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.model.LoggedUser;
import com.springvuegradle.seng302team600.repository.UserRepository;
import com.springvuegradle.seng302team600.exception.*;

import org.json.JSONObject;
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
     * For testing
     * Return a list of Users saved in the repository
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
        return repository.findAll();
    }

    /**
     * Return a User saved in the repository via userId
     *
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

            User user = repository.findByUserId(userId);

            user.setPassword(null);
            return user;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }

    /**
     * Return a users emails
     * @param request
     * @param response
     * @return JSON object with primaryEmails and additionalEmails field
     */
    @GetMapping("/emails")
    public Object findUserEmails(HttpServletRequest request, HttpServletResponse response) {
        //getSession(false) ensures that a session is not created
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            //Gets userId from client session
            Long userId = ((LoggedUser) session.getAttribute("loggedUser")).getUserId();
            response.setStatus(HttpServletResponse.SC_OK);

            JSONObject emails = new JSONObject();
            User user = repository.findByUserId(userId);
            emails.put("primaryEmail", user.getPrimaryEmail());
            emails.put("additionalEmails", user.getAdditionalEmails());
            return emails;
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
    public User newUser(@Validated @RequestBody User newUser, HttpServletRequest request, HttpServletResponse response)
            throws EmailAlreadyRegisteredException, InvalidDateOfBirthException, UserTooYoungException, InvalidUserNameException {
        HttpSession session = request.getSession();
        if (session.getAttribute("loggedUser") != null) { //Check if already logged in
            //Removes this user's ID from session
            session.removeAttribute("loggedUser");
        }
        for (User checkUser : repository.findAll()) {
            for (Email email : checkUser.getEmails()) {
                // if email of existing checkUser is identical to newUser's email
                if (email.getEmail().equals(newUser.getPrimaryEmail())) {
                    throw new EmailAlreadyRegisteredException(newUser.getPrimaryEmail());
                }
            }
        }

        //Throws errors if user is erroneous
        newUser.isValid();

        //Saving generates user id
        //If mandatory fields not given, exception in UserRepository.save ends function execution and makes response body
        //Gives request status:400 and specifies needed field if null in required field
        User user = repository.save(newUser);
        //Sets this user's ID to session userId
        session.setAttribute("loggedUser", new LoggedUser(user.getUserId(), activeUsers));
        response.setStatus(HttpServletResponse.SC_CREATED); //201
        return user;
    }
        
        
    @PostMapping("/profiles/{profileId}/emails")
    public void addEmail(@RequestBody String jsonString, @PathVariable Long profileId, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, UserNotFoundException, EmailAlreadyRegisteredException, MaximumEmailsException, MustHavePrimaryEmailException {
        ObjectNode node = new ObjectMapper().readValue(jsonString, ObjectNode.class);
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("loggedUser") != null) {
            if (profileId == repository.findByUserId(profileId).getUserId()) {
                //Gets userId from client session
                Long userId = ((LoggedUser) session.getAttribute("loggedUser")).getUserId();
                if (node.has("additional_email")) {
                    List<String> additionalEmails = node.findValuesAsText("additional_email");
                    System.out.println(additionalEmails.toString());
                    if (userId == profileId) {
                        User updateUser = repository.findByUserId(profileId);
                        updateUser.setAdditionalEmails(additionalEmails);
                        response.setStatus(HttpServletResponse.SC_OK);
                        repository.save(updateUser);
                    }
                }
            } else {
                throw new UserNotFoundException(profileId);
            }
        }
    }

    //TODO: Tests for this method. Tested in postman but will update the current user thats logged in with the primary email due to unimplementation of adding a list of secondary emails in the database.
    //check if session is null, check if the profile id is the logged in id, check if user exists after

    /**
     * Updates primary and secondary emails from a given profileID
     *
     * @param jsonString
     * @param profileId
     * @throws JsonProcessingException
     */
    @PutMapping("/profiles/{profileId}/emails")
    public void updateEmail(@RequestBody String jsonString, @PathVariable Long profileId, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, UserNotFoundException, MaximumEmailsException, MustHavePrimaryEmailException {
        ObjectNode node = new ObjectMapper().readValue(jsonString, ObjectNode.class);
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("loggedUser") != null) {
            if (profileId == repository.findByUserId(profileId).getUserId()) {
                //Gets userId from client session
                Long userId = ((LoggedUser) session.getAttribute("loggedUser")).getUserId();
                if (node.has("primary_email") && node.has("additional_email")) {
                    String primaryEmail = node.get("primary_email").asText();
                    List<String> additionalEmails = node.findValuesAsText("additional_email");
                    System.out.println(additionalEmails.toString());
                    if (userId == profileId) {
                        User updateUser = repository.findByUserId(profileId);
                        updateUser.setPrimaryEmail(primaryEmail);
                        updateUser.setAdditionalEmails(additionalEmails);
                        response.setStatus(HttpServletResponse.SC_OK);
                        repository.save(updateUser);
                    }
                }
            } else {
                throw new UserNotFoundException(profileId);
            }
        }
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

            for (User user: repository.findAll()) {
                for (Email checkEmail : user.getEmails()) {
                    if (checkEmail.getEmail().equals(email)) {
                        if (user.checkPassword(password)) {
                            //Client session will store the ID of currently logged in user
                            session.setAttribute("loggedUser", new LoggedUser(user.getUserId(), activeUsers));
                            response.setStatus(HttpServletResponse.SC_CREATED);
                            return;
                        } else {
                            throw new IncorrectPasswordException(email);
                        }
                    }
                }
            }
            throw new UserNotFoundException(email);
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
