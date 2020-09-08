<template>
    <div>
        <b-card class="flex-fill" border-variant="secondary">
            <div v-if="isMapVisible">
            <b-button id="hideMapButton" v-on:click="hideMap()">Hide Map</b-button>
            <map-viewer
                    :center="center"
                    :pins="pins"
            ></map-viewer>
            <h2>Search and add a pin</h2>
            <label>
                <gmap-autocomplete
                        @place_changed="getPlaceField">
                </gmap-autocomplete>
                <button @click="addMarker">Add</button>
            </label>
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
                pins:[{lat:-46.53, lng:172.63},],
                center:{lat:-43.53, lng:172.63},
                locations: [],
                locationInput: "",
                awaitingSearch: false,
                inputDisplay: '',
                currentPlace: null
            }
        },

        mounted() {
            this.geolocate();
        },

        methods: {

            hideMap() {
                this.isMapVisible = false;
            },

            showMap() {
                this.isMapVisible = true;
            },

            // receives a place object via the autocomplete component
            getPlaceField(place) {
                this.currentPlace = place;
            },

            addMarker() {
                if (this.currentPlace) {
                    const pin = {
                        lat: this.currentPlace.geometry.location.lat(),
                        lng: this.currentPlace.geometry.location.lng()
                    };
                    this.pins.push(pin);
                    this.locations.push(this.currentPlace);
                    this.center = pin;
                    this.currentPlace = null;
                }
            },

            geolocate: function() {
                navigator.geolocation.getCurrentPosition(position => {
                    this.center = {
                        lat: position.coords.latitude,
                        lng: position.coords.longitude
                    };
                });
            }
        }
    }
</script>

<style scoped>

</style>