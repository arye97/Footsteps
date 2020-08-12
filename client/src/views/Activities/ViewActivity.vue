<template>
    <div id="app">
        <!-- Header -->
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
        <!-- Content container -->
        <b-container class="contents" fluid>
            <div class="row h-100">
                <div class="col-12 text-center">
                    <div v-if="errored">
                        <p class="font-weight-light">{{this.error}}</p>
                        <p class="font-weight-light">{{this.errorMessage}}</p>
                    </div>
                    <div v-else>
                        <div v-if="loading"> Loading... <br/><br/><b-spinner variant="primary" label="Spinning"></b-spinner></div>
                        <div v-else class="font-weight-light">
                            <br/>
                            <!-- Title -->
                            <h1 class="font-weight-light" id="activityTitle"><strong>{{this.activityTitle}}</strong></h1>
                            <br/>
                            <!-- Activity Descirption -->
                            <h5 class="font-weight-light" v-if="this.description" id="description">"{{this.description}}"<br/></h5>
                            <br/>
                            <!-- Edit Activity Button -->
                            <div v-if="this.creatorId==this.activeUserId">
                                <b-button type="submit" variant="success"
                                          size="med" v-on:click="goToPage(`/activities/edit/${activityId}`)"
                                          id="editActivity">Edit Activity</b-button>
                                <br/><br/>
                            </div>
                            <!-- Creator -->
                            <h3 class="font-weight-light"><strong>Creator:</strong></h3><br/>
                            <b-card class="flex-fill" border-variant="secondary" id="creatorName">
                                <b-card-text class="font-weight-light">
                                    {{this.creatorName}}
                                </b-card-text>
                            </b-card>
                            <br/>
                            <!-- Location -->
                            <h3 class="font-weight-light"><strong>Location:</strong></h3><br/>
                            <b-card class="flex-fill" border-variant="secondary" id="location">
                                <b-card-text class="font-weight-light">
                                    {{this.location}}
                                </b-card-text>
                            </b-card>
                            <br/>
                            <!-- Time details -> only relevant for duration activities -->
                            <div v-if="!continuous">
                                <b-row class="mb-1">
                                    <b-col>
                                        <!--Start Time-->
                                        <h3 class="font-weight-light"><strong>Start Date:</strong></h3><br/>
                                        <b-card class="flex-fill" border-variant="secondary" id="startTime">
                                            <b-card-text class="font-weight-light">
                                                {{getDateTime(startTime)}}
                                            </b-card-text>
                                        </b-card>
                                    </b-col>
                                    <b-col>
                                        <!--End Time-->
                                        <h3 class="font-weight-light"><strong>End Date:</strong></h3><br/>
                                        <b-card class="flex-fill" border-variant="secondary" id="endTime">
                                            <b-card-text class="font-weight-light">
                                                {{getDateTime(endTime)}}
                                            </b-card-text>
                                        </b-card>
                                    </b-col>
                                </b-row>
                                <br/>
                                <!--Total Duration-->
                                <!--End Time-->
                                <h3 class="font-weight-light"><strong>Total Duration:</strong></h3><br/>
                                <b-card class="flex-fill" border-variant="secondary" id="totalDuration">
                                    <b-card-text class="font-weight-light">
                                        {{duration}}
                                    </b-card-text>
                                </b-card>
                            </div>
                            <br/>
                            <!-- Activity Types -->
                            <h3 class="font-weight-light"><strong>Activity Types:</strong></h3><br/>
                            <b-list-group v-if="this.activityTypes.length >= 1">
                                <b-card v-for="activityType in this.activityTypes" v-bind:key="activityType.name"
                                        class="flex-fill" border-variant="secondary" id="activityType">
                                    <b-card-text class="font-weight-light">
                                        {{activityType.name}}
                                    </b-card-text>
                                </b-card>
                            </b-list-group>
                            <b-list-group v-else>
                                <b-card class="flex-fill" border-variant="secondary" id="noActivityType">
                                    <b-card-text class="font-weight-light">
                                        No activity types selected
                                    </b-card-text>
                                </b-card>
                            </b-list-group>
                            <br/>
                            <!-- Add Results/Following Button Group -->
                            <b-button block v-if="this.isFollowing || this.creatorId == this.activeUserId"
                                      variant="success" id="addResults">Add My Results</b-button>
                            <br/>
                            <div v-if="this.creatorId != this.activeUserId">
                                <b-button block v-if="!this.isFollowing"
                                          variant="outline-dark"
                                          class="footerButton"
                                          @click="followActivity"
                                          id="followButton"
                                >
                                    Follow Activity
                                    <img src="../../../assets/png/footsteps_icon_hollow.png" class="footSteps" alt="Footsteps Logo">
                                </b-button>
                                <b-button block v-else
                                          variant="outline-dark"
                                          class="footerButton"
                                          @click="unfollowActivity"
                                          id="unfollowButton"
                                >
                                    Unfollow Activity
                                    <img src="../../../assets/png/footsteps_icon.png" class="footSteps" alt="Footsteps Logo">
                                </b-button>
                                <br/>
                            </div>
                            <!--View Participants and Results Buttons-->
                            <b-row class="mb-1">
                                <b-col>
                                    <!--View Participants-->
                                    <b-button type="submit" variant="success" size="med"
                                              v-on:click="viewParticipants" id="viewParticipants">
                                        View Participants</b-button>
                                </b-col>
                                <b-col>
                                    <!--View Results-->
                                    <b-button type="submit" variant="success" size="med"
                                              v-on:click="viewResults" id="viewResults">
                                        View Results</b-button>
                                </b-col>
                            </b-row>
                        </div>
                    </div>
                </div>
            </div>
        </b-container>
        <br/>
    </div>
