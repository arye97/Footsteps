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
            <p>Sorry, looks like we can't get your info! Please try again soon.</p>
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
                                        Primary Email:
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
                                <td>
                                    <button type="submit" class="btn btn-primary float-right" v-on:click="setPrimary(index)">
                                        Make Primary
                                    </button>
                                </td>
                                <td>
                                    <button type="submit" class="btn btn-danger float-right" v-on:click="deleteEmail">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        </table>

                        <form v-on:submit.prevent="addEmail" id="addEmail">
<!--                        <form id="addEmail">-->
                            <table>
                                <tr>
                                    <th>
                                        <label for="newEmailInserted">Add New Email:</label>
                                    </th>
                                </tr>
                                <tr>
                                    <td>
                                        <input
                                                v-model="insertedEmail"
                                                class="form-control"
                                                id="newEmailInserted"
                                                placeholder="Email address"
                                        >
                                    </td>
                                    <td>
                                        <button type="submit" class="btn btn-secondary">Add</button>
                                    </td>
                                </tr>

                            </table>
                        </form>

                        <div id="confirmationButtons">
                            <router-link to="/" class="btn btn-outline-success btn-lg float-left">Back</router-link>
                            <button type="submit" class="btn btn-success btn-lg float-right" v-on:click="submitEmail">Submit</button>
                        </div>
                    </section>

                </article>


<!--                <form method="put" v-on:submit.prevent="submitEmail">-->
<!--                    <dl class="form-group">-->
<!--                        <span>Primary email address:</span><br/>-->
<!--                        <dt>-->
<!--                            <span>{{ this.primaryEmail }}</span>-->
<!--                            <p class="mt-2"></p>-->
<!--                        </dt>-->
<!--                        <dd>-->
                        <!-- Multiselect for choosing new primary email -->
<!--                        <div class="form-group">-->
<!--                            <label for="primary_email_select">Edit Additional Emails:</label>-->
<!--                            &lt;!&ndash; to change "gender" to something else&ndash;&gt;-->
<!--                            <multiselect v-model="selectedEmail" id="primary_email_select" :searchable="false"-->
<!--                                         :close-on-select="true" :options="additionalEmails" :show-labels="false"-->
<!--                                         :preselectFirst="true" :allow-empty="false"-->
<!--                                         placeholder="No Additional Emails">-->
<!--                            </multiselect>-->
<!--                        </div>-->
<!--                        <button type="submit" class="btn btn-secondary" v-on:click="setPrimary">Set as Primary</button>-->
<!--                        <button type="submit" class="btn btn-secondary" v-on:click="deleteEmail">Delete</button>-->
<!--                        <br/>-->
<!--                        <br/>-->
<!--                        <br/>-->

<!--                        -->


<!--                        <div class="form-group">-->
<!--                            &lt;!&ndash; full-name field&ndash;&gt;-->
<!--                            <label for="add-email">Add email address</label>-->
<!--                            <input type="email" class="form-control" v-model="addedEmail" id="add-email" name="add-email" placeholder="Email address">-->
<!--                        </div>-->


<!--                        <button type="submit" class="btn btn-secondary" v-on:click="addEmail">Add</button>-->
<!--                        </dd>-->
<!--                    </dl>-->
<!--                </form>-->
<!--                <div class="form-group">-->
<!--                    &lt;!&ndash; SignIn Button&ndash;&gt;-->
<!--                    <button type="submit" class="btn btn-primary" v-on:click="submitEmail">Submit</button>-->
<!--                    <router-link to="/" class="btn btn-link">Go Back</router-link>-->
<!--                </div>-->
            </section>
        </section>
    </div>
</template>

