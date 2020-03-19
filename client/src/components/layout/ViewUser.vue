<template>
    <div id="app">
        <h1><br/></h1>
        <h1>Welcome to Hakinakina!</h1>

        <section v-if="errored">
            <p>Sorry, looks like we can't get your info! Please try again soon.</p>
        </section>

        <section v-else>
            <div v-if="loading"> Loading...</div>
            <div v-else class="form-group">
                <h1>Hi {{this.user.firstname}}!</h1>
                <p>You're logged in to your Hakinakina Account</p>
                <h3>All about you</h3>
                <span class="accordion">
                    <span v-if="this.user.nickname">Nickname: {{ this.user.nickname }}</span><br/>
                    <span >Gender: {{ this.user.gender }}</span><br/>
                    <span>Date Of Birth: {{ this.user.date_of_birth }}</span><br/>
                    <span>Email: {{ this.user.primary_email }}</span><br/>
<!--                    <span>Secondary Emails: {{this.user.additional_email.join(", ")}}-->
                    <button type="button" class="btn btn-link" v-on:click="editEmail">Edit Emails</button><br/>
                    <span v-if="this.user.passports">Passports: {{this.user.passports.join(", ")}}</span><br/>
                    <span v-if="this.user.fitness">Fitness Level: {{this.user.fitness}}</span><br/>
                    <span v-if="this.user.bio">Bio: {{ this.user.bio }}</span><br/>
                </span>
            </div>
        </section>

    </div>
</template>

<script>
    import server from "../../Api";

    export default {
        name: "ViewUser",
        data() {
            return {
                user: null,
                loading: true,
                errored: false
            }
        },
        methods: {
            editEmail() {
                this.$router.push('/profile/emails');
            }
        },
        mounted() {
            server.get(  'http://localhost:9499/profiles',
                {headers:
                        {'Content-Type': 'application/json'}, withCredentials: true
                }, )
            .then(response => {
                if (response.status === 200) {
                    console.log('Status = OK. response.data:');
                    console.log(response.data);
                    //user is set to the user data retrieved
                    this.user = response.data;
                    //no longer loading, so show data
                    this.loading = false;
                }
            }).catch(function(error) {
                console.error(error);
                console.error(error.response);

            })
        },
    }
</script>
