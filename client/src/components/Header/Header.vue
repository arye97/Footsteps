<template>
    <b-nav :key=this.isLoggedIn class="navbar navbar-expand-lg navbar-light bg-light shadow fixed-top">
        <div class="container">
            <!--Logo-->
            <!--The below v-on:click does: If your logged in, go to your profile.  Else go to the main page.-->
            <b-navbar-brand v-on:click="$router.push('/')">
                <img id="logo" src="../../../assets/png/Footsteps_full.png" width="180" alt="Footsteps Logo">
            </b-navbar-brand>
            <!--Collapsed nav-bar expansion button-->
            <b-navbar-toggle target="nav-collapse" class="navbar-toggler-right" id="togglerButton"></b-navbar-toggle>
            <!--Collapsible componentry (all links except for the logo)-->
            <b-navbar-nav class="ml-auto">
                <b-collapse id="nav-collapse" is-nav>
                    <b-nav-item  v-if=this.isLoggedIn>
                        <router-link :to="'/'"
                                     class="nav-link">Home</router-link>
                    </b-nav-item>
                    <b-nav-item v-if=!this.isLoggedIn>
                        <router-link to='/register' class="nav-link">Register</router-link>
                    </b-nav-item>
                    <b-nav-item  v-if="this.isLoggedIn && this.isAdmin">
                        <router-link to="/admin"
                                     class="nav-link">Admin Dashboard</router-link>
                    </b-nav-item>
                    <b-nav-item  v-if="this.isLoggedIn && this.isGlobalAdmin === false">
                        <router-link :to="{ name: 'allActivities' }"
                                     class="nav-link">My Activities</router-link>
                    </b-nav-item>
                    <b-nav-item  v-if=this.isLoggedIn>
                        <router-link
                                :to="{ name: 'searchPage' }"
                                class="nav-link">Search</router-link>
                    </b-nav-item>
                    <b-nav-item v-if="this.isLoggedIn && this.isGlobalAdmin === false">
                        <router-link
                                :to="{ name: 'myProfile' }"
                                class="nav-link">Profile</router-link>
                    </b-nav-item>
                    <b-nav-item v-if=this.isLoggedIn>
                        <router-link to="" v-on:click.native="logout" class="nav-link">Logout</router-link>
                    </b-nav-item>
                    <b-nav-item v-if=!this.isLoggedIn>
                        <router-link to='/login' class="nav-link">Login</router-link>
                    </b-nav-item>
                </b-collapse>
            </b-navbar-nav>
        </div>
    </b-nav>
</template>


<script>
    import api from "../../Api";
    export default {
        name: 'Header',
        isLoggedIn: false,
        data: function () {
            return {
                isLoggedIn : sessionStorage.getItem("token") !== null,
                isAdmin: false,
                isGlobalAdmin: false
            }
        },
        props: ['userId'],
        watch: {
            isLoggedIn: function () {
                this.setIsLoggedIn();
            }
        },
        async mounted() {
            this.isAdmin = sessionStorage.getItem("role") >= 10;
            this.isGlobalAdmin = sessionStorage.getItem("role") === "20";
            if (this.isLoggedIn) {
                await api.getAllUserData().then(response => {
                    this.isAdmin = false;
                    this.isGlobalAdmin = false;
                    sessionStorage.setItem("role", "0");
                    if (response.data.role >= 10) {
                        this.isAdmin = true;
                        sessionStorage.setItem("role", "10");
                    }
                    if (response.data.role === 20) {
                        this.isGlobalAdmin = true;
                        sessionStorage.setItem("role", "20")
                    }
                }).catch(() => this.logout());
            }
        },
        methods: {
            setIsLoggedIn: function() {
                this.isLoggedIn = (sessionStorage.getItem("token") !== null);
            },
            async logout() {
                await api.logout().then(() => {
                    sessionStorage.clear();
                    this.isLoggedIn = (sessionStorage.getItem("token") !== null);
                    this.$forceUpdate();
                    this.$router.push('/login'); //Routes to home on logout
                }).catch(() => {
                    sessionStorage.clear();
                    this.isLoggedIn = (sessionStorage.getItem("token") !== null);
                    this.$forceUpdate();
                    this.$router.push('/login'); //Routes to home on logout
                })
            }
        }
    }

</script>

<style scoped>

</style>
