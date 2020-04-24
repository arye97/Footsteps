package com.springvuegradle.seng302team600.service;

import com.springvuegradle.seng302team600.model.Email;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.repository.EmailRepository;
import com.springvuegradle.seng302team600.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserValidationService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Generates a token and stores token in repository if valid email and password
     * @param email user's email to login
     * @param password user's password to login
     * @return the token generated or empty string
     */
    public String login(String email, String password) {
        String token = "";
        Email userEmail = emailRepository.findByEmail(email);
        if (userEmail == null) {
            return token;
        }
        User user = userEmail.getUser();
        if (user.checkPassword(password)) {
            //TODO Make the token
            user.setToken(token);
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
        }
    }

}
