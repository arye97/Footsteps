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
                <b-button style="margin-bottom: 1.7em; margin-top: 0.8em" variant="primary"
                          v-on:click="goToPage('/activities/create')">Create New Activity
                </b-button>
            </div>


            <activity-list/>
        </b-container>

    <br/><br/>
    </div>

</template>

<script>
    import Header from '../../components/Header/Header.vue';
    // import api from "../../Api";
    // import { formatDateTime } from "../../util";
    import ActivityList from "../../components/Activities/ActivityList";
    export default {
        name: "AllActivities",
        components : {
            ActivityList,
            Header
        },
    //     data() {
    //         return {
    //             activityList : [],
    //             userId : null,
    //             creatorId: null,
    //             creatorName: null,
    //             noMore: false,
    //             activeTab: 0,
    //             loading: true
    //         }
    //     },
    //     beforeMount() {
    //         this.checkLoggedIn();
    //     },
    //     async mounted() {
    //         this.userId = await this.getUserId();
    //         await this.getListOfActivities();
    //         await this.getCreatorName();
    //     },
    //     methods: {
    //         getDateTime: formatDateTime,
    //         getDays(activity) {
    //             return Math.floor(((new Date(activity.end_time) - new Date(activity.start_time))/1000/60/60/24))
    //         },
    //         getHours(activity) {
    //             return Math.ceil(((new Date(activity.end_time) - new Date(activity.start_time))/1000/60/60)) % 24
    //         },
    //         checkLoggedIn() {
    //             if (!sessionStorage.getItem("token")) {
    //                 this.$router.push("/login");
    //             }
    //         },
    //         /**
    //          * Get the Id of the current Logged in user.
    //          * @returns {Promise<*>}
    //          */
    //         async getUserId() {
    //             let userId = null;
    //             await api.getUserId().then(response => {
    //                 userId = response.data;
    //                 this.creatorId = userId;
    //             });
    //             return userId
    //         },
    //         async getListOfActivities() {
    //             await api.getUserActivities(this.userId)
    //                 .then(response => { //If successfully registered the response will have a status of 201
    //                     if (response.data.length === 0) {
    //                         this.noMore = true;
    //                     }
    //                     this.loading = false;
    //                     this.activityList = response.data;
    //                 }).catch(error => {
    //                     console.error(error);
    //             })
    //         },
    //         goToPage(url) {
    //             this.$router.push(url);
    //         },
    //         /**
    //          * When the page is loaded, make the tab active that has activities.  If the non-continuous tab has
    //          * activities, but the continuous tab doesn't, make the non-continuous tab active.
    //          * Else make continuous active.
    //          * @param isContinuousTab true if called by continuous tab, false of non-continuous tab.
    //          */
    //         continuousIsActive(isContinuousTab) {
    //             if (this.activityList.filter(a => !a.continuous).length > 0 && this.activityList.filter(a => a.continuous).length === 0) {
    //                 return !isContinuousTab;
    //             } else {
    //                 return isContinuousTab;
    //             }
    //         },
    //         /**
    //          * Opens a dialog box (modal) to confirm deletion.  If Ok is pressed, then it removes the activity from the
    //          * database.
    //          * @param activityId Id of the activity to delete
    //          * @returns {Promise<void>}
    //          */
    //         async deleteActivity(activityId) {
    //             let confirmDeleteActivity = false;
    //
    //             // Open dialog box
    //             await this.$bvModal.msgBoxConfirm("Are you sure you want to delete this Activity?")
    //                 .then(value => {
    //                     confirmDeleteActivity = value
    //                 })
    //                 .catch(err => {
    //                     console.error(err)
    //                 });
    //             if (!confirmDeleteActivity) {
    //                 return;
    //             }
    //
    //             // Delete from local memory
    //             this.activityList.splice(this.activityList.findIndex(a => a.id === activityId), 1);
    //
    //             // Delete from database
    //             await api.deleteActivity(this.userId, activityId).catch(error => {
    //                 console.error(error);
    //             })
    //         },
    //       async getCreatorName() {
    //           await api.getUserData(this.creatorId).then((response) => {
    //               this.creatorName = `${response.data.firstname} ${response.data.lastname}`;
    //           }).catch(() => {
    //               this.creatorName = "Could not load creator's name";
    //           });
    //       }
    //     }
    }
</script>

<style scoped>
    .noMore {
        text-align: center;

    }
    .text-justified {
        text-align: justify;
    }

    .activity-button-group {
        padding-top: 40px;
        padding-left: 70px;
    }

    .activity-button-group button {
        width: 200%;
    }
</style>