package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.UserResponse;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;

@Service("userService")
public class UserValidationService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String CHAR_LIST =
            "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Login helper function. Generates a random String value for token, to be stored in the database.
     * @return Random String for Token.
     */
    private String generateNewToken() {
        int length = 30;
        StringBuffer strBuffer = new StringBuffer(length);
        SecureRandom secureRandom = new SecureRandom();
        for(int i = 0; i < length; i++)
            strBuffer.append(CHAR_LIST.charAt(secureRandom.nextInt(CHAR_LIST.length())));
        return strBuffer.toString();
    }

    /**
     * Generates a token and stores token in repository if valid email and password
     * @param email user's email to login
     * @param password user's password to login
     * @return the token generated or ResponseStatusException is thrown
     */
    public UserResponse login(String email, String password) {
        Email userEmail = emailRepository.findByEmail(email);
        if (userEmail == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Could not find user with email " + email);
        }
        User user = userEmail.getUser();
        if (user.checkPassword(password)) {
            String token = generateNewToken();
            user.setToken(token);
            user.setTokenTime();
            userRepository.save(user);
            return new UserResponse(token, user.getUserId());
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login unsuccessful, please enter a valid password");
    }

    /**
     * Find user which contains token and remove it
     * @param token a user's token
     */
    public void logout(String token) {
        User user = userRepository.findByToken(token);
        if (user != null) {
            user.setToken(null);
            userRepository.save(user);
        }
    }

    /**
     * Finds a user with the given token, if not found or timed out token return null
     * @param token stored at user
     * @return User requested or ResponseStatusException is thrown (if unauthorized or timed out)
     */
    public User findByToken(String token) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found, invalid credentials");
        }
        if (user.isTimedOut()) {
            user.setToken(null);
            userRepository.save(user);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User credentials timed out");
        }
        user.setTokenTime();
        userRepository.save(user);
        return user;
    }

    /**
     * Finds a user with the given id, if the current user authorized to do so.
     * @param token belongs to session user
     * @param id of user requested
     * @return user requested or ResponseStatusException is thrown (if unauthorized or timed out token)
     */
    public User findByUserId(String token, Long id) {
        User thisUser = findByToken(token);
        if (thisUser.getUserId().equals(id)) {
            return thisUser;
        }
        if (validUser(thisUser.getUserId(), id)) { // Check if authorized to access the user with given id
            User user = userRepository.findByUserId(id);
            if (user != null) {
                return user;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id);
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User forbidden from accessing user with ID: " + id);
    }

    /**
     * Finds a user by the id
     * @param id the user id of the requested user
     * @param token the user login token
     * @return the user requested or ResponseStatusException is the user is not found
     */
    public User viewUserById(Long id, String token) {
        findByToken(token); // Checks that a user is logged in
        User user = userRepository.findByUserId(id);
        if (user != null) {
            return user;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id);
    }

    /**
     * Will need to be changed later when the database is fully implemented
     * Checks that the session gives the request access to modify the user with a given id
     * This will allow for checking both admins and users
     * @param userId the id of the user linked to the session
     * @param profileId the id of the user to access the profile of
     * @return true if the session has permission to modify the user; false otherwise
     */
    private boolean validUser(long userId, long profileId) {
        if (userId == profileId) {
            return true;
        } else {
            return false;
        }
    }
}
