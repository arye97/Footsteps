<template>
    <div id="app">
        <h1><br/></h1>
        <h1>Welcome to Hakinakina!</h1>
        <ViewUser v-if="this.isLoggedIn"></ViewUser>
        <HomeLayout v-else></HomeLayout>
    </div>
</template>

<script>
    import ViewUser from '../../components/layout/ViewUser'
    import HomeLayout from '../../components/layout/HomeLayout'
    import Log from '../../views/Login/Login'
    export default {
        name: "Home",
        components: {ViewUser, HomeLayout},
        data () {
            return {
                isloggedIn: Log.loggedIn
            }
        }
    }
</script>

<style>
    h1 {
        display: block;
        flex-direction: column;
        text-align: center;
        font-family: Roboto, monospace;
    }
</style>