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
            <activity-form :submit-activity-func="submitCreateActivity" :activity="activity"></activity-form>
        </b-container>
        <br/><br/>
    </div>
</template>

<script>
    import Header from '../../components/Header/Header.vue'
    import api from "../../Api";
    import ActivityForm from "../../components/Activities/ActivityForm.vue";

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
                }
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

                // Send the activityForm to the server to create a new activity
                await api.createActivity(activityForm, this.activity.profileId)
                    .then(() => { // If successfully registered the response will have a status of 201
                        this.$router.push({name: 'allActivities', params: {alertMessage: 'Activity added successfully', alertCount: 5}});
                    }).catch(err => {
                        console.error(err);
                    })
            },

            /**
             * Get the Id of the current Logged in user.
             * @returns {Promise<*>}
             */
            async getUserId() {
                let userId = null;
                await api.getUserId().then(response => {
                    userId = response.data;
                });
                return userId
            }
        }
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