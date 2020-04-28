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
            <div v-if="loading" class="loading">
                Loading...
            </div>
            <div v-else class="form-group">
                <form method="put" v-on:submit.prevent="submitEmail">
                    <dl class="form-group">

<!--                        Displays Current Primary Email-->
                            <span>Primary email address:</span><br/>
                        <dt>
                            <span>{{ this.primaryEmail }}</span>
                            <p class="mt-2">
                            </p>
                        </dt>
                        <dd>

                            <!-- Multiselect for choosing new primary email -->
                            <div class="form-group">
                                <label for="primary_email_select">Edit Additional Emails:</label>
                                <!-- to change "gender" to somethigne else-->
                                <multiselect v-model="selectedEmail" id="primary_email_select" :searchable="false"
                                             :close-on-select="true" :options="additionalEmails" :show-labels="false"
                                             :preselectFirst="true" :allow-empty="false"
                                             placeholder="No Additional Emails">
                                </multiselect>
                            </div>
                            <button type="submit" class="btn btn-secondary" v-on:click="setPrimary">Set as Primary</button>
                            <button type="submit" class="btn btn-secondary" v-on:click="deleteEmail">Delete</button>
                            <br/><br/><br/>

                            <div class="form-group">
                                <!-- full-name field-->
                                <label for="add-email">Add email address</label>
                                <input type="email" class="form-control" v-model="addedEmail" id="add-email" name="add-email" placeholder="Email address">
                            </div>


                            <button type="submit" class="btn btn-secondary" v-on:click="addEmail">Add</button>
                        </dd>
                    </dl>
                </form>
                <div class="form-group">
                    <!-- SignIn Button-->
                    <button type="submit" class="btn btn-primary" v-on:click="submitEmail">Submit</button>
                    <router-link to="/" class="btn btn-link">Go Back</router-link>
                </div>
            </div>
        </section>
    </div>
</template>

<script>
    import server from '../../Api';
    import Sidebar from '../../components/layout/ProfileEditSidebar'
    import Multiselect from "vue-multiselect";

    export default {
        name: "EditEmail",
        components: {Sidebar, Multiselect},
        data () {
            return {
                loading: true,
                post: null,
                error: false,
                userId: null,
                primaryEmail: null,
                additionalEmails: null,
                selectedEmail: null,
                addedEmail: null,
            }
        },
        mounted() {
            server.get(  `/emails`,
                { headers:
                        {'Content-Type': 'application/json'}, withCredentials: true
                }
            ).then(response => {
                    if (response.status === 200) {
                        console.log('Status = OK. response.data:');
                        console.log(response.data);

                        this.userId = response.data["userId"];
                        this.primaryEmail = response.data["primaryEmail"];
                        this.additionalEmails = response.data["additionalEmails"];

                        console.log("User Id :" + this.userId);
                        console.log('THIS.PRIMARY EMAIL BELOW!!!');
                        console.log(this.primaryEmail);
                        console.log('TYPE OF PRIMARY EMAIL BELOW');
                        console.log(typeof this.primaryEmail);

                        console.log('THIS.ADDITIONAL EMAILS BELOW!!!');
                        console.log(this.additionalEmails);
                        console.log('TYPE OF ADDITIONAL EMAILS BELOW');
                        console.log(typeof this.additionalEmails);

                        this.addedEmail = "";
                        //no longer loading, so show data
                        this.loading = false;
                    }
                }).catch(function(error) {
                console.error(error);
                console.error(error.response);

            })
        },
        methods: {
            deleteEmail() {
                //Remove an email
                let selectedIndex = this.additionalEmails.indexOf(this.selectedEmail);
                if (selectedIndex > -1) {
                    this.additionalEmails.splice(selectedIndex, 1);
                }
            },
            setPrimary() {
                let newPrimary = this.selectedEmail;
                let oldPrimary = this.primaryEmail;
                this.deleteEmail();
                this.additionalEmails.unshift(oldPrimary);
                this.primaryEmail = newPrimary;


                console.log('newPrimary: ' + newPrimary + ' Old primary: ' + oldPrimary)
                //}
            },
            addEmail() {
                //Make sure there are no more than 5 emails already and the email is valid
                if (this.additionalEmails.length >= 4) {
                    console.log('Error: maximum number of emails reached. Please remove an email before adding any more (Limit 5)')
                } else if (this.additionalEmails.includes(this.selectedEmail) || (this.primaryEmail == this.selectedEmail)) {
                    //Can't have duplicate emails
                    console.log("Error: Can't add duplicate email");
                } else if (/(.+)@(.+){2,}\.(.+){2,}/.test(this.addedEmail)) {
                    //Email is valid
                    this.additionalEmails.unshift(this.addedEmail);
                } else {
                    //Email's not valid
                    console.log('Error: Invalid email. Please change to proper email format and try again')
                }
            },

            submitEmail() {
                const updateEmail = {
                    primaryEmail: this.primaryEmail,
                    additionalEmails: this.addedEmail
                };
                server.put(`/profiles/${this.userId}/emails`,
                    updateEmail,
                    {
                        headers: {"Access-Control-Allow-Origin": "*", "content-type": "application/json"},
                        withCredentials: true
                    }
                ).then(function() {
                    console.log('User Emails updated Successfully!');
                }).catch(error => {
                    console.log(error);
                    //Get alert bar element
                    let errorAlert = document.getElementById("alert");
                    if (error.message === "Network Error") {
                        this.message = error.message;
                    } else if (error.response.status === 403) { //Error 401: Email already exists or invalid date of birth
                        this.message = error.response.data.toString(); //Set alert bar message to error message from server
                    } else if (error.response.status === 400) { //Error 400: Bad request (missing fields)
                        this.message = "An invalid update request has been received please try again"
                    } else {    //Catch for any errors that are not specifically caught
                        this.message = "An unknown error has occurred during update"
                    }
                    errorAlert.hidden = false;          //Show alert bar
                    setTimeout(function () {    //Hide alert bar after ~5000ms
                        errorAlert.hidden = true;
                    }, 5000);
                });
            }
        }
    }
</script>

<style scoped>
    .btn {
        margin-right: 1%;
    }
</style>