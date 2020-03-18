
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
                            <router-link to="/" class="nav-link">Home</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link to='/register' class="nav-link">Register</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link v-if="notLoggedIn" to='/login' class="nav-link" replace>Logout</router-link>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>


    </div>
</template>


<script>
    export default {
        name: 'Header',
    }
</script>

<style scoped>

</style>
