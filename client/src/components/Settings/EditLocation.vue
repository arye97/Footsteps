<template>
    <b-container class="contents" fluid>
        <section v-if="loading">
            <div v-if="this.isRedirecting">
                {{ redirectionMessage }}
            </div>
            <div class="loading text-center">
                <b-spinner variant="primary" label="Spinning"></b-spinner>
                <br/>
            </div>
        </section>
        <section v-if="loggedIn">
            <!-- ToDo: put location things in here!-->
        </section>
    </b-container>
</template>

<script>
export default {
    name: "EditLocation",
    data() {
        return {
            isLoggedIn: false,
            loading: true,
            error_message: "Something went wrong! Try again later",
            errored: false
        }
    },
    methods: {
        /** ToDo: methods must check for whether is logged in or not
         *  Once the users data has loaded you must change loading to false
         *  Use the 'errored' and 'error_message' data vars for error handling/displaying error messages to the user
         *  Once this is implemented delete this bulk comment!
         */
    }
}
</script>

<style scoped>

</style>