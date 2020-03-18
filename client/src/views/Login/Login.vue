<template>
    <div id="app">
        <div class="alert alert-danger alert-dismissible fade show sticky-top" role="alert" hidden="true" id="alert">
            {{  message  }}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
        <!-- Full Page Image Header with Vertically Centered Content -->
        <h1><br/></h1>
        <header class="masthead">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light">Welcome to Hakinakina</h1>
                        <p class="lead">Plan your route with the best</p>
                    </div>
                </div>
            </div>
        </header>


        <div>
            <h1>Login</h1>
        </div>
        <form @submit.prevent="login">
            <div class="form-group">
                <label for="email">Email Address: </label>
                <input type="email" class="form-control" v-model="email" id="email" placeholder="Email Address" required><br/>
                <div class="form-group">
                    <label for="password">Password: </label>
                    <input type="password" class="form-control" v-model="password" id="password" placeholder="Password" required> <br/>
                </div>
                <div class="form-group">
                    <input v-on:submit="login" class="btn btn-primary" type="submit" value="Sign In">
                    <router-link to="/register" class="btn btn-link">Register</router-link>

                </div>
            </div>
        </form>
    </div>
</template>

<style>
</style>

<script>
    import server from '../../Api';
    export default {
        data() {
            return {
                email: '',
                password: '',
                hasError: false,
                loggedIn: false,
                message: ""
            }
        },
        methods: {

            login() {
                const userLogin = {
                    email: this.email,
                    password: this.password
                };
                //Perform password encryption
                server.post('http://localhost:9499/login', userLogin,
                ).then(response => {
                    if (response.status === 201) {
                        console.log('User Logged In Successfully!');
                        this.loggedIn = true;
                        this.$router.push("/profile");
                    }
                }).catch(error => {
                    this.hasError = true;
                    console.log(error.response);
                    let errorAlert = document.getElementById("alert");
                    if (error.response.status === 401) {
                        this.message = error.response.data.toString();
                    } else if (error.response.status === 400) {
                        this.message = "An invalid login request has been received please try again"
                    } else {
                        this.message = "An unknown error has occurred during login"
                    }
                    this.hasError = true;
                    errorAlert.hidden = false;
                    setTimeout(function () {
                        errorAlert.hidden = true;
                    }, 5000);
                });
            }
        }
    }
</script>
