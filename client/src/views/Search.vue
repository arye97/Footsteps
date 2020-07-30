<template>
    <div>
    <header class="masthead">
        <br/><br/><br/>
        <div class="container h-100">
            <div class="row h-100 align-items-center">
                <div class="col-12 text-center">
                    <Header/>
                    <br/>
                </div>
            </div>
        </div>
    </header>

    <b-container class="contents">
        <div class="container h-100">
            <b-form-input v-model="searchTerm" placeholder="Enter your name"></b-form-input>
            <b-button style="margin-bottom: 1.7em; margin-top: 0.8em" variant="primary" v-on:click="search(searchTerm)">Search</b-button>
        </div>
        <div style="text-align: center">
            <b-button style="margin-bottom: 1.7em; margin-top: 0.8em" variant="primary" v-on:click="goToPage('/profile')">Profile</b-button>
        </div>
    </b-container>
    </div>
</template>

<script>
    import api from '../Api';

    export default {
        name: "Search",

        data() {
            return {
                searchTerm : ""
            }
        },

        methods: {
            goToPage(url) {
                this.$router.push(url);
            },

            /**
             * Searches a user based on a string of activity types and a method AND or OR
             * @param searchTerm a string of activity types
             * @param method a string indicating AND or OR
             */
            search(searchTerm, method) {
                api.getUsersByActivityType(searchTerm, method)
                    .then(response => {
                        if (response.status === 200) {
                            console.log(response.data)
                            // Show users in page
                        }
                    })
            }
        }
    }


</script>

<style scoped>

</style>