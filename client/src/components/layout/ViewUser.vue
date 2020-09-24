<template>
    <div id="app">
        <h1>
            <br/>
            <br/>
        </h1>
        <b-container class="contents" fluid>
            <div class="container">
                <Header v-if="!modalView" :userId="this.userId"/>
                <div class="row h-100">
                    <div class="col-12 text-center">
                        <section v-if="errored">
                            <p class="font-weight-light">{{this.error}}</p>
                            <p class="font-weight-light">Sorry, please log in to access your profile, or try again
                                later.
                            </p>
                        </section>
                        <section v-else>
                            <div v-if="loading">Loading...
                                <br/>
                                <br/>
                                <b-spinner variant="primary" label="Spinning"></b-spinner>
                            </div>
                            <div v-else class="font-weight-light">
                                <br/>
                                <h1 class="font-weight-light" v-if="this.user.nickname">
                                    <strong>{{this.user.firstname}} {{this.user.middlename}} {{this.user.lastname}}
                                    </strong> | {{this.user.nickname}}
                                </h1>
                                <h1 v-else class="font-weight-light">
                                    <strong>{{this.user.firstname}} {{this.user.middlename}} {{this.user.lastname}}
                                    </strong>
                                </h1>

                                <h1 class="font-weight-light" style="word-break: break-word; font-size: x-large" v-if="this.user.bio">"{{ this.user.bio }}"</h1>

                                <b-button type="submit" variant="success" size="med"
                                          v-if="this.isEditable" v-on:click="editProfile">Edit Profile
                                </b-button>
                                <br/>
                                <br/>

                                <b-row>
                                    <b-col>
                                        <div>
                                            <div class="flex-fill" border-variant="secondary">
                                                <h3 class="font-weight-light" style="font-size: large; word-break: break-word;"><strong>{{this.user.primary_email}}</strong> (Primary)</h3>
                                            </div>
                                            <div v-for="email in this.user.additional_email" v-bind:key="email"
                                                    class="flex-fill" border-variant="secondary">
                                                <h3 style="font-size: large; word-break: break-word;" class="font-weight-light">{{email}}</h3>
                                            </div>
                                        </div>
                                        <div>
                                            <div v-if="this.user.passports.length >= 1 && this.user.passports.length <= 2">
                                                <b-button-group v-for="country in this.user.passports" v-bind:key="country"
                                                                border-variant="secondary">
                                                    <b-button pill variant="primary" style="word-break: break-word;" disabled class="font-weight-light countries">{{country}}</b-button>
                                                </b-button-group>
                                                <br/>
                                            </div>
                                        </div>
                                    </b-col>

                                    <b-col>
                                        <div class="flex-fill" border-variant="secondary">
                                            <div class="font-weight-light">
                                                <h3 style="font-size: large" class="font-weight-light">{{this.user.gender}}</h3>
                                            </div>
                                        </div>
                                        <div>
                                            <div class="flex-fill" border-variant="secondary" v-if="this.user.fitness >= 0">
                                                <h3 style="font-size: large" class="font-weight-light">{{this.fitness}}</h3>
                                            </div>
                                            <div v-else>
                                                <h3 style="font-size: large" class="font-weight-light">No Fitness Level set</h3>
                                            </div>
                                        </div>
                                            <div class="flex-fill" border-variant="secondary">
                                                <h3 style="font-size: large" class="font-weight-light"><strong>DOB</strong> {{this.user.date_of_birth}}</h3>
                                            </div>
                                    </b-col>
                                </b-row>
                                <div>

                                    <div v-if="this.user.passports.length > 2">
                                        <hr/>
                                        <b-button-group v-for="country in this.user.passports" v-bind:key="country"
                                                        border-variant="secondary">
                                            <b-button pill variant="primary" disabled class="font-weight-light countries">{{country}}</b-button>
                                        </b-button-group>
                                        <br/>
                                    </div>
                                    <div v-else-if="this.user.passports.length === 0">
                                        <hr/>
                                        <h3 style="font-size: large" class="font-weight-light">No Passport Countries set</h3>
                                    </div>
                                    <hr/>
                                </div>


                                <!-- Activity Types -->
                                <div>
                                    <div v-if="this.user.activityTypes.length >= 1">
                                        <b-button-group v-for="activityType in this.user.activityTypes"
                                                        v-bind:key="activityType.name" border-variant="secondary">
                                            <b-button pill variant="secondary"  disabled class="font-weight-light activityTypes">{{activityType.name}}</b-button>
                                        </b-button-group>
                                    </div>


                                    <b-list-group v-else horizontal="md">
                                        <b-card class="flex-fill" border-variant="secondary">
                                            <b-card-text class="font-weight-light">
                                                No selected Activity Types
                                            </b-card-text>
                                        </b-card>
                                    </b-list-group>
                                </div>

                                <hr/>
                                <b-row>
                                    <b-col>
                                        <h3 class="font-weight-light"><strong>Public Location</strong></h3>
                                        <div v-if="this.user.public_location">
                                            <div class="address">
                                                <h3 id="public-location-Name" class="font-weight-light"> {{user.public_location.name}} </h3>
                                            </div>
                                        </div>
                                        <div v-else>
                                            <div class="address">
                                                <h3 class="font-weight-light">No Location Specified yet </h3>
                                            </div>
                                        </div>
                                    </b-col>
                                    <b-col>
                                        <h3 class="font-weight-light"><strong>Private Location</strong></h3>
                                        <div v-if="this.user.private_location">
                                            <div class="address">
                                                <h3 id="private-location-Name" class="font-weight-light"> {{user.private_location.name}} </h3>
                                            </div>
                                        </div>
                                        <div v-else>
                                            <div class="address">
                                                <h3 class="font-weight-light">No Location Specified yet </h3>
                                            </div>
                                        </div>
                                    </b-col>
                                </b-row>
                                <div v-if="this.user.public_location || this.user.private_location">
                                    <location-i-o id="location"
                                                  pin-legend-mode="ViewUser"
                                                  :view-only="true"
                                                  :parent-pins="getPinData()"
                                                  :parent-center="getMapCenter()"
                                                  class="font-weight-normal"
                                    >
                                    </location-i-o>
                                </div>
                                <br/>
                            </div>
                        </section>
                    </div>
                </div>
                <section v-if="pageUrl === '/search/users' && userDataLoaded">
                    <ActivityList :user-id-prop="userId"/>
                </section>
            </div>
        </b-container>
        <br/>
        <br/>
    </div>
