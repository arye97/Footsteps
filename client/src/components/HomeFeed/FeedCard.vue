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
                    <!--Display You subscribed etc. if you initiated the event-->
                    <b-card-text v-if="event.userId === viewerId && !isNaN(viewerId)"
                                 id="description"><strong>You {{actionText}} the activity {{event.activityName}}</strong></b-card-text>
                    <b-card-text v-else id="description"><strong>{{firstName}} {{lastName}} {{actionText}} the activity {{event.activityName}}</strong></b-card-text>
                </b-col>
            </b-row>
            <!-- Buttons and separator not required for delete events -->
            <hr style="border-color: inherit" v-if="event.feedEventType !== 'DELETE'">
            <b-row class="mb-1"  v-if="event.feedEventType !== 'DELETE'">
                <b-col>
                    <!-- View Activity button -->
                    <b-button @click="goToViewActivityPage" block id="viewActivityButton" variant="success">View Activity</b-button>
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
                userId: Number,
                activityName: String,
                outcomeTitle: String
            },
            viewerId: {
                default: null,
                type: Number,
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
            await this.extractData();
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
                const vowelRegex = '^[aieouAIEOU].*';
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
                    case 'ADD_RESULT':
                        if (this.event.outcomeTitle.match(vowelRegex)) {  // Use 'an' or 'a'?
                            this.actionText = 'added an "' + this.event.outcomeTitle + '" result to';
                        } else {
                            this.actionText = 'added a "' + this.event.outcomeTitle + '" result to';
                        }
                        break;
                    default:
                        this.errored = true;
                }
                // Convert the time of event to a useful display time
                const timestamp = new Date(this.event.timeStamp);
                this.time = (new Date().getTime()) - (timestamp.getTime());
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
            },
            /**
             * Redirect to the given activities page.
             */
            goToViewActivityPage() {
                this.$router.push({name: 'viewActivity', params: {activityId: this.event.activityId}});
            }
        }
    }
</script>