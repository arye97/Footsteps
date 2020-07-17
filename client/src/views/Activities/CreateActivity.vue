<template>
    <div>
        <Header />
        <h1><br/><br/></h1>
        <header class="masthead">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light">Welcome to Hakinakina</h1>
                        <p class="lead">Plan your route with the best</p>
                    </div>
                </div>
            </div>
        </header>
        <activity-form :submit-activity-func="submitCreateActivity" :activity="activity"></activity-form>
    </div>
</template>

<script>
    import Header from '../../components/Header/Header.vue'
    import server from "../../Api";
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
            this.activity.profileId = await this.getUserId();
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
                    location: this.activity.location
                };
                // If this Activity is continuous, add a start/end time to the activityForm
                if (!this.activity.continuous) {
                    // If no time provided, manually concatenating Thh:mm, which is bad, might use Moment.js instead but will consult team
                    let formattedStartTime = this.activity.startTime;
                    let formattedEndTime = this.activity.endTime;
                    if (this.activity.startTime.length === 10) {
                        formattedStartTime = formattedStartTime.concat('T23:59')
                    }
                    if (this.activity.endTime.length === 10) {
                        formattedEndTime = formattedEndTime.concat('T23:59')
                    }
                    activityForm["start_time"] = formattedStartTime.concat(':00+1300');
                    activityForm["end_time"] = formattedEndTime.concat(':00+1300');
                }

                // Send the activityForm to the server to create a new activity
                await server.post(`/profiles/${this.activity.profileId}/activities`,
                    activityForm, {
                        headers: {
                            "Access-Control-Allow-Origin": "*",
                            "Content-Type": "application/json",
                            "Token": sessionStorage.getItem("token")
                        },
                        withCredentials: true,
                    }).then(response => { // If successfully registered the response will have a status of 201
                        if (response.status === 201) {
                            this.$router.push("/activities");
                            // somehow can't get back to profile
                            // this.$router.push('/profile');
                        }
                    }
                )
            },

            /**
             * Get the Id of the current Logged in user.
             * @returns {Promise<*>}
             */
            async getUserId() {
                let userId = null;
                await server.get(`profiles/userId`,
                    {
                        headers: {
                            "Access-Control-Allow-Origin": "*",
                            "Content-Type": "application/json",
                            "Token": sessionStorage.getItem("token")
                        },
                        withCredentials: true
                    }
                ).then(response => {
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