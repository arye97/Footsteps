<template>
    <div>
        <div>
            <br/>
            <div>
                <b-form @submit.prevent @keydown.enter="search()">
                <b-row>
                    <b-col cols="8" v-if="searchMode==='activityType' || searchMode==='activityLocation'">
                        <multiselect v-model="selectedActivityTypes" id="searchBoxActivities"
                                     :options="activityTypes" :multiple="true" :searchable="true"
                                     :close-on-select="false"
                                     placeholder="Select your activity types">
                            <template slot="noResult">Invalid activity type</template>
                        </multiselect>
                    </b-col>
                    <b-col cols="8" v-if="searchMode==='activityName'">
                        <b-form-input id="searchBoxActivityTitle" v-model="activityTitle"
                                      placeholder="Search for Activities by Title"></b-form-input>
                    </b-col>
                    <b-col class="multi-search" cols=4>
                        <b-form-select id="searchModeSelect" v-model="searchMode"
                                       :options="searchModes"></b-form-select>
                    </b-col>
                </b-row>
                <div v-if="searchMode==='activityType' || searchMode==='activityLocation'">
                    <b-row style="margin-bottom: 1.7em; margin-top: 0.8em">
                        <b-col cols="6" align-self="center">
                            <b-form-radio id="andRadioButton" v-model="searchType" name="andType" value="and">Must
                                include all selections
                            </b-form-radio>
                        </b-col>
                        <b-col cols="6" align-self="center">
                            <b-form-radio id="orRadioButton" v-model="searchType" name="orType" value="or">Must include
                                one selection
                            </b-form-radio>
                        </b-col>
                    </b-row>
                </div>
                <b-col v-if="searchMode==='activityLocation'" class="distanceSlider">
                    <p for="range-1">Please specify the distance range to search for:</p>
                    <b-form-input id="range-1" v-model="cutoffDistance" type="range" min="0" :max="MAX_DISTANCE"
                                  step="100"></b-form-input>
                    <div class="mt-2">
                        Distance in Kilometres:
                        {{ (cutoffDistance != MAX_DISTANCE)?cutoffDistance:MAX_DISTANCE.toString().concat("+") }}
                    </div>
                    <LocationIO v-if="!mapLoading" :parent-center="currentLocation" :max-pins="maxPins"
                                :parent-pins="pins" :draggable="true" @child-pins="pinsChanged"></LocationIO>
                </b-col>
                <b-row v-if="searchMode==='activityName'">
                    <b-button class="rulesButton" v-if="!showRules" size="sm" variant="link"  v-on:click="showRules=true">Search Help</b-button>
                    <b-button class="rulesButton" v-if="showRules" size="sm" variant="link"  v-on:click="showRules=false">Close Search Help</b-button>
                </b-row>
                    <br/>
                <b-row v-if="searchMode==='activityName' && showRules">
                    <ul v-if="showRules" style="align-content: center;">
                        <li>All searches are case insensitive including exact match searches</li>
                        <li>Use double quotes around your search for exact matching</li>
                        <li>Use OR between keywords to search for anything with any of the keywords</li>
                        <li>Use AND between keywords to search for anything with all of the keywords</li>
                        <li>AND and OR keywords must be spelt with capitals</li>
                    </ul>
                </b-row>
                <b-row>
                    <b-button class="searchButton" id="searchButton" variant="primary" v-on:click="search()">
                        Search
                    </b-button>
                </b-row>
                </b-form>
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
                <activity-card v-bind:activity="activity"
                               v-bind:activity-types-searched-for="activityTypesSearchedFor"></activity-card>
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
import LocationIO from "../../components/Map/LocationIO";

