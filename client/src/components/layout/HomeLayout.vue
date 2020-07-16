<template>

    <div id="app">
        <h1><br/><br/></h1>
        <b-container class="contents" fluid>

            <header class="masthead">
                <div class="container h-100">

                    <div class="row h-100 align-items-center">
                        <div class="col-12 text-center">
                            <h1 class="font-weight-light">Welcome to Hakinakina</h1>
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


                                    Hi there! Feel free to use the navigation bar at the top of the screen to make your way around.

                                </div>




                            </div>
                        </div>
                    </div>
                </div>



            </header>

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
