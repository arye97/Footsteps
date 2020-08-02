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
                    <b-form-radio id="andRadioButton" v-model="searchType" name="andType" value="and">Search including all</b-form-radio>
                </b-col>
                <b-col cols="4" align-self="center">
                    <b-form-radio id="orRadioButton" v-model="searchType" name="orType" value="or">Search including some</b-form-radio>
                </b-col>
            </b-row>
        </div>
        <section v-for="user in this.userList" :key="user.id">
            <!-- User List -->
            <b-card border-variant="secondary" style="background-color: #f3f3f3">
                <b-row class="mb-1">
                    <b-col>
                        <b-card-text><strong>{{ user.firstname }} {{ user.lastname }}</strong></b-card-text>
                    </b-col>
                    <b-col>
                        <!-- View user button -->
                        <b-button id="viewProfileButton" style="float: right" variant="primary" v-on:click="viewProfile(user.id)">View Profile</b-button>
                    </b-col>
                </b-row>
                <hr style="border-color: inherit">
                <b-row class="mb-1">
                    <b-col>
                        <!-- user.primary_email would be better but is null from BE -->
                        <strong>Email: </strong>{{ user.emails[0].email }}
                        <br/><br/>
                        <div v-if="user.bio.length <= 75">
                            {{ user.bio }}
                        </div>
                        <div v-else>
                            {{ user.bio.substring(0,75)+"...." }}
                        </div>
                    </b-col>
                    <b-col v-if="user.activityTypes.length >= 1">
                        <b-list-group>
                            <section v-for="activityType in user.activityTypes" v-bind:key="activityType">
                                <!-- Only display queried activity types -->
                                <b-list-group-item v-if="selectedActivityTypes.indexOf(activityType.name) > -1" variant="success">
                                    {{ activityType.name }}
                                </b-list-group-item>
                            </section>
                        </b-list-group>
                    </b-col>
                </b-row>
            </b-card>
            <br>
        </section>
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

        data() {
            return {
                userList: [],
                searchMode: 'activityType',
                searchModes: [  //can be expanded to allow for different searching mode (ie; search by username, email... etc)
                    { value: 'activityType', text: 'Activity Type'}
                ],
                selectedActivityTypes : [],
                //todo: this list should be grabbed from the database, only hard coded for simplicity
                activityTypes: [
                    "Archery",
                    "E-sports",
                    "Fencing",
                    "Boating",
                    "Hiking",
                    "Biking"
                ],
                searchType: "and"
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
             * Searches a user based on a string of activity types and a method AND or OR
             * @param searchTerm a string of activity types
             * @param method a string indicating AND or OR
             */
            async search() {
                let activityTypes = this.selectedActivityTypes.join(" ");
                // Endpoint stuff

                api.getUsersByActivityType(activityTypes, this.searchType)
                    .then(response => {
                        if (response.status === 200) {
                            console.log(response.data)
                            // Show users in page
                            this.userList = response.data;
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
            }
        }
    }


</script>

<style scoped>

</style>