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
    <section>
        <!-- Search features -->
    </section>

    <b-container class="contents">
        <div class="container h-100">
            <div class="row h-100 align-items-center">
                <div class="col-12 text-center">
                    <h1 class="font-weight-light">
                        <br/>
                        Find something to do!<br/>
                    </h1>
                </div>
            </div>
        </div>
        <b-alert variant="success" dismissible fade :show=this.alertCount>
            {{this.alertMessage}}
        </b-alert>
        <div style="text-align: center">
            <b-button style="margin-bottom: 1.7em; margin-top: 0.8em" variant="primary" v-on:click="goToPage('/activities/create')">Create New Activity</b-button>
        </div>
        <div v-if="this.loading" style="text-align: center">
            <b-spinner class="margin-bottom: 1.7em; margin-top: 0.8em" variant="primary" label="Spinning"></b-spinner>
        </div>
        <div v-else-if="errored" class="text-center text-align:center">
            <div class="alert alert-danger alert-dismissible fade show text-center" role="alert" id="alert">
                {{error_message}}
            </div>
        </div>
        <b-tabs v-else content-class="mt-4" justified>
            <b-tab title="Continuous" :active="continuousIsActive(true)">
                <section v-for="activity in this.activityList" :key="activity.id">
                    <!-- Activity List -->
                    <b-card border-variant="secondary" style="background-color: #f3f3f3" v-if="activity.continuous">
                        <b-row no-gutters>
                            <b-col md="6">
                                <b-card-text>
                                    <strong>{{activity.activity_name}} | {{creatorName}}</strong>
                                    <hr/>
                                    <div v-if="activity.description.length <= 100">
                                        <strong>{{activity.description}}</strong>
                                    </div>
                                    <div v-else>
                                        {{activity.description.substring(0,100)+"...."}}
                                    </div>
                                </b-card-text>
                            </b-col>
                            <b-col md="6">
                                <div class="activity-button-group float-right">
                                    <b-button-group vertical>
                                        <b-modal :id="'activity' + activity.id + '-continuous-modal'" size="lg" centered ok-only scrollable :title="activity.activity_name">
                                            <b-card class="flex-fill" border-variant="secondary">
                                                <b-row class="mb-1">
                                                    <b-col><strong>Creator: </strong></b-col>
                                                    <b-col>{{creatorName}}</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>Location: </strong></b-col>
                                                    <b-col>{{activity.location}}</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>Activity types: </strong></b-col>
                                                    <b-col>
                                                        <section v-for="activityType in activity.activity_type" v-bind:key="activityType.name">
                                                            <div>
                                                                {{activityType.name}}
                                                            </div>
                                                        </section>
                                                    </b-col>
                                                </b-row>
                                            </b-card>
                                            <br>
                                            <b-card class="flex-fill" border-variant="secondary">
                                                <p class="text-justified">
                                                    <strong>Description: </strong><br>
                                                    {{activity.description}}
                                                </p>
                                            </b-card>
                                            <template v-slot:modal-footer v-if="creatorId!==userId">
                                                <div class="w-100">
                                                    <b-button
                                                            variant="outline-dark"
                                                            class="footerButton"
                                                            @click="followActivity(activity.id)"
                                                    >
                                                        Follow Activity
                                                        <div v-b-hover="footerHover">
                                                            <img v-if="isHovered" src="../../../assets/png/footsteps_icon.png" class="footSteps" alt="Footsteps Logo">
                                                            <img v-else src="../../../assets/png/footsteps_icon_hollow.png" class="footSteps" alt="Footsteps Logo">
                                                        </div>
                                                    </b-button>
                                                </div>
                                            </template>
                                        </b-modal>
                                        <b-button variant="outline-primary" v-on:click="goToPage(`/activities/edit/${activity.id}`)">Edit</b-button>
                                        <b-button variant="outline-primary" v-b-modal="'activity' + activity.id + '-continuous-modal'">Details</b-button>
                                        <b-button
                                                variant="outline-primary"
                                                @click="followActivity(activity.id)"
                                                v-if="creatorId!==userId"
                                        >
                                            Follow Activity
                                            <img v-if="isHovered" src="../../../assets/png/footsteps_icon.png" class="footStepsSimplified" alt="Footsteps Logo">
                                            <img v-else src="../../../assets/png/footsteps_icon_hollow.png" class="footStepsSimplified" alt="Footsteps Logo">
                                        </b-button>
                                        <b-button variant="outline-danger" v-on:click="deleteActivity(activity.id)">Delete</b-button>
                                    </b-button-group>
                                </div>
                            </b-col>
                        </b-row>
                    </b-card>
                </section>
                <hr/>
                <footer class="noMore">No more activities to show</footer>
            </b-tab>
            <b-tab title="Duration" :active="continuousIsActive(false)">
                <section v-for="activity in this.activityList" :key="activity.id">
                    <!-- Activity List -->
                    <b-card border-variant="secondary" style="background-color: #f3f3f3" v-if="!activity.continuous">
                            <b-row no-gutters>
                                <b-col md="6">
                                    <b-card-text>
                                        <strong>{{activity.activity_name}} | {{creatorName}}</strong>
                                        <hr/>
                                        <strong>Start Date: </strong>{{getDateTime(activity.start_time)}}
                                        <br/>
                                        <strong>End Date: </strong>{{getDateTime(activity.end_time)}}
                                        <br/><br/>
                                        <div v-if="activity.description.length <= 100">
                                            <strong>{{activity.description}}</strong>
                                        </div>
                                        <div v-else>
                                            {{activity.description.substring(0,100)+"...."}}
                                        </div>
                                    </b-card-text>
                                </b-col>
                                <b-col md="6">
                                    <div class="activity-button-group float-right">
                                        <b-button-group vertical>

                                            <b-modal :id="'activity' + activity.id + '-duration-modal'" size="lg" centered ok-only scrollable :title="activity.activity_name">
                                            <b-card class="flex-fill" border-variant="secondary">
                                                <b-row class="mb-1">
                                                    <b-col><strong>Creator: </strong></b-col>
                                                    <b-col>{{creatorName}}</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>Start Date: </strong></b-col>
                                                    <b-col>{{getDateTime(activity.start_time)}}</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>End Date: </strong></b-col>
                                                    <b-col>{{getDateTime(activity.end_time)}}</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>Duration: </strong></b-col>
                                                    <b-col>{{getDays(activity)}} Days
                                                        {{getHours(activity)}} Hours</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>Location: </strong></b-col>
                                                    <b-col>{{activity.location}}</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>Activity types: </strong></b-col>
                                                    <b-col>
                                                        <section v-for="activityType in activity.activity_type" v-bind:key="activityType.name">
                                                            <div>
                                                                {{activityType.name}}
                                                            </div>
                                                        </section>
                                                    </b-col>
                                                </b-row>
                                            </b-card>
                                            <br>
                                            <b-card class="flex-fill" border-variant="secondary">
                                                <p class="text-justified">
                                                    <strong>Description: </strong><br>
                                                    {{activity.description}}
                                                </p>
                                            </b-card>
                                            <template v-slot:modal-footer v-if="creatorId!==userId">
                                                <div class="w-100">
                                                    <b-button
                                                            variant="outline-dark"
                                                            class="footerButton"
                                                            @click="followActivity(activity.id)"
                                                    >
                                                        <div>
                                                            Follow Activity
                                                        </div>
                                                        <div v-b-hover="footerHover">
                                                            <img v-if="isHovered" src="../../../assets/png/footsteps_icon.png" class="footSteps" alt="Footsteps Logo">
                                                            <img v-else src="../../../assets/png/footsteps_icon_hollow.png" class="footSteps" alt="Footsteps Logo">
                                                        </div>
                                                    </b-button>
                                                </div>
                                            </template>
                                        </b-modal>
                                            <b-button variant="outline-primary" v-on:click="goToPage(`/activities/edit/${activity.id}`)">Edit</b-button>
                                            <b-button variant="outline-primary" v-b-modal="'activity' + activity.id + '-duration-modal'">Details</b-button>
                                            <b-button
                                                    variant="outline-primary"
                                                    @click="followActivity(activity.id)"
                                                    v-if="creatorId!==userId"
                                            >
                                                Follow Activity
                                                <img v-if="isHovered" src="../../../assets/png/footsteps_icon.png" class="footStepsSimplified" alt="Footsteps Logo">
                                                <img v-else src="../../../assets/png/footsteps_icon_hollow.png" class="footStepsSimplified" alt="Footsteps Logo">
                                            </b-button>
                                            <b-button variant="outline-danger" v-on:click="deleteActivity(activity.id)">Delete</b-button>
                                        </b-button-group>
                                    </div>
                                </b-col>
                            </b-row>
                    </b-card>
                </section>
                <hr/>
                <footer class="noMore">No more activities to show</footer>
            </b-tab>

        </b-tabs>
        <br/>
    </b-container>

    <br/><br/>
        <div class="alert alert-danger alert-dismissible fade show sticky-top" role="alert" id="overall_message" hidden>
            <p id="alert-message">{{overallMessageText}}</p>
        </div>
    </div>

