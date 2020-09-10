<template>
    <div>
        <b-card class="flex-fill" border-variant="secondary">
            <div v-if="isMapVisible">
                <b-button id="hideMapButton" @click="isMapVisible=false">Hide Map</b-button>
                    <map-viewer
                            id="mapComponent"
                            :center="center"
                            :pins="pins"
                    ></map-viewer>
                <div v-if="!viewOnly">
                    <h3 class="font-weight-light"><strong>Search and add a pin</strong></h3>

                    <!--We should add to fields in :options if we want to receive other data from the API-->
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
                <b-button id="showMapButton" @click="isMapVisible=true">Show Map</b-button>
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
            singleOnly: {
                default: false,
                type: Boolean
            },
            currentLocation: {
                default: undefined,
                type: Object
            }
        },

        data() {
            return {
                isMapVisible: false,
                pins: [],
                center: undefined,
                currentPlace: null
            }
        },

        mounted() {
            if (this.currentLocation) {
                let pin = {
                    lat: this.currentLocation.latitude,
                    lng: this.currentLocation.longitude,
                    name: this.currentLocation.name
                };
                if (this.singleOnly && this.pins) {
                    this.pins[0] = pin;
                } else {
                    this.pins.push(pin);
                }
                this.center = pin;
            }
        },


        methods: {

            /**
             * Receives a place object via the autocomplete component
             * @param place response object from Google API that includes latitude and longitude
             */
            getPlaceField(place) {
                this.currentPlace = place;
            },

            /**
             * Add a marker centred on the coordinates of this.currentPlace
             */
            addMarker() {
                if (this.currentPlace) {
                    const pin = {
                        lat: this.currentPlace.geometry.location.lat(),
                        lng: this.currentPlace.geometry.location.lng(),
                        name: this.currentPlace.formatted_address
                    };
                    if (this.singleOnly && this.pins) {
                        this.pins[0] = pin;
                    } else {
                        this.pins.push(pin);
                    }
                    this.center = pin;
                    this.currentPlace = null;
                    this.$emit("child-pins", this.pins);
                }
            }
        }
    }
</script>

<style scoped>

</style>