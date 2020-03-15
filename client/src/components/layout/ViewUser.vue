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
                <h1>Hi {{user.firstname}}!</h1>
                <p>You're logged in to your Hakinakina Account</p>
                <h3>All about you</h3>
                <span class="accordion">
                    <span v-if="user.nickname">Nickname: {{ user.nickname }}</span>
                    <span>Gender: {{ user.gender }}</span>
                    <span>Date Of Birth: {{ user.date_of_birth }}</span>
                    <span>Email: {{ user.primary_email }}</span>
                    <span>Bio: {{ user.bio }}</span>
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
        async mounted() {
            server.get(  'http://localhost:9499/profiles')
            .then(function (response) {
                console.log(response);
                this.loading = false;
            }).catch(function(error) {
                console.error(error.response);

            })
        }
    }
</script>
