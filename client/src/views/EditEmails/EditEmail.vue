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
                            <span>{{ this.user.primary_email }}</span>
                            <p class="mt-2">
                            </p>
                        </dt>
                        <dd>

                            <!-- Multiselect for choosing new primary email -->
                            <div class="form-group">
                                <label for="primary_email_select">Edit Additional Emails:</label>
                                <!-- to change "gender" to somethigne else-->
                                <multiselect v-model="selectedEmail" id="primary_email_select" :searchable="false" :close-on-select="true" :options="edits" :show-labels="false" :preselectFirst="true" :allow-empty="false" placeholder="No Additional Emails"></multiselect>
                            </div>

                            <div class="form-group">
                                <!-- full-name field-->
                                <label for="add-email">Add email address</label>
                                <input type="text" class="form-control" v-model="addedEmail" id="add-email" name="add-email" placeholder="Email address">
                            </div>


                            <button type="submit" class="btn btn-success">Add </button>
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
        <footer>
            Entries marked with * are required
        </footer>
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
                user: null,
                primaryEmail: null,
                secondaryEmails: null,
                edits: null,
                selectedEmail: null,
                addedEmail: null,
            }
        },
        mounted() {
            server.get(  `http://localhost:9499/profiles`,
                {headers:
                        {'Content-Type': 'application/json'}, withCredentials: true
                }
            ).then(response => {
                    if (response.status === 200) {
                        console.log('Status = OK. response.data:');
                        console.log(response.data);

                        //user is set to the user data retrieved
                        this.user = response.data;
                        this.primaryEmail = this.user.primary_email.primaryEmail;
                        this.secondaryEmails = this.user.primary_email.secondaryEmails;
                        console.log('THIS.PRIMARYEMAIL BELOW!!!')
                        console.log(this.primaryEmail)
                        console.log('TYPE OF BELOW')
                        console.log(typeof this.primaryEmail)
                        //Set the drop down list to contain users emails
                        //Fake list of secondary emails until we have the ability to add our own secondary emails
                        let mock_secondaries = ["fake1@sekj.com", "fake2@skeg.com"]  // JUST FOR TESTING.  REMOVE SOON.
                        this.edits = mock_secondaries;
                        //no longer loading, so show data
                        this.loading = false;
                    }
                }).catch(function(error) {
                console.error(error);
                console.error(error.response);

            })
        },
        methods: {
            submitEmail() {
                const updateEmail = {
                    primaryEmail: this.primaryEmail,
                    secondaryEmails: this.secondaryEmails
                }
                server.put(`http://localhost:9499/profiles/${this.user.id}/emails`,
                    updateEmail
                ).then(function() {
                    console.log('User Emails updated Successfully!');
                }).catch(function(error) {
                    console.error(error);
                    console.error(error.response);
                })
            }
        }
    }
</script>

<style scoped>
</style>