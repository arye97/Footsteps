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
                        </div>
                    </div>
                </div>
            </header>
            <activity-form :submit-activity-func="submitEditActivity" :activity="activity"></activity-form>
        </b-container>
        <br/><br/>
    </div>
</template>

<script>
    import Header from '../../components/Header/Header.vue'
    import api from "../../Api";
    import ActivityForm from "../../components/Activities/ActivityForm";

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

                // Send the activityForm to the server to create a new activity
                await api.updateActivity(activityForm, this.activity.profileId, this.activityId)
                  .then(response => { // If successfully registered the response will have a status of 201
                        if (response.status === 200) {
                            this.$router.push("/activities");
                            // somehow can't get back to profile
                            // this.$router.push('/profile');
                        }
                    }
                )
            },

            //Getting the data from the selected activity to update
            async getActivityData(activityId) {
                await api.getActivityData(activityId).then(response => {
                    this.activity.activityName = response.data.activity_name;
                    this.activity.profileId = response.data.creatorUserId;
                    this.activity.continuous = (response.data.continuous === true);
                    this.activity.description = response.data.description;
                    this.activity.location = response.data.location;
                    this.activity.submitStartTime = response.data.start_time;
                    this.activity.submitEndTime = response.data.end_time;
                    for (let i = 0; i < response.data.activity_type.length; i++) {
                        this.activity.selectedActivityTypes.push(response.data.activity_type[i].name);
                    }
                    //need to also add in the activities activity types
                }).catch(error => {
                    if (error.response.data.status === 401) {
                        this.$router.push('/login');
                    } else {
                        this.$router.push({ name: 'myProfile' });
                    }
                })
            },

            /**
             * Convert the date received from backend into frontend date string
             */
            dateFormatToString() {
                if (!this.activity.continuous) {
                    this.activity.submitStartTime = this.activity.submitStartTime.substring(0, 16);
                    this.activity.submitEndTime = this.activity.submitEndTime.substring(0, 16);
                } else {
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
</style>