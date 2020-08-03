<template>
    <div v-if="this.isRedirecting">
        {{ redirectionMessage }}
        <br/><br/><b-spinner variant="primary" label="Spinning"></b-spinner>
    </div>
    <div v-else>
        <b-container class="contentsExtendedBottom" fluid>
            <section v-if="error">
                <p>Sorry, looks like we can't get your info! Please try again.</p>
                <p>{{ error }}</p>
            </section>

            <section v-else>

                <section v-if="loading">
                    <div class="loading">
                        <b-spinner variant="primary" label="Spinning"></b-spinner>
                    </div>
                </section>

                <section v-else>
                    <article class="emails">
                        <section class="primaryEmailDisplay">
                            <table id="primaryEmailTable" class="table table-borderless">
                                <tr>
                                    <td>
                                        <p id="primaryEmailLabel">
                                            <b>Primary Email:</b>
                                        </p>
                                    </td>
                                    <td>
                                        <p id="primaryEmail">
                                            <b>{{ this.primaryEmail }}</b>
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </section>
                        <hr>
                        <section class="additionalEmailsDisplay">
                            <table id="additionalEmailsTable" class="table table-borderless">
                                <tr v-for="(additionalEmail, index) in this.additionalEmails"
                                    v-bind:key="additionalEmail">
                                    <td>
                                        <p :id="'additionalEmail' + index">
                                            {{ additionalEmail }}
                                        </p>
                                    </td>
                                    <td class="deleteButtonTd">
                                        <b-button type="submit" variant="danger" id="deleteButton" v-on:click="deleteEmail(index)">
                                            <b-icon-trash-fill></b-icon-trash-fill>
                                        </b-button>
                                    </td>
                                    <td class="makePrimaryButtonTd">
                                        <b-button type="submit" variant="primary" id="primaryButton" v-on:click="setPrimary(index)">
                                            Make Primary
                                        </b-button>
                                    </td>
                                </tr>
                            </table>
                            <form v-on:submit.prevent="addEmail" id="addEmail">
                                <table>
                                    <tr>
                                        <th id="addNewEmailTh">
                                            <label for="newEmailInserted">
                                                Add New Email:
                                            </label>
                                        </th>
                                        <th id="emailMessageTh">
                                            <label for="newEmailInserted" class="has-error">
                                                {{ this.emailMessage }}
                                            </label>
                                        </th>
                                    </tr>
                                </table>
                                <table>
                                    <tr>
                                        <td>
                                            <input v-model="insertedEmail"
                                                   class="form-control"
                                                   id="newEmailInserted"
                                                   placeholder="Email address"
                                                   @keyup="checkEmail()"
                                            >
                                        </td>
                                        <td>
                                        <!--Disable button if duplicateEmailError is not null-->
                                            <b-button type="submit"
                                                    variant="secondary"
                                                    v-bind:disabled="duplicateEmailError!==null"
                                            >
                                                <p class="h5 mb-0">
                                                    <b-icon-plus></b-icon-plus>
                                                </p>
                                            </b-button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <label for="newEmailInserted" class="has-error" id="errorMessage">
                                                {{ duplicateEmailError }}
                                            </label>
                                        </td>
                                    </tr>

                                </table>
                            </form>
                            <div id="confirmationButtons">
                                <b-button type="submit" variant="success float-left"
                                          size="lg" id="back" :key=this.toReload
                                          v-on:click="backAlert">Back</b-button>
                                <b-button type="submit" variant="success float-right"
                                          size="lg" v-on:click="saveChanges"
                                          v-bind:disabled="changesHaveBeenMade===false">Save Changes</b-button>
                            </div>
                        </section>
                    </article>
                </section>
            </section>
        </b-container>
    </div>
