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
            <!-- User List -->
            <feed-card v-bind:event="event"/>
            <br>
        </section>
        <!-- Pagination Nav Bar -->
        <b-pagination
                v-if="!errored && !loading && feedEventList.length >= 1"
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
                eventsPerPage:10,
                currentPageEventList: []
            }
        },
        computed: {
            rows() {
                return this.feedEventList.length;
            }
        }
    }
</script>