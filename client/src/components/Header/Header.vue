
<template>
        <nav :key=this.isLoggedIn class="navbar navbar-expand-lg navbar-light bg-light shadow fixed-top">
            <div class="container">
                <!--The below v-on:click does: If your logged in, go to your profile.  Else go to the main page.-->
                <a class="navbar-brand" v-on:click="isLoggedIn ? this.$router.push('/profile') : this.$router.push('/')" >Hakinakina</a>
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarResponsive">
                    <ul class="navbar-nav ml-auto">
                        <li class="nav-item active">
                            <router-link v-if=this.isLoggedIn to="/profile" class="nav-link">Home</router-link>
                        </li>
                        <li v-if=!this.isLoggedIn class="nav-item">
                            <router-link to='/register' class="nav-link">Register</router-link>
                        </li>
                        <li v-if=this.isLoggedIn class="nav-item">
                            <router-link
                                         :to="{ name: 'details',
                                                params: { userId: this.userId } }"
                                         class="nav-link">Edit Profile</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link to="" v-if="this.isLoggedIn" v-on:click.native="logout" class="nav-link">Logout</router-link>
                            <router-link  v-else to='/login' class="nav-link">Login</router-link>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
</template>


<script>
    // import {tokenStore} from '../../main';
    import server from "../../Api";
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
                await server.post('/logout', null,
                    {
                        headers: {"Access-Control-Allow-Origin": "*", "content-type": "application/json", 'Token': sessionStorage.getItem("token")},
                        withCredentials: true
                    }
                ).then(response => {
                    console.log(response);
                    console.log('User Logged Out Successfully!');
                    sessionStorage.clear();
                    // tokenStore.setToken(null);
                    this.isLoggedIn = (sessionStorage.getItem("token") !== null);
                    this.$forceUpdate();
                    this.$router.push('/'); //Routes to home on logout
                }).catch(error => {
                    console.error(error);
                    this.isLoggedIn = (sessionStorage.getItem("token") !== null);
                    this.$forceUpdate();
                    this.$router.push('/'); //Routes to home on logout
                })
            }
        }
    }

</script>

<style scoped>

</style>
