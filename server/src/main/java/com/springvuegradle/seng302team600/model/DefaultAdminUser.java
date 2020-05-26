package com.springvuegradle.seng302team600.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.List;

@Entity
public class DefaultAdminUser extends User {

    private static Log log = LogFactory.getLog(DefaultAdminUser.class);

    @Transient
//    @Value("${spring.security.user.name}")    // This is not working
    private String defaultAdminUsername = "default@default.com";   // This and password need to be retrieved from application.properties

    @Transient
//    @Value("${spring.security.user.password}")
    private String defaultAdminPassword = "dummypass1";       // Double secret private

    public DefaultAdminUser() {
        super();
        super.setPrimaryEmail(defaultAdminUsername);
        super.setPassword(defaultAdminPassword);

        // Set some dummy values for other required fields
        super.setFirstName("Default");
        super.setLastName("Admin");
        super.setGender(Gender.NON_BINARY);
        super.setRole(UserRole.DEFAULT_ADMIN);
        LocalDate now = LocalDate.now();
        super.setDateOfBirth(java.sql.Date.valueOf(now.minusYears(18)));   // Set date of birth to 18 years ago
    }


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

    @Override
    public void setRole(int role) {
        log.error(UnavailableMethodErrorStr("setRole"));
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