</template>

<script>
    import Header from '../../components/Header/Header.vue'
    import api from "../../Api";
    import {formatDateTime} from "../../util";

    /**
     * Sets the passed through error message to an alert bar as its message and also sets the timeouts for the alert bar
     * @param alert_name is the string passed through that is sent on the vue as the alert bar component
     */
    function showError(alert_name) {
        let errorAlert = document.getElementById(alert_name);

        errorAlert.hidden = false;          //Show alert bar
        setTimeout(function () {    //Hide alert bar after ~9000ms
            errorAlert.hidden = true;
        }, 9000);
    }

    export default {
        name: "AllActivities",
        components : {
            Header
        },
        data() {
            return {
                activityList : [],
                userId : null,
                creatorId: null,
                creatorName: null,
                noMore: false,
                activeTab: 0,
                loading: true,
                errored: false,
                error_message: "Something went wrong! Try again later!",
                isHovered: false
            }
        },
        props: {
            alertCount: {
                type: Number,
                default: 0
            },
            alertMessage: {
                type: String,
                default: ''
            }
        },
        beforeMount() {
            this.checkLoggedIn();
        },
        async mounted() {
            this.userId = await this.getUserId();
            await this.getListOfActivities();
            await this.getCreatorName();
        },
        methods: {
            followActivity(id) {
                console.log(id);
                //this function will contain the api call to assign a user to follow the activity
                //is provided the id, no more information should be necessary
                //waiting for backend endpoint to be written as of 05/08/20
            },
            footerHover(hovered) {
                this.isHovered = hovered;
            },
            getDateTime: formatDateTime,
            getDays(activity) {
                return Math.floor(((new Date(activity.end_time) - new Date(activity.start_time))/1000/60/60/24))
            },
            getHours(activity) {
                return Math.ceil(((new Date(activity.end_time) - new Date(activity.start_time))/1000/60/60)) % 24
            },
            checkLoggedIn() {
                if (!sessionStorage.getItem("token")) {
                    this.$router.push("/login");
                }
            },
            /**
             * Get the Id of the current Logged in user.
             * @returns {Promise<*>}
             */
            async getUserId() {
                let userId = null;
                await api.getUserId().then(response => {
                    userId = response.data;
                    this.creatorId = userId;
                }).catch(error => {this.throwError(error, true)});
                return userId
            },
            async getListOfActivities() {
                await api.getUserActivities(this.userId)
                    .then(response => { //If successfully registered the response will have a status of 201
                        this.errored = false;
                        if (response.data.length === 0) {
                            this.noMore = true;
                        }
                        this.loading = false;
                        this.activityList = response.data;
                    }).catch(error => {this.throwError(error, true)})
            },
            goToPage(url) {
                this.$router.push(url);
            },
            /**
             * When the page is loaded, make the tab active that has activities.  If the non-continuous tab has
             * activities, but the continuous tab doesn't, make the non-continuous tab active.
             * Else make continuous active.
             * @param isContinuousTab true if called by continuous tab, false of non-continuous tab.
             */
            continuousIsActive(isContinuousTab) {
                if (this.activityList.filter(a => !a.continuous).length > 0 && this.activityList.filter(a => a.continuous).length === 0) {
                    return !isContinuousTab;
                } else {
                    return isContinuousTab;
                }
            },
            /**
             * Opens a dialog box (modal) to confirm deletion.  If Ok is pressed, then it removes the activity from the
             * database.
             * @param activityId Id of the activity to delete
             * @returns {Promise<void>}
             */
            async deleteActivity(activityId) {
                let confirmDeleteActivity = false;

                // Open dialog box
                await this.$bvModal.msgBoxConfirm("Are you sure you want to delete this Activity?")
                    .then(value => {
                        confirmDeleteActivity = value
                    })
                    .catch(err => {
                        this.overallMessageText = err.message;
                        showError('overall_message');
                    });
                if (!confirmDeleteActivity) {
                    return;
                }

                // Delete from local memory
                this.activityList.splice(this.activityList.findIndex(a => a.id === activityId), 1);

                // Delete from database
                await api.deleteActivity(this.userId, activityId).catch(error => {this.throwError(error, false)})
            },
          async getCreatorName() {
              await api.getUserData(this.creatorId).then((response) => {
                  this.creatorName = `${response.data.firstname} ${response.data.lastname}`;
              }).catch(() => {
                  this.creatorName = "Could not load creator's name";
              });
          },
            /**
             * Helper function for when errors are thrown by server after hitting an endpoint,
             * @param servError Error thrown by server endpoint
             * @param isGet boolean value if the error is from a get endpoint, true if it is, false if it isnt, so that
             * the function will use the necessary if statements to handle these
             */
            throwError(servError, isGet) {
                if (!isGet) {
                    switch (servError.response.status) {
                        case 401:
                            this.$router.push("/login");
                            break;
                        case 404:
                            this.overallMessageText = "Activity not found";
                            showError('overall_message');
                            break;
                        case 403:
                            this.overallMessageText = "Sorry unable to delete this activity (forbidden access)";
                            showError('overall_message');
                            break;
                        default:
                            this.overallMessageText = "An unknown error occurred";
                            showError('overall_message');
                    }
                } else if (isGet) {
                    switch (servError.response.status) {
                        case 401:
                            this.$router.push("/login");
                            break;
                        case 403:
                            this.overallMessageText = "Sorry unable to get activities (forbidden access)";
                            showError('overall_message');
                            break;
                        default:
                            this.$router.push({name: 'myProfile'});
                    }
                }
            }
        }
    }
</script>

<style scoped>
    .noMore {
        text-align: center;

    }
    .text-justified {
        text-align: justify;
    }

    .activity-button-group {
        padding-top: 7.5%;
        padding-right: 40%;
        padding-bottom: 7.5%;
    }

    .activity-button-group button {
        margin-left: 35.5%;
        width: 110%;
    }

    .footerButton {
        width: 100%;
    }

    .footSteps {
        width: 7.5%;
        height: 7.5%;
    }

    .footStepsSimplified {
        width: 16%;
        height: 16%;
    }
</style>