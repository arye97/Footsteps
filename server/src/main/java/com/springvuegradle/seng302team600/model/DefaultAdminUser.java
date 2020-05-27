package com.springvuegradle.seng302team600.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.List;

@Entity
public class DefaultAdminUser extends User {

    private static Log log = LogFactory.getLog(DefaultAdminUser.class);

    public DefaultAdminUser() {
        super();

        // Set some dummy values for other required fields
        super.setFirstName("Default");
        super.setLastName("Admin");
        super.setGender(Gender.NON_BINARY);
        this.role = UserRole.DEFAULT_ADMIN;
        LocalDate now = LocalDate.now();
        super.setDateOfBirth(java.sql.Date.valueOf(now.minusYears(18)));   // Set date of birth to 18 years ago
    }


    /**
     * Calls this method when DefaultAdminUser is created as
     * a Bean (See Application class) and uses and environment
     * variable as the argument
     * @param email the default email set from env-var
     */
    @Value("${spring.security.default.email}")
    private void setDefaultEmail(String email) {
        System.out.println("Setting Email: " + email);
        super.setPrimaryEmail(email);
    }

    /**
     * Calls this method when DefaultAdminUser is created as
     * a Bean (See Application class) and uses and environment
     * variable as the argument
     * @param password the default password set from env-var
     */
    @Value("${spring.security.default.password}")
    private void setDefaultPassword(String password) {
        System.out.println("Setting Pass: " + password);
        super.setPassword(password);
    }


    // Override methods of User that shouldn't be used by a DefaultAdmin

    @Override
    public void setPrimaryEmail(String newPrimaryEmail) {
        log.error(UnavailableMethodErrorStr("setPrimaryEmail"));
    }

    @Override
    public void setPassword(String password) {
        // This is necessary because password is set to null when a user is retrieved for security reasons.
        if (password == null) {
            super.setPassword(null);
        } else {
            log.error(UnavailableMethodErrorStr("setPassword"));
        }
    }

    @Override
    public void setAdditionalEmails(List<String> newAdditionalEmails) {
        log.error(UnavailableMethodErrorStr("setAdditionalEmails"));
    }


    /**
     * Get an informative string about a method that can't be used with Default Admin
     * @param methodName The name of the method that is trying to be called
     * @return an error message
     */
    private String UnavailableMethodErrorStr(String methodName) {
        return String.format("%s can't be called on Default Admins.", methodName);
    }
}
