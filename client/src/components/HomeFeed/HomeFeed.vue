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
        <section v-else-if="this.rows > 0">
            <section v-for="event in this.currentPageEventList" :key="event.id">
                <!-- Feed Event List -->
                <feed-card v-bind:event="event" v-bind:viewer-id="userId"/>
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
        </section>
        <section v-else>
            <b-card border-variant="secondary" style="background-color: #f3f3f3">
                Could not find any events to display
            </b-card>
        </section>
    </div>
</template>

<script>
    import FeedCard from "./FeedCard";
    import api from '../../Api.js'
    export default {
        name: "HomeFeed",
        components: {
            FeedCard
        },
        data: function() {
            return {
                errored: false,
                loading: true,
                feedEventList: [],
                currentPage: 1,
                eventsPerPage: 5,
                currentPageEventList: [],
                error_message: "",
                userId: null
            }
        },
        computed: {
            rows() {
                return this.feedEventList.length;
            }
        },
        watch: {
            /**
             * Watcher is called whenever currentPage is changed, via reloading events
             * or using the pagination bar.
             */
            currentPage() {
                this.setCurrentPageEventList();
            }
        },
        async mounted() {
            this.userId = await this.getUserId();
            await api.getFeedEvents(this.userId).then(response => {
                this.feedEventList = response.data;
                // Flip the list order so the most recent event shows first
                // Update what is shown on this page of the pagination
                this.feedEventList.reverse();
                this.setCurrentPageEventList();
                this.loading = false;
            }).catch(() => {
                this.feedEventList = [];
                this.setCurrentPageEventList();
                this.errored = true;
                this.error_message = "There was an error when fetching your event feeds"
            });
            this.currentPage = 1;
        },
        methods: {
            async getUserId() {
                let userId = null;
                await api.getUserId().then(response => {
                    userId = response.data;
                }).catch(error => {this.throwError(error, true)});
                return userId
            },
            /**
             * Calculate the feedEvents to be displayed from the current page number.
             * This function is called when the pagination bar is altered,
             * changing the currentPage variable.
             */
            setCurrentPageEventList() {
                let leftIndex = (this.currentPage - 1) * this.eventsPerPage;
                let rightIndex = leftIndex + this.eventsPerPage;
                if (rightIndex > this.feedEventList.length) {
                    rightIndex = this.feedEventList.length;
                }
                this.currentPageEventList = this.feedEventList.slice(leftIndex, rightIndex);
                window.scrollTo(0,0);
            },
        }
    }
</script>