export default {
    name: "ActivitySearch",
    components: {
        LocationIO,
        ActivityCard,
        Multiselect
    },
    data() {
        return {
            currentLocation: {draggable: false},
            searchPin: null,
            mapLoading: true,
            maxPins: 1,
            pins: [],
            MAX_DISTANCE: 10000,
            activitiesPerPage: 5,
            cutoffDistance: 1,
            currentPage: 1,
            activitiesList: [],
            searchMode: 'activityType',
            searchModes: [  //can be expanded to allow for different searching mode (ie; search by username, email... etc)
                {value: 'activityType', text: 'Activity Type'},
                {value: 'activityName', text: 'Activity Name'},
                {value: 'activityLocation', text: 'Location'}
            ],
            // These are the ActivityTypes selected in the Multiselect
            selectedActivityTypes: [],
            // These are a copy of selectedActivityTypes passed to the UserCard (to avoid mutation after clicking search)
            activityTypesSearchedFor: [],
            activityTitle: "",
            activityTypes: [],
            searchType: "and",
            errored: false,
            error_message: "Something went wrong! Please try again.",
            loading: false,
            rows: null,
            resultsFound: false,
            showRules: false
        }
    },
    async mounted() {
        // If not logged in
        await api.getUserId().catch(() => this.logout())
        await api.getAllUserData().then(response => {
            if (response.data.private_location !== null) {
                this.currentLocation.lng = response.data.private_location.longitude;
                this.currentLocation.lat = response.data.private_location.latitude;
                this.currentLocation.name = response.data.private_location.name;
            } else if (response.data.public_location !== null) {
                this.currentLocation.lng = response.data.public_location.longitude;
                this.currentLocation.lat = response.data.public_location.latitude;
                this.currentLocation.name = response.data.public_location.name;
            }
        });
        this.pins.push(this.currentLocation);
        this.mapLoading = false;
        await this.fetchActivityTypes();
    },
    watch: {
        /**
         * Watcher is called whenever currentPage is changed, via searching new query
         * or the pagination bar.
         */
        currentPage() {
            this.getPaginatedActivitiesByActivityTitle();
        }
    },
    methods: {
        pinsChanged(pins) {
            this.maxPins = pins.length;
            this.pins = pins;
        },
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
        },

        /**
         * Fetches a paginated list of activities, filtered by specified activity name,
         * through an API call.
         */
        async getPaginatedActivitiesByActivityTitle() {
            let pageNumber = this.currentPage - 1;
            api.getActivityByActivityTitle(this.activityTitle, pageNumber)
                .then(response => {
                    this.activitiesList = response.data;
                    if (this.activityTitle.length != 0 && (response.data).length === 0) {
                        this.errored = true;
                        this.error_message = "No activities with activity names ".concat(this.activityTitle) + " have been found!"
                    }
                    this.rows = response.headers["total-rows"];
                    this.loading = false;
                    this.resultsFound = true;
                }).catch(error => {
                this.loading = false;
                this.errored = true;
                this.userList = [];
                if ((error.code === "ECONNREFUSED") || (error.code === "ECONNABORTED")) {
                    this.error_message = "Cannot connect to server - please try again later!";
                } else {
                    if (error.response.status === 401) {
                        this.error_message = "You aren't logged in! You're being redirected!";
                        setTimeout(() => {
                            this.logout()
                        }, 3000)
                    } else if (error.response.status === 400) {
                        this.error_message = error.response.data.message;
                    } else if (error.response.status === 404) {
                        this.error_message = "No results found, please try again."
                    } else {
                        this.error_message = "Something went wrong! Please try again."
                    }
                }
            });
        },

        /**
         * Fetches a paginated list of activities, filtered by specified activity types,
         * through an API call.
         */
        async getPaginatedActivitiesByActivityType() {
            let pageNumber = this.currentPage - 1;
            api.getActivityByActivityType(this.activityTypesSearchedFor, this.searchType, pageNumber)
                .then(response => {
                    this.activitiesList = response.data;
                    this.rows = response.headers["total-rows"];
                    this.loading = false;
                    this.resultsFound = true;
                }).catch(error => {
                this.loading = false;
                this.errored = true;
                this.userList = [];
                if ((error.code === "ECONNREFUSED") || (error.code === "ECONNABORTED")) {
                    this.error_message = "Cannot connect to server - please try again later!";
                } else {
                    if (error.response.status === 401) {
                        this.error_message = "You aren't logged in! You're being redirected!";
                        setTimeout(() => {
                            this.logout()
                        }, 3000)
                    } else if (error.response.status === 400) {
                        this.error_message = error.response.data.message;
                    } else if (error.response.status === 404) {
                        this.error_message = "No results found, please try again."
                    } else {
                        this.error_message = "Something went wrong! Please try again."
                    }
                }
            });
        },

        /**
         * Searches a activity based on a string of activity types or an activity name by the AND or OR method
         */
        async search() {
            // Converts list of activity types into string
            // e.g. ["Hiking", "Biking"] into "Hiking Biking"
            this.errored = false;
            this.loading = true;
            this.currentPage = 1;
            if (this.searchMode === 'activityType') {
                // Set is as a copy so the User card is only updated after clicking search
                this.activityTypesSearchedFor = this.selectedActivityTypes.slice();
                this.getPaginatedActivitiesByActivityType();
            } else if (this.searchMode === 'activityName') {
                if (this.activityTitle.length === 0) {
                    this.errored = true;
                    this.error_message = "Cannot have empty search field, please try again!";
                } else if (this.activityTitle.length === 75) {
                    this.errored = true;
                    this.error_message = "Cannot have more than 75 characters in the search field.";
                }
                this.getPaginatedActivitiesByActivityTitle();
            }
        }

    }
}
</script>

<style scoped>
.searchButton {
    width: 200%;
}

.distanceSlider {
    margin-top: 1rem !important;
    margin-left: -1rem !important;
}

.rulesButton {
    margin-top: 3px;
    margin-left: 1rem !important;
}

</style>