</template>
<script>
    import api from '../../Api';
    export default {
        name: "EditEmail",
        data () {
            return {
                //each time toReload increases then the html el will reload
                toReload: 0,
                loading: true,
                error: false,
                userId: '', //y
                primaryEmail: null, //y
                additionalEmails: [], //y
                originalPrimaryEmail: null, //y
                originalAdditionalEmails: [], //y
                insertedEmail: null, //y
                emailCount: 0, //y
                emailMessage: null, //y
                duplicateEmailError: "", //y
                changesHaveBeenMade: false,
                isEditable: false,
                isRedirecting: false,
                redirectionMessage: ''
            }
        },
        async mounted() {
            await this.init();
        },
        methods: {
            async init() {
                this.toReload = 0;
                this.loading = true;
                this.error = false;
                this.isEditable = false;
                this.emailMessage = null;
                this.duplicateEmailError = "";
                this.changesHaveBeenMade = false;

                this.userId = this.$route.params.userId;
                if (this.userId === undefined) {
                    this.isEditable = true;
                    this.userId = '';
                } else {
                    await this.editable(); // If allowed to edit, userId is set
                }
                if (this.isEditable) {

                    await api.getUserData(this.userId).then(response => {
                        this.userId = response.data.id;
                    }).catch(error => {
                        if (error.response.data.status === 401) {
                            this.logout();
                        }
                    });

                    await api.getUserEmails(this.userId).then(response => {
                        if (response.status === 200) {
                            this.loading = false;
                            this.userId = response.data["userId"];
                            this.primaryEmail = response.data["primaryEmail"];
                            this.additionalEmails = response.data["additionalEmails"];
                            this.originalPrimaryEmail = response.data["primaryEmail"];
                            this.originalAdditionalEmails = Array.from(response.data["additionalEmails"]);
                            this.emailCount = this.additionalEmails.length + 1;
                            this.setEmailCountMessage();
                        }
                    }).catch(function(error) {
                        if (error.response.status === 401) {
                            this.$router.push("/login");
                        }
                        else if (error.response.status === 500) {
                            console.error(error.response.data.message);
                            // Return to root home screen when timeout.
                            this.$router.push('/');
                        }
                    })
                }
            },
            /**
             * Adds an email to the list of displayed additional emails.
             * Additionally sets up mechanisms associated with the disabling/enabling
             * of the SAVE button and the ADD (+) button.
             */
            addEmail() {
                if (this.additionalEmails.length >= 4) {
                    // Can't exceed length of 5 emails
                    console.log('Error: maximum number of emails reached. Please remove an email before adding any more (Limit 5)')
                } else if (this.additionalEmails.includes(this.insertedEmail) || (this.primaryEmail === this.insertedEmail)) {
                    //Can't have duplicate emails
                    console.log("Error: Can't add duplicate email");
                } else if (!(/(.+)@(.+){2,}\.(.+){2,}/).test(this.insertedEmail)) {
                    //Email's not valid
                    console.log('Error: Invalid email. Please change to proper email format and try again')
                } else {
                    //Email is valid
                    this.additionalEmails.push(this.insertedEmail);
                    this.emailCount++;
                    this.setEmailCountMessage();
                    this.checkIfChangesMade();
                    if (this.insertedEmail === this.primaryEmail || this.additionalEmails.includes(this.insertedEmail)) {
                        // Disable add button if user already assigned to email
                        this.duplicateEmailError = "";
                    }
                }
                this.insertedEmail = null;
            },

            /**
             * Demotes the currently displayed primary email to the list of displayed additional emails and
             * promotes an additional email from the list to a displayed primary email.
             * Additionally sets up mechanisms associated with the disabling/enabling of the SAVE button.
             * @param emailIndex index of additional email to be set to primary
             */
            setPrimary(emailIndex) {
                // Obtain Primary Email Candidate from list of Additional Emails
                let candidatePrimaryEmail = this.additionalEmails[emailIndex];
                // Replace Primary Email Candidate from list of Additional Emails with Old Primary Email
                this.additionalEmails.splice(emailIndex, 1, this.primaryEmail);
                // Set Primary Email Candidate
                this.primaryEmail = candidatePrimaryEmail;
                // Disables/Enables the SAVE button
                this.checkIfChangesMade();
            },

            /**
             * Removes an email from the displayed list of additional emails.
             * Additionally sets up mechanisms associated with the disabling/enabling
             * of the SAVE button and the ADD (+) button.
             * @param emailIndex index of additional email to be removed
             */
            deleteEmail(emailIndex) {
                let emailToBeRemoved = this.additionalEmails[emailIndex];
                // Remove emailToBeRemoved from this.additionalEmails
                this.additionalEmails = this.additionalEmails.filter(
                    function(email) {
                        return email !== emailToBeRemoved
                    });
                this.emailCount--;

                // Disables/Enables the SAVE button
                this.changesHaveBeenMade = this.additionalEmails !== this.originalAdditionalEmails;

                // Disables/Enables the ADD (+) button
                if (this.insertedEmail === this.primaryEmail || this.additionalEmails.includes(this.insertedEmail)) {
                    // Disable add button if user already assigned to email
                    this.duplicateEmailError = "You are already assigned to this email!";
                } else if (this.insertedEmail === this.originalPrimaryEmail
                    || this.insertedEmail === emailToBeRemoved
                    || this.originalAdditionalEmails.includes(this.insertedEmail)
                    || (this.duplicateEmailError !== "You are already assigned to this email!"
                        && this.duplicateEmailError !== "We're sorry, that email is taken.")) {
                    // Enable add button if email assigned TO ORIGINAL PRIMARY EMAIL AND ADDITIONAL EMAIL AND IS TO BE REMOVED
                    this.duplicateEmailError = null;
                } else {
                    // Disable button
                    this.duplicateEmailError = "";
                }
                this.setEmailCountMessage();
            },

            /**
             * Checks the validity of an email that a user has provided in the text box.
             * In particular, it performs a GET request to the back-end to check
             * if an email has been associated with another user (if its a duplicate).
             * This function is called to set the disabling/enabling of the ADD (+) button.
             */
            checkEmail() {
                // Check if Email is formatted correctly
                if ((/(.+)@(.+){2,}\.(.+){2,}/).test(this.insertedEmail)) {
                    if (this.emailMessage === "Email limit reached!") {
                        // Disable add button if email limit reached
                        this.duplicateEmailError = "";
                    } else if (this.insertedEmail === this.primaryEmail || this.additionalEmails.includes(this.insertedEmail)) {
                        // Disable add button if user already assigned to email
                        this.duplicateEmailError = "You are already assigned to this email!";
                    } else if (this.insertedEmail === this.originalPrimaryEmail || this.originalAdditionalEmails.includes(this.insertedEmail)) {
                        this.duplicateEmailError = null;
                    } else {
                        api.checkUserEmail(this.insertedEmail).then(() => {
                            this.duplicateEmailError = null;
                        }).catch(error => {
                            if (error.response.status === 400) {
                                let message = "Bad Request: email " + this.insertedEmail + " is already in use";
                                // Disable add button if email is in use
                                if (error.response.data.message === message) {
                                    this.duplicateEmailError = "We're sorry, that email is taken."
                                }
                            }
                            if (error.response.status === 401) {
                                // Not logged in
                                this.$router.push('/login'); //Routes to home on logout
                            }
                        })
                    }
                } else {
                    if (this.insertedEmail) {
                        this.duplicateEmailError = "Please enter a valid email!";
                    } else {
                        this.duplicateEmailError = "";
                    }
                }
            },

            /**
             * Called when the BACK button is pressed
             * and conjures a pop-up if changes have been made in the email editing form.
             * Otherwise redirects the user back to the profile screen.
             */
            backAlert() {
                this.checkIfChangesMade();
                if (this.changesHaveBeenMade) {
                    if (confirm("Cancel changes?")) {
                        this.backToProfile();
                    } else {
                        this.toReload += 1;
                    }
                } else {
                    this.backToProfile();
                }
            },

            /**
             * Saves changes associated with email editing by a user.
             * Depending on the changes made, it either performs
             * a POST request to save additional emails or
             * a PUT request to save primary and additional emails.
             * Additionally sets up mechanisms associated with the disabling/enabling
             * of the SAVE button.
             */
            async saveChanges() {
                this.checkIfChangesMade();
                if (!this.changesHaveBeenMade) {
                    return
                }
                let savedEmails;
                // Primary Email has not been replaced (POST)
                if (this.primaryEmail === this.originalPrimaryEmail) {
                    savedEmails = {
                        additional_email: this.additionalEmails
                    };

                    api.setAdditionalEmails(savedEmails, this.userId).then(() => {
                        console.log("Additional Emails updated successfully!");
                        window.alert("Successfully saved changes!");
                        this.updateOriginalAdditionalEmails();
                        this.checkIfChangesMade();
                    }).catch(async error => {
                        await this.processGetError(error);
                        this.primaryEmail = this.originalPrimaryEmail;
                        this.additionalEmails = Array.from(this.originalAdditionalEmails);
                    })
                }

                // Primary Email has been replaced (PUT)
                if (this.primaryEmail !== this.originalPrimaryEmail) {
                    savedEmails = {
                        primary_email: this.primaryEmail,
                        additional_email: this.additionalEmails
                    };
                    api.setEmails(savedEmails, this.userId).then(() => {
                        console.log('Primary Email and Additional Emails updated successfully!');
                        window.alert("Successfully saved changes!");
                        this.updateOriginalPrimaryEmail();
                        this.updateOriginalAdditionalEmails();
                        this.checkIfChangesMade();
                    }).catch(async error => {
                        await this.processGetError(error);
                        this.primaryEmail = this.originalPrimaryEmail;
                        this.additionalEmails = Array.from(this.originalAdditionalEmails);
                    });
                }
            },

            /**
             * Checks if changes have been made by a user, in particular,
             * to set up mechanisms associated with the disabling/enabling
             * of the SAVE button.
             */
            checkIfChangesMade() {
                this.changesHaveBeenMade = false;
                if (this.primaryEmail !== this.originalPrimaryEmail) {
                    this.changesHaveBeenMade = true
                } else if (this.additionalEmails.length !== this.originalAdditionalEmails.length) {
                    this.changesHaveBeenMade = true;
                } else if (this.additionalEmails.length === this.originalAdditionalEmails.length) {
                    for (let index in this.originalAdditionalEmails) {
                        if (!this.additionalEmails.includes(this.originalAdditionalEmails[index])) {
                            this.changesHaveBeenMade = true;
                        }
                    }
                }
            },

            /**
             * Simple function to set the originalAdditionalEmails with the updated additionalEmails list.
             */
            updateOriginalAdditionalEmails() {
                this.originalAdditionalEmails = Array.from(this.additionalEmails)
            },

            /**
             * Simple function to set the originalPrimaryEmail with the updated primaryEmail.
             */
            updateOriginalPrimaryEmail() {
                this.originalPrimaryEmail = this.primaryEmail;
            },

            /**
             * Function called to set messages associated with emails.
             * Additionally sets up mechanisms associated with the disabling/enabling
             * of the ADD button.
             */
            setEmailCountMessage() {
                let remaining = 5 - this.emailCount;
                if (this.emailCount >= 5) {
                    this.emailMessage = "Email limit reached!";
                    this.duplicateEmailError = "";
                } else if (this.emailCount === 4) {
                    this.emailMessage = remaining + " spot left for additional emails!";
                } else {
                    this.emailMessage = remaining + " spots left for additional emails!";
                }
            },

            /**
             * Check if the current userId is allowed to be edited by this user session
             */
            async editable() {
                if (this.userId === '') {
                    this.isEditable = true;
                    return;
                }
                await api.checkProfile(this.userId).then(() => {
                    //Status code 200
                    //User can edit this profile
                    this.isEditable = true;
                }).catch(error => {
                    this.isEditable = false;
                    if (error.response.data.status === 401) {
                        this.logout();
                    } else {
                        this.$router.push({ name: 'emailsNoId' });
                        this.init();
                    }
                });
            },

            /**
             * Logs the user out and clears session token
             */
            logout () {
                api.logout().then(() => {
                    sessionStorage.clear();
                    // tokenStore.setToken(null);
                    this.isLoggedIn = (sessionStorage.getItem("token") !== null);
                    this.$forceUpdate();
                    this.$router.push('/login'); //Routes to home on logout
                }).catch(() => {
                    sessionStorage.clear();
                    this.isLoggedIn = (sessionStorage.getItem("token") !== null);
                    this.$forceUpdate();
                    this.$router.push('/login'); //Routes to home on logout
                })
            },

            /**
             * Redirect to view user screen
             */
            backToProfile() {
                this.$router.push({ name: 'profile', params: {userId: this.userId} });
            },

            /**
             * This helper function is called when an error is caught when performing a Get request to the server.<br>
             * Conditions handled are:<br>
             * 401 (UNAUTHORIZED) redirect to login page,<br>
             * 403 (FORBIDDEN) and 404 (NOT_FOUND) redirect to this user's edit profile page,<br>
             * Otherwise unknown error so redirect to user's home page
             */
            async processGetError(error) {
                this.isRedirecting = true;
                if (error.response.status === 401) {
                    this.redirectionMessage = "Sorry, you are no longer logged in,\n" +
                        "Redirecting to the login page.";
                    setTimeout(() => {
                        this.logout()
                    }, 4000);
                } else if (error.response.status === 403) {
                    // If user ever gets to another user's edit email page and makes changes to it
                    this.redirectionMessage = "Sorry, you are not allowed to edit another user's profile,\n" +
                        "Redirecting to your edit emails page.";
                    setTimeout(() => {
                        this.$router.push(`/profile/${this.userId}/edit`);
                    }, 4000);
                } else if (error.response.status === 404) {
                    this.redirectionMessage = "Sorry, the user does not exist,\n" +
                        "Redirecting to your edit emails page.";
                    setTimeout(() => {
                        this.$router.push(`/profile/${this.userId}/edit`);
                        this.init();
                    }, 4000);
                } else {
                    this.redirectionMessage = "Sorry, an unknown error occurred when retrieving profile info,\n" +
                        "Redirecting to your home page.";
                    setTimeout(() => {
                        this.$router.push(`/profile`);
                    }, 4000);
                }
            }
        }
    }
