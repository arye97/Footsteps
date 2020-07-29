<template>
    <b-nav :key=this.isLoggedIn class="navbar navbar-expand-lg navbar-light bg-light shadow fixed-top">
        <div class="container">
            <!--The below v-on:click does: If your logged in, go to your profile.  Else go to the main page.-->
            <b-navbar-brand v-on:click="$router.push('/')">
                <img id="logo" src="../../../assets/png/Footsteps_full.png" width="20%" alt="Footsteps Logo">
            </b-navbar-brand>
            <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>
            <b-collapse id="navbarResponsive" is-nav>
                <b-navbar-nav class="ml-auto">
                    <b-nav-item>
                        <router-link v-if=this.isLoggedIn
                                     :to="'/'"
                                     class="nav-link">Home</router-link>
                    </b-nav-item>
                    <b-nav-item v-if=!this.isLoggedIn>
                        <router-link to='/register' class="nav-link">Register</router-link>
                    </b-nav-item>
                    <b-nav-item>
                        <router-link v-if=this.isLoggedIn
                                     :to="{ name: 'allActivities' }"
                                     class="nav-link">My Activities</router-link>
                    </b-nav-item>
                    <b-nav-item v-if=this.isLoggedIn>
                        <router-link
                                :to="{ name: 'myProfile' }"
                                class="nav-link">Profile</router-link>
                    </b-nav-item>
                    <b-nav-item>
                        <router-link to="" v-if="this.isLoggedIn" v-on:click.native="logout" class="nav-link">Logout</router-link>
                        <router-link  v-else to='/login' class="nav-link">Login</router-link>
                    </b-nav-item>
                </b-navbar-nav>
            </b-collapse>
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
            async logout() {
                await api.logout().then(() => {
                    sessionStorage.clear();
                    // tokenStore.setToken(null);
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
