<!--TODO: Make dropdown be able to change the primary email, Make a list of email below that which we can edit/change Have an empty text box we can add new secondary emails into, Have default option in dropdown menu as the primary email-->

<template>
    <div>
    <Sidebar/>
        <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
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
                                <td class="additionalEmailsButtons">
                                    <button type="submit" class="btn btn-danger" id="deleteButton" v-on:click="deleteEmail(index)">
                                        Delete
                                    </button>
                                </td>
                                <td class="additionalEmailsButtons">
                                    <button type="submit" class="btn btn-primary" id="primaryButton" v-on:click="setPrimary(index)">
                                        Make Primary
                                    </button>
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
                                        <button type="submit"
                                                class="btn btn-secondary"
                                                v-bind:disabled="duplicateEmailError!==null"
                                        >
                                            Add
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
<!--                                        <label v-if="alreadyInDB" for="newEmailInserted" class="has-error" id="errorMessage">-->
                                        <label for="newEmailInserted" class="has-error" id="errorMessage">
                                            {{ duplicateEmailError }}
                                        </label>
                                    </td>
                                </tr>

                            </table>
                        </form>
                        <div id="confirmationButtons">
<!--?                           <router-link to="/profile" class="btn btn-outline-success btn-lg float-left" @click.native.prevent="backAlert">Back</router-link>-->
                            <button type="submit" class="btn btn-success btn-lg float-left" id="back" :key=this.toReload v-on:click="backAlert">Back</button>
                            <button type="submit"
                                    class="btn btn-success btn-lg float-right"
                                    v-on:click="saveChanges"
                                    v-bind:disabled="changesHaveBeenMade===false">Save Changes</button>
                        </div>
                    </section>
                </article>
            </section>
        </section>
    </div>
</template>