</template>

<script>
    import Header from "../../components/Header/Header";
    import api from "../../Api";
    import {formatDateTime} from "../../util";

    export default {
        name: "ViewActivity",
        components: {Header},
        data () {
            return {
                errored: false,
                errorMessage: "",
                loading: false,
                activityId: null,
                activityTitle: "",
                description: "",
                creatorId: null,
                creatorName: "",
                location: "",
                startTime: null,
                endTime: null,
                activityTypes: [],
                activeUserId: null,
                continuous: false,
                duration: "",
                isFollowing: false
            }
        },
        async mounted () {
            await this.init();
        },
        methods: {
            /**
             * The initialisation function.
             * Gets all of the data required for the page
             * @returns {Promise<void>}
             */
            async init() {
                this.loading = true;
                this.activityId = this.$route.params.activityId;
                if (isNaN(this.activityId)) {
                    this.errored = true;
                    this.errorMessage = "Invalid Activity ID, must be a number";
                }
                await this.getActivityDetails();
                this.getTime();
                await this.getActiveUserId();
                await this.getFollowingDetails();
                await this.getCreatorUserDetails();
                this.loading = false;
            },
            /**
             * Gets and set the details about the activity
             * @returns {Promise<void>}
             */
            async getActivityDetails() {
                await api.getActivityData(this.activityId).then(response => {
                    this.activityTitle = response.data.activity_name;
                    this.description = response.data.description;
                    this.creatorId = response.data.creatorUserId;
                    this.location = response.data.location;
                    this.startTime = response.data.start_time;
                    this.endTime = response.data.end_time;
                    this.activityTypes = response.data.activity_type;
                    this.continuous = response.data.continuous;
                }).catch(err => {
                    this.errored = true;
                    this.errorMessage = err.response.message;
                });
            },
            /**
             * Get the ID of the user who is viewing the activity
             * @returns {Promise<void>}
             */
            async getActiveUserId() {
                await api.getUserId().then(response => {
                    this.activeUserId = response.data;
                }).catch(err => {
                    this.errored = true;
                    this.errorMessage = err.response.message;
                })
            },
            /**
             * Get the name of the activity creator
             * @returns {Promise<void>}
             */
            async getCreatorUserDetails() {
                await api.getUserData(this.creatorId).then(response => {
                    this.creatorName = response.data.firstname + " " + response.data.lastname;
                }).catch(err => {
                    this.errored = true;
                    this.errorMessage = err.response.message;
                })
            },
            /**
             * Utilises the formatDateTime utility function
             */
            getDateTime: formatDateTime,
            /**
             * Gets the duration of the activity
             */
            getTime() {
                let days = Math.floor(((new Date(this.endTime) - new Date(this.startTime))/1000/60/60/24));
                let hours =  Math.ceil(((new Date(this.endTime) - new Date(this.startTime))/1000/60/60)) % 24;
                if (days > 0) {
                    this.duration += days + " Days";
                }
                if (hours > 0) {
                    this.duration += hours + " Hours";
                }
            },
            /**
             * Routes to a given url
             * @param url
             */
            goToPage(url) {
                this.$router.push(url);
            },
            /**
             * Determines whether the viewer of the page is currently following this activity
             * @returns {Promise<void>}
             */
            async getFollowingDetails() {
                await api.getUserSubscribed(this.activityId, this.activeUserId).then((response) => {
                    this.isFollowing = response.data.subscribed;
                }).catch((err) => {
                    this.errored = true;
                    this.errorMessage = err.response.message;
                });

            },
            /**
             * Sends the post request so a user can follow the activity
             * @returns {Promise<void>}
             */
            async followActivity() {
                await api.setUserSubscribed(this.activityId, this.activeUserId).then(() => {
                    this.isFollowing = true;
                }).catch((error) => {
                    this.errored = true;
                    this.errorMessage = error.response.message;
                });
            },
            /**
             * Sends the delete request so the user can unfollow the activity
             * @returns {Promise<void>}
             */
            async unfollowActivity() {
                await api.deleteUserSubscribed(this.activityId, this.activeUserId).then(() => {
                    this.isFollowing = false;
                }).catch((error) => {
                    this.errored = true;
                    this.errorMessage = error.response.message;
                });
            },
        }
    }
</script>

<style scoped>
    .footSteps {
        width: 7.5%;
        height: 7.5%;
    }
    .footStepsSimplified {
        width: 16%;
        height: 16%;
    }
    .noMore {
        text-align: center;

    }
    .text-justified {
        text-align: justify;
    }

    .activity-button-group {
        padding: 7.5% 30px;
    }

    .activity-button-group button {
        width: 190px;
    }

    .footerButton {
        width: 100%;
    }
</style>
