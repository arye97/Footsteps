<template>
    <div>
        <Header />
        <h1><br/><br/></h1>
        <b-container class="contents" fluid>
            <h1><br/></h1>
            <header class="masthead">
                <div class="container h-100">
                    <div class="row h-100 align-items-center">
                        <div class="col-12 text-center">
                            <h1 class="font-weight-light">Edit your activity</h1>
                            <p class="lead">Plan your route with the best</p>
                            <hr>
                        </div>
                    </div>
                </div>
            </header>
            <activity-form :submit-activity-func="submitEditActivity"
                           :activity="activity"
                           :outcome-list="outcomeList"
                           :original-outcome-list="originalOutcomeList"/>
        </b-container>
        <br/><br/>
    </div>
</template>

<script>
    import Header from '../../components/Header/Header.vue'
    import api from "../../Api";
    import ActivityForm from "../../components/Activities/ActivityForm";
    import {backendDateToLocalTimeZone, UnitType} from "../../util";

    /**
     * A view used to edit an activity
     */
    export default {
        components: {ActivityForm, Header },
        name: "EditActivity",
        data() {
            return {
                activity: {
                    profileId: null,
                    activityName: null,
                    description: null,
                    selectedActivityTypes: [],
                    continuous: true,
                    submitStartTime: null,
                    submitEndTime: null,
                    location: null,
                    startTime: null,
                    endTime: null,
                },
                activityId: null,
                show: true,
                outcomeList: [],
                originalOutcomeList: [],
            }
        },
        async created() {
            // If not logged in
            if (!sessionStorage.getItem("token")) {
                this.$router.push('/login'); //Routes to home on logout
            }

            // Get the activityId out of the URL
            let url = window.location.pathname;
            let activityId = url.substring(url.lastIndexOf('/') + 1);
            this.activityId = url.substring(url.lastIndexOf('/') + 1);

            // Inserts the received activity data into the activity object
            await this.getActivityData(activityId);
            this.dateFormatToString();
            // Potential problem later with Admins, will always assume your the creator
            this.activity.profileId = await this.getUserId();
        },


        methods: {

            /**
             * Makes a PUT request to the back-end to edit an activity
             */
            async submitEditActivity() {
                // Create an activity object to be sent as a json to the server
                const activityForm = {
                    activity_name: this.activity.activityName,
                    description: this.activity.description,
                    activity_type: this.activity.selectedActivityTypes,
                    continuous: this.activity.continuous,
                    location: this.activity.location,
                    start_time: this.activity.submitStartTime,
                    end_time: this.activity.submitEndTime
                };

                // Send the activityForm to the server to edit the activity
                await api.updateActivity(activityForm, this.activity.profileId, this.activityId)
                  .catch(error => {this.throwError(error, false)});

                await this.editAllOutcomes(this.outcomeList, this.originalOutcomeList, this.activityId);

                this.$router.push({name: 'allActivities', params: {alertMessage: 'Activity modified successfully', alertCount: 5}});
            },


            /**
             * Sends all new Outcomes to the backend.  Doesn't re-send outcomes that already exist.  Should be used
             * when submitting an Activity.  Only adds new outcomes, doesn't delete or modify existing ones.
             * In a future story this will likely be modified to support edit and delete.
             * @param editedOutcomes Array of outcomes belonging to the Activity after editing
             * @param originalOutcomes Array of outcomes from backend before editing.  Used to prevent duplicates being created.
             * @param activityId id of newly created activities
             */
            async editAllOutcomes(editedOutcomes, originalOutcomes, activityId) {

                // Create an Array containing only new outcomes (like set difference)
                let outcomes = [...editedOutcomes].filter(o => ![...originalOutcomes].includes(o));

                for (let i=0, outcome; i < outcomes.length; i++) {  // Seems to need this type of loop for some reason
                    outcome = outcomes[i];

                    const outcomeRequest = {
                        activity_id: activityId,
                        title: outcome.title,
                        unit_name: outcome.unit_name,
                        unit_type: Object.keys(UnitType).includes(outcome.unit_type) ? outcome.unit_type : UnitType.TEXT,
                    };

                    await api.createOutcome(outcomeRequest).catch(serverError => {
                        this.throwError(serverError, false);
                    });
                }

            },


            /**
             * Get OK if the user can edit the given activity. Otherwise redirect to AllActivities.vue
             */
            async isActivityEditable(activityId) {
                await api.isActivityEditable(activityId).catch(error => {this.throwError(error, true)});
            },

            /**
             * Get the data of the selected activity and outcomes from the backend.  Load it into the activity object
             * and outcomeList array.
             * @param activityId id if the current activity
             */
            async getActivityData(activityId) {
                await this.isActivityEditable(activityId);
                await api.getActivityData(activityId).then(response => {
                    this.activity.activityName = response.data.activity_name;
                    this.activity.profileId = response.data.creatorUserId;
                    this.activity.continuous = (response.data.continuous === true);
                    this.activity.description = response.data.description;
                    this.activity.location = response.data.location;
                    this.activity.submitStartTime = backendDateToLocalTimeZone(response.data.start_time);
                    this.activity.submitEndTime = backendDateToLocalTimeZone(response.data.end_time);
                    for (let i = 0; i < response.data.activity_type.length; i++) {
                        this.activity.selectedActivityTypes.push(response.data.activity_type[i].name);
                    }
                }).catch(error => this.throwError(error, true));

                await api.getActivityOutcomes(activityId).then(response => {
                    this.originalOutcomeList = [...response.data];
                    this.outcomeList = [...response.data];
                }).catch(error => this.throwError(error, true));
            },

            /**
             * Convert the date received from backend into frontend date string
             */
            dateFormatToString() {
                if (!this.activity.continuous) {
                    this.activity.submitStartTime = this.activity.submitStartTime.substring(0, 16);
                    this.activity.submitEndTime = this.activity.submitEndTime.substring(0, 16);
                } else {
                    // Make it null, that way it shows as blank if activity is given a duration
                    this.activity.submitStartTime = null;
                    this.activity.submitEndTime = null;
                }
                this.activity.startTime = this.activity.submitStartTime;
                this.activity.endTime = this.activity.submitEndTime;
            },

            /**
             * Get the Id of the current Logged in user.
             * @returns {Promise<*>}
             */
            async getUserId() {
                let userId = null;
                await api.getUserId().then(response => {
                    userId = response.data;
                }).catch(error => {this.throwError(error, true)});
                return userId
            },

            /**
             * Helper function for when errors are thrown by server after hitting an endpoint,
             * @param servError Error thrown by server endpoint
             * @param isGet boolean value if the error is from a get endpoint, true if it is, false if it isn't, so that
             * the function will use the necessary if statements to handle these
             */
             throwError(servError, isGet) {
                 if (!isGet) {
                    switch (servError.response.status) {
                        case 401:
                            sessionStorage.clear();
                            this.$router.push("/login");
                            break;
                        case 404:
                            throw new Error("Activity not found");
                        case 403:
                            throw new Error("Sorry unable to edit this activity (forbidden access)");
                        default:
                            throw new Error("Unknown error has occurred whilst editing this activity");
                    }
                } else if (isGet) {
                    switch (servError.response.status) {
                        case 401:
                            sessionStorage.clear();
                            this.$router.push("/login");
                            break;
                        case 403:
                            this.$router.push({name: 'allActivities'});
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
    footer {
        padding-top: 55px;
    }
</style>