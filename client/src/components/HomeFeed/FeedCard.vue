<template>
    <div v-if="!errored">
        <b-card border-variant="secondary" style="background-color: #f3f3f3">
            <b-row no-gutters>
                <b-col col="12">
                    <!-- Time Stamp -->
                    <b-card-text id="time">{{event.timeStamp}}</b-card-text>
                </b-col>
            </b-row>
            <b-row class="mb-1">
                <b-col>
                    <b-card-text id="text"><strong>{{firstName}} {{lastName}} {{actionText}} the activity {{activityTitle}}</strong></b-card-text>
                </b-col>
            </b-row>
            <hr style="border-color: inherit">
            <b-row class="mb-1">
                <b-col>
                    <!-- View Activity button -->
                    <b-button id="viewActivityButton" style="float: right" variant="primary">View Activity</b-button>
                </b-col>
                <b-col v-if="event.feedEventType != 'DELETE'">
                    <!-- Unfollow button -->
                    <b-button id="unfollowButton" style="float: right" variant="primary">Unfollow Activity</b-button>
                </b-col>
            </b-row>
        </b-card>
    </div>
    <div v-else>
        <b-card-text id="error">An error occurred, please try again later</b-card-text>
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
                errored: false
            }
        },
        async mounted () {
            await this.extractData();
        },
        methods: {
            extractData: async function() {
                await api.getUserData(this.event.userId).then((response) => {
                    this.firstName = response.data.firstname;
                    this.lastName = response.data.lastname;
                }).catch(() => {
                    this.errored = true;
                });
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
                await api.getActivityData(this.event.activityId).then((response) => {
                    this.activityTitle = response.data.title;
                }).catch(() => {
                    this.errored = true;
                })
            }
        }
    }
</script>