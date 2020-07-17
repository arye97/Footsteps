<template>
    <div id="app">
        <h1><br/><br/></h1>
        <b-container class="contents" fluid>
                <div class="container">
                    <div class="row h-100">
                        <div class="col-12 text-center">
                            <h1 class="font-weight-light"><strong>Welcome to Hakinakina</strong></h1>
                            <p class="lead">Plan your route with the best</p><br/>
                            <form v-on:submit.prevent="register" v-if="!this.isLoggedIn">
                                <div class="col text-center">
                                    <button type="submit" id="loginButton" class="btn btn-primary" v-on:click="login" >Log In</button><br/><br/>
                                    <button type="submit" id="registerButton" class="btn btn-outline-primary" v-on:click="register">Register</button>
                                </div>
                            </form>
                            <!--div for main page stuff when user is logged in-->
                            <div v-if="this.isLoggedIn">
                                <div class="col-sm-12 text-center">
                                    <p class="lead">ACTIVITIES GO HERE</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
        </b-container>
    </div>
</template>



<script>
    export default {
        name: "HomeLayout",
        isLoggedIn: false,
        data: function () {
            return {
                isLoggedIn : sessionStorage.getItem("token") !== null,
            }
        },
        props: ['userId'],
        watch: {
            isLoggedIn: function () {
                this.setIsLoggedIn();
            }
        },
        methods: {
            setIsLoggedIn: function() {
                this.isLoggedIn = (sessionStorage.getItem("token") !== null);
            },
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