</script>


<style scoped>
    table {
        width: 100%;
    }

    #primaryEmailTable {
        padding-bottom: 0;
        margin-bottom: 0;
        border-collapse: collapse;
        font-size: 20px;
    }

    #primaryEmailTable td {
        padding: 0;
    }

    #primaryEmailLabel {
        text-align: left;
        padding-bottom: 0;
        margin-bottom: 0;

    }

    #primaryEmail {
        text-align: right;
        padding-bottom: 0;
        margin-bottom: 0;
    }

    #additionalEmailsTable {
        font-size: 17px;
    }

    #additionalEmailsTable td {
        padding-bottom: 0;
        padding-left: 5px;
        padding-right: 5px;
    }

    .makePrimaryButtonTd {
        float: right;
    }

    .deleteButtonTd {
        float: right;
    }

    #additionalEmailsTable button {
        text-align: center;
        padding: 0;
        margin-left: 0;
        margin-right: 0;
        font-size: 17px;
    }

    #deleteButton {
        /*width:100px;*/
        width:40px;
        height:40px;
    }

    #primaryButton {
        width:135px;
        height:40px;
    }

    .additionalEmailsTable p {
        padding-bottom: 0;
        margin-bottom: 0;
        height:40px;
        vertical-align: middle;
        line-height: 39px;
    }

    #addEmail {
        margin: 4px;
        padding-bottom: 15px;
    }

    #addEmail td {
        width: 100%;
    }

    #addEmail input {
        margin-left: -5px;
    }

    #addEmail button {
        margin-left: -2px;
    }

    #addNewEmailTh {
        width: 25%;
    }

    #emailMessageTh {
        width: 100%;
        margin-bottom:0;
        padding-bottom:0;
        text-align: right;
        color: orangered;
    }

    #errorMessage {
        margin-top: 5px;
        color: red;
    }
</style>