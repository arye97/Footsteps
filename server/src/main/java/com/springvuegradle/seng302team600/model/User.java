package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.payload.RegisterRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

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

    private static int tokenDecayTime = 30000 * 30; // 30 minutes (30 sec * 30 mins = 15 mins)

    final static public int MAX_EMAILS = 5;

    final static public int MIN_AGE = 13;
    final static public int MAX_AGE = 150;

    final static private int FIELD_LEN = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    @JsonProperty("id")
    private Long userId;

    private String token;

    @Column(name = "token_time")
    private Date tokenTime;

    @NotNull(message = "Please provide a first name")
    @Column(name = "first_name", length = FIELD_LEN, nullable = false)
    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("middlename")
    @Column(name = "middle_name", length = FIELD_LEN)
    private String middleName;

    @NotNull(message = "Please provide a last name")
    @Column(name = "last_name", length = FIELD_LEN, nullable = false)
    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("nickname")
    @Column(name = "nickname", length = FIELD_LEN)
    private String nickName;

    @JsonProperty("bio")
    private String bio;

    @Transient
    @JsonProperty("primary_email")
    private String primaryEmail;

    @Transient
    @JsonProperty("additional_email")
    private List<String> additionalEmails = new ArrayList<>();

    @NotNull
    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    //orphan removal removes 'child' when 'parent' is deleted
    private List<Email> emails = new ArrayList<>();

    @NotNull(message = "Please provide a password")
    @Column(name = "password", nullable = false)
    @JsonProperty("password")
    private String password;

    @NotNull(message = "Please provide a date of birth")
    @Column(name = "date_of_birth", nullable = false, columnDefinition = "DATE")
    @JsonFormat(pattern="yyyy-MM-dd")
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @NotNull(message = "Please provide a gender from the following: male, female, non_binary")
    @Column(name = "gender", length = FIELD_LEN, nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("fitness")
    private int fitnessLevel;

    @ElementCollection
    @CollectionTable(
            name="passport_countries",
            joinColumns=@JoinColumn(name="user_id")
    )
    @Column(name="passport_countries")
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

    /**
     * Builds user from the payload, using getters and setters.
     * @param userData payload for registering.
     * @return the built user.
     */
    public User builder(RegisterRequest userData) {
        this.setFirstName(userData.getFirstName());
        this.setMiddleName(userData.getMiddleName());
        this.setLastName(userData.getLastName());
        this.setNickName(userData.getNickName());
        this.setBio(userData.getBio());
        this.setPrimaryEmail(userData.getPrimaryEmail());
        this.setPassword(userData.getPassword());
        this.setDateOfBirth(userData.getDateOfBirth());
        this.setGender(userData.getGender());
        this.setFitnessLevel(userData.getFitnessLevel());
        this.setPassports(userData.getPassports());
        return this;
    }

    public Long getUserId() {
        return userId;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isTimedOut() {
        ///time calculated in milliseconds
        if (this.tokenTime != null) {
            Date now = new Date();
            long diff = now.getTime() - tokenTime.getTime();
            return diff >= tokenDecayTime || diff < 0;
        } else return true; //Default to the user being timed out
    }

    public void setTokenTime() {
        this.tokenTime = new Date();
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

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    /**
     * Sets primary email of User
     * @param newPrimaryEmail an email to be set primary
     */
    public void setPrimaryEmail(String newPrimaryEmail) {
        // Call this function to set up primaryEmail and additional Emails string from existing Email objects
        setTransientEmailStrings();

        if (primaryEmail != null) {
            // If newPrimaryEmail is already the primary email
            if (primaryEmail.equals(newPrimaryEmail)) {
                return;
            }
            // If at max email count and newPrimaryEmail doesn't already exist
            if (emails.size() >= MAX_EMAILS && !additionalEmails.contains(newPrimaryEmail)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum email limit reached");
            }

            // Change old primary to additional
            for (Email email : emails) {
                if (email.getEmail().equals(primaryEmail)) {
                    additionalEmails.add(primaryEmail);
                    email.setIsPrimary(false);
                    break;
                }
            }

            // Change newPrimaryEmail to primary email
            for (Email email : emails) {
                if (email.getEmail().equals(newPrimaryEmail)) {
                    primaryEmail = newPrimaryEmail;
                    additionalEmails.remove(newPrimaryEmail);
                    email.setIsPrimary(true);
                    return;
                }
            }
        }

        // IF EMAIL HAS NOT BEEN ASSOCIATED TO USER
        primaryEmail = newPrimaryEmail;
        Email email = new Email(newPrimaryEmail, true, this);
        emails.add(email);
    }

    public List<String> getAdditionalEmails() {
        return additionalEmails;
    }

    /**
     * Iterates over a list of additional email strings,
     * adds each string to the list of additional emails,
     * before appending them to a list of Email objects.
     * If primary email has not been set in User throw ResponseStatusException
     * @param newAdditionalEmails a String list of additional emails
     * @throws ResponseStatusException if primary email has not been set
     * if maximum emails limit reached
     */
    public void setAdditionalEmails(List<String> newAdditionalEmails) {
        // Call this function to set up primaryEmail and additional Emails string from existing Email objects
        setTransientEmailStrings();

        if (primaryEmail == null) {
            // primaryEmail can never be null
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Primary email must be created before additional emails are added");
        }

        List<String> removals = new ArrayList<>();
        boolean remove = false;
        for (String email: additionalEmails) {
            if (!newAdditionalEmails.contains(email)) {
                removals.add(email);
                remove = true;
            }
        }

        if (remove) {
            for (String email: removals) {
                deleteAdditionalEmail(email);
            }
        }

        for (String email: newAdditionalEmails) {
            // If email in newAdditionalEmails is a duplicate from additionalEmails
            if (additionalEmails.contains(email) || primaryEmail.equals(email)) {
                continue;
            } else if (emails.size() < MAX_EMAILS) {
                additionalEmails.add(email);
                Email additionalEmail = new Email(email, false, this);
                emails.add(additionalEmail);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum email limit reached");
            }
        }
    }

    /**
     * Removes additional email from String list of additional emails
     * and Email object list of emails
     * @param removedAdditionalEmail additional email to be removed
     */
    public void deleteAdditionalEmail(String removedAdditionalEmail) {
        // Call this function to set up primaryEmail and additional Emails string from existing Email objects
        setTransientEmailStrings();

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
     * Iterates through a list of Email objects and
     * assigns @Transient String variables a primaryEmail and additionalEmails.
     * Simplifies data processing associated with emails
     * and also its representation in the front end.
     */
    public void setTransientEmailStrings() {
        primaryEmail = null;
        additionalEmails.clear();
        for (Email e: emails) {
            if (e.getIsPrimary()) {
                primaryEmail = e.getEmail();
            } else {
                additionalEmails.add(e.getEmail());
            }
        }
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

    /**
     * Runs a sanity check on the user and throws errors if the are invalid fields
     * @throws ResponseStatusException thrown if the users first, middle or last names are invalid
     * thrown if the user is younger than 13
     * thrown if the user is older than 150yr
     * @return returns true if valid user
     */
    public boolean isValid() {
        final String nameRegex = "^[a-zA-Z'-]+$";
        String nameError = "Name must contain at least one letter and no non-letter characters";
        if (firstName == null || lastName == null) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name. " + nameError); }
        if (! firstName.matches(nameRegex) ) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid first name. " + nameError); }
        if (! lastName.matches(nameRegex) ) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid last name. " + nameError); }
        if (middleName != null) {
            if (! middleName.matches(nameRegex) && ! middleName.trim().isEmpty() ) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid middle name. " + nameError); }
        }
        if (ageCheck(dateOfBirth, MIN_AGE, true)) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must be at least 13 years old to register for this app"); }
        if (ageCheck(dateOfBirth, MAX_AGE, false)) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date of birth"); }
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
        if ( younger ) {
            return ageDate.before(DoB);
        } else {
            return ageDate.after(DoB);
        }
    }
}
