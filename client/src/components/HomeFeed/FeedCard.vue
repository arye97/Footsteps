<template>
    <div>
        <b-card border-variant="secondary" style="background-color: #f3f3f3">
            <b-row class="mb-1">
                <b-col>
                    <b-card-text id="headers"><strong>{{firstName}} {{lastName}} {{actionText}} the activity {{activityTitle}}</strong></b-card-text>
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
</template>

<script>

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
                activityTitle: ""
            }
        }
    }
</script>