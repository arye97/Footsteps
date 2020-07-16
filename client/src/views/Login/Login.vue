<template>
    <div id="app">
        <h1><br/><br/></h1>
        <b-container class="contents" fluid>

            <div class="container">
                    <div class="row">
                        <div class="col-sm-6 offset-sm-3">
                            <Header />
                            <router-view></router-view>
                        </div>
                    </div>
                </div>
            <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
            <!-- Full Page Image Header with Vertically Centered Content -->
            <header class="masthead">
                <div class="container h-100">
                    <div class="row h-100 align-items-center">
                        <div class="col-sm-12 text-center">
                            <h1 class="font-weight-light">Welcome to Hakinakina</h1>
                            <p class="lead">Plan your route with the best</p>
                            <h1 class="font-weight-light">Login</h1>
                            <form @submit.prevent="login">
                                <div class="form-group">
                                <label for="email">Email Address: </label>
                                <input type="email" class="form-control" v-model="email" id="email" placeholder="Email Address"><br/>
                                <div class="form-group ">
                                    <label for="password">Password: </label>
                                    <input type="password" class="form-control" v-model="password" id="password" placeholder="Password"> <br/>
                                </div>
                                    <div class="alert alert-danger alert-dismissible fade show sticky-top" role="alert" hidden="true" id="alert">
                                        {{  message  }}
                                    </div>
                                <div class="form-group">
                                    <input v-on:submit="login" class="btn btn-primary" type="submit" value="Sign In">
                                    <router-link to="/register" class="btn btn-link">Register</router-link>
                                </div>
                            </div>
                            </form>
                        </div>
                    </div>
                </div>
            </header>
        </b-container>
    </div>
</template>

<style>
    form {
        width: 75%;
        padding-left: 25%;
    }
</style>

<script>
    import server from '../../Api';
    // import {tokenStore} from "../../main";
    import Header from '../../components/Header/Header.vue'

    async function validUser(userLogin) {
        return (userLogin.email !== '' && userLogin.password !== '')
    }

    function showError(alert_name) {
        let errorAlert = document.getElementById(alert_name);
        errorAlert.hidden = false;          //Show alert bar
        setTimeout(function () {    //Hide alert bar after ~5000ms
            errorAlert.hidden = true;
        }, 10000);
    }

    export default {
        name: "Login",
        components : {
            Header
        },
        data() {
            return {
                email: '',
                password: '',
                message: ""
            }
        },
        methods: {
            async login() {
                const userLogin = {
                    email: this.email.trim(),
                    password: this.password.trim()
                };
                let valid = await validUser(userLogin);
                if (!valid) {
                    this.message = 'Email and password must be input to login';
                    showError('alert');
                    return;
                }
                // Send login post to serve
                server.post('/login',
                    userLogin,
                    {
                        headers: {"Access-Control-Allow-Origin": "*", "Content-Type": "application/json"},
                        withCredentials: true
                    }
                ).then(response => { //If successfully logged the response will have a status of 201
                    if (response.status === 201) {
                        sessionStorage.setItem("token", response.data.Token);
                        let userId = response.data.userId;
                        this.$router.push({ name: 'profile', params: {userId: userId} }); //Route to profile screen on successful login
                    }
                }).catch(error => { //If an error occurs during login (includes server side errors)
                    //Get alert bar element
                    if (error.message === "Network Error") {
                        this.message = error.message;
                    } else if (error.response.data.status === 401) { //Error 401: User not found or incorrect password
                        this.message = error.response.data.message.toString(); //Set alert bar message to error message from server
                    } else if (error.response.data.status === 400) { //Error 400: Bad request (email and/or password fields not given)
                        this.message = "An invalid login request has been received please try again"
                    } else {    //Catch for any errors that are not specifically caught
                        this.message = "An unknown error has occurred during login"
                    }
                    sessionStorage.clear();
                    showError('alert')
                });
            }
        }
    }
</script>
