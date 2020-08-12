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
        async mounted() {
            this.userId = await this.getUserId();
            api.getFeedEvents(this.userId).then(response => {
                this.feedEventList = response.data;
                // Flip the list order so the most recent event shows first
                // Update what is shown on this page of the pagination
                this.feedEventList.reverse();
                this.currentPageEventList = this.feedEventList;
                this.loading = false;
            }).catch(() => {
                this.errored = true;
                this.error_message = ""
            });
        },
      methods: {
        async getUserId() {
          let userId = null;
          await api.getUserId().then(response => {
            userId = response.data;
          }).catch(error => {this.throwError(error, true)});
          return userId
        }
      }
    }
</script>