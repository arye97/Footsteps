<template>
    <div id="main">
        <div v-if="this.loading" style="text-align: center">
            <b-spinner class="margin-bottom: 1.7em; margin-top: 0.8em" variant="primary" label="Spinning"></b-spinner>
        </div>
        <div v-else-if="this.errored" class="alert alert-danger alert-dismissible fade show " role="alert" id="alert">
            {{  error_message  }}
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
                                            <template v-slot:modal-footer v-if="creatorId===activeUserId">
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
                                        <b-button v-if="creatorId===activeUserId" variant="outline-primary" v-on:click="goToPage(`/activities/edit/${activity.id}`)">Edit</b-button>
                                        <b-button variant="outline-primary" v-b-modal="'activity' + activity.id + '-continuous-modal'">Details</b-button>
                                        <b-button
                                                variant="outline-primary"
                                                @click="followActivity(activity.id)"
                                                v-if="creatorId!==activeUserId"
                                        >
                                            Follow Activity
                                            <img v-if="isHovered" src="../../../assets/png/footsteps_icon.png" class="footStepsSimplified" alt="Footsteps Logo">
                                            <img v-else src="../../../assets/png/footsteps_icon_hollow.png" class="footStepsSimplified" alt="Footsteps Logo">
                                        </b-button>
                                        <b-button v-if="creatorId==activeUserId" variant="outline-danger" v-on:click="deleteActivity(activity.id)">Delete</b-button>
                                    </b-button-group>
                                </div>
                            </b-col>
                        </b-row>
                    </b-card>
                    <br/>
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
                                        <b-button v-if="creatorId===activeUserId" variant="outline-primary" v-on:click="goToPage(`/activities/edit/${activity.id}`)">Edit</b-button>
                                        <b-button variant="outline-primary" v-b-modal="'activity' + activity.id + '-duration-modal'">Details</b-button>
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
                                            <template v-slot:modal-footer v-if="creatorId!==activeUserId">
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
                                        <b-button v-if="creatorId===activeUserId" variant="outline-danger" v-on:click="deleteActivity(activity.id)">Delete</b-button>
                                    </b-button-group>
                                </div>
                            </b-col>
                        </b-row>
                        <br/>
                    </b-card>
                </section>
                <hr/>
                <footer class="noMore">No more activities to show</footer>
            </b-tab>
        </b-tabs>

    </div>



</template>

<script>
    import {formatDateTime} from "../../util";
    import api from "../../Api";

    export default {
        name: "ActivityList",
        props: {
            //this is an underscore as props need to be set to a data(){} attribute, not directly assigned
            // through a prop!!
            user_Id: {
                default: null
            }
        },
        data() {
            return {
                activityList : [],
                creatorId: null,
                creatorName: null,
                noMore: false,
                activeTab: 0,
                loading: true,
                userId: this.user_Id,
                errored: false,
                error_message: "Could not load user activities",
                activeUserId: null,
                isHovered: false
            }
        },
        beforeMount() {
            this.checkLoggedIn();
        },
        async mounted() {
            await this.getActiveUserId();
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
            getActiveUserId() {
                api.getUserId().then(response => {
                    this.activeUserId = response.data;
                }).catch()
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

            async getListOfActivities() {
                this.errored = false;
                await api.getUserActivities(this.userId).then(
                    response => { //If successfully registered the response will have a status of 201
                        if (response.data.length === 0) {
                            this.noMore = true;
                        }
                        this.loading = false;
                        this.activityList = response.data;
                    }).catch(() => {
                        this.errored = true;
                    })
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
                this.errored = false;
                let confirmDeleteActivity = false;

                // Open dialog box
                await this.$bvModal.msgBoxConfirm("Are you sure you want to delete this Activity?")
                    .then(value => {
                        confirmDeleteActivity = value
                    })
                    .catch(() => {
                        this.errored = true;
                        this.error_message = "Could not delete activity";
                    });
                if (!confirmDeleteActivity) {
                    return;
                }

                // Delete from local memory
                this.activityList.splice(this.activityList.findIndex(a => a.id === activityId), 1);

                // Delete from database
                await api.deleteActivity(this.userId, activityId).catch(() => {
                    this.errored = true;
                    this.error_message = "Could not delete activity";
                })
            },
            async getCreatorName() {
                if (this.creatorId === null) {
                    this.creatorId = this.userId;
                }
                await api.getUserData(this.creatorId).then((response) => {
                    this.creatorName = `${response.data.firstname} ${response.data.lastname}`;
                }).catch(() => {
                    this.creatorName = "Could not load creator's name";
                });
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
</style>