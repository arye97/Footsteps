<template>
    <div id="app">
        <ViewUser v-if="checkLoggedIn"></ViewUser>
        <HomeLayout v-else></HomeLayout>
    </div>
</template>

<script>
    import ViewUser from '../../components/layout/ViewUser'
    import HomeLayout from '../../components/layout/HomeLayout'
    import Log from '../../views/Login/Login'
    import Reg from '../../views/Register/Register'
    export default {
        name: "Home",
        components: {ViewUser, HomeLayout},
        data () {
            return {
                isLoggedIn: false
            }
        },
        methods: {
            checkLoggedIn() {
                ///DIDNT LIKE THE || OPERATOR FOR ONE IF STATMENT??
                if (Log.loggedIn === true) {
                    this.isLoggedIn = true;
                } else if (Reg.isLoggedIn === true) {
                    this.isLoggedIn = true;
                }
                return false;
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