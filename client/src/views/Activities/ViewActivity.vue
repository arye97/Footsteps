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
                            <h1 class="font-weight-light"><strong>{{this.activityTitle}}</strong></h1>
                            <br/>
                            <!-- Activity Descirption -->
                            <h5 class="font-weight-light" v-if="this.description">"{{this.description}}"<br/></h5>
                            <br/>
                            <!-- Edit Activity Button -->
                            <div v-if="this.creatorId==this.activeUserId">
                                <b-button type="submit" variant="success" size="med" v-on:click="goToPage(`/activities/edit/${activityId}`)">Edit Activity</b-button>
                                <br/><br/>
                            </div>
                            <!-- Creator -->
                            <h3 class="font-weight-light"><strong>Creator:</strong></h3><br/>
                            <b-card class="flex-fill" border-variant="secondary">
                                <b-card-text class="font-weight-light">
                                    {{this.creatorName}}
                                </b-card-text>
                            </b-card>
                            <br/>
                            <!-- Location -->
                            <h3 class="font-weight-light"><strong>Location:</strong></h3><br/>
                            <b-card class="flex-fill" border-variant="secondary">
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
                                        <b-card class="flex-fill" border-variant="secondary">
                                            <b-card-text class="font-weight-light">
                                                {{getDateTime(startTime)}}
                                            </b-card-text>
                                        </b-card>
                                    </b-col>
                                    <b-col>
                                        <!--End Time-->
                                        <h3 class="font-weight-light"><strong>End Date:</strong></h3><br/>
                                        <b-card class="flex-fill" border-variant="secondary">
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
                                <b-card class="flex-fill" border-variant="secondary">
                                    <b-card-text class="font-weight-light">
                                        {{duration}}
                                    </b-card-text>
                                </b-card>
                            </div>
                            <br/>
                            <!-- Activity Types -->
                            <h3 class="font-weight-light"><strong>Activity Types:</strong></h3><br/>
                            <b-list-group v-if="this.activityTypes.length >= 1">
                                <b-card v-for="activityType in this.activityTypes" v-bind:key="activityType.name" class="flex-fill" border-variant="secondary">
                                    <b-card-text class="font-weight-light">
                                        {{activityType.name}}
                                    </b-card-text>
                                </b-card>
                            </b-list-group>
                            <b-list-group v-else>
                                <b-card class="flex-fill" border-variant="secondary">
                                    <b-card-text class="font-weight-light">
                                        No activity types selected
                                    </b-card-text>
                                </b-card>
                            </b-list-group>
                            <br/>
                            <!-- Add Results/Following Button Group -->
                            <b-button block v-if="this.isFollowing || this.creatorId == this.activeUserId" variant="success">Add My Results</b-button>
                            <br/>
                            <div v-if="this.creatorId != this.activeUserId">
                                <b-button block v-if="!this.isFollowing" variant="outline-success">Follow</b-button>
                                <b-button block v-if="this.isFollowing" variant="outline-success">Unfollow</b-button>
                                <br/>
                            </div>
                            <!--View Participants and Results Buttons-->
                            <b-row class="mb-1">
                                <b-col>
                                    <!--View Participants-->
                                    <b-button type="submit" variant="success" size="med" v-on:click="viewParticipants">View Participants</b-button>
                                </b-col>
                                <b-col>
                                    <!--View Results-->
                                    <b-button type="submit" variant="success" size="med" v-on:click="viewResults">View Results</b-button>
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
                isFollowing: true
            }
        },
        async mounted () {
            await this.init();
        },
        methods: {
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
                await this.getCreatorUserDetails();
                this.loading = false;
            },
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
            async getActiveUserId() {
                await api.getUserId().then(response => {
                    this.activeUserId = response.data;
                }).catch(err => {
                    this.errored = true;
                    this.errorMessage = err.response.message;
                })
            },
            async getCreatorUserDetails() {
                await api.getUserData(this.creatorId).then(response => {
                    this.creatorName = response.data.firstname + " " + response.data.lastname;
                }).catch(err => {
                    this.errored = true;
                    this.errorMessage = err.response.message;
                })
            },
            getDateTime: formatDateTime,
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
            goToPage(url) {
                this.$router.push(url);
            },
        }
    }
</script>