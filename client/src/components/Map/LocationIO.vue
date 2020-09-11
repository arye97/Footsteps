<template>
    <div>
        <b-card class="flex-fill" border-variant="secondary">
            <div v-if="isMapVisible">
                <b-button id="hideMapButton" @click="isMapVisible=false">Hide Map</b-button>
                    <map-viewer
                            id="mapComponent"
                            ref="mapViewerRef"
                            :pins="pins"
                            @pin-move="setInputBoxUpdatePins"
                    ></map-viewer>
                <div v-if="!viewOnly">
                    <h3 class="font-weight-light"><strong>Search and add a pin</strong></h3>

                    <!--We should add to fields in :options if we want to receive other data from the API-->
                    <gmap-autocomplete
                            id="gmapAutoComplete"
                            :value="address"
                            :options="{fields: ['geometry', 'formatted_address', 'address_components']}"
                            @place_changed="addMarker"
                            class="form-control" style="width: 100%">
                    </gmap-autocomplete>

                    <b-button id='addMarkerButton' block @click="addMarker()">Drop Pin</b-button>
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
        },

        data() {
            return {
                isMapVisible: false,
                pins:[],
                address: ""
            }
        },

        methods: {

            /**
             * Add a marker centred on the coordinates of place, or the center of the map if place is not defined.
             * @param place object received via the autocomplete component.
             */
            addMarker(place) {
                let pin;

                if (place && Object.prototype.hasOwnProperty.call(place, 'geometry')) {
                    pin = {
                        lat: place.geometry.location.lat(),
                        lng: place.geometry.location.lng()
                    };

                    // Without this the contents of autocomplete is overwritten by the value of this.address
                    this.address = place.formatted_address;

                } else {
                    pin = {
                        lat: this.$refs.mapViewerRef.currentCenter.lat,
                        lng: this.$refs.mapViewerRef.currentCenter.lng
                    };
                }
                this.pins.push(pin);
            },

            /**
             * Called when a pin is repositioned in MapViewer.  Sets the input box to contain the location of the
             * repositioned pin.  Updates that pin in the pins Array.
             * @param pin the pin that was moved
             * @param pinIndex the index of this pin in the array
             */
            setInputBoxUpdatePins(pin, pinIndex) {
                if (pinIndex < this.pins.length) {
                    this.pins[pinIndex] = pin
                }
                // ToDo replace these coordinates with an address string from the API's Reverse-Geocoding
                this.address = pin.lat.toFixed(5) + ', ' + pin.lng.toFixed(5);

            }
        }
    }
</script>

<style scoped>

</style>