<template>
    <div id="app">
        <h1><br/><br/></h1>

        <header class="masthead">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light">Welcome to Hakinakina</h1>
                        <p class="lead">Plan your route with the best</p><br/>
                        <form v-on:submit.prevent="register">
                            <div class="col text-center">
                                <button type="submit" id="loginButton" class="btn btn-primary" v-on:click="login">Log In</button><br/><br/>
                                <button type="submit" id="registerButton" class="btn btn-outline-primary" v-on:click="register">Register</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </header>

    </div>
</template>

<script>
    export default {
        name: "HomeLayout",
        methods: {
            login() {
                this.$router.push('/login');
                // this.$emit('login');
            },
            register() {
                this.$router.push('/register');
                // this.$emit('register');
            }
        },
    }
</script>

<style scoped>
        button {
            width: 100%;
        }
</style>
