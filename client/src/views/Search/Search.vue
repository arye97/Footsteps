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
                <b-col cols="5" class="floatLeft">
                    <h3>Search for:</h3>
                </b-col>
                <b-col class="floatRight">
                    <b-form-select id="searchModeSelect" v-model="searchOption" :options="searchOptions"></b-form-select>
                </b-col>
            </b-row>
            <br/>
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
                <b-col cols="6" align-self="center">
                    <b-form-radio id="andRadioButton" v-model="searchType" name="andType" value="and">Search results match all selections</b-form-radio>
                </b-col>
                <b-col cols="6" align-self="center">
                    <b-form-radio id="orRadioButton" v-model="searchType" name="orType" value="or">Search results match at least one selection</b-form-radio>
                </b-col>
            </b-row>
            <b-row>
                <b-button class="searchButton" id="searchButton" variant="primary" v-on:click="search()">
                    Search
                </b-button>
                <br/>
            </b-row>
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
        <section v-else v-for="user in this.currentPageUserList" :key="user.id">
            <!-- User List -->
            <user-card v-bind:user="user" v-bind:activity-types-searched-for="activityTypesSearchedFor"/>
            <br>
        </section>
        <!-- Pagination Nav Bar -->
        <b-pagination
                v-if="!errored && !loading && userList.length >= 1"
                align="fill"
                v-model="currentPage"
                :total-rows="rows"
                :per-page="usersPerPage"
        ></b-pagination>
    </b-container>
    </div>
</template>

<script>
    import Header from '../../components/Header/Header.vue';
    import Multiselect from 'vue-multiselect';
    import UserCard from "./UserCard";
    import api from '../../Api';

    export default {
        name: "Search",
        components: {
            UserCard,
            Header,
            Multiselect
        },

        data() {
            return {
                usersPerPage: 5,
                currentPage: 1,
                currentPageUserList: [],
                userList: [],
                searchMode: 'activityType',
                searchModes: [  //can be expanded to allow for different searching mode (ie; search by username, email... etc)
                    { value: 'activityType', text: 'Activity Type'}
                ],
                searchOption: 'users',
                searchOptions: [
                    //{value: 'activities', text: 'Activities'},
                    {value: 'users', text: 'Users'}
                ],
                // These are the ActivityTypes selected in the Multiselect
                selectedActivityTypes : [],
                // These are a copy of selectedActivityTypes passed to the UserCard (to avoid mutation after clicking search)
                activityTypesSearchedFor : [],

                activityTypes: [],
                searchType: "and",
                errored: false,
                error_message: "Something went wrong! Please try again.",
                loading: false
            }
        },

        async mounted() {
            // If not logged in
            await api.getUserId().catch(() => this.logout())
            await this.fetchActivityTypes();
        },

        watch: {
            /**
             * Watcher is called whenever currentPage is changed, via searching new query
             * or the pagination bar.
             */
            currentPage() {
                this.setCurrentPageUserList();
            }
        },

        computed: {
            /**
             * Finds the number of users (rows) gained from the search query.
             */
            rows() {
                return this.userList.length;
            }
        },

        methods: {
            goToPage(url) {
                this.$router.push(url);
            },

            /**
             * Function for redirecting to user profile via userId
             */
            viewProfile(userId) {
                this.goToPage({ name: 'profile', params: {userId: userId} })
            },

            /**
             * Calculate the users to be displayed from the current page number.
             * This function is called when the pagination bar is altered,
             * changing the currentPage variable.
             */
            setCurrentPageUserList() {
                let leftIndex = (this.currentPage - 1) * this.usersPerPage;
                let rightIndex = leftIndex + this.usersPerPage;
                if (rightIndex > this.userList.length) {
                    rightIndex = this.userList.length;
                }
                this.currentPageUserList = this.userList.slice(leftIndex, rightIndex);
                window.scrollTo(0,0);
            },

            /**
             * Searches a user based on a string of activity types and a method AND or OR
             */
            async search() {
                // Converts list of activity types into string
                // e.g. ["Hiking", "Biking"] into "Hiking Biking"
                this.errored = false;
                this.loading = true;
                // Set is as a copy so the User card is only updated after clicking search
                this.activityTypesSearchedFor = this.selectedActivityTypes.slice();
                api.getUsersByActivityType(this.activityTypesSearchedFor, this.searchType)
                    .then(response => {
                        if (response.status === 200) {
                            // Show users in page
                            this.userList = response.data;
                            this.setCurrentPageUserList();
                        }
                        this.loading = false;
                    }).catch(err => {
                        this.loading = false;
                        this.errored = true;
                        this.userList = [];
                        this.setCurrentPageUserList();
                        if ((err.code === "ECONNREFUSED") || (err.code === "ECONNABORTED")) {
                            this.error_message = "Cannot connect to server - please try again later!";
                        } else {
                            if (err.response.status === 401) {
                                this.error_message = "You aren't logged in! You're being redirected!";
                                setTimeout(() => {this.logout()}, 3000)
                            } else if (err.response.status === 400) {
                                this.error_message = err.response.data.message;
                            } else if (err.response.status === 404) {
                                this.error_message = "No users with activity types ".concat(this.selectedActivityTypes) + " have been found!"
                            } else {
                                this.error_message = "Something went wrong! Please try again."
                            }
                        }
                });
                this.currentPage = 1;
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