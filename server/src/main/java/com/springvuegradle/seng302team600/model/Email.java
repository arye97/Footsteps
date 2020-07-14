package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * A class that handles the emails of a User.  An Email object holds one primary email and
 * several secondary emails as Strings.  Secondary emails are ordered.
 * Ensures that there is always a primary email and no more than MAX_EMAILS - 1 secondary emails.
 * */
@Entity
@Table(name = "email")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonProperty("id")
    private Long emailId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "isPrimary", columnDefinition = "boolean", nullable = false)
    private boolean isPrimary;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Default constructor for Email.
     * Mandatory for repository actions.
     */
    public Email() {}

    /**
     * Create a new Emails object
     * @param email an email address
     * @param isPrimary the rank of the email - true = primary email
     * @param user the User it references
     */
    public Email(String email, boolean isPrimary, User user) {
        this.email = email;
        this.isPrimary = isPrimary;
        this.user = user;
    }

    public Long getEmailId() {
        return emailId;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    @Override
    public String toString() {
        return "Email{" +
                "emailId=" + emailId +
                ", email='" + email + '\'' +
                ", isPrimary=" + isPrimary +
                ", user=" + user +
                '}';
    }
}
