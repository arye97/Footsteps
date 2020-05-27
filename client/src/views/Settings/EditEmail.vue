<template>
    <div>
        <h1><br/></h1>
        <template v-if="userId">
            <div>
                <div class="container">
                    <div class="row">
                        <div class="col-sm-6 offset-sm-3">
                            <Header :userId="this.userId"/>
                            <router-view></router-view>
                        </div>
                    </div>
                </div>
            </div>
            <Sidebar :userId="this.userId"/>
        </template>


        <h1><br/></h1>
        <header class="masthead">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light">Edit Emails</h1>
                        <p class="lead">Edit the emails linked to your profile</p><br/>
                    </div>
                </div>
            </div>
        </header>

        <section v-if="error">
            <p>Sorry, looks like we can't get your info! Please try again.</p>
            <p>{{ error }}</p>
        </section>

        <section v-else>

            <section v-if="loading">
                <div class="loading">
                    Loading...
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
                                        {{ this.primaryEmail }}
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
    </div>
</template>
<script>
    import server from '../../Api';
    // import {tokenStore} from "../../main";
    import Sidebar from '../../components/layout/ProfileEditSidebar';
    import Header from '../../components/Header/Header.vue'
    export default {
        name: "EditEmail",
        components: { Sidebar, Header },
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
                changesHaveBeenMade: false
            }
        },
        async beforeMount() {

            await server.get(`/profiles/${this.userId}`,
                {headers: {'Content-Type': 'application/json', 'Token': sessionStorage.getItem("token")},
                    withCredentials: true
                }, ).then(response => {
                this.userId = response.data.id;
            }).catch(error => {
                if (error.response.data.status === 401) {
                    this.$router.push("/login");
                }
            });

            console.log(this.userId);

            await server.get(  `/profiles/${this.userId}/emails`,
                {
                    headers: {
                        'Content-Type': 'application/json',
                               'Token': sessionStorage.getItem("token")
                    },
                    withCredentials: true
                }

            ).then(response => {
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
                    console.log(error.response.data.message);
                    // Return to root home screen when timeout.
                    this.$router.push('/');
                }
            })
        },
        methods: {

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
                    let emailTextBox = document.getElementById("newEmailInserted").value;
                    if (emailTextBox === this.primaryEmail || this.additionalEmails.includes(emailTextBox)) {
                        // Disable add button if user already assigned to email
                        this.duplicateEmailError = "";
                    }
                }
            },

            /**
             * Demotes the currently displayed primary email to the list of displayed additional emails and
             * promotes an additional email from the list to a displayed primary email.
             * Additionally sets up mechanisms associated with the disabling/enabling of the SAVE button.
             * @param emailIndex index of additional email to be set to primary
             */
            setPrimary(emailIndex) {
                let additionalEmailId = "additionalEmail" + emailIndex;
                // Obtain Primary Email Candidate from list of Additional Emails
                let candidatePrimaryEmail = document.getElementById(additionalEmailId).innerText;
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
                let additionalEmailId = "additionalEmail" + emailIndex;
                let emailToBeRemoved = document.getElementById(additionalEmailId).innerText;
                // Remove emailToBeRemoved from this.additionalEmails
                this.additionalEmails = this.additionalEmails.filter(
                    function(email) {
                        return email !== emailToBeRemoved
                    });
                this.emailCount--;

                // Disables/Enables the SAVE button
                if (this.additionalEmails === this.originalAdditionalEmails) {
                    this.changesHaveBeenMade = false;
                } else {
                    this.changesHaveBeenMade = true;
                }

                // Disables/Enables the ADD (+) button
                let emailTextBox = document.getElementById("newEmailInserted").value;
                if (emailTextBox === this.primaryEmail || this.additionalEmails.includes(emailTextBox)) {
                    // Disable add button if user already assigned to email
                    this.duplicateEmailError = "You are already assigned to this email!";
                } else if (emailTextBox === this.originalPrimaryEmail
                        || emailTextBox === emailToBeRemoved
                        || this.originalAdditionalEmails.includes(emailTextBox)
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
                let emailTextBox = document.getElementById("newEmailInserted").value;
                // Check if Email is formatted correctly
                if ((/(.+)@(.+){2,}\.(.+){2,}/).test(emailTextBox)) {
                    if (this.emailMessage === "Email limit reached!") {
                        // Disable add button if email limit reached
                        this.duplicateEmailError = "";
                    } else if (emailTextBox === this.primaryEmail || this.additionalEmails.includes(emailTextBox)) {
                        // Disable add button if user already assigned to email
                        this.duplicateEmailError = "You are already assigned to this email!";
                    } else if (emailTextBox === this.originalPrimaryEmail || this.originalAdditionalEmails.includes(emailTextBox)) {
                        this.duplicateEmailError = null;
                    } else {
                        server.get(`/email`,
                            {
                                headers: {
                                    'Content-Type': 'application/json',
                                           'Token': sessionStorage.getItem("token"),
                                           'email': emailTextBox
                                },
                                withCredentials: true
                            }
                        ).then(() => {
                            this.duplicateEmailError = null;
                        }).catch(error => {
                            if (error.response.status === 400) {
                                console.log(error.response.data.message);
                                let message = "Bad Request: email " + emailTextBox + " is already in use";
                                // Disable add button if email is in use
                                if (error.response.data.message === message) {
                                    this.duplicateEmailError = "We're sorry, that email is taken."
                                }
                            }
                            if (error.response.status === 401) {
                                console.log(error.response.data.message);
                            }


                        })
                    }
                } else {
                    this.duplicateEmailError = "";
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
                        this.$router.push("/profile")
                    } else {
                        this.toReload += 1;
                    }
                } else {
                    this.$router.push("/profile")
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
            saveChanges() {
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

                    server.post(`/profiles/${this.userId}/emails`,
                        savedEmails,
                        {
                            headers: {
                                "Access-Control-Allow-Origin": "*",
                                               "Content-Type": "application/json",
                                                      "Token": sessionStorage.getItem("token")
                            },
                            withCredentials: true
                        }
                    ).then(() => {
                        console.log("Additional Emails updated successfully!");
                        window.alert("Successfully saved changes!");
                        this.updateOriginalAdditionalEmails();
                        this.checkIfChangesMade();
                    }).catch(error => {
                        if (error.response.status === 400) {
                            console.log(error.response.data.message);
                        }
                        else if (error.response.status === 401) {
                            console.log(error.response.data.message);
                        }
                        else if (error.response.status === 403) {
                            console.log(error.response.data.message);
                        }
                        else if (error.response.status === 404) {
                            console.log(error.response.data.message);
                        }
                        this.primaryEmail = this.originalPrimaryEmail;
                        this.additionalEmails = Array.from(this.originalAdditionalEmails);
                        window.alert("Could not save changes! :(");
                    })
                }

                // Primary Email has been replaced (PUT)
                if (this.primaryEmail !== this.originalPrimaryEmail) {
                    savedEmails = {
                        primary_email: this.primaryEmail,
                        additional_email: this.additionalEmails
                    };
                    console.log(`/profiles/${this.userId}/emails`);
                    server.put(`/profiles/${this.userId}/emails`,
                        savedEmails,
                        {
                            headers: {
                                "Access-Control-Allow-Origin": "*",
                                               "Content-Type": "application/json",
                                                      "Token": sessionStorage.getItem("token")
                            },
                            withCredentials: true
                        }
                    ).then(() => {
                        console.log('Primary Email and Additional Emails updated successfully!');
                        window.alert("Successfully saved changes!");
                        this.updateOriginalPrimaryEmail();
                        this.updateOriginalAdditionalEmails();
                        this.checkIfChangesMade();
                    }).catch(error => {
                        if (error.response.status === 400) {
                            console.log(error.response.data.message);
                        }
                        else if (error.response.status === 401) {
                            console.log(error.response.data.message);
                        }
                        else if (error.response.status === 403) {
                            console.log(error.response.data.message);
                        }
                        else if (error.response.status === 404) {
                            console.log(error.response.data.message);
                        }
                        this.primaryEmail = this.originalPrimaryEmail;
                        this.additionalEmails = Array.from(this.originalAdditionalEmails);
                        window.alert("Could not save changes! :(");
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
        color: #707070;
    }

    #errorMessage {
        margin-top: 5px;
        color: chocolate;
    }
</style>

