package com.springvuegradle.seng302team600.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

import com.springvuegradle.seng302team600.exception.MustHavePrimaryEmailException;


/**
 * A class that handles the emails of a User.  An Email object holds one primary email and
 * several secondary emails as Strings.  Secondary emails are ordered.
 * Ensures that there is always a primary email and no more than MAX_EMAILS - 1 secondary emails.
 * */
@Entity
@Table(name = "email")
public class Email {

//    @Transient
//    private final int MAX_EMAILS = 5;
//
//    @Transient
//    private final String MAX_EMAILS_EXCEPTION_MESSAGE = "Only " + (MAX_EMAILS-1) + "or less secondary emails are permitted";
//    @Transient
//    private final String EMAIL_ALREADY_REGISTERED_MESSAGE = "This email is already registered.";
//    @Transient
//    private LinkedList<String> secondaryEmails = new LinkedList<String>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "isPrimary", columnDefinition = "boolean", nullable = false)
    private boolean isPrimary;

    @JsonBackReference
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Email() {

    }

    /**
     * Create a new Emails object
     * @param email an email address
     * @param isPrimary the rank of the email - true = primary email
     */
    public Email(String email, boolean isPrimary) {
        this.email = email;
        this.isPrimary = isPrimary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setIsPrimary(boolean isPrimary) throws MustHavePrimaryEmailException {
        if (this.isPrimary == isPrimary) {
            return;
        }
        if (isPrimary == false) {
            throw new MustHavePrimaryEmailException(this.email, "a secondary email as no primary email is declared");
        }
    }

    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", isPrimary=" + isPrimary +
                ", user=" + user +
                '}';
    }



//    /**
//     * Create a new Emails object
//     * */
//    public Emails() {
//    }
//
//    /**
//     * Create a new Emails object
//     * @param primaryEmail a primary email address
//     * */
//    public Emails(String primaryEmail) {
//        this.primaryEmail = primaryEmail;
//        this.secondaryEmails = new LinkedList<String>();
//    }
//    /**
//     * Create a new Emails object and add secondary emails.  If secondaryEmails is larger than MAX_EMAILS - 1,
//     * then it is truncated.
//     * @param primaryEmail a primary email address
//     * @param secondaryEmails a collection of other secondary emails to initialize with.
//     *                        Note must be true that: secondaryEmails.size <= MAX_EMAILS - 1
//     * */
//    public Emails(String primaryEmail, List<String> secondaryEmails) {
//        this.primaryEmail = primaryEmail;
//        this.secondaryEmails = new LinkedList<String>();
//        for (int i=0; i < MAX_EMAILS - 1 && i < secondaryEmails.size(); i++) {  // Copy at max MAX_EMAILS - 1
//            this.secondaryEmails.add(secondaryEmails.get(i));
//        }
//    }
//
//    /**
//     * Add a secondary email.
//     * @param email an email to add as secondary
//     * @param index where in the secondaryEmails list to add
//     * @throws MaximumEmailsException If the object is full, throws MaximumEmailsException.
//     * */
//    public void addSecondaryEmail(String email, int index) throws MaximumEmailsException, EmailAlreadyRegisteredException, IndexOutOfBoundsException {
//        if (primaryEmail == null) {setPrimaryEmail(email); return;}
//        if (this.contains(email)) {throw new EmailAlreadyRegisteredException(EMAIL_ALREADY_REGISTERED_MESSAGE);}
//        if (secondaryEmails.size() >= MAX_EMAILS-1) {
//            throw new MaximumEmailsException(MAX_EMAILS_EXCEPTION_MESSAGE);
//        }
//        secondaryEmails.add(index, email);
//    }
//
//    /**
//     * Add a secondary email to the end of the list of secondaryEmails.
//     * @param secondaryEmail an email to add as secondary
//     * @throws MaximumEmailsException If the object is full, throws MaximumEmailsException.
//     * */
//    public void addSecondaryEmail(String secondaryEmail) throws MaximumEmailsException, EmailAlreadyRegisteredException {
//        addSecondaryEmail(secondaryEmail, secondaryEmails.size());
//    }
//
//
//    /**
//     * Does one of two operations:
//     * - If email is a new unique email address, it is made primary and the last primary email
//     *   is put into secondary emails, if there is room.
//     * - If email is already contained in secondaryEmails, then is is moved to the primary, and
//     *   the last primary is moved to secondary.
//     * @param email either a new email address or an email adress that is a secondary email
//     * */
//    public void setPrimaryEmail(String email) throws MaximumEmailsException, EmailAlreadyRegisteredException {
//        if (primaryEmail.equals(email)) {return;}
//
//        // If email is already in secondaryEmails, use the index of it, else use the last index
//        int index = secondaryEmails.contains(email) ? secondaryEmails.indexOf(email) : secondaryEmails.size();
//
//        secondaryEmails.remove(email);
//        if (secondaryEmails.size() >= MAX_EMAILS-1) {
//            throw new MaximumEmailsException(MAX_EMAILS_EXCEPTION_MESSAGE);
//        }
//        secondaryEmails.add(index, primaryEmail);
//        primaryEmail = email;
//    }
//
//    /**
//     * Takes the secondaryEmail at index and makes it primary.  If primaryEmail == null it does nothing
//     * @param index index of secondary email to make primary
//     * */
//    public void setPrimaryEmail(int index) throws IndexOutOfBoundsException {
//        if (primaryEmail == null) {return;}
//        String oldPrimary = primaryEmail;
//        primaryEmail = secondaryEmails.get(index);
//        secondaryEmails.remove(index);
//        try {
//            addSecondaryEmail(oldPrimary, index);
//        } catch (MaximumEmailsException | EmailAlreadyRegisteredException e) {
//            // Do nothing because there will always be room, but print stack trace just in case.
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Removes and returns the primary email.  The next secondary email is made primary.
//     * If there is no secondary email, it does nothing.
//     * @return the primary email
//     * */
//    public String removePrimaryEmail() {
//        String oldPrimary = primaryEmail;
//        if (secondaryEmails.size() > 0) {
//            primaryEmail = secondaryEmails.pollFirst();
//        } else {
//            // Either do nothing or throw some sort of exception.  I think nothing is fine for now.
//        }
//        return oldPrimary;
//    }
//
//    /**
//     * Removes any emails that equal email.  Can be primary or secondary.
//     * @param email an email to remove.
//     * @return true if email was present
//     * */
//    public boolean removeEmail(String email) {
//        if (email.equals(primaryEmail)) {
//            removePrimaryEmail();
//            return true;
//        } else {
//            return secondaryEmails.remove(email);
//        }
//    }
//
//    /**
//     * Removes secondaryEmail at index
//     * @param index the index of secondaryEmails to remove.
//     * @return the name of the email removed
//     * */
//    public String removeEmail(int index) throws IndexOutOfBoundsException {
//        return secondaryEmails.remove(index);
//    }
//
//    /**
//     * Shifts the order of a secondary email by an amount
//     * @param email the email in question
//     * @param amount the amount to shift by.  Can be positive or negative.  If it is greater than the
//     *               length of secondaryEmails, then it wraps
//     * */
//    public void shiftSecondaryEmail(String email, int amount) {
//        int index = secondaryEmails.indexOf(email);
//        if (index >= 0) {
//            shiftSecondaryEmail(index, amount);
//        }
//
//    }
//    // Test this one well
//    /**
//     * Shifts the order of a secondary email by an amount
//     * @param index the index of the email in secondaryEmails
//     * @param amount the amount to shift by.  Can be positive or negative.  If it is greater than the
//     *               length of secondaryEmails, then it wraps
//     * */
//    public void shiftSecondaryEmail(int index, int amount) {
//        String shiftedEmail = secondaryEmails.get(index);
//        int newIndex = (index + amount) % secondaryEmails.size();
//        // Because Java modulus is different than python's for negative numbers
//        newIndex = newIndex < 0 ? newIndex + secondaryEmails.size() : newIndex;
//        secondaryEmails.remove(index);
//        secondaryEmails.add(newIndex, shiftedEmail);
//    }
//
//    /**
//     * @return the primary email
//     * */
//    public String getPrimaryEmail() {
//        return primaryEmail;
//    }
//    /**
//     * Returns all secondary emails in the correct order as an ArrayList
//     * @return secondary emails contained within the object
//     * */
//    public ArrayList<String> getSecondaryEmails() {
//        return new ArrayList<String>(this.secondaryEmails);
//    }
//
//    public boolean contains(String email) {
//        return primaryEmail.equals(email) || secondaryEmails.contains(email);
//    }
//
//    public int size() {
//        int size = 0;
//        if (primaryEmail != null) {size++;}
//        size += secondaryEmails.size();
//        return size;
//    }
//
//    public String toString() {
//        return String.format("Primary: %s | Secondaries: %s", getPrimaryEmail(), getSecondaryEmails().toString());
//    }

}
