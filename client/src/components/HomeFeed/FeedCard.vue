<template>
    <div v-if="!errored">
        <b-card border-variant="secondary" style="background-color: #f3f3f3">
            <b-row no-gutters>
                <b-col class="h-25">
                    <!-- Time Stamp -->
                    <b-card-text id="time" class="text-secondary"  style="float: right"><small>{{this.time}}</small></b-card-text>
                </b-col>
            </b-row>
            <b-row class="mb-1">
                <!-- Core Card Text -->
                <b-col>
                    <b-card-text id="description"><strong>{{firstName}} {{lastName}} {{actionText}} the activity {{activityTitle}}</strong></b-card-text>
                </b-col>
            </b-row>
            <!-- Buttons and seperator not required for delete events -->
            <hr style="border-color: inherit" v-if="event.feedEventType != 'DELETE'">
            <b-row class="mb-1"  v-if="event.feedEventType != 'DELETE'">
                <b-col>
                    <!-- View Activity button -->
                    <b-button id="viewActivityButton" variant="success">View Activity</b-button>
                </b-col>
                <b-col>
                    <!-- Unfollow button -->
                    <b-button id="unfollowButton" variant="outline-success">
                        <img id="follow" src="../../../assets/png/footsteps_icon_hollow.png" width="15%" alt="Footsteps">
                        Unfollow Activity
                    </b-button>
                </b-col>
            </b-row>
        </b-card>
    </div>
    <div v-else>
        <b-card border-variant="secondary" style="background-color: #f3f3f3">
            <b-card-text id="error">An error occurred, please try again later</b-card-text>
        </b-card>
    </div>
</template>

<script>
    import api from "../../Api";

    export default {
        name: "FeedCard",
        props: {
            event: {
                id: Number,
                feedEventType: String,
                timeStamp: Date,
                activityId: Number,
                userId: Number
            }
        },
        data: function() {
            return {
                firstName: "",
                lastName: "",
                actionText: "",
                activityTitle: "",
                time: null,
                errored: false
            }
        },
        async mounted () {
            this.extractData();
        },
        methods: {
            /**
             * This method converts the event data into what we need to display for the feed events
             * Async as needs to make some api calls to extract some of the data from IDs
             * @returns {Promise<void>}
             */
            extractData: async function() {
                // Get the details of the person who triggered the event
                await api.getUserData(this.event.userId).then((response) => {
                    this.firstName = response.data.firstname;
                    this.lastName = response.data.lastname;
                }).catch(() => {
                    this.errored = true;
                });
                // Determine what type of event occurred
                switch (this.event.feedEventType) {
                    case 'DELETE':
                        this.actionText = "deleted";
                        break;
                    case 'MODIFY':
                        this.actionText = "modified";
                        break;
                    case 'UNFOLLOW':
                        this.actionText = "unfollowed";
                        break;
                    case 'FOLLOW':
                        this.actionText = "followed";
                        break;
                    default:
                        this.errored = true;
                }
                // Get the name of the activity affected
                await api.getActivityData(this.event.activityId).then((response) => {
                    this.activityTitle = response.data.activity_name;
                }).catch(() => {
                    this.errored = true;
                });
                // Convert the time of event to a useful display time
                this.time = (new Date().getTime()) - (this.event.timeStamp.getTime());
                if (this.time >= 86400000) {
                    this.time = Math.round(this.time / (1000 * 60 * 60 * 24)) + ' days ago';
                } else if (this.time >= 3600000) {
                    this.time = Math.round(this.time / (1000 * 60 * 60)) + ' hours ago';
                } else if (this.time >= 60000) {
                    this.time = Math.round(this.time / (1000 * 60)) + ' minutes ago';
                } else if (this.time >= 10000) {
                    this.time = Math.round(this.time / 1000) + ' seconds ago';
                } else {
                    this.time = "Just now";
                }
            }
        }
    }
</script>