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
            <section v-for="event in this.paginatedFeedEventList" :key="event.id">
                <!-- Feed Event List -->
                <feed-card v-bind:event="event" v-bind:viewer-id="userId"/>
                <br>
            </section>
            <!-- Pagination Nav Bar -->
            <b-pagination
                    v-if="!errored && !loading "
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
                paginatedFeedEventList: [],
                currentPage: 1,
                eventsPerPage: 5,
                error_message: "",
                userId: null,
                rows: null
            }
        },
        watch: {
            /**
             * Watcher is called whenever currentPage is changed, via reloading events
             * or using the pagination bar.
             */
            async currentPage() {
                await this.getPaginatedFeedEvents();
            }
        },
        async mounted() {
            this.userId = await this.getUserId();
            await this.getPaginatedFeedEvents();
        },
        methods: {

            /**
             * Fetches a paginated list of FeedEvents through an API call.
             */
            async getPaginatedFeedEvents() {
                window.scrollTo(0,450);
                let pageNumber = this.currentPage - 1;
                await api.getFeedEvents(this.userId, pageNumber).then(response => {
                    this.paginatedFeedEventList = response.data;
                    this.rows = response.headers["total-rows"];
                    this.loading = false;
                }).catch(() => {
                    this.paginatedFeedEventList = [];
                    this.errored = true;
                    this.error_message = "There was an error when fetching your event feeds"
                });
            },

            async getUserId() {
                let userId = null;
                await api.getUserId().then(response => {
                    userId = response.data;
                }).catch( () => {
                    this.logout();
                });
                return userId;
            },

            /**
             * Logs the user out and clears session token
             */
            logout () {
                api.logout();
                sessionStorage.clear();
                this.isLoggedIn = (sessionStorage.getItem("token") !== null);
                this.$forceUpdate();
                this.$router.push('/login');
            },
        }
    }
</script>