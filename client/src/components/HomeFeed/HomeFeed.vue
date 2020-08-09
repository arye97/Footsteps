<template>
    <div id="app">
        <section v-if="errored" class="text-center">
            <div class="alert alert-danger alert-dismissible fade show text-center" role="alert" id="alert">
                {{ error_message }}
            </div>
        </section>
        <section v-else-if="loading" class="text-center">
            <br/>
            <b-spinner variant="primary" label="Spinning"></b-spinner>
            <br/>
            <br/>
        </section>
        <section v-else v-for="event in this.currentPageEventList" :key="event.id">
            <!-- Feed Event List -->
            <feed-card v-bind:event="event"/>
            <br>
        </section>
        <!-- Pagination Nav Bar -->
        <b-pagination
                v-if="!errored && !loading && feedEventList.length >= eventsPerPage"
                align="fill"
                v-model="currentPage"
                :total-rows="rows"
                :per-page="eventsPerPage"
        ></b-pagination>
    </div>
</template>

<script>
    import FeedCard from "./FeedCard";
    export default {
        name: "HomeFeed",
        components: {
            FeedCard
        },
        data: function() {
            return {
                errored: false,
                loading: false,
                feedEventList: [],
                currentPage: 1,
                eventsPerPage: 5,
                currentPageEventList: []
            }
        },
        computed: {
            rows() {
                return this.feedEventList.length;
            }
        },
        mounted() {
            //TODO - Replace this with an api call when endpoints are generated
            this.feedEventList = [
                {
                    id: 1,
                    feedEventType: 'FOLLOW',
                    timeStamp: new Date("August 07, 2020 17:00:00"),
                    activityId: 145,
                    userId: 1
                },
                {
                    id: 2,
                    feedEventType: 'MODIFY',
                    timeStamp: new Date("August 09, 2020 15af:50:30"),
                    activityId: 113,
                    userId: 113
                },
                {
                    id: 3,
                    feedEventType: 'DELETE',
                    timeStamp: new Date(),
                    activityId: 72,
                    userId: 54
                }
            ];
            // Flip the list order so the most recent event shows first
            this.feedEventList.reverse();
            // Update what is shown on this page of the pagination
            this.currentPageEventList = this.feedEventList;
        }
    }
</script>