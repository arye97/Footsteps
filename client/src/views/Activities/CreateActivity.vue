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
                            <h1 class="font-weight-light">Create an Activity</h1>
                            <p class="lead">Plan your route with the best</p>
                            <hr>
                        </div>
                    </div>
                </div>
            </header>
            <activity-form :submit-activity-func="submitCreateActivity" :activity="activity" :outcome-list="outcomeList"/>
        </b-container>
        <br/><br/>
    </div>
</template>

<script>
    import Header from '../../components/Header/Header.vue'
    import api from "../../Api";
    import ActivityForm from "../../components/Activities/ActivityForm.vue";
    import { UnitType } from "../../util";

    /**
     * A view used to create an activity
     */
    export default {
        components: { Header, ActivityForm },
        name: "CreateActivity",
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
                    startTime: null,
                    endTime: null,
                    location: null
                },
                outcomeList: []
            }
        },
        async mounted() {
            // If not logged in
            if (!sessionStorage.getItem("token")) {
                this.$router.push('/login'); //Routes to home on logout
            }
            let userId = await this.getUserId();
            this.activity.profileId = userId;
        },
        methods: {

            /**
             * Makes a POST request to the back-end to create an activity
             */
            async submitCreateActivity() {
                let activityId;
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

                // Send the activityForm to the server to create a new activity, and get it's id
                await api.createActivity(activityForm, this.activity.profileId).then(response => { // If successfully registered the response will have a status of 201
                    activityId = response.data;
                }).catch(error => {this.throwError(error, false)});

                // Send the outcomes to the server.  Adds the activityId to the outcomes.
                await this.createAllOutcomes(this.outcomeList, activityId).then(() => {
                    this.$router.push({name: 'allActivities', params: {alertMessage: 'Activity added successfully', alertCount: 5}});
                });
            },

            /**
             * Sends all Outcomes to the backend.  Should be used when submitting an Activity.
             * @param newOutcomes Array of outcomes to save to the database
             * @param activityId the id of the associated activity, added to each Outcome
             */
            async createAllOutcomes(newOutcomes, activityId) {
                let outcomes = [...newOutcomes];  // Convert to an array (if needed)

                for (let i=0, outcome; i < outcomes.length; i++) {  // Seems to need this type of loop for some reason
                    outcome = outcomes[i];

                    const outcomeRequest = {
                        activity_id: isNaN(outcome.activity_id) ? activityId : outcome.activity_id,
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
             * @param isGet boolean value if the error is from a get endpoint, true if it is, false if it isnt, so that
             * the function will use the necessary if statements to handle these
             */
            throwError(servError, isGet) {
                if (!isGet) {
                    switch (servError.response.status) {
                        case 401:
                            sessionStorage.clear();
                            this.$router.push("/login");
                            break;
                        case 400:
                            throw new Error("Entered activity field(s) are invalid");
                        case 403:
                            throw new Error("Sorry unable to create this activity (forbidden access)");
                        default:
                            throw new Error("Unknown error has occurred whilst creating this activity");
                    }
                } else if (isGet) {
                    switch (servError.response.status) {
                        case 401:
                            sessionStorage.clear();
                            this.$router.push("/login");
                            break;
                        default:
                            this.$router.push({name: 'myProfile'});
                    }
                }
            }
        },
    }

</script>

<style scoped>
    footer {
        padding-top: 55px;
    }

    .word-count {
        padding-top: 7px;
        color: #707070;
        font-size: 0.8em;
    }

    .checkbox-time {
        padding-top: 10px;
    }
</style>