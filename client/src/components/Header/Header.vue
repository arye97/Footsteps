
<template>
    <div>
        <nav class="navbar navbar-expand-lg navbar-light bg-light shadow fixed-top">
            <div class="container">
                <a class="navbar-brand" href="#">Hakinakina</a>
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarResponsive">
                    <ul class="navbar-nav ml-auto">
                        <li class="nav-item active">
                            <router-link v-if=isLoggedIn to="/profile" class="nav-link">Home</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link to='/register' class="nav-link">Register</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link to='/login' class="nav-link" >Login</router-link>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>


    </div>
</template>


<script>
    import {tokenStore} from '../../main';
    export default {
        name: 'Header',
        methods: {
            isLoggedIn() {
                //checks if the user token hasn't timed out, used in header for home button functionality
                return tokenStore.state.token !== null;
            }
        }
    }
</script>

<style scoped>

</style>
