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
        <div style="text-align: center">
            <b-button style="margin-bottom: 1.7em; margin-top: 0.8em" variant="primary" v-on:click="goToPage('/activities/create')">Create New Activity</b-button>
        </div>
        <div v-if="this.loading" style="text-align: center">
            <b-spinner class="margin-bottom: 1.7em; margin-top: 0.8em" variant="primary" label="Spinning"></b-spinner>
        </div>
        <b-tabs v-else content-class="mt-4" justified>
            <b-tab title="Continuous" :active="continuousIsActive(true)">
                <section v-for="activity in this.activityList" :key="activity.id">
                    <!-- Activity List -->
                    <b-card v-if="activity.continuous">
                        <b-row no-gutters>
                            <b-col md="6">
                                <b-card-text>
                                    <br/>
                                    Name: {{activity.activity_name}}
                                    <br/><br/>
                                    Description: {{activity.description}}
                                    <br/><br/>
                                    Creator: {{creatorName}}
                                </b-card-text>
                            </b-col>
                            <b-col md="6">
                                <b-card-body class="cardButtons">
                                    <b-button variant="outline-primary" v-on:click="goToPage(`/activities/edit/${activity.id}`)">Edit</b-button>
                                </b-card-body>
                                <b-card-body class="cardButtons">
                                    <b-button variant="outline-primary" v-on:click="goToPage(`/activities/${activity.id}`)">Details</b-button>
                                </b-card-body>
                                <b-card-body class="cardButtons">
                                    <b-button variant="outline-danger" v-on:click="deleteActivity(activity.id)">Delete</b-button>
                                </b-card-body>
                            </b-col>
                        </b-row>
                    </b-card>
                </section>
                <hr/>
                <footer class="noMore">No more activities to show</footer>
            </b-tab>
            <b-tab title="Non-Continuous" :active="continuousIsActive(false)">
                <section v-for="activity in this.activityList" :key="activity.id">
                    <br/>
                    <!-- Activity List -->
                    <b-card v-if="!activity.continuous">
                            <b-row no-gutters>
                                <b-col md="6">
                                    <b-card-text>
                                        Name: {{activity.activity_name}} <br/><br/>
                                        Description: {{activity.description}} <br/><br/>
                                        Start Date: {{getDateTime(activity.start_time)}} <br/><br/>
                                        End Date: {{getDateTime(activity.end_time)}} <br/><br/>
                                        Duration: {{getDays(activity)}} Days
                                        {{getHours(activity)}} Hours <br/><br/>
                                        Creator: {{creatorName}}
                                    </b-card-text>
                                </b-col>
                                <b-col md="6">
                                    <b-card-body class="cardButtons">
                                        <b-button variant="outline-primary" v-on:click="goToPage(`/activities/edit/${activity.id}`)">Edit</b-button>
                                    </b-card-body>
                                    <b-card-body class="cardButtons">
                                        <b-button variant="outline-primary" v-on:click="goToPage(`/activities/${activity.id}`)">Details</b-button>
                                    </b-card-body>
                                    <b-card-body class="cardButtons">
                                        <b-button variant="outline-danger" v-on:click="deleteActivity(activity.id)">Delete</b-button>
                                    </b-card-body>
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
    </div>

</template>

<script>
    import Header from '../../components/Header/Header.vue'
    import api from "../../Api";
    import { formatDateTime } from "../../util";
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
                loading: true
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
                    this.creatorId = this.userId;
                });
                return userId
            },
            async getListOfActivities() {
                await api.getUserActivities(this.userId)
                    .then(response => { //If successfully registered the response will have a status of 201
                        if (response.data.length === 0) {
                            this.noMore = true;
                        }
                        this.loading = false;
                        this.activityList = response.data;
                    }).catch(error => {
                        console.error(error);
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
                let confirmDeleteActivity = false;

                // Open dialog box
                await this.$bvModal.msgBoxConfirm("Are you sure you want to delete this Activity?")
                    .then(value => {
                        confirmDeleteActivity = value
                    })
                    .catch(err => {
                        console.error(err)
                    });
                if (!confirmDeleteActivity) {
                    return;
                }

                // Delete from local memory
                this.activityList.splice(this.activityList.findIndex(a => a.id === activityId), 1);

                // Delete from database
                await api.deleteActivity(this.userId, activityId).catch(error => {
                    console.error(error);
                })
            },
          async getCreatorName() {
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
    .noMore {
        text-align: center;

    }
    .cardButtons {
        display: flex;
        align-items: center;
        justify-content: center;
    }
</style>