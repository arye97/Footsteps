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
        <activity-form :submit-activity-func="submitEditActivity" :activity="activity"></activity-form>
    </div>
</template>

<script>
    import Header from '../../components/Header/Header.vue'
    import server from "../../Api";
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
                    startTime: null,
                    endTime: null,
                    location: null
                },

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

            // Inserts the recieved activity data into the activity object
            await this.getActivityData(activityId);
        },


        methods: {
            submitEditActivity() {
                console.log("Simulating Edit activity")
                console.log(this.activity)
                // ToDo Similar to CreateActivity.vue, create a activityForm obj, but send put request
            },

            //Getting the data from the selected activity to update
            async getActivityData(activityId) {
                await server.get(`activities/${activityId}`,
                    {headers: {'Content-Type' : 'application/json', 'Token' : sessionStorage.getItem('token')}
                    }
                ).then(response => {
                    this.activity.activityName = response.data.activity_name;
                    this.activity.continuous = (response.data.continuous === true);
                    this.activity.description = response.data.description;
                    this.activity.location = response.data.location;
                    this.activity.startTime = response.data.start_time;
                    this.activity.endTime = response.data.end_time;
                    for (let i = 0; i < response.data.activity_type.length; i++) {
                        this.activity.selectedActivityTypes.push(response.data.activity_type[i].name);
                    }
                    //need to also add in the activities activity types
                }).catch(error => {
                    this.processGetError(error);
                })
            },

            processGetError(error) {
                // ToDo this should probably be handled better
                console.error(error);
            }
        }
    }

</script>

<style scoped>
    footer {
        padding-top: 55px;
    }
</style>