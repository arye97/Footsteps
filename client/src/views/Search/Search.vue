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
                <div v-if="searchOption === 'users'">
                    <UserSearch/>
                </div>
                <div v-else-if="searchOption === 'activities'">
                    <ActivitySearch/>
                </div>
            </div>
        </b-container>
    </div>
</template>

<script>
    import Header from '../../components/Header/Header.vue';
    import api from '../../Api';
    import UserSearch from './UserSearch';
    import ActivitySearch from "./ActivitySearch";

    export default {
        name: "Search",
        components: {
            ActivitySearch,
            UserSearch,
            Header
        },

        data() {
            return {
                usersPerPage: 5,
                currentPage: 1,
                userList: [],

                searchMode: 'activityType',
                searchModes: [  //can be expanded to allow for different searching mode (ie; search by username, email... etc)
                    { value: 'activityType', text: 'Activity Type'}
                ],
                searchOption: 'users',
                searchOptions: [
                    {value: 'users', text: 'Users'},
                    {value: 'activities', text: 'Activities'}
                ],
                // These are the ActivityTypes selected in the Multiselect
                selectedActivityTypes : [],
                // These are a copy of selectedActivityTypes passed to the UserCard (to avoid mutation after clicking search)
                activityTypesSearchedFor : [],

                activityTypes: [],
                searchType: "and",
                errored: false,
                error_message: "Something went wrong! Please try again.",
                loading: false,
                rows: null
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
                this.getPaginatedUsersByActivityType();
            }
        },

        methods: {
            goToPage(url) {
                this.$router.push(url);
            },

            /**
             * Function for redirecting to user profile via userId.
             */
            viewProfile(userId) {
                this.goToPage({ name: 'profile', params: {userId: userId} })
            },


            /**
             * Fetches a paginated list of users, filtered by specified activity types,
             * through an API call.
             */
            async getPaginatedUsersByActivityType() {
                let pageNumber = this.currentPage - 1;
                api.getUsersByActivityType(this.activityTypesSearchedFor, this.searchType, pageNumber)
                    .then(response => {
                        this.userList = response.data;
                        this.rows = response.headers["total-rows"];
                        this.loading = false;
                    }).catch(error => {
                    this.loading = false;
                    this.errored = true;
                    this.userList = [];
                    if ((error.code === "ECONNREFUSED") || (error.code === "ECONNABORTED")) {
                        this.error_message = "Cannot connect to server - please try again later!";
                    } else {
                        if (error.response.status === 401) {
                            this.error_message = "You aren't logged in! You're being redirected!";
                            setTimeout(() => {this.logout()}, 3000)
                        } else if (error.response.status === 400) {
                            this.error_message = error.response.data.message;
                        } else if (error.response.status === 404) {
                            this.error_message = "No users with activity types ".concat(this.selectedActivityTypes) + " have been found!"
                        } else {
                            this.error_message = "Something went wrong! Please try again."
                        }
                    }
                });
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
                this.getPaginatedUsersByActivityType();
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