</template>

<script>
    import api from "../../Api";
    import {fitnessLevels} from '../../constants'
    import Header from '../../components/Header/Header';
    import ActivityList from "../Activities/ActivityList";
    import LocationIO from "../../components/Map/LocationIO";

    export default {
        name: "ViewUser",
        components: {
            ActivityList,
            Header,
            LocationIO
        },
        props: {
            modalView: {
                default: false,
                type: Boolean
            },
            userIdProp: {
                default: ''
            }
        },
        data() {
            return {
                userId: this.userIdProp,
                user: null,
                loading: true,
                errored: false,
                error: null,
                fitness: null,
                formattedDate: "",
                isEditable: true,
                activityTypes: [],
                continuousActivities: [],
                discreteActivities: [],
                userDataLoaded: false,
                pageUrl: this.$route.fullPath
            }
        },
        async mounted() {
            await this.init();
        },
        methods: {
            async init() {
                this.user = null;
                this.errored = false;
                this.error = null;
                this.fitness = null;
                if (this.$route.params.userId) {
                    this.userId = this.$route.params.userId;
                } else if (this.userId === undefined || isNaN(this.userId)) {  // Check if the inputted userId prop wasn't used
                    this.userId = this.getUserId();
                }
                this.loading = true;
                await this.fetchActivityTypes();
                this.continuousActivities = [];
                this.discreteActivities = [];
                await this.editable();
                await this.getProfile();
                this.loading = false;


            },
            getUserId() {
                let id = null;
                api.getUserId().then(response => {
                    id = response.data;
                }).catch(() => {
                    id = '';
                });
                return id;
            },
            /**
             * Fetch all possible activity types from the server.
             */
            async fetchActivityTypes() {
                this.activityTypes = null;
                await api.getActivityTypes().then(response => {
                    this.activityTypes = response.data.map(activity => activity['name']);
                    this.activityTypes.sort(function (a, b) {
                        return a.toLowerCase().localeCompare(b.toLowerCase());
                    });
                }).catch(error => {
                    this.processGetError(error);
                });
            },
            async getProfile() {
                await api.getUserData(this.userId).then(response => {
                    if (response.status === 200) {
                        //user is set to the user data retrieved
                        this.user = response.data;
                        this.userId = this.user.id;
                        for (let i = 0; i < fitnessLevels.length; i++) {
                            if (fitnessLevels[i].value === this.user.fitness) {
                                this.fitness = fitnessLevels[i].desc;
                            }
                        }
                        if (this.user.role === 20) {
                            // Is the global admin
                            this.$router.push('/home');
                            return;
                        }
                        this.userDataLoaded = true;
                    }
                }).catch(error => {
                    this.errored = true;
                    this.error = error.response.data.message;
                    if (error.response.data.status === 404 && sessionStorage.getItem('token') !== null) {
                        this.$router.push({name: 'myProfile'});
                        this.init();
                    } else {
                        this.logout();
                    }
                });
            },
            logout() {
                api.logout().then(() => {
                    sessionStorage.clear();
                    // tokenStore.setToken(null);
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
            editProfile() {
                this.$router.push({name: 'editProfile', params: {userId: this.userId}});
            },

            /**
             * Checks if the user ID currently held, is editable by this user/client
             */
            async editable() {
                if (this.userId === '') {
                    this.isEditable = true;
                    return;
                }
                await api.checkProfile(this.userId).then(() => {
                    // Status code 200
                    // User can edit this profile
                    // If admin will return 200
                    this.isEditable = true;
                }).catch(error => {
                    this.isEditable = false;
                    if (error.response.data.status === 401) {
                        this.logout();
                    }
                });
            },
            getPinData() {
                let userPins = []
                if (this.user.public_location) {
                    userPins.push({
                        lat: this.user.public_location.latitude,
                        lng: this.user.public_location.longitude,
                        colour: 'red',
                        title: 'Public Location',
                        location_name: this.user.public_location.name,
                    });
                }
                if (this.user.private_location) {
                    userPins.push({
                        lat: this.user.private_location.latitude,
                        lng: this.user.private_location.longitude,
                        colour:'red',
                        title: 'Private Location',
                        location_name: this.user.private_location.name,
                    });
                }
                return userPins;
            },
            getMapCenter() {
                if (this.user.public_location) {
                    return {lat: this.user.public_location.latitude, lng: this.user.public_location.longitude}
                }
                if (this.user.private_location) {
                    return {lat: this.user.private_location.latitude, lng: this.user.private_location.longitude}
                }
            }
        }
    }
</script>

<style scoped>
    .countries {
        margin: 3px;
        display: inline-block;
    }

    .activityTypes {
        margin: 3px;
        display: inline-block;
    }


</style>
