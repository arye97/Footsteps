<template>
    <div id="app">
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
        <label v-show="hasError" id="error">Error</label>
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
                loggedIn: false
            }
        },
        methods: {

            login() {
                const userLogin = {
                    email: this.email,
                    password: this.password
                };
                //Perform password encryption
                server.post('http:/localhost:9499/login', userLogin, {headers: {"Access-Control-Allow-Origin": "*"}}
                ).then(function (user) {
                        console.log(user);
                        console.log('User Logged In Successfully!');
                        this.loggedIn = true;
                    }
                ).catch(error => {
                    this.hasError = true;
                    console.log(error.response);
                    // let errorLabel = document.getElementById("error");
                    // if (error.response.status == 403) {
                    //     errorLabel.textContent = error.response.data.toString();
                    // } else if (error.response.status == 400) {
                    //     errorLabel.textContent = "An invalid login request has been received please try again"
                    // } else {
                    //     errorLabel.textContent = "An unknown error has occurred during login"
                    // }

                });
                if (this.loggedIn && !this.hasError) {
                    this.$router.push("/profile");
                }

            }
        }
    }
</script>
