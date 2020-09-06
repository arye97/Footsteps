<template>
    <div>
        <b-card class="flex-fill" border-variant="secondary">
            <div v-if="isMapVisible">
            <b-button id="hideMapButton" v-on:click="hideMap()">Hide Map</b-button>
            <map-viewer
                    :center="center"
                    :pins="pins"
            ></map-viewer>
            <b-row>
                <b-col>
                    <b-form-input
                        id="locationInput"
                        v-model="locationInput"
                        type="text"
                        placeholder="Input address here"
                    ></b-form-input>
                </b-col>
                <b-col>
                    <b-button
                            id="LocationSubmit"
                            v-on:click="addLocation()"
                    >Place pin</b-button>
                </b-col>
            </b-row>
                <label>{{ inputDisplay }}</label>
            </div>
            <div v-else>
                <b-button id="showMapButton" v-on:click="showMap()">Show Map</b-button>
            </div>
        </b-card>
    </div>

</template>

<script>
    import MapViewer from "../../components/Map/MapViewer";
    import api from "../../Api"

    export default {
        name: "LocationIO",

        components: {
            MapViewer
        },
        data() {
            return {
                isMapVisible: false,
                pins:[{lat:-46.53, lng:172.63},],
                center:{lat:-43.53, lng:172.63},
                locations: [],
                locationInput: "",
                awaitingSearch: false,
                inputDisplay: ''
            }
        },

        watch: {
            locationInput: function () {
                if (!this.awaitingSearch) {
                    setTimeout(() => {

                        this.awaitingSearch = false;
                        this.inputDisplay = this.locationInput;
                    }, 1000); // 1 sec delay
                    //todo: ping auto complete
                    // this.inputDisplay = this.locationInput;
                }
                this.awaitingSearch = true;
            },
        },

        methods: {

            async addLocation() {
                // let goeApi = "https://maps.googleapis.com/maps/api/geocode/json?address=" + this.locationInput + "&key=YOUR_API_KEY"
                //todo: validate and format location

                this.locations.push(this.locationInput);
                let response = await api.getCoordinateDataByLocation(this.locationInput);
                console.log(response);
                this.pins.push(response.data.results[0].geometry.location);
            },

            hideMap() {
                this.isMapVisible = false;
            },

            showMap() {
                this.isMapVisible = true;
            }
        }
    }
</script>

<style scoped>

</style>