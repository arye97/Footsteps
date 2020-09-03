<template>
    <div>
        <div>
            <br/>
            <div>
                <b-row>
                    <b-col cols="8" v-if="searchMode==='activityType'">
                        <multiselect v-model="selectedActivityTypes" id="searchBoxActivities"
                                     :options="activityTypes" :multiple="true" :searchable="true" :close-on-select="false"
                                     placeholder="Select your activity types">
                            <template slot="noResult">Invalid activity type</template>
                        </multiselect>
                    </b-col>
                    <b-col cols="8" v-if="searchMode==='activityName'">
                        <b-form-input id="searchBoxActivityTitle" v-model="activityTitle" placeholder="Search activity by title"></b-form-input>
                    </b-col>
                    <b-col cols="4">
                        <b-form-select id="searchModeSelect" v-model="searchMode" :options="searchModes"></b-form-select>
                    </b-col>
                </b-row>
                <b-row style="margin-bottom: 1.7em; margin-top: 0.8em">
                    <b-col cols="6" align-self="center">
                        <b-form-radio id="andRadioButton" v-model="searchType" name="andType" value="and">Must include all selections</b-form-radio>
                    </b-col>
                    <b-col cols="6" align-self="center">
                        <b-form-radio id="orRadioButton" v-model="searchType" name="orType" value="or">Must include one selection</b-form-radio>
                    </b-col>
                </b-row>
                <b-row>
                    <b-button class="searchButton" id="searchButton" variant="primary" v-on:click="search()">
                        Search
                    </b-button>
                    <br/>
                </b-row>
            </div>
            <div v-if="resultsFound">
                <hr/>
                <br/>
            </div>
            <section v-if="errored" class="text-center">
                <div class="alert alert-danger alert-dismissible fade show text-center" role="alert" id="alert">
                    {{ error_message }}
                </div>
            </section>
            <section v-else-if="loading" class="text-center">
                <br/>
                <b-spinner variant="primary" label="Spinning"></b-spinner>
                <br/>
                <br/>
            </section>
            <section v-else v-for="activity in this.activitiesList" :key="activity.id">
                <!-- Activity List -->
                <activity-card v-bind:activity="activity" v-bind:activity-types-searched-for="activityTypesSearchedFor"></activity-card>
                <br>
            </section>
            <!-- Pagination Nav Bar -->
            <b-pagination
                v-if="!errored && !loading && activitiesList.length >= 1"
                align="fill"
                v-model="currentPage"
                :total-rows="rows"
                :per-page="activitiesPerPage"
            ></b-pagination>
        </div>
    </div>
</template>

<script>
import Multiselect from "vue-multiselect";
import api from "../../Api";
import ActivityCard from "./ActivityCard";

export default {
    name: "ActivitySearch",
    components: {
        ActivityCard,
        Multiselect
    },
    data() {
        return {
            activitiesPerPage: 5,
            currentPage: 1,
            activitiesList: [{ // ToDo remove this placeholder when actual activity search is implemented
                id: 1,
                creatorUserId: 1,
                activity_name: "Snow trip",
                description: "A fun Snow skiing trip. With lots of snow and scary looking tricks. Don't be late.",
                activity_type: [{activity_type_id: 1, name: 'Skiing'}],
                continuous: false,
                start_time: new Date(),
                end_time: new Date(),
                location: "Queenstown"
            }],
            searchMode: 'activityType',
            searchModes: [  //can be expanded to allow for different searching mode (ie; search by username, email... etc)
                { value: 'activityType', text: 'Activity Type'},
                { value: 'activityName', text: 'Activity Name'}
            ],
            // These are the ActivityTypes selected in the Multiselect
            selectedActivityTypes : [],
            // These are a copy of selectedActivityTypes passed to the UserCard (to avoid mutation after clicking search)
            activityTypesSearchedFor : ['Skiing'], // ToDo remove this placeholder when actual activity search is implemented
            activityTitle: "",
            activityTypes: [],
            searchType: "and",
            errored: false,
            error_message: "Something went wrong! Please try again.",
            loading: false,
            rows: null,
            resultsFound: false
        }
    },
    async mounted() {
        // If not logged in
        await api.getUserId().catch(() => this.logout())
        await this.fetchActivityTypes();
    },

    methods: {
        goToPage(url) {
            this.$router.push(url);
        },
        /**
         * Logout is used for when an error needs redirection
         * we must actually log out the user rather than just redirect them
         */
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
            }).catch(() => {
                this.errored = true;
                this.error_message = "Unable to connect to server - please try again later"
                setTimeout(() => {
                    this.logout()
                }, 3000);
            });
        }
    }
}
</script>

<style scoped>
.searchButton {
    width: 200%;
}

</style>