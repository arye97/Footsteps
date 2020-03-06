<template>
    <div id="app">
        <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
        <!-- Full Page Image Header with Vertically Centered Content -->
        <h1><br/></h1>
        <header v-i="notHome" class="masthead">
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
            <div class="form-signin">
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
        <span class="response" v-if="responseMessage">{{  message  }}</span>
    </div>
</template>

<style>
</style>

<script>
    import axios from 'axios';
    export default {
        data() {
            return {
                email: '',
                password: '',
                responseMessage: false,
                message: ''
            }
        },
        methods: {
            login() {
                //Perform password encryption
                axios.post('/login', {password: this.password, email: this.email})
                    .then(function (response) {
                        switch (response.status) {
                            case 200 :
                                this.responseMessage = true;
                                this.message = 'Login successful. You will be redirected shortly.';
                                this.router.push('/home')
                                break;
                            case 404 :
                                this.responseMessage = true;
                                this.message = 'Login unsuccessful. You will be redirected shortly.';
                                break;
                            case 401 :
                                this.responseMessage = true;
                                this.message = 'Login unsuccessful. Bad email or password.';
                                break;
                            case 500 :
                                this.responseMessage = true;
                                this.message = 'Login unsuccessful due to a server error. Please try again shortly.';
                                break;
                        }
                    });
            },
        }
    }
</script>
