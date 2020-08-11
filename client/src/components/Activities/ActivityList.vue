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
                                    <strong>{{ activity.activity_name }} | {{ activity.creatorName }}</strong>
                                    <hr/>
                                    <div v-if="activity.description.length <= 100">
                                        {{ activity.description }}
                                    </div>
                                    <div v-else>
                                        {{ activity.description.substring(0,100)+"...." }}
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
                                                    <b-col>{{ activity.creatorName }}</b-col>
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
                                            <br>
                                            <template v-slot:modal-footer v-if="activity.creatorUserId!==activeUserId">
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


                                            <!--  Here for participants-->
                                            <b-card class="flex-fill" border-variant="secondary">
                                                    <strong>Participants: </strong><br>

                                                
                                                    <section v-for="participant in activityList[0].participants" :key="participant.firstname">
                                                        {{participant.firstname}} {{participant.lastname}}
                                                    </section>

<!--                                                    {{activity.participants}}-->
<!--                                                    <b-pagination-->
<!--                                                            v-if="!errored && !loading && participantList.length >= 1"-->
<!--                                                            align="fill"-->
<!--                                                            v-model="currentPage"-->
<!--                                                            :total-rows="rows"-->
<!--                                                            :per-page="participantsPerPage"-->
<!--                                                    ></b-pagination>-->

                                            </b-card>













                                        </b-modal>
                                        <b-button v-if="activity.creatorUserId === activeUserId"
                                                  variant="outline-primary"
                                                  v-on:click="goToPage(`/activities/edit/${activity.id}`)">
                                            Edit
                                        </b-button>
                                        <b-button variant="outline-primary"
                                                  v-b-modal="'activity' + activity.id + '-continuous-modal'">
                                            Details
                                        </b-button>
                                        <b-button variant="outline-primary"
                                                  @click="followActivity(activity.id)"
                                                  v-if="activity.creatorUserId!==activeUserId"
                                        >
                                            Follow Activity
                                            <img v-if="isHovered" src="../../../assets/png/footsteps_icon.png" class="footStepsSimplified" alt="Footsteps Logo">
                                            <img v-else src="../../../assets/png/footsteps_icon_hollow.png" class="footStepsSimplified" alt="Footsteps Logo">
                                        </b-button>
                                        <b-button v-if="activity.creatorUserId === activeUserId"
                                                  variant="outline-danger"
                                                  v-on:click="deleteActivity(activity.id)">
                                            Delete
                                        </b-button>
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
                                    <strong>{{ activity.activity_name }} | {{ activity.creatorName }}</strong>
                                    <hr/>
                                    <strong>Start Date: </strong>{{ getDateTime(activity.start_time )}}
                                    <br/>
                                    <strong>End Date: </strong>{{ getDateTime(activity.end_time) }}
                                    <br/><br/>
                                    <div v-if="activity.description.length <= 100">
                                        {{ activity.description }}
                                    </div>
                                    <div v-else>
                                        {{ activity.description.substring(0,100)+"...." }}
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
                                                    <b-col>{{ activity.creatorName }}</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>Start bs Date: </strong></b-col>
                                                    <b-col>{{ getDateTime(activity.start_time) }}</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>End Date: </strong></b-col>
                                                    <b-col>{{ getDateTime(activity.end_time) }}</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>Duration: </strong></b-col>
                                                    <b-col>{{ getDays(activity) }} Days
                                                        {{ getHours(activity) }} Hours</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>Location: </strong></b-col>
                                                    <b-col>{{ activity.location }}</b-col>
                                                </b-row>
                                                <b-row class="mb-1">
                                                    <b-col><strong>Activity types: </strong></b-col>
                                                    <b-col>
                                                        <section v-for="activityType in activity.activity_type" v-bind:key="activityType.name">
                                                            <div>
                                                                {{ activityType.name }}
                                                            </div>
                                                        </section>
                                                    </b-col>
                                                </b-row>
                                            </b-card>
                                            <br>
                                            <b-card class="flex-fill" border-variant="secondary">
                                                <p class="text-justified">
                                                    <strong>Description: </strong><br>
                                                    {{ activity.description }}
                                                </p>
                                            </b-card>
                                            <br>


                                            <!--  Card for displaying the participants-->
                                            <b-card class="flex-fill" border-variant="secondary">

                                                    <strong>Participants: </strong><br>
                                                    <section v-for="participant in activityList[0].participants" :key="participant.firstname">
                                                        {{participant.firstname}} {{participant.lastname}}
                                                    </section>

                                            </b-card>



                                            <template v-slot:modal-footer v-if="activity.creatorUserId !== activeUserId">
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
                                        <b-button v-if="activity.creatorUserId === activeUserId"
                                                  variant="outline-primary"
                                                  v-on:click="goToPage(`/activities/edit/${activity.id}`)">
                                            Edit
                                        </b-button>
                                        <b-button variant="outline-primary"
                                                  v-b-modal="'activity' + activity.id + '-duration-modal'">
                                            Details
                                        </b-button>
                                        <b-button variant="outline-primary"
                                                  @click="followActivity(activity.id)"
                                                  v-if="activity.creatorUserId!==activeUserId"
                                        >
                                            Follow Activity
                                            <img v-if="isHovered" src="../../../assets/png/footsteps_icon.png" class="footStepsSimplified" alt="Footsteps Logo">
                                            <img v-else src="../../../assets/png/footsteps_icon_hollow.png" class="footStepsSimplified" alt="Footsteps Logo">
                                        </b-button>
                                        <b-button v-if="activity.creatorUserId === activeUserId"
                                                  variant="outline-danger"
                                                  v-on:click="deleteActivity(activity.id)">
                                            Delete
                                        </b-button>
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
                participantsPerPage: 5,
                currentPage: 1,
                currentPageParticipantList: [],
                participantList: [],


                activityList : [],
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
            //await this.fetchActivityTypes();
            // await this.fetchParticipants();
            await this.fetchParticipantsForActivities();
            await this.getCreatorNamesForActivities();
            //await this.getCreatorName();
        },
        //
        // watch: {
        //     /**
        //      * Watcher is called whenever currentPage is changed,
        //      * thanks to the pagination bar.
        //      */
        //     currentPage() {
        //         this.setCurrentPageParticipantList();
        //     }
        // },
        //
        // computed: {
        //     /**
        //      * Gets the total amount of participants (AKA no. of rows) for an activity
        //      */
        //     rows(){
        //         return this.participantList.length;
        //     }
        // },


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

            /**
             * Retrieves a list of activities
             * that a user is following or has created
             */
            async getListOfActivities() {
                this.errored = false;
                await api.getUserActivities(this.userId).then(
                    response => { //If successfully registered the response will have a status of 201
                        if (response.data.length === 0) {
                            this.noMore = true;
                        }
                        console.log(response.data)
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

            /**
             * Iterates over list of activities
             * and obtains name of creator of an activity.
             * Then manually assigns a "creatorName" property to each activity.
             */
            async fetchParticipantsForActivities() {
                console.log('activityList = ' + this.activityList)
                console.log('activityList length = ' + this.activityList.length)
                for (let i = 0; i < this.activityList.length; i++) {
                    await api.getParticipants(this.activityList[i].id).then(response => {
                        let participants = [];
                        for (let j = 0; j < response.data.length; j++) {
                            participants.push(response.data[j]);
                            // console.log('response.data[i]' + response.data[i])
                            // console.log(participants)
                        }
                        this.activityList[i].participants = participants;
                        //console.log(this.activityList[i]);
                        //console.log('here' + this.activityList[i][participants])
                        // console.log('we have success, response is:  ' + response.data);
                        //TODO: Parse the response properly (and then return list of participants)
                        //console.log(participants)
                        //this.participantList = response.data.map( participant => participant['user_id']);
                        // this.participantList.sort(function (a, b) {
                        //     return a.toLowerCase().localeCompare(b.toLowerCase());
                        // });
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
                }
            },

            /**
             * Iterates over list of activities
             * and obtains name of creator of an activity.
             * Then manually assigns a "creatorName" property to each activity.
             */
            async getCreatorNamesForActivities() {
                for (let i = 0; i < this.activityList.length; i++) {
                    await api.getUserData(this.activityList[i].creatorUserId).then((response) => {
                        this.activityList[i]["creatorName"] = `${response.data.firstname} ${response.data.lastname}`;
                    }).catch(() => {
                        this.activityList[i]["creatorName"] = "Could not load creator's name";
                    });
                }
                this.loading = false;
            },



            // /**
            //  * Calculate the participants to be displayed from the current page number.
            //  * This function is called when the pagination bar is altered,
            //  * changing the currentPage variable.
            //  */
            // setCurrentPageParticipantList() {
            //     let leftIndex = (this.currentPage - 1) * this.participantsPerPage;
            //     let rightIndex = leftIndex + this.participantsPerPage;
            //     if (rightIndex > this.participantList.length) {
            //         rightIndex = this.participantList.length;
            //     }
            //     this.currentPageParticipantList = this.participantList.slice(leftIndex, rightIndex);
            //     window.scrollTo(0,0);
            //
            // },
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