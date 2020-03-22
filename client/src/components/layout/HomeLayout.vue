<template>
    <div id="app">
        <h1><br/></h1>
        <h1>Welcome to Hakinakina!</h1>
        <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
        <div class="text-center">
            <p></p>
        </div>
        <form v-on:submit.prevent="registerUser">
            <div class="col text-center">
                <button type="submit" id="loginButton" class="btn btn-primary" v-on:click="login">Log In</button><br/><br/>
                <button type="submit" id="registerButton" class="btn btn-outline-primary" v-on:click="register">Register</button>
            </div>
        </form>
    </div>

</template>

<script>
    export default {
        name: "HomeLayout",
        methods: {
            login() {
                this.$router.push('/login');
                this.$emit('login');
            },
            register() {
                this.$router.push('/register');
                this.$emit('register');
            }
        }
    }
</script>

<style scoped>
        button {
            width: 40%;
        }
</style>
