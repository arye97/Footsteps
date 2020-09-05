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
            </div>
            <div v-else>
                <b-button id="showMapButton" v-on:click="showMap()">Show Map</b-button>
            </div>
        </b-card>
    </div>

</template>

<script>
    import MapViewer from "../../components/Map/MapViewer";

    export default {
        name: "LocationIO",

        components: {
            MapViewer
        },
        data() {
            return {
                isMapVisible: false,
                pins:[{id:1, lat:-43.53, lng:172.63}],
                center:{lat:-43.53, lng:172.63},
                locations: [],
                locationInput: ""
            }
        },

        methods: {

            addLocation() {
                //todo: validate and format location
                this.locations.push(this.locationInput);
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