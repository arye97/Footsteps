package com.springvuegradle.seng302team600.steps;

import com.springvuegradle.seng302team600.cucumberSpringBase;
import com.springvuegradle.seng302team600.model.User;
import com.springvuegradle.seng302team600.payload.response.LoginResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class loginAndRegisterSteps extends cucumberSpringBase {

    private String authenticationToken = "IAMLOGGEDIN";

    @Given("I have a database connection")
    public void i_have_a_database_connection() {
        setup();
        this.savedUsers = new HashMap<>();
    }

    @Given("the database contains the following user information")
    public void the_database_contains_the_following_user_information(io.cucumber.datatable.DataTable dataTable) throws Exception{
        List<Map<String, String>> userData = dataTable.asMaps(String.class, String.class);
        for (int i = 0; i < userData.size(); i++) {
            User newUser = new User();
            newUser.setFirstName(userData.get(i).get("firstName"));
            newUser.setLastName(userData.get(i).get("lastName"));
            newUser.setPrimaryEmail(userData.get(i).get("primaryEmail"));
            newUser.setPassword(userData.get(i).get("passwordText"));
            Date dob = new SimpleDateFormat("dd/MM/yyyy").parse(userData.get(i).get("dateOfBirth"));
            newUser.setDateOfBirth(dob);
            if (userData.get(i).get("gender") == "Male") {
                newUser.setGender(User.Gender.MALE);
            } else if (userData.get(i).get("gender") == "Female") {
                newUser.setGender(User.Gender.FEMALE);
            } else if (userData.get(i).get("gender") == "Non-Binary") {
                newUser.setGender(User.Gender.NON_BINARY);
            }
            savedUsers.put(userData.get(i).get("primaryEmail"), newUser);
        }
    }

    @Given("I am logged in as Bob with email {string} and password {string}")
    public void i_am_logged_in_as_Bob_with_email_and_password(String email, String password) {
        when(userAuthenticationService.login(email, password)).thenAnswer(i -> {
            User user = savedUsers.get(email);
            if (user != null) {
                user.setToken(authenticationToken);
                return new LoginResponse(authenticationToken, user.getUserId());
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Could not login");
        });
        LoginResponse freshLogin = userAuthenticationService.login(email, password);
        assertEquals(authenticationToken, freshLogin.getToken());
    }

    private void logUserOut(String authenticate) {
        for (User user: this.savedUsers.values()) {
            if (user.getToken() == authenticate) {
                user.setToken(null);
            }
        }
    }

    @When("I log out")
    public void i_log_out() {
        doNothing().when(userAuthenticationService).logout(authenticationToken);
        userAuthenticationService.logout(authenticationToken);
        logUserOut(authenticationToken);
    }

    @Then("my user \\(with email {string}) has no token")
    public void my_user_with_email_has_no_token(String email) {
        User user = savedUsers.get(email);
        assertNull(user.getToken());
    }

}
