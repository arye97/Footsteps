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
            <b-alert class="align-centre" variant="success" dismissible fade :show=alertCount>
                {{ alertMessage }}
            </b-alert>
            <b-alert class="align-centre" variant="danger" dismissible fade :show=isErrorAlert>
                {{ errorAlertMessage }}
            </b-alert>
            <div class="align-centre">
                <b-button id="creatActivityButton" style="margin-bottom: 1.7em; margin-top: 0.8em" variant="primary"
                          v-on:click="goToPage('/activities/create')">Create New Activity
                </b-button>
            </div>
            <div class="align-centre" id="mapComponent" v-if="!loading">
                <location-i-o
                        ref="mapComponentRef"
                        :view-only="true"
                        :parent-center="centerPin ? {lat: centerPin.lat, lng: centerPin.lng} : null"
                        :parent-pins.sync="pins"
                        :max-pins="rows"
                        :description="'Your location is red, created activities are blue and following activities are green.'"
                ></location-i-o>
                <br/>
            </div>
            <section>
                <ActivityList id="activityList" v-if="userId !== null" :user-id-prop="userId"/>
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
            await this.checkUserPin();
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
                    pins = [null];
                }
                this.loading = false;
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
                        if (pin.pin_type !== "USER") {
                            pins.push(pin);
                        }
                    }
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
            },

            /**
             * Checking the value of user pin
             * This method allows validation that the user pin matches the user location
             */
            async checkUserPin() {
                await api.getAllUserData().then(response => {
                    let user = response.data;
                    if (user.private_location !== null) {
                        this.centerPin = user.private_location;
                        this.pins.push(user.private_location);
                    } else if (user.public_location !== null) {
                        this.centerPin = user.public_location;
                        this.pins.push(user.public_location);
                    } else {
                        this.centerPin = null;
                    }
                }).catch( error => {
                    if (error.response.status === 401) {
                        sessionStorage.clear();
                        this.$router.push("/login");
                    } else {
                        throw new Error("Unknown error has occurred")
                    }
                });
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

    .light-info-message {
        color: dimgrey;
    }
</style>