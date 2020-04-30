package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.exception.UserNotFoundException;
import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;

@Service("userService")
public class UserValidationService {

    public final UserRepository userRepository;
    public final EmailRepository emailRepository;

    @Autowired
    public UserValidationService(UserRepository userRepository, EmailRepository emailRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
    }

    private static final String CHAR_LIST =
            "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Login helper function. Generates a random String value for token, to be stored in the database.
     * @return Random String for Token.
     */
    public String generateNewToken() {
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
     * @return the token generated or null
     */
    public String login(String email, String password) {
        String token = null;
        Email userEmail = emailRepository.findByEmail(email);
        if (userEmail == null) {
            return null;
        }
        User user = userEmail.getUser();
        if (user.checkPassword(password)) {
            token = generateNewToken();
            user.setToken(token);
            user.setTokenTime();
            userRepository.save(user);
        }
        return token;
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
     * @return User requested or null (if unauthorized or timed out)
     */
    public User findByToken(String token) {
        if (token == null) {
            return null;
        }
        User user = userRepository.findByToken(token);
        if (user == null) {
            return null;
        }
        if (user.isTimedOut()) {
            user.setToken(null);
            userRepository.save(user);
            return null;
        }
        user.setTokenTime();
        userRepository.save(user);
        return user;
    }

    /**
     * Finds a user with the given id, if the current user authorized to do so.
     * @param token belongs to session user
     * @param id of user requested
     * @return user requested or null (if unauthorized or timed out token)
     * @throws UserNotFoundException thrown if requested user is not in database
     */
    public User findByUserId(String token, Long id) throws UserNotFoundException {
        if (token == null) {
            return null;
        }
        User thisUser = userRepository.findByToken(token);
        if (thisUser == null) {
            return null;
        }
        if (thisUser.isTimedOut()) {
            thisUser.setToken(null);
            userRepository.save(thisUser);
            return null;
        }
        thisUser.isTimedOut();
        userRepository.save(thisUser);
        if (thisUser.getUserId().equals(id)) {
            return thisUser;
        }
        if (validUser(thisUser.getUserId(), id)) { // Check if authorized to access the user with given id
            User user = userRepository.findByUserId(id);
            if (user != null) {
                return user;
            } else {
                throw new UserNotFoundException(id);
            }
        }
        return null;
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
