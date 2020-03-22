package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.springvuegradle.seng302team600.exception.InvalidDateOfBirthException;
import com.springvuegradle.seng302team600.exception.InvalidUserNameException;
import com.springvuegradle.seng302team600.exception.UserTooYoungException;
import com.springvuegradle.seng302team600.exception.MaximumEmailsException;
import com.springvuegradle.seng302team600.exception.MustHavePrimaryEmailException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
public class User {

    private static Log log = LogFactory.getLog(User.class);

    private static PasswordEncoder encoder = new BCryptPasswordEncoder();

    final static public int MAX_EMAILS = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    @JsonProperty("id")
    private Long userId;

    @NotNull(message = "Please provide a first name")
    @Column(name = "first_name", length = 15, nullable = false)
    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("middlename")
    @Column(name = "middle_name", length = 15)
    private String middleName;

    @NotNull(message = "Please provide a last name")
    @Column(name = "last_name", length = 15, nullable = false)
    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("nickname")
    @Column(name = "nickname", length = 15)
    private String nickName;

    @JsonProperty("bio")
    private String bio;

    @NotNull(message = "Please provide a primary email address")
    @Transient
    @JsonProperty("primary_email")
    private String primaryEmail;

    @Transient
    @JsonProperty("additional_email")
    private List<String> additionalEmails = new ArrayList<>();

    @NotNull(message = "Please provide a primary email address")
    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    //orphan removal removes 'child' when 'parent' is deleted
    private List<Email> emails = new ArrayList<>();

    @NotNull(message = "Please provide a password")
    @Column(name = "password", nullable = false)
    @JsonProperty("password")
    private String password;

