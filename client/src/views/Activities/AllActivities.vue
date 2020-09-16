<template>
    <div>
        <header class="masthead">
            <br/><br/><br/>
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <Header id="header"/>
                        <br/>
                    </div>
                </div>
            </div>
        </header>
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
            <b-alert variant="success" dismissible fade :show=this.alertCount>
                {{this.alertMessage}}
            </b-alert>
            <div style="text-align: center">
                <b-button id="creatActivityButton" style="margin-bottom: 1.7em; margin-top: 0.8em" variant="primary"
                          v-on:click="goToPage('/activities/create')">Create New Activity
                </b-button>
            </div>
            <div class="map-pane" id="mapComponent">
                <location-i-o
                        :view-only="true"
                        :parent-center="{lat: userPin.latitude, lng: userPin.longitude}"
                        :parent-pins="pins"
                        :max-pins="rows"
                ></location-i-o>
            </div>
            <section>
                <ActivityList id="activityList" v-if="userId !== null" :user-id-prop="this.userId"/>
            </section>

        </b-container>
    </div>

</template>

<script>
    import Header from '../../components/Header/Header.vue';
    import api from "../../Api";
    import ActivityList from "../../components/Activities/ActivityList";
    import LocationIO from "../../components/Map/LocationIO";

    export default {
        name: "AllActivities",
        components: {
            ActivityList,
            Header,
            LocationIO
        },
        data() {
            return {
                pinsPerBlock: 20,
                rows: null,
                userPin: null,
                pins: [],
                pinBlock: 0,
                userId: null
            }
        },
        props: {
            alertCount: {
                type: Number,
                default: 0
            },
            alertMessage: {
                type: String,
                default: ''
            }
        },
        async mounted() {
            this.userId = await this.getUserId();
            await this.getPins();
        },
        methods: {
            /**
             * Gets all the activity pins and one user pin for the LocationIO component.
             * Gets pins in blocks of 20. The first block contains the user at the front of the list.
             */
            async getPins() {
                let pinBlock = 0;
                let pins = await this.requestPinBlock(pinBlock);
                this.userPin = pins[0];
                this.pins = pins;
                for (pinBlock = 1; pinBlock < this.rows / this.pinsPerBlock; pinBlock++) {
                    pins = await this.requestPinBlock(pinBlock);
                    this.pins = this.pins.concat(pins);
                }
            },

            /**
             * Get one block of pins from the back-end.
             * @param pinBlock the block number to request, first block is zero
             * @return Array a list of pins for the given block
             */
            async requestPinBlock(pinBlock) {
                let pins = [];
                await api.getActivityPins(this.userId, pinBlock).then(response => {
                    pins = response.data;
                    this.rows = response.headers['total-rows'];
                }).catch(error => {
                    if (error.response.status === 401) {
                        sessionStorage.clear();
                        this.$router.push('/login');
                    } else if (error.response.status === 403) {
                        this.$router.go(); // Reloads the page
                    } else {
                        this.alertMessage = 'Could not load map pins, please try again';
                        this.alertCount = 5;
                    }
                });
                return pins;
            },

            /**
             * Get the Id of the current Logged in user.
             * @returns {Promise<*>}
             */
            async getUserId() {
                let userId = null;
                await api.getUserId().then(response => {
                    userId = response.data;
                }).catch(error => {
                    if (error.response.status === 401) {
                        sessionStorage.clear();
                        this.$router.push("/login");
                    } else {
                        throw new Error("Unknown error has occurred")
                    }
                });
                return userId
            },

            goToPage(url) {
                this.$router.push(url);
            },
        }
    }
</script>

<style scoped>
    .activity-button-group button {
        margin-left: 35.5%;
        width: 110%;
    }
</style>