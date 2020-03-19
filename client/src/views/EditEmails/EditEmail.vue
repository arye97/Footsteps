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
                        <dt>
                            <label id="primary_email_select_label" for="primary_email_select">Primary email address</label>
                            <p class="mt-2">
                            </p>
                        </dt>
                        <dd>
                            <select id="primary_email_select" name="id" class="form-select">
                            </select>
                            <button type="submit" class="btn">Save</button>
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

    export default {
        name: "EditEmail",
        components: {Sidebar},
        data () {
            return {
                loading: true,
                post: null,
                error: false,
                user: null,
                primaryEmail: null,
                secondaryEmails: null
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
                        this.primaryEmail = this.user.primary_email[0];
                        this.secondaryEmails = this.user.primary_email[1];
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