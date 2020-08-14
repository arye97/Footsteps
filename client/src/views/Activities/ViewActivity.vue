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
                            <!--Add results Modal-->
                            <b-modal id="addResultsModel">
                                <h3 class="font-weight-light"><strong>Outcomes</strong></h3><br/>
                                <section v-for="outcome in this.outcomeList" :key="outcome.outcome_id">
                                    <b-card>
                                        {{ outcome.title }}
                                        <div v-if="outcome.activeUsersResult.submitted">
                                            {{ outcome.activeUsersResult }}
                                        </div>
                                        <div v-else>
                                            <b-form-input
                                                    id="input-value"
                                                    v-model="outcome.activeUsersResult.value"
                                                    placeholder="Input your result here..."
                                            ></b-form-input>
                                            <b-button block :id="'addResultOutcome' + outcome.outcome_id"
                                                      @click="submitOutcomeResult(outcome.outcome_id)" variant="success">
                                                Submit
                                            </b-button>
                                        </div>
                                    </b-card>
                                </section>
                            </b-modal>
                            <!-- Add Results/Following Button Group -->
                            <b-button block v-if="this.isFollowing || this.creatorId == this.activeUserId"
                                      variant="success" id="addResults" v-b-modal="'addResultsModel'">Add My Results</b-button>
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
                            <!--View Participants Modal-->
                            <b-modal id="viewParticipantsModal" size="lg" centered ok-only scrollable :title="activityTitle + ' Participants'">
                                <b-card class="flex-fill" border-variant="secondary">
                                    <strong>Participants: </strong><br>
                                    <b-button-group v-for="participant in participants" :key="participant.id">
                                        <b-button
                                                class="participantButton" pill variant="success"
                                                v-on:click="toUserProfile(participant.id)" id="participant">{{participant.name}}</b-button>
                                    </b-button-group>
                                    <p v-if="participants.length == 0" id="noParticipants">No participants to show</p>
                                </b-card>
                            </b-modal>
                            <!--View Participants and Results Buttons-->
                            <b-row class="mb-1">
                                <b-col>
                                    <!--View Participants-->
                                    <b-button type="submit" variant="success" size="med"
                                              v-b-modal="'viewParticipantsModal'" id="viewParticipants">
                                        View Participants</b-button>
                                </b-col>
                                <b-col>
                                    <!--View Results-->
                                    <b-button type="submit" variant="success" size="med"
                                              id="viewResults">
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
                isFollowing: false,
                participants: [],
                outcomeList: [
                    { // Outcome object
                        outcome_id: null,
                        title: "",
                        activity_id: null,
                        unit_name: "",
                        unit_type: "",
                        activeUsersResult:
                            {
                            result_id: null,
                            user_id: null,
                            outcome_id: null,
                            value: "",
                            did_not_finish: false,
                            comment: "",
                            submitted: false
                            },
                        results: [
                            { // Result object
                                result_id: null,
                                user_id: null,
                                outcome_id: null,
                                value: "",
                                did_not_finish: false,
                                comment: "",
                                submitted: false
                            }
                        ]
                    }
                ],
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
                await this.fetchParticipantsForActivities();
                await this.fetchOutcomesForActivity();
                await this.fetchResultsForOutcomes();
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
                let timeUnits = []
                let days = Math.floor(((new Date(this.endTime) - new Date(this.startTime))/1000/60/60/24));
                let hours =  Math.ceil(((new Date(this.endTime) - new Date(this.startTime))/1000/60/60)) % 24;
                if (days > 0) {
                    timeUnits.push(days + " Days");
                }
                if (hours > 0) {
                    timeUnits.push(hours + " Hours");
                }
                this.duration = timeUnits.join(", ")
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
                await this.fetchParticipantsForActivities();
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
                await this.fetchParticipantsForActivities();
            },
            /**
             * Sends you to the profile page of a specific user
             * @param id
             */
            toUserProfile(id) {
                this.$router.push({ name: 'profile', params: {userId: id} });
            },
            /**
             * Gets list of participants for the activity
             */
            async fetchParticipantsForActivities() {
                await api.getParticipants(this.activityId).then(response => {
                    let participants = [];
                    let user;
                    for (let j = 0; j < response.data.length; j++) {
                        user = response.data[j];
                        participants.push({"name":user.firstname + ' ' + user.lastname,
                            "id":user.id});
                    }
                    this.participants = participants;
                }).catch(error => {
                    //Log out if error
                    if(error.response.status === 401) {
                        sessionStorage.clear();
                        this.$router.push('/login');
                    }else{
                        this.errored = true;
                        this.error_message = "Unable to get participants - please try again later";
                    }
                });
            },
            /**
             * Gets the list of outcomes for the activity
             */
            async fetchOutcomesForActivity() {
                await api.getActivityOutcomes(this.activityId).then(response => {
                    this.outcomeList = response.data;
                }).catch(error => {
                    if (error.response.status === 401) {
                        sessionStorage.clear();
                        this.goToPage('/login');
                    } else {
                        this.errored = true;
                        this.error_message = "Unable to get outcomes - please try again later";
                    }
                });
            },
            /**
             * Gets a list of results for each outcome
             */
            async fetchResultsForOutcomes() {
                for (let i = 0; i < this.outcomeList.length; i++) {
                    let outcomeId = this.outcomeList[i].outcome_id;
                    // TODO: Add api call getOutcomeResults here. Add data from response to outcome, like below
                    // this.outcomeList[i].results = response.data;

                    // If the active user's result is not returned create one
                    this.outcomeList[i].results = []; //TODO remove this line when getOutcomeResults is implemented
                    let activeUsersResults = this.outcomeList[i].results.filter(i => i.user_id === this.activeUserId);
                    if (activeUsersResults.length < 1) {
                        this.outcomeList[i].activeUsersResult = {
                                user_id: this.activeUserId,
                                outcome_id: outcomeId,
                                value: "",
                                did_not_finish: false,
                                comment: "",
                                submitted: false
                        };
                    } else {
                        this.outcomeList[i].activeUsersResult = activeUsersResults[0];
                        this.outcomeList[i].activeUsersResult.submitted = true;
                    }
                }
            },
            /**
             * Submit result to back-end for an outcome
             * @param outcomeId the outcome ID
             */
            async submitOutcomeResult(outcomeId) {
                // Finds the active user's result for the given outcome
                let outcomes = this.outcomeList.filter(i => i.outcome_id === outcomeId);
                if (outcomes.length < 1) return;

                await api.createResult(outcomes[0].activeUsersResult, outcomeId).catch(error => {
                    this.processPostError(error);
                });
            },
            /**
             * Process the error in terms of status codes returned
             * @param error being processed
             */
            processPostError(error) {
                this.errored = true;
                switch (error.response.status) {
                    case 401:
                        sessionStorage.clear();
                        this.goToPage('/login');
                        break;
                    case 400:
                        this.error_message = error.response.data.message;
                        break;
                    case 403:
                        this.error_message = "User is not a participant of this outcome's activity";
                        break;
                    case 404:
                        this.error_message = "Outcome not found";
                        break;
                    default:
                        this.error_message = "Unable to get outcomes - please try again later";
                        break;
                }
            }
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

    .participantButton {
        margin: 3px;
        display: inline-block;
    }
</style>
