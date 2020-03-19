package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.springvuegradle.seng302team600.exception.MaximumEmailsException;
import com.springvuegradle.seng302team600.exception.MustHavePrimaryEmailException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class User {

    private static Log log = LogFactory.getLog(User.class);

    private static PasswordEncoder encoder = new BCryptPasswordEncoder();

    final static public int MAX_EMAILS = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @NotNull(message = "Please provide a first name")
    @Column(name = "first_name", length = 15, nullable = false)
    @JsonProperty("firstname")
    private String firstName;

    @Column(name = "middle_name", length = 15, nullable = false)
    @JsonProperty("middlename")
    private String middleName;

    @NotNull(message = "Please provide a last name")
    @Column(name = "last_name", length = 15, nullable = false)
    @JsonProperty("lastname")
    private String lastName;

    @Column(name = "nickname", length = 15)
    @JsonProperty("nickname")
    private String nickName;

    @JsonProperty("bio")
    private String bio;

    @Transient
    @JsonProperty("primary_email")
    private String primaryEmail;

    @Transient
    @JsonProperty("additional_emails")
    private List<String> additionalEmails = new ArrayList<>();

    @NotNull(message = "Please provide a primary email address")
    @JsonManagedReference
    @OneToMany(mappedBy = "email", cascade = CascadeType.ALL, orphanRemoval = true)
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
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 15, nullable = false)
    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("fitness")
    private int fitnessLevel;
    
    @Transient
    @JsonProperty("passports")
    private List<String> passports;

    public enum Gender {
        @JsonProperty("male")
        MALE,
        @JsonProperty("female")
        FEMALE,
        @JsonProperty("non_binary")
        NON_BINARY
    }

    //Can implement later, makes more sense in the long run
    public enum FitnessLevel {
        SEDENTARY, LOW, MEDIUM, HIGH, VERY_HIGH
    }

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public void updatePrimaryEmail(String updatedPrimaryEmail) throws MustHavePrimaryEmailException {
//        Email newPrimaryEmail = new Email(updatedPrimaryEmail, true);
//        if (primaryEmail == null) {
//            primaryEmail = updatedPrimaryEmail;
//            emails.add(newPrimaryEmail);
//        } else {
        for (Email email: emails) {
            // Set current primary email to false
            if (email.getEmail().equals(primaryEmail)) {
                email.setIsPrimary(false);
            }
        }

        primaryEmail = updatedPrimaryEmail;
        Email newPrimaryEmail = new Email(updatedPrimaryEmail, true);
        emails.add(newPrimaryEmail);
    }

    public void deletePrimaryEmail(String deletedPrimaryEmail) {
        for (Email email: emails) {
            if (email.getEmail().equals(deletedPrimaryEmail)) {
                emails.remove(email);
                break;
            }
        }
        this.primaryEmail = null;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    /**
     * Sets primary email of User
     * @param primaryEmail an email to be set primary
     * @throws MustHavePrimaryEmailException
     */
    public void setPrimaryEmail(String primaryEmail) throws MustHavePrimaryEmailException {
        // this.primaryEmail = primaryEmail;

        // if email already in list of emails
        boolean isAlreadyIn = false;
        // iterate through emails
        for (Email email: emails) {
            // if email in user == email provided
            if (email.getEmail().equals(primaryEmail)) {
                this.primaryEmail = primaryEmail;
                email.setIsPrimary(true);
                isAlreadyIn = true;
                break;
            }
        }

        // when email not already stored in User
        int numOfEmails = emails.size();
        if (!isAlreadyIn && numOfEmails < MAX_EMAILS) {
            this.primaryEmail = primaryEmail;
            Email newEmail = new Email(primaryEmail, true);
            newEmail.setUser(this);
            emails.add(newEmail);
        }
        setEmails(emails);
        // should we have a case where emails is null (when creating user) we call setEmails?
        // because if email alreadyIn the list then its already pointing to User, so we don't need to point it to User again right?
        // actually don't really know how it updates the database though so might still nee dto call setEmails
    }


    public List<String> getAdditionalEmails() {
        return additionalEmails;
    }

    /**
     * Iterates over a list of additional email strings,
     * adds each string to the list of additional emails,
     * before appending them to a list of Email objects
     * @param additionalEmails a String list of additional emails
     */
    public void setAdditionalEmails(List<String> additionalEmails) throws MaximumEmailsException {
//        this.additionalEmails = additionalEmails;
        for (String email: additionalEmails) {
            if (emails.size() < MAX_EMAILS) {
                this.additionalEmails.add(email);
                emails.add(new Email(email, false));
            } else {
                throw new MaximumEmailsException("Maximum email limit reached");
            }
        }
        setEmails(emails);
    }



// Creating account with Primary Email: Create an Email object and add to Email List
// Updating primary email: Can't set primary email unless Email is already in List
// If only one email address, then cannot change to not primary









    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
//        emails.get(0).setUser(this);
        this.emails = emails;
    }

    public boolean checkPassword(String password) {
        return encoder.matches(password, this.password);
    }

    public void setPassword(String password) {
        this.password = encoder.encode(password);
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

//    public void increaseAllEmailRanks() {
//        for (Email e: emails) {
//            e.setRank(e.getRank() + 1);
//        }
//    }


//    @PrePersist
//    public void logNewUserAttempt() {
//        log.info("Attempting to add new user with email: " + emails);
//    }
//
//    @PostPersist
//    public void logNewUserAdded() {
//        log.info("Added user '" + firstName + " " + lastName + "' with primary email: " + emails.getPrimaryEmail());
//    }
//
//    @PreRemove
//    public void logUserRemovalAttempt() {
//        log.info("Attempting to delete user: " + emails.getPrimaryEmail());
//    }
//
//    @PostRemove
//    public void logUserRemoval() {
//        log.info("Deleted user: " + emails.getPrimaryEmail());
//    }
//
//    @PreUpdate
//    public void logUserUpdateAttempt() {
//        log.info("Attempting to update user: " + emails.getPrimaryEmail());
//    }
//
//    @PostUpdate
//    public void logUserUpdate() {
//        log.info("Updated user: " + emails.getPrimaryEmail());
//    }

    @Override
    public String toString() {
        return String.format("%s %s, ID: %d, %s", getFirstName(), getLastName(), getId(), super.toString());
    }
}
