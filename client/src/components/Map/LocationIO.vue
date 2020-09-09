<template>
    <div>
        <b-card class="flex-fill" border-variant="secondary">
            <div v-if="isMapVisible">
                <b-button id="hideMapButton" v-on:click="isMapVisible=false">Hide Map</b-button>
                    <map-viewer
                            id="mapComponent"
                            :center="center"
                            :pins="pins"
                    ></map-viewer>
                <div v-if="!viewOnly">
                    <h3 class="font-weight-light"><strong>Search and add a pin</strong></h3>
                    <gmap-autocomplete
                            id="gmapAutoComplete"
                                :options="{fields: ['geometry', 'formatted_address', 'address_components']}"
                            @place_changed="getPlaceField"
                            class="form-control" style="width: 100%">
                    </gmap-autocomplete>
                    <b-button id='addMarkerButton' block @click="addMarker">Add</b-button>
                </div>
            </div>
            <div v-else>
                <b-button id="showMapButton" v-on:click="isMapVisible=true">Show Map</b-button>
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

        props: {
            viewOnly: {
                default: false,
                type: Boolean
            },
        },

        data() {
            return {
                isMapVisible: false,
                pins:[],
                center:undefined,
                currentPlace: null
            }
        },

        methods: {

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
                    this.center = pin;
                    this.currentPlace = null;
                }
            }
        }
    }
</script>

<style scoped>

</style>