    @NotNull(message = "Please provide a date of birth")
    @Column(name = "date_of_birth", nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd")
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @NotNull(message = "Please provide a gender from the following: male, female, non_binary")
    @Column(name = "gender", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("fitness")
    private int fitnessLevel;

    @Transient
    @JsonProperty("passports")
    private List<String> passports;


    public enum Gender {
        @JsonProperty("Male")
        MALE,
        @JsonProperty("Female")
        FEMALE,
        @JsonProperty("Non-Binary")
        NON_BINARY
    }

    //Can implement later, makes more sense in the long run
    public enum FitnessLevel {
        SEDENTARY, LOW, MEDIUM, HIGH, VERY_HIGH
    }

    /**
     * Default constructor for User.
     * Mandatory for repository actions.
     */
    public User() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Gets the primary email from Emails
     * @return primaryEmail or null
     */
    public String getPrimaryEmail() {
        return primaryEmail;
    }

    /**
     * Sets primary email of User
     * @param newPrimaryEmail an email to be set primary
     */
    public void setPrimaryEmail(String newPrimaryEmail) {
        // IF EMAIL ALREADY ASSOCIATED TO USER (FROM ADDITIONAL EMAILS)
        // Set isPrimary to false for current primary Email object
        // If newPrimaryEmail in Email list
        // Add old primary email to additional email list
        // Set primary email string field to newPrimaryEmail string
        // Remove newPrimaryEmail from additional emails list
        // Set isPrimary to true in newPrimaryEmail Email object
        boolean isAlreadyIn = false;
        for (Email email: emails) {
            if (email.getEmail().equals(primaryEmail)) {
                email.setIsPrimary(false);
            } else if (email.getEmail().equals(newPrimaryEmail)) {
                additionalEmails.add(primaryEmail);
                primaryEmail = newPrimaryEmail;
                additionalEmails.remove(newPrimaryEmail);
                email.setIsPrimary(true);
                isAlreadyIn = true;
            }
        }

        // IF EMAIL HAS NOT BEEN ASSOCIATED TO USER
        // TWO CASES:
        // - If primary email exists and needs to be replaced
        // - If primary email does not exist (creating a user)
        // --- Set primary email field to new primary email
        // --- Create new Email object and add to list of Email objects
        int numOfEmails = emails.size();
        if (!isAlreadyIn && numOfEmails < MAX_EMAILS) {
            primaryEmail = newPrimaryEmail;
            Email email = new Email(newPrimaryEmail, true, this);
            emails.add(email);
        }
    }

    /**
     * Gets a String list of additional emails
     * @return a list of additional email Strings
     */
    public List<String> getAdditionalEmails() {
        return additionalEmails;
    }

    /**
     * Iterates over a list of additional email strings,
     * adds each string to the list of additional emails,
     * before appending them to a list of Email objects.
     * If primary email has not been set in User throw MustHavePrimaryEmailException
     * @param newAdditionalEmails a String list of additional emails
     * @throws MustHavePrimaryEmailException if primary email has not been set
     * @throws MaximumEmailsException if maximum emails limit reached
     */
    // Maybe add handler for duplicate additional emails
    // Only able to add if primaryEmail is not null
    public void setAdditionalEmails(List<String> newAdditionalEmails) throws MustHavePrimaryEmailException, MaximumEmailsException {
        if (primaryEmail == null) {
            // primaryEmail can never be null
            throw new MustHavePrimaryEmailException();
        }

        for (String email: newAdditionalEmails) {
            // If email in newAdditionalEmails is a duplicate from additionalEmails
            if (additionalEmails.contains(email)) {
                continue;
            } else if (emails.size() < MAX_EMAILS) {
                additionalEmails.add(email);
                Email additionalEmail = new Email(email, false, this);
                emails.add(additionalEmail);
            } else {
                throw new MaximumEmailsException();
            }
        }
    }

    /**
     * Removes additional email from String list of additional emails
     * and Email object list of emails
     * @param removedAdditionalEmail additional email to be removed
     */
    public void deleteAdditionalEmail(String removedAdditionalEmail) {
        additionalEmails.remove(removedAdditionalEmail);
        for (Email email: emails) {
            if (email.getEmail().equals(removedAdditionalEmail)) {
                emails.remove(email);
                break;
            }
        }
    }

    public List<Email> getEmails() {
        return emails;
    }

    /**
     * Private method to setEmails that should never be called
     * @param emails a list of Email objects
     */
    private void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public boolean checkPassword(String password) {
        return encoder.matches(password, this.password);
    }

    public void setPassword(String password) {
        if (password == null) {
            this.password = null;
        } else {
            this.password = encoder.encode(password);
        }
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(int fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }

    public List<String> getPassports() {
        return passports;
    }

    public void setPassports(List<String> passports) {
        this.passports = passports;
    }

    public void addPassport(String passport) {
        passports.add(passport);
    }

    public boolean removePassport(String passport) {
        return passports.remove(passport);
    }

    @PrePersist
    public void logNewUserAttempt() {
        log.info("Attempting to add new user with email: " + primaryEmail);
    }

    @PostPersist
    public void logNewUserAdded() {
        log.info("Added user with primary email: " + primaryEmail);
    }

    @PreRemove
    public void logUserRemovalAttempt() {
        log.info("Attempting to delete user: " + primaryEmail);
    }

    @PostRemove
    public void logUserRemoval() {
        log.info("Deleted user: " + primaryEmail);
    }

    @PreUpdate
    public void logUserUpdateAttempt() {
        log.info("Attempting to update user: " + primaryEmail);
    }

    @PostUpdate
    public void logUserUpdate() {
        log.info("Updated user: " + primaryEmail);
    }

//    @Override
//    public String toString() {
//        return String.format("%s %s, ID: %d, %s", getFirstName(), getLastName(), getUserId(), super.toString());
//    }

    /**
     * Runs a sanity check on the user and throws errors if the are invalid fields
     * @throws InvalidUserNameException thrown if the users first, middle or last names are invalid
     * @throws UserTooYoungException thrown if the user is younger than 13
     * @throws InvalidDateOfBirthException thrown if the user if older than 150yr
     * @return returns true if valid user
     */
    public boolean isValid() throws InvalidUserNameException, UserTooYoungException, InvalidDateOfBirthException {
        if (firstName == null || lastName == null) { throw new InvalidUserNameException(); }
        if (! firstName.matches("[a-zA-Z]+") ) { throw new InvalidUserNameException(); }
        if (! lastName.matches("[a-zA-Z]+") ) { throw new InvalidUserNameException(); }
        if (middleName != null) {
            if (! middleName.matches("[a-zA-Z]*") ) { throw new InvalidUserNameException(); }
        }
        if (ageCheck(dateOfBirth, 13, true)) { throw new UserTooYoungException(); }
        if (ageCheck(dateOfBirth, 150, false)) { throw new InvalidDateOfBirthException(); }
        return true;
    }

    /**
     * Checks if the given date of birth would result in the user being younger or older than the given age
     * @param DoB date of birth for prespective new user
     * @param age age to check against
     * @param younger boolean tag to determine if checking if the person is younger or older (false:older, true:younger)
     * @return boolean tag denoting how given DoB compares to given age with respect to younger tag
     */
    private boolean ageCheck(Date DoB, int age, boolean younger) {
        Calendar calendar = Calendar.getInstance();
        //Lock calender time to end of day to ensure comparison is accurate and reliable
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 99);
        calendar.add(Calendar.YEAR, -age);
        Date ageDate = calendar.getTime();
        System.out.println(ageDate);
        if ( younger ) {
            return ageDate.before(DoB);
        } else {
            return ageDate.after(DoB);
        }
    }
}
