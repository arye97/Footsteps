<template>
    <div id="app">
        <h1><br/><br/></h1>
        <b-container class="contents" fluid>
                <div class="container">
                    <h1><br/></h1>
                    <div class="row h-100">
                        <div class="col-12 text-center">
                            <img id="logo" src="../../../assets/png/Footsteps_full.png" width="80%" alt="Footsteps Logo">
                            <br/><br/>
                            <p class="lead">Plan your route with the best</p>
                            <hr><br/>
                            <form v-on:submit.prevent="register" v-if="!this.isLoggedIn">
                                <div class="col text-center">
                                    <button type="submit" id="loginButton" class="btn btn-primary" v-on:click="login" >Log In</button><br/><br/>
                                    <button type="submit" id="registerButton" class="btn btn-outline-primary" v-on:click="register">Register</button>
                                </div><br/>
                            </form>
                            <!--div for main page stuff when user is logged in-->
                            <div v-if="this.isLoggedIn">
                                <div class="col-sm-12 text-center">
                                    <footer class="lead" v-if="this.isAdmin">
                                        <b-button variant="success" size="med" v-on:click="$router.push('/admin')">Admin dashboard</b-button><br/><br/>
                                    </footer>
                                    <footer class="lead" v-if="!this.isGlobalAdmin">
                                        <b-button variant="success" size="med" v-on:click="$router.push({name: 'myProfile'})">My Profile</b-button><br/><br/>
                                    </footer>
                                    <footer class="lead" v-if="!this.isGlobalAdmin">
                                        <b-button variant="success" size="med" v-on:click="$router.push({name: 'allActivities'})">My Activities</b-button><br/><br/>
                                    </footer>
                                </div>
                                <hr><br/>
                                <!-- Show recent online actions -->
                                <HomeFeed v-if="!this.isGlobalAdmin"></HomeFeed>
                            </div>
                        </div>
                    </div>
                </div>
        </b-container>
    </div>
</template>



<script>
    import HomeFeed from "../HomeFeed/HomeFeed";
    import api from "../../Api";
    export default {
        name: "HomeLayout",
        components: {HomeFeed},
        isLoggedIn: false,
        data: function () {
            return {
                isLoggedIn : sessionStorage.getItem("token") !== null,
                isAdmin: false,
                isGlobalAdmin: false
            }
        },
        props: ['userId'],
        async mounted() {
            this.setIsLoggedIn();
            await api.getAllUserData().then(response => {
                if (response.data.role >= 10) {
                    this.isAdmin = true;
                }
                if (response.data.role === 20) {
                    this.isGlobalAdmin = true;
                }
            }).catch(() => {
                // Redirect to login
                this.login();
            });

        },
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
                sessionStorage.getItem("token")
                this.$router.push('/login');
            },
            register() {
                this.$router.push('/register');
            }
        },
    }
</script>

<style scoped>
        button {
            width: 100%;
        }
</style>
