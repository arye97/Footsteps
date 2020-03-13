<template>
    <div id="app">
        <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
        <div class="text-center">
            <p></p>
        </div>
        <form v-on:submit.prevent="registerUser">
            <div class="col text-center">
                <button type="submit" class="btn btn-primary" v-on:click="login">Log In</button><br/><br/>
                <button type="submit" class="btn btn-outline-primary" v-on:click="register">Register</button>
            </div>
        </form>
    </div>

</template>

<script>
    export default {
        name: "HomeLayout",
        methods: {
            login() {
                this.$router.push('login');
            },
            register() {
                this.$router.push('register');
            }
        }
    }
</script>

<style scoped>
        button {
            width: 40%;
        }
</style>
