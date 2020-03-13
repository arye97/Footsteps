<template>
    <div id="app">
        <h1>Hi {{this.user.firstname + " " + this.user.lastname}}!</h1>
        <p>You're logged in to your Hakinakina Account</p>
        <h3>All about you</h3>
        <p v-if="this.user.nickname">Nickname: {{ this.user.nickname }}</p>
        <p>Gender: {{ this.user.gender }}</p>
        <p>Date Of Birth: {{ this.user.date_of_birth }}</p>
        <p>Email: {{ this.user.primary_email }}</p>
        <p v-if="this.user.fitnessLevel">Fitness Level: {{ this.user.fitnessLevel }}</p>
        <p v-if="this.user.passportCountry">Passport Country: {{ this.user.passportCountry }}</p>
        <p v-if="this.user.bio">Bio: {{ this.user.bio }}</p>
    </div>
</template>

<script>
    import server from "../../Api";

    export default {
        name: "ViewUser",
        data() {
            return {
                user: null
            }
        },
        mounted() {
            server.get(  'http://localhost:9499/profiles').
            then(function(res){
                this.user = res; //set our user set above to returned user from backend
                console.log('User Data Acquired Successfully!');
                }
            ).catch(error => {
                console.log(error.response); // catch error as the backend response status code
            });
        }
    }
</script>