<script>
    import server from '../../Api';
    import {tokenStore} from "../../main";
    import Sidebar from '../../components/layout/ProfileEditSidebar';
    // eslint-disable-next-line no-unused-vars
    // import Multiselect from "vue-multiselect";

    export default {
        name: "EditEmail",
        components: {Sidebar}, //Multiselect},
        data () {
            return {
                loading: true, //y
                post: null,
                error: false,
                userId: null, //y
                primaryEmail: null, //y
                additionalEmails: [], //y
                originalPrimaryEmail: null, //y
                additionalEmailsToBeAdded: [], //y
                selectedEmail: null,
                insertedEmail: null, //y
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
                    this.userId = response.data["userId"];
                    this.primaryEmail = response.data["primaryEmail"];
                    this.originalPrimaryEmail = response.data["primaryEmail"];
                    this.additionalEmails = response.data["additionalEmails"];
                    this.loading = false;
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
                    this.additionalEmails.unshift(this.insertedEmail);
                    this.additionalEmailsToBeAdded.unshift(this.insertedEmail);
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
            },

            submitEmail() {
                let submittedEmail;
                // Primary Email has not been replaced
                if (this.primaryEmail === this.originalPrimaryEmail) {
                    submittedEmail = {
                        additionalEmails: this.additionalEmails
                    };
                    console.log(submittedEmail)
                    server.post(`/profiles/${this.userId}/emails`,
                        submittedEmail,
                        {
                            headers: {
                                "Access-Control-Allow-Origin": "*",
                                               "content-type": "application/json",
                                                      "Token": tokenStore.state.token
                            },
                            withCredentials: true
                        }
                    ).then(() => {
                        console.log('Additional Emails updated successfully!');
                        this.resetAdditionalEmailsToBeAdded();
                    });
                }

                // Primary Email has been replaced
                if (this.primaryEmail !== this.originalPrimaryEmail) {
                    submittedEmail = {
                        candidatePrimaryEmail: this.primaryEmail,
                        originalPrimaryEmail: this.originalPrimaryEmail,
                        additionalEmails: this.additionalEmails
                    };

                    // TEST Don't mind me!!!
                    console.log("New Primary:" + submittedEmail.candidatePrimaryEmail);
                    console.log("og Primary:" + submittedEmail.originalPrimaryEmail);
                    console.log(submittedEmail.additionalEmails);
                    // TEST Leave me be!!!

                    server.put(`/profiles/${this.userId}/emails`,
                        submittedEmail,
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
                        // this.resetAdditionalEmailsToBeAdded();
                    });
                }









                // const updateEmail = {
                //     primaryEmail: this.primaryEmail,
                //     additionalEmails: this.insertedEmail
                // };
                // server.put(`/profiles/${this.userId}/emails`,
                //     updateEmail,
                //     {
                //         headers: {"Access-Control-Allow-Origin": "*",
                //             "content-type": "application/json",
                //             "Token": tokenStore.state.token},
                //         withCredentials: true
                //     }
                // ).then(function() {
                //     console.log('User Emails updated Successfully!');
                // }).catch(error => {
                //     console.log(error);
                //     //Get alert bar element
                //     let errorAlert = document.getElementById("alert");
                //     if (error.message === "Network Error") {
                //         this.message = error.message;
                //     } else if (error.response.status === 403) { //Error 401: Email already exists or invalid date of birth
                //         this.message = error.response.data.toString(); //Set alert bar message to error message from server
                //     } else if (error.response.status === 400) { //Error 400: Bad request (missing fields)
                //         this.message = "An invalid update request has been received please try again"
                //     } else {    //Catch for any errors that are not specifically caught
                //         this.message = "An unknown error has occurred during update"
                //     }
                //     errorAlert.hidden = false;          //Show alert bar
                //     setTimeout(function () {    //Hide alert bar after ~5000ms
                //         errorAlert.hidden = true;
                //     }, 5000);
                // });
            },

            resetAdditionalEmailsToBeAdded() {
                this.additionalEmailsToBeAdded = [];
            },



            deleteEmail() {
                //Remove an email
                let selectedIndex = this.additionalEmails.indexOf(this.selectedEmail);
                if (selectedIndex > -1) {
                    this.additionalEmails.splice(selectedIndex, 1);
                }
            }
        }
    }
</script>

<style scoped>
    .btn {
        margin-right: 1%;
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

    #additionalEmailsTable button {
        text-align: center;
        padding: 0;
        margin-left: 0;
        margin-right: 0;
        font-size: 17px;
        width:130px;
        height:40px;
    }

    .additionalEmail {
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
</style>