<script>
    import server from '../../Api';
    import {tokenStore} from "../../main";
    import Sidebar from '../../components/layout/ProfileEditSidebar';
    export default {
        name: "EditEmail",
        components: {Sidebar},
        data () {
            return {
                //each time toReload increases then the html el will reload
                toReload: 0,
                loading: true,
                error: false,
                userId: null, //y
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
        mounted() {
            server.get(  `/emails`,
                {
                    headers: {
                        'Content-Type': 'application/json',
                               'Token': tokenStore.state.token
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
                console.error(error);
                console.error(error.response);
            })
        },
        methods: {
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
                    // document.getElementById("newEmailInserted").value = "";
                }
            },

            setPrimary(emailIndex) {
                let additionalEmailId = "additionalEmail" + emailIndex;
                // Obtain Primary Email Candidate from list of Additional Emails
                let candidatePrimaryEmail = document.getElementById(additionalEmailId).innerText;
                // Replace Primary Email Candidate from list of Additional Emails with Old Primary Email
                this.additionalEmails.splice(emailIndex, 1, this.primaryEmail);
                // Set Primary Email Candidate
                this.primaryEmail = candidatePrimaryEmail;
                this.checkIfChangesMade();
            },

            deleteEmail(emailIndex) {
                let additionalEmailId = "additionalEmail" + emailIndex;
                let emailToBeRemoved = document.getElementById(additionalEmailId).innerText;
                // Remove emailToBeRemoved from this.additionalEmails
                this.additionalEmails = this.additionalEmails.filter(
                    function(email) {
                        return email !== emailToBeRemoved
                    });
                this.emailCount--;

                if (this.additionalEmails === this.originalAdditionalEmails) {
                    this.changesHaveBeenMade = false;
                } else {
                    this.changesHaveBeenMade = true;
                }

                let emailTextBox = document.getElementById("newEmailInserted").value;
                if (emailTextBox === this.primaryEmail || this.additionalEmails.includes(emailTextBox)) {
                    // Disable add button if user already assigned to email
                    this.duplicateEmailError = "You are already assigned to this email!";
                } else if (emailTextBox === this.originalPrimaryEmail
                        || emailTextBox === emailToBeRemoved
                        || this.originalAdditionalEmails.includes(emailTextBox)) {
                    this.duplicateEmailError = null;
                } else {
                    this.duplicateEmailError = "";
                }
                this.setEmailCountMessage();
            },

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
                                           'Token': tokenStore.state.token,
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
                        })
                    }
                } else {
                    this.duplicateEmailError = "";
                }
            },

            backAlert() {
                this.checkIfChangesMade()
                if (this.changesHaveBeenMade) {
                    if (confirm("Abort changes?")) {
                        this.$router.push("/profile")
                    } else {
                        console.log(document.getElementById("confirmationButtons"))
                        console.log(document.getElementById("back").classList)
                        this.toReload += 1;
                    }
                } else {
                    this.$router.push("/profile")
                }
            },

            saveChanges() {
                this.checkIfChangesMade()
                if (!this.changesHaveBeenMade) {
                    return
                }

                let savedEmails;
                // Primary Email has not been replaced (POST)
                if (this.primaryEmail === this.originalPrimaryEmail) {
                    savedEmails = {
                        additionalEmails: this.additionalEmails
                    };
                    server.post(`/profiles/${this.userId}/emails`,
                        savedEmails,
                        {
                            headers: {
                                "Access-Control-Allow-Origin": "*",
                                               "content-type": "application/json",
                                                      "Token": tokenStore.state.token
                            },
                            withCredentials: true
                        }
                    ).then(() => {
                        console.log("Additional Emails updated successfully!");
                        window.alert("Successfully saved changes!");
                        this.updateOriginalAdditionalEmail();
                        this.checkIfChangesMade()
                    });
                    // else reset page with error code?
                }

                // Primary Email has been replaced (PUT)
                if (this.primaryEmail !== this.originalPrimaryEmail) {
                    savedEmails = {
                        candidatePrimaryEmail: this.primaryEmail,
                        originalPrimaryEmail: this.originalPrimaryEmail,
                        additionalEmails: this.additionalEmails
                    };
                    server.put(`/profiles/${this.userId}/emails`,
                        savedEmails,
                        {
                            headers: {
                                "Access-Control-Allow-Origin": "*",
                                               "content-type": "application/json",
                                                      "Token": tokenStore.state.token
                            },
                            withCredentials: true
                        }
                    ).then(() => {
                        console.log('Primary Email and Additional Emails updated successfully!');
                        window.alert("Successfully saved changes!");
                        this.updateOriginalPrimaryEmail();
                        this.checkIfChangesMade()
                        //}).catch(error => {
                        // console.log(error);
                        // //Get alert bar element
                        // let errorAlert = document.getElementById("alert");
                        // if (error.message === "Network Error") {
                        //     this.message = error.message;
                        // } else if (error.response.status === 403) { //Error 401: Email already exists or invalid date of birth
                        //     this.message = error.response.data.toString(); //Set alert bar message to error message from server
                        // } else if (error.response.status === 400) { //Error 400: Bad request (missing fields)
                        //     this.message = "An invalid update request has been received please try again"
                        // } else {    //Catch for any errors that are not specifically caught
                        //     this.message = "An unknown error has occurred during update"
                        // }
                        // errorAlert.hidden = false;          //Show alert bar
                        // setTimeout(function () {    //Hide alert bar after ~5000ms
                        //     errorAlert.hidden = true;
                        // }, 5000);
                    });
                }
            },

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
                            // break;
                        }
                    }
                }
            },

            updateOriginalAdditionalEmail() {
                this.originalAdditionalEmails = Array.from(this.additionalEmails)
            },

            updateOriginalPrimaryEmail() {
                this.originalPrimaryEmail = this.primaryEmail;
            },

            setEmailCountMessage() {
                let remaining = 5 - this.emailCount;
                if (this.emailCount >= 5) {
                    this.emailMessage = "Email limit reached!";
                    this.duplicateEmailError = "";
                } else if (this.emailCount == 4) {
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
        padding-bottom: 0px;
        margin-bottom: 0px;
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

    .additionalEmailsButtons {
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
        width:100px;
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

    /*#confirmationButtons:focus {*/
    /*    outline: none;*/
    /*    box-shadow: none;*/
    /*}*/

</style>

