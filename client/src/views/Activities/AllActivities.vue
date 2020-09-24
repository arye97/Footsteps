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
                            <strong>Find something to do</strong>
                        </h1>
                    </div>
                </div>
            </div>
            <hr/>
            <b-alert class="align-centre" variant="success" dismissible fade :show=alertCount>
                {{ alertMessage }}
            </b-alert>
            <b-alert class="align-centre" variant="danger" dismissible fade :show=isErrorAlert>
                {{ errorAlertMessage }}
            </b-alert>
            <div class="align-centre">
                <b-button id="creatActivityButton" style="margin-bottom: 1em; margin-top: 0.8em" variant="primary"
                          v-on:click="goToPage('/activities/create')">Create Activity
                </b-button>
            </div>
            <hr/>
            <div class="align-centre" id="mapComponent" v-if="!loading">
                <location-i-o
                        pin-legend-mode="AllActivities"
                        ref="mapComponentRef"
                        :view-only="true"
                        :parent-center="centerPin === null ? null : {lat: centerPin.lat, lng: centerPin.lng}"
                        :parent-pins.sync="pins"
                        :max-pins="rows"
                ></location-i-o>
            </div>
            <section>
                <ActivityList @deleteUpdate="getPins" id="activityList" v-if="userId !== null" :user-id-prop="userId"/>
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
                isErrorAlert: false,
                errorAlertMessage: '',
                rows: 0,
                hasNext: false,
                centerPin: null,
                pins: [],
                loading: true,
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
            this.addPinsToMap(this.pins.reverse());  // reverse() Puts the user's location at end of the array
        },
        methods: {
            /**
             * Gets all the activity pins and one user pin for the LocationIO component.
             * Gets pins in blocks of 20. The first block contains the user at the front of the list.
             */
            async getPins() {
                this.loading = true;
                let pinBlock = 0;
                let pins = await this.requestPinBlock(pinBlock);
                if (pins.length < 1) {
                    pins = [{lat: -40.9006, lng: 174.8860, colour: 'red'}]; // No pins received, default to New Zealand
                    this.rows = 1;
                }
                this.loading = false;
                this.centerPin = pins[0];
                if (this.centerPin.pin_type !== "USER") {
                    this.centerPin = null;
                }
                this.pins = pins;
                while (this.hasNext) {
                    pinBlock++;
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
                    for (let pin of response.data) {
                        pin.windowOpen = false;
                        if (!("name" in pin)) {
                            pin["name"] = pin["location_name"];
                        }
                        pins.push(pin);
                    }
                    pins = response.data;
                    this.hasNext = response.headers['has-next'] === 'true';
                    this.rows += pins.length;
                }).catch(error => {
                    this.hasNext = false;
                    if (error.response.status === 401) {
                        sessionStorage.clear();
                        this.$router.push('/login');
                    } else if (error.response.status === 403) {
                        this.$router.go(); // Reloads the page
                    } else {
                        this.errorAlertMessage = "Could not load the map markers, please try again";
                        this.isErrorAlert = true;
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

            /**
             * This method was needed, as it doesn't seem possible to stub $refs when they are called in a Vue's
             * mounted hook.
             * @param pins Array of pin Objects containing lat, lng, name.
             */
            addPinsToMap(pins) {
                this.$refs.mapComponentRef.addMarkers(pins);
            }
        }
    }
</script>

<style scoped>
    .activity-button-group button {
        margin-left: 35.5%;
        width: 110%;
    }

    .align-centre {
        text-align: center;
    }

    #mapComponent {
        height: 575px;
    }

    .light-info-message {
        color: dimgrey;
    }
</style>