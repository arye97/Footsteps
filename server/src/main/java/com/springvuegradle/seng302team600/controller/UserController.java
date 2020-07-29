package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springvuegradle.seng302team600.Utilities.UserValidator;
import com.springvuegradle.seng302team600.Utilities.PasswordValidator;
import com.springvuegradle.seng302team600.model.ActivityType;
import com.springvuegradle.seng302team600.model.DefaultAdminUser;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.model.UserRole;
import com.springvuegradle.seng302team600.payload.EditPasswordRequest;
import com.springvuegradle.seng302team600.payload.UserRegisterRequest;
import com.springvuegradle.seng302team600.payload.UserResponse;
import com.springvuegradle.seng302team600.repository.*;
import com.springvuegradle.seng302team600.service.ActivityTypeService;
import com.springvuegradle.seng302team600.service.UserAuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class UserController {

    private UserAuthenticationService userService;
    private ActivityTypeService activityTypeService;

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final ActivityActivityTypeRepository activityActivityTypeRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final UserValidator userValidator;

    private static Log log = LogFactory.getLog(UserController.class);


    /**
     * The DefaultAdminUser that is added to the Database if one doesn't
     * already exist. This is @Autowired so Spring can set it's
     * primaryEmail and password through environment variables
     */
    @Autowired
    private DefaultAdminUser defaultAdmin;
    private boolean _DAexists = false;


    public UserController(UserRepository userRepository, EmailRepository emailRepository,
                          UserAuthenticationService userService, ActivityTypeService activityTypeService,
                          ActivityActivityTypeRepository activityActivityTypeRepository,
                          ActivityTypeRepository activityTypeRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.userService = userService;
        this.activityTypeService = activityTypeService;
        this.activityActivityTypeRepository = activityActivityTypeRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.userValidator = userValidator;
    }

    /**
     * Checks if there is a default admin in the Database.  If there isn't, one is created and added.
     * The email and password of the default admin are specified as environment variables in application.properties
     * The annotation @PostConstruct causes the method to be called during construction, but after all beans have
     * been initialized.  (Calling it in UserController() constructor won't work.)
     */
    @PostConstruct
    private void createDefaultAdmin() {
        if (!userRepository.existsUserByRole(UserRole.DEFAULT_ADMIN)) {
            log.info("No Default Admin in database.  Creating: " + defaultAdmin);
            userRepository.save(defaultAdmin);
        }
        // Flag used for testing that a default admin exists or was created
        _DAexists = true;
    }

    /**
     * Return a User saved in the repository if authorized
     * @param request the http request to the
     * @param response the http response
     * @return User requested or null
     */
    @GetMapping("/profiles")
    public User findUserData(HttpServletRequest request,
                             HttpServletResponse response) {
        String token = request.getHeader("Token");
        User user = userService.findByToken(token);
        user.setTransientEmailStrings();
        //Security breach if password sent to client
        user.setPassword(null);
//        user.setToken(null);
        response.setStatus(HttpServletResponse.SC_OK); //200
        return user;
    }

    /**
     * Return a user id saved in the repository if authorized
     * @param request the http request to the
     * @param response the http response
     * @return User id requested or null
     */
    @GetMapping("/profiles/userId")
    public Long findUserId(HttpServletRequest request, HttpServletResponse response) {
        User user = findUserData(request, response);
        return user.getUserId();
    }

    /**
     * Return a User saved in the repository based on the user id
     * @param request the http request
     * @param response the http response
     * @return User requested or null
     */
    @GetMapping("/profiles/{profileId}")
    public User findSpecificUserData(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "profileId") Long profileId) {
        String token = request.getHeader("Token");
        User user = userService.viewUserById(profileId, token);
        user.setTransientEmailStrings();
        // Security breach if password is sent to the client
        user.setPassword(null);
//        user.setToken(null);
        response.setStatus(HttpServletResponse.SC_OK); //200
        return user;
    }

    /**
     * Creates and returns a new User from the requested body
     * @param newUserData payload of request, data to be registered
     * @param response the http response
     */
    @PostMapping("/profiles")
    public UserResponse newUser(@Validated @RequestBody UserRegisterRequest newUserData, HttpServletResponse response) {
        if (emailRepository.existsEmailByEmail(newUserData.getPrimaryEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email: " + newUserData.getPrimaryEmail() + " is already registered"); //409. It may be worth consider to a 200 error for security reasons
        }

        // Validate the password
        PasswordValidator.validate(newUserData.getPassword());

        User newUser = new User(newUserData);
        // Check the user input and throw ResponseStatusException if invalid stopping execution
        userValidator.validate(newUser);

        // Use ActivityType entities from the database.  Don't create duplicates.
        newUser.setActivityTypes(
                activityTypeService.getMatchingEntitiesFromRepository(newUser.getActivityTypes())
        );

        //Saving generates user id
        //If mandatory fields not given, exception in UserRepository.save ends function execution and makes response body
        //Gives request status:400 and specifies needed field if null in required field
        userRepository.save(newUser);
        UserResponse userResponse = userService.login(newUserData.getPrimaryEmail(), newUserData.getPassword());
        response.setStatus(HttpServletResponse.SC_CREATED); //201
        return userResponse;
    }

    /**
     * Logs in a valid user with a registered email and password
     * @param jsonLogInString the json body of the request as a string
     * @param response the http response
     * @throws JsonProcessingException thrown if there is an issue when converting the body to an object node
     * @return token to be stored by the client.
     */
    @PostMapping("/login")
    public UserResponse logIn(@RequestBody String jsonLogInString, HttpServletResponse response) throws JsonProcessingException {
        ObjectNode node = new ObjectMapper().readValue(jsonLogInString, ObjectNode.class);

        if (node.has("email") && node.has("password")) {
            String email = node.get("email").toString().replace("\"", "");
            String password = node.get("password").toString().replace("\"", "");
            //ResponseStatusException thrown if email or password incorrect
            UserResponse userResponse = userService.login(email, password);
            response.setStatus(HttpServletResponse.SC_CREATED); //201
            return userResponse;
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
     * If the current user has authorization edit the users password.  Only edits the password
     * if the following conditions hold:
     *     old_password doesn't equal the user's password
     *     new_password != repeat_password
     *     new_password == old_password    (New pass can't be the same as old)
     *     new_password passes regular expression rules
     * @param passwordRequest the payload containing the passwords (old, new, repeat)
     * @param request the http request to the endpoint
     * @param response the http response
     * @param profileId user id obtained from the request url
     */
    @PutMapping("/profiles/{profileId}/password")
    public void editPassword(@Validated @RequestBody EditPasswordRequest passwordRequest, HttpServletRequest request,
                             HttpServletResponse response, @PathVariable(value = "profileId") Long profileId) {

        String token = request.getHeader("Token");
        //ResponseStatusException thrown if user unauthorized or forbidden from accessing requested user
        User user = userService.findByUserId(token, profileId);   // Get the user to modify

        String oldPassword = passwordRequest.getOldPassword();
        String newPassword = passwordRequest.getNewPassword();
        String repeatPassword = passwordRequest.getRepeatPassword();

        // Old Password doesn't match current password (invalid password)
        if (!user.checkPassword(oldPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Given password does not equal current password");
        }
        // Check rules and compare repeated password for validation
        PasswordValidator.validateEdit(oldPassword, newPassword, repeatPassword);

        user.setPassword(newPassword);
        userRepository.save(user);
        response.setStatus(HttpServletResponse.SC_OK); //200
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

        // Check if user has admin privileges
        User user = userService.findByUserId(token, profileId);   // Get the user to modify
        //Remove fields that should not be modified here
        ObjectNode modData = nodeMapper.readValue(jsonEditProfileString, ObjectNode.class);
        modData.remove("primary_email");
        modData.remove("additional_email");
        modData.remove("password");

        ObjectReader userReader = nodeMapper.readerForUpdating(user);
        User modUser = userReader.readValue(modData);   // Create the modified user
        //Throws errors if user is erroneous
        userValidator.validate(modUser); // If this user has authorization


        // Use ActivityType entities from the database.  Don't create duplicates.
        modUser.setActivityTypes(
                activityTypeService.getMatchingEntitiesFromRepository(modUser.getActivityTypes())
        );

        userRepository.save(modUser);
        response.setStatus(HttpServletResponse.SC_OK); //200
    }

    /**
     * Takes a token and a user id and queries the repository
     * to check if a user is logged in with a provided token.
     * @param request the http request to the endpoint
     * @param response the http response
     * @param profileId user id obtained from the request url
     */
    @GetMapping("/check-profile/{profileId}")
    public void checkIfUserIdMatchesToken(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @PathVariable(value = "profileId") Long profileId) {
        String token = request.getHeader("Token");
        // Checks if a user is an admin/default admin
        userService.findByUserId(token, profileId);
    }

    /**
     * Adds additional activity-types to a user
     * @param request the http request to the endpoint
     * @param response the http response
     * @param profileId the user who is to be updated, taken from request url
     */
    @PutMapping("/profiles/{profileId}/activity-types")
    public void updateUserActivityTypes( HttpServletRequest request,
                                         HttpServletResponse response,
                                         @PathVariable(value = "profileId") Long profileId,
                                         @RequestBody String jsonUserEditString) {
        //user must be logged in
        try {
            String token = request.getHeader("Token");
            if (!userService.findByToken(token).getUserId().equals(profileId)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user, unauthorized to edit this data");
            }
            User user = userService.findByUserId(token, profileId);
            ObjectMapper nodeMapper = new ObjectMapper();
            ObjectNode editedData = nodeMapper.readValue(jsonUserEditString, ObjectNode.class);
            JsonNode activityTypesNode = editedData.get("activities");
            Set<ActivityType> activityTypes = new HashSet<>();
            for (JsonNode element : activityTypesNode) {
                activityTypes.add(new ActivityType(element.asText()));
            }
            user.setActivityTypes(activityTypeService.getMatchingEntitiesFromRepository(activityTypes));
            userRepository.save(user);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid user id, user not found");
        }

    }

    /**
     * Returns a user's role, based on users id
     * @param request the http request to the endpoint
     * @param response the http response
     * @profileId the user's id from the request url
     */
    @GetMapping("/profiles/{profileId}/role")
    public int getUsersRole(HttpServletRequest request,
                             HttpServletResponse response,
                             @PathVariable(value = "profileId") Long profileId) {
        try {
            String token = request.getHeader("Token");
            User user = userService.findByUserId(token, profileId);
            return user.getRole();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to access this data");
        }
    }

    @RequestMapping(
            value = "/profiles",
            params = { "activity", "method" },
            method = RequestMethod.GET
    )
    public List<User> getUserByActivityType(HttpServletRequest request,
                                            HttpServletResponse response,
                                            @RequestParam(value="activity") String activityTypes,
                                            @RequestParam(value="method") String method) {
        String[] activity_types = activityTypes.split("%");

        if (method.equals("and")) {
            List<Long> longs = new ArrayList<Long>();
            longs.add(12L);
            longs.add(34L);

            System.out.println(activityActivityTypeRepository.findByActivityTypeIds(longs, 2));
            return null;
        }

        return null;
    }
}

