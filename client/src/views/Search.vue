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
        <Header/>
        <br/>
        <div class="container h-100">
            <b-row>
                <b-col cols="8">
                    <multiselect v-model="selectedActivityTypes" id="searchBoxActivities"
                                 :options="activityTypes" :multiple="true" :searchable="true" :close-on-select="false"
                                 placeholder="Select your activity types">
                        <template slot="noResult">Invalid activity type</template>
                    </multiselect>
                </b-col>
                <b-col cols="4">
                    <b-form-select id="searchModeSelect" v-model="searchMode" :options="searchModes"></b-form-select>
                </b-col>
            </b-row>
            <b-row style="margin-bottom: 1.7em; margin-top: 0.8em">
                <b-col cols="2" align-self="center">
                    <b-button id="searchButton" variant="primary" v-on:click="search()">
                        Search</b-button>
                </b-col>
                <b-col cols="4" align-self="center">
                    <b-form-radio id="andRadioButton" v-model="searchType" name="andType" value="and">Search results match all selections</b-form-radio>
                </b-col>
                <b-col cols="4" align-self="center">
                    <b-form-radio id="orRadioButton" v-model="searchType" name="orType" value="or">Search results match at least one selection</b-form-radio>
                </b-col>
            </b-row>
        </div>
        <div style="text-align: center">
            <b-card>
                <b-card-text>{{ searchMode }}</b-card-text>
                <b-card-text>{{ selectedActivityTypes }}</b-card-text>
                <b-card-text>{{ searchType }}</b-card-text>
            </b-card>
        </div>
    </b-container>
    </div>
</template>

<script>
    import Header from '../components/Header/Header.vue';
    import Multiselect from 'vue-multiselect'
    import api from '../Api';

    export default {
        name: "Search",
        components: {
            Header,
            Multiselect
        },
        //todo: replace b-card on lines 45-49 with user cards. Current card is just for debugging/testing
        data() {
            return {
                searchMode: 'activityType',
                searchModes: [  //can be expanded to allow for different searching mode (ie; search by username, email... etc)
                    { value: 'activityType', text: 'Activity Type'}
                ],
                selectedActivityTypes : [],
                activityTypes: [],
                searchType: "and"
            }
        },

        async mounted() {
            // If not logged in
            if (!sessionStorage.getItem("token")) {
                this.$router.push('/login'); //Routes to home on logout
            }
            await this.fetchActivityTypes();
        },

        methods: {
            goToPage(url) {
                this.$router.push(url);
            },

            /**
             * Searches a user based on a string of activity types and a method AND or OR
             */
            async search() {
                // Converts list of activity types into string
                // e.g. ["Hiking", "Biking"] into "Hiking Biking"
                let activityTypes = this.selectedActivityTypes.join(" ");
                api.getUsersByActivityType(activityTypes, this.searchType)
                    .then(response => {
                        if (response.status === 200) {
                            console.log(response.data)
                            // Show users in page
                        }
                    }).catch(err => {
                        if (err.response.status === 401) {
                            // User is not logged in
                            // todo redirecting screen message
                            console.log(err.response.data.message)
                            this.$router.push('/login');
                        } else if (err.response.status === 400) {
                            if (err.response.data.message === "Activity Types must be specified") {
                                // todo alert activity types must be specified (can't be empty)
                                console.log(err.response.data.message)
                            } else if (err.response.data.message === "Method must be specified as either (AND, OR)") {
                                // todo must specify method, although UI doesn't give you option to do this
                                // I think we should instead do an "Unknown error occurred" page and then refresh page
                                console.log(err.response.data.message)
                            }
                        }
                })
            },

            /**
             * Fetch all possible activity types from the server
             */
            async fetchActivityTypes() {
                await api.getActivityTypes().then(response => {
                    this.activityTypes = response.data.map(activity => activity['name']);
                    this.activityTypes.sort(function (a, b) {
                        return a.toLowerCase().localeCompare(b.toLowerCase());
                    });
                }).catch(err => {
                    // To Code reviewer: I'm not sure if we've defined any errors for
                    // this function in the back-end
                    // Only thing I can think of is a 500 error
                    console.log(err.response.status)
                });
            }
        }
    }


</script>

<style scoped>

</style>