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
        <b-tabs content-class="mt-4" justified>
            <b-tab title="Continous" :active="continuousIsActive(true)">
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
            <b-tab title="Non-Continous" :active="continuousIsActive(false)">
                <section v-for="activity in this.activityList" :key="activity.id">
                    <br/>
                    <!-- Activity List -->
                    <b-card v-if="!activity.continuous">
                            <b-row no-gutters>
                                <b-col md="6">
                                    <b-card-text>
                                        Name: {{activity.activity_name}} <br/><br/>
                                        Description: {{activity.description}} <br/><br/>
                                        Start Date: {{new Date(activity.start_time).toDateString()}} <br/><br/>
                                        End Date: {{new Date(activity.end_time).toDateString()}} <br/><br/>
                                        Duration: {{Math.floor(((new Date(activity.end_time) - new Date(activity.start_time))/1000/60/60/24))}} Days
                                        {{Math.floor(((new Date(activity.end_time) - new Date(activity.start_time))/1000/60/60/365))}} Hours
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
    export default {
        name: "AllActivities",
        components : {
            Header
        },
        data() {
            return {
                activityList : [],
                userId : null,
                noMore: false,
                activeTab: 0
            }
        },
        async mounted() {
            await this.getUserId();
            this.checkLoggedIn();
            await this.getListOfActivities();
        },
        methods: {
            checkLoggedIn() {
                if (!sessionStorage.getItem("token")) {
                    this.$router.push("/login");
                }
            },
            async getUserId() {
                await api.getUserId().then(response => {
                    this.userId = response.data;
                }).catch(error => {
                    console.error(error);
                })
            },
            async getListOfActivities() {
                await api.getUserActivities(this.userId).then(response => { //If successfully registered the response will have a status of 201

                        if (response.data.length === 0) {
                            this.noMore = true;
                        }
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