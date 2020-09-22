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
                           :original-outcome-list="originalOutcomeList"
                           :is-edit="true"
                           @add-outcome="addOutcome"
                           @delete-outcome="deleteOutcome"/>
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
                    startDate: null,
                    startTime: null,
                    endDate: null,
                    endTime: null,
                },
                defaultTime: "12:00",
                activityId: null,
                show: true,
                outcomeList: [],
                originalOutcomeList: [],
            }
        },
        async created() {
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
             * Adds an outcome to outcomeList
             * to prevent prop mutation.
             * @param outcomeToBeAdded Outcome to be added to list
             */
            addOutcome(outcomeToBeAdded) {
                this.outcomeList.push(outcomeToBeAdded);
            },
            /**
             * Removes a specified outcome from outcomeList
             * @param outcomeToBeRemoved Outcome to be removed from list
             */
            deleteOutcome(outcomeToBeRemoved) {
                this.outcomeList = this.outcomeList.filter(
                    function(outcome) {
                        return outcome !== outcomeToBeRemoved;
                    }
                );
            },

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
             * @param currentOutcomes Array of outcomes belonging to the Activity after editing
             * @param originalOutcomes Array of outcomes from backend before editing.  Used to prevent duplicates being created.
             * @param activityId id of newly created activities
             */
            async editAllOutcomes(currentOutcomes, originalOutcomes, activityId) {
                let deletedOutcomes = [], createdOutcomes = [], editedOutcomes = [];
                for (let i = 0; i < currentOutcomes.length; i++) {
                    if (currentOutcomes[i].isEdited === true && currentOutcomes[i].outcome_id !== undefined) {
                        editedOutcomes.push(currentOutcomes[i]); // Outcome was edited
                    } else if (!originalOutcomes.includes(currentOutcomes[i])) {
                        createdOutcomes.push(currentOutcomes[i]); // Outcome was just created
                    }
                }

                for (let i = 0; i < originalOutcomes.length; i++) {
                    if (!currentOutcomes.includes(originalOutcomes[i])) {
                        deletedOutcomes.push(originalOutcomes[i]);
                    }
                }

                // Created Outcomes
                for (let i = 0, outcome; i < createdOutcomes.length; i++) {  // Seems to need this type of loop for some reason
                    outcome = createdOutcomes[i];
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

                // Edited Outcomes
                for (let i = 0; i < editedOutcomes.length; i++) {
                    await api.updateOutcome(editedOutcomes[i]).catch(serverError => {
                        this.throwError(serverError, false);
                    });
                }

                // Deleted Outcomes
                for (let i = 0; i < deletedOutcomes.length; i++) {  // Seems to need this type of loop for some reason
                    await api.deleteOutcome(deletedOutcomes[i].outcome_id).catch(serverError => {
                        this.throwError(serverError, false);
                    });
                }

            },


            /**
             * Get OK if the user can edit the given activity. Otherwise redirect to AllActivities.vue
             */
            async isActivityEditable(activityId) {
                await api.isActivityEditable(activityId).catch(() => {
                    this.$router.push({name: 'allActivities', params: {alertMessage: 'Activity is not editable', alertCount: 5}});
                });
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
                }).catch((err) => {
                    this.throwError(err, true);
                });
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
                if (this.activity.submitStartTime) {
                    let startArray = this.activity.submitStartTime.split("T");
                    let endArray = this.activity.submitEndTime.split("T");
                    this.activity.startDate = startArray[0];
                    this.activity.startTime = startArray[1];
                    this.activity.endDate = endArray[0];
                    this.activity.endTime = endArray[1];
                } else {
                    this.activity.startTime = this.defaultTime;
                    this.activity.endTime = this.defaultTime;
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
                }).catch(() => {
                    this.logout();
                });
                return userId
            },

            /**
             * Logs the user out and clears session token
             */
            logout () {
                api.logout();
                sessionStorage.clear();
                this.isLoggedIn = (sessionStorage.getItem("token") !== null);
                this.$forceUpdate();
                this.$router.push('/login');
            },

            /**
             * Helper function for when errors are thrown by server after hitting an endpoint,
             * @param servError Error thrown by server endpoint
             * @param isGet boolean value if the error is from a get endpoint, true if it is, false if it isn't, so that
             * the function will use the necessary if statements to handle these
             */
             throwError(servError, isGet) {
                // Prevents "can't read status of undefined" error
                servError = 'response' in servError ? servError.response : servError;
                 if (!isGet) {
                    switch (servError.status) {
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
                    switch (servError.status) {
                        case 401:
                            sessionStorage.clear();
                            this.$router.push("/login");
                            break;
                        case 403:
                            this.$router.push({
                                name: 'allActivities',
                                params: {
                                    alertCount: 5,
                                    alertMessage: "Activity is not editable"
                                }
                            });
                            break;
                        default:
                            this.$router.push({
                                name: 'allActivities',
                                params: {
                                    alertCount: 5,
                                    alertMessage: "Can't get Activity data"
                                }
                            });
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