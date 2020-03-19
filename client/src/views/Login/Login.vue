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
                // Send login post to serve
                server.post('http://localhost:9499/login',
                    userLogin,
                    { headers: { "Access-Control-Allow-Origin": "*", "content-type":"application/json"},
                        withCredentials: true}
                ).then(response => { //If successfully logged the response will have a status of 201
                    if (response.status === 201) {
                        console.log('User Logged In Successfully!');
                        this.loggedIn = true;
                        this.$router.push("/profile"); //Route to profile screen on successful login
                    }
                }).catch(error => { //If an error occurs during login (includes server side errors)
                    console.log(error.response);
                    //Get alert bar element
                    let errorAlert = document.getElementById("alert");
                    if (error.response.status === 401) { //Error 401: User not found or incorrect password
                        this.message = error.response.data.toString(); //Set alert bar message to error message from server
                    } else if (error.response.status === 400) { //Error 400: Bad request (email and/or password fields not given)
                        this.message = "An invalid login request has been received please try again"
                    } else {    //Catch for any errors that are not specifically caught
                        this.message = "An unknown error has occurred during login"
                    }
                    errorAlert.hidden = false;          //Show alert bar
                    setTimeout(function () {    //Hide alert bar after ~5000ms
                        errorAlert.hidden = true;
                    }, 5000);
                });
            }
        }
    }
</script>
