package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class User {

    private static Log log = LogFactory.getLog(User.class);

    private static PasswordEncoder encoder = new BCryptPasswordEncoder();

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

    @NotNull(message = "Please provide a primary email address")
    @OneToMany(mappedBy = "email", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("primary_email")
    private List<Emails> emails;

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
    private ArrayList<String> passports;

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

    public List<Emails> getEmails() {
        return emails;
    }

    public void setEmails(List<Emails> emails) {
        //hacky
        emails.get(0).setUser(this);
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

    public ArrayList<String> getPassports() {
        return passports;
    }

    public void setPassports(ArrayList<String> passports) {
        this.passports = passports;
    }

    public void addPassport(String passport) {
        passports.add(passport);
    }

    public boolean removePassport(String passport) {
        return passports.remove(passport);
    }


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
