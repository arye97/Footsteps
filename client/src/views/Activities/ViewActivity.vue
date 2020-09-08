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
                        <h1 class="font-weight-light">{{this.errorMessage}}</h1>
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
                            <div v-if="this.creatorId===this.activeUserId">
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
                                    {{this.location.name}}
                                </b-card-text>
                            </b-card>
                            <br/>
                            <div>
                                <b-button v-b-toggle="'map-area'" variant="primary">Toggle Map</b-button>
                                <b-collapse id="map-area">
                                    <b-card>
                                        <map-viewer
                                          :pins="[{lat: this.location.latitude, lng: this.location.longitude}]"
                                          :center="{lat: this.location.latitude, lng: this.location.longitude}"
                                        ></map-viewer>
                                    </b-card>
                                </b-collapse>
                            </div>
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
                            <b-modal id="addResultsModel" centered ok-only ok-variant="secondary" ok-title="Back"
                                     scrollable title="Here are your results">
                                <b-alert dismissible variant="danger" id="addResultAlert"
                                         :show="dismissCountDown"
                                         @dismissed="dismissCountDown=0"
                                         @dismiss-count-down="countDownChanged">
                                    {{ this.errorMessage }}
                                </b-alert>
                                <section v-for="outcome in this.outcomeList" :key="outcome.outcome_id">
                                    <b-card id="outcomeAddResultCard" :title="outcome.title">
                                        <div v-if="outcome.activeUsersResult.submitted">
                                            <div style="color:red" v-if="outcome.activeUsersResult.did_not_finish" >
                                                Did not Finish
                                            </div>
                                            <b-input-group
                                                    :append="outcome.unit_name">
                                                <b-input :id="'submittedInputValue' + outcome.outcome_id"
                                                         v-model="outcome.activeUsersResult.value"
                                                         disabled></b-input>
                                            </b-input-group>
                                        </div>
                                        <div v-else>
                                            <b-input-group
                                                    :append="outcome.unit_name">
                                                <b-input :id="'NotSubmittedInputValue' + outcome.outcome_id" v-model="outcome.activeUsersResult.value"
                                                         placeholder="Input your result here..."></b-input>
                                            </b-input-group>
                                            <b-form-checkbox
                                                    :id="'checkboxDNF' + outcome.outcome_id"
                                                    v-model="outcome.activeUsersResult.did_not_finish"
                                                    name="checkboxDNF"
                                            >
                                                I did not finish
                                            </b-form-checkbox>
                                            <b-button block id="submitResult" variant="success"
                                                      @click="submitOutcomeResult(outcome.outcome_id)">
                                                Submit
                                            </b-button>
                                        </div>
                                    </b-card>
                                </section>
                            </b-modal>
                            <!-- Add Results/Following Button Group -->
                            <b-button block v-if="(this.isFollowing || this.creatorId === this.activeUserId) && this.hasOutcomes"
                                      variant="success" id="addResults" v-b-modal="'addResultsModel'">Add My Results</b-button>
                            <br/>
                            <div v-if="this.creatorId !== this.activeUserId">
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
                                <b-card class="flex-fill" border-variant="secondary" id="viewParticipantsCard">
                                    <strong>Participants: </strong><br>
                                    <b-button-group v-for="participant in participants" :key="participant.id" id="viewParticipantsButtonGroup">
                                        <b-button
                                                class="participantButton" pill variant="success"
                                                v-on:click="toUserProfile(participant.id)" id="participant">{{participant.name}}</b-button>
                                    </b-button-group>
                                    <p v-if="participants.length === 0" id="noParticipants">No participants to show</p>
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
                                <b-col v-if="this.hasOutcomes">
                                    <!--View Results-->
                                    <b-button type="submit" variant="success" size="med" disabled
                                              id="viewResultsError" v-if="resultError">
                                        There was an error fetching results</b-button>
                                    <b-button type="submit" variant="success" size="med"
                                              id="viewResults" v-b-modal="'resultsModal'" v-else>
                                        View Results</b-button>

                                    <b-modal id="resultsModal" title="Activity Results" scrollable ok-only ok-variant="secondary" ok-title="Back">
                                        <div v-for="outcome in this.outcomeList" :key="outcome.outcome_id">
                                            <b-button v-b-toggle="'collapse' + outcome.outcome_id" variant="success" block>{{outcome.title}}</b-button>
                                            <b-collapse :id="'collapse' + outcome.outcome_id">
                                                <div v-if="outcome.results.length > 0">
                                                    <b-card v-for="result in outcome.results" :key="result.result_id">
                                                        <div v-if="result.did_not_finish">{{result.user_name}}: Did not finish</div>
                                                        <div v-else>{{result.user_name}}: {{result.value}} {{outcome.unit_name}}</div>
                                                        <div v-if="result.comment"> ({{result.comment}})</div>
                                                    </b-card>
                                                </div>
                                                <b-card v-else>
                                                    This outcome currently has no registered results
                                                </b-card>
                                            </b-collapse><br/>
                                        </div>
                                    </b-modal>
                                </b-col>
                            </b-row>
                          <br/>
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
    import MapViewer from "../../components/Map/MapViewer";

    export default {
        name: "ViewActivity",
        components: {MapViewer, Header},
        data () {
            return {
                errored: false,
                errorMessage: "",
                dismissSecs: 5,
                dismissCountDown: 0,
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
                resultError: false,
                duration: "",
                activeUserName: "",
                isFollowing: false,
                participants: [],
                hasOutcomes: false,
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
                            user_name: null,
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
                                user_name: null,
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
             * Update dismissCountDown
             */
            countDownChanged(dismissCountDown) {
                this.dismissCountDown = dismissCountDown;
            },
            /**
             * Display add result error alert
             */
            showAlert() {
                this.dismissCountDown = this.dismissSecs
                window.scrollTo(0,0);
            },
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
                await this.getActiveUserName();
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
                if (!this.errored) {
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
                        this.errorMessage = err.response.data.message;
                    });
                }
            },
            /**
             * Get the ID of the user who is viewing the activity
             * @returns {Promise<void>}
             */
            async getActiveUserId() {
                if (!this.errored) {
                    await api.getUserId().then(response => {
                        this.activeUserId = response.data;
                    }).catch(err => {
                        this.errored = true;
                        this.errorMessage = err.response.data.message;
                    })
                }
            },
            /**
             * Get the name of the user who is viewing the activity
             * @returns {Promise<void>}
             */
            async getActiveUserName() {
                if (!this.errored) {
                    await api.getUserData(this.activeUserId).then(response => {
                        this.activeUserName = response.data.firstname + " " + response.data.lastname;
                    }).catch(err => {
                        this.errored = true;
                        this.errorMessage = err.response.data.message;
                    })
                }
            },
            /**
             * Get the name of the activity creator
             * @returns {Promise<void>}
             */
            async getCreatorUserDetails() {
                if (!this.errored) {
                    await api.getUserData(this.creatorId).then(response => {
                        this.creatorName = response.data.firstname + " " + response.data.lastname;
                    }).catch(err => {
                        this.errored = true;
                        this.errorMessage = err.response.data.message;
                    })
                }
            },
            /**
             * Utilises the formatDateTime utility function
             */
            getDateTime: formatDateTime,
            /**
             * Gets the duration of the activity
             */
            getTime() {
                if (!this.errored) {
                    let timeUnits = [];
                    let days = Math.floor(((new Date(this.endTime) - new Date(this.startTime)) / 1000 / 60 / 60 / 24));
                    let hours = Math.ceil(((new Date(this.endTime) - new Date(this.startTime)) / 1000 / 60 / 60)) % 24;
                    if (days > 0) {
                        timeUnits.push(days + " Days");
                    }
                    if (hours > 0) {
                        timeUnits.push(hours + " Hours");
                    }
                    this.duration = timeUnits.join(", ")
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
                if (!this.errored) {
                    await api.getUserSubscribed(this.activityId, this.activeUserId).then((response) => {
                        this.isFollowing = response.data.subscribed;
                    }).catch((err) => {
                        this.errored = true;
                        this.errorMessage = err.response.data.message;
                    });
                }
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
                    this.errorMessage = error.response.data.message;
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
                    this.errorMessage = error.response.data.message;
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
                if (!this.errored) {
                    await api.getParticipants(this.activityId).then(response => {
                        let participants = [];
                        let user;
                        for (let j = 0; j < response.data.length; j++) {
                            user = response.data[j];
                            participants.push({
                                "name": user.firstname + ' ' + user.lastname,
                                "id": user.id
                            });
                        }
                        this.participants = participants;
                    }).catch(error => {
                        //Log out if error
                        if (error.response.status === 401) {
                            sessionStorage.clear();
                            this.$router.push('/login');
                        } else {
                            this.errored = true;
                            this.errorMessage = "Unable to get participants - please try again later";
                        }
                    });
                }
            },
            /**
             * Gets the list of outcomes for the activity
             */
            async fetchOutcomesForActivity() {
                if (!this.errored) {
                    await api.getActivityOutcomes(this.activityId).then(response => {
                        this.outcomeList = response.data;
                        if (this.outcomeList.length > 0) {
                            this.hasOutcomes = true;
                        }
                    }).catch(error => {
                        if (error.response.status === 401) {
                            sessionStorage.clear();
                            this.goToPage('/login');
                        } else {
                            this.errored = true;
                            this.errorMessage = "Unable to get outcomes - please try again later";
                        }
                    });
                }
            },
            /**
             * Gets a list of results for each outcome
             */
            async fetchResultsForOutcomes() {
                if (!this.errored) {
                    for (let i = 0; i < this.outcomeList.length; i++) {
                        let outcomeId = this.outcomeList[i].outcome_id;
                        await api.getOutcomeResults(outcomeId).then(response => {
                            this.outcomeList[i].results = response.data;
                        }).catch(() => {
                            this.resultError = true;
                        });
                        // If the active user's result is not returned create one
                        let activeUsersResults = this.outcomeList[i].results.filter(i => i.user_id === this.activeUserId);
                        if (activeUsersResults.length < 1) {
                            this.outcomeList[i].activeUsersResult = {
                                user_id: this.activeUserId,
                                outcome_id: outcomeId,
                                user_name: this.activeUserName,
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

                await api.createResult(outcomes[0].activeUsersResult, outcomeId).then(() => {
                    outcomes[0].activeUsersResult.submitted = true;
                    outcomes[0].results.push(outcomes[0].activeUsersResult);
                    this.$forceUpdate();
                }).catch(error => {
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
                        this.errored = false;
                        this.errorMessage = error.response.data.message;
                        this.showAlert();
                        break;
                    case 403:
                        this.errorMessage = "User is not a participant of this outcome's activity";
                        break;
                    case 404:
                        this.errorMessage = "Outcome not found - please try again later";
                        break;
                    default:
                        this.errorMessage = "Unable to get outcomes - please try again later";
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

    .word-count {
        padding-top: 7px;
        color: #707070;
        font-size: 0.8em;
    }
</style>
