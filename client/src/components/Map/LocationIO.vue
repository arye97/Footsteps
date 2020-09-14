<template>
    <div>
        <b-card class="flex-fill" border-variant="secondary">
            <div v-if="isMapVisible">
                <b-button id="hideMapButton" variant="info" @click="isMapVisible=false">Hide Map</b-button>
                    <map-viewer
                            id="mapComponent"
                            ref="mapViewerRef"
                            :pins="pins"
                            :initial-center="center"
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

                    <b-button id='addMarkerButton' variant="primary" block @click="addMarker()">Drop Pin</b-button>
                </div>
            </div>
            <div v-else>
                <b-button id="showMapButton" variant="primary" @click="isMapVisible=true">Show Map</b-button>
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

        watch: {
            pins: function () {
                if ((this.singleOnly && this.pins.length > 1) || (this.maxPins >= 0 && this.pins.length > this.maxPins)) {
                    this.pins.shift();
                }
            }
        },

        props: {
            viewOnly: {
                default: false,
                type: Boolean
            },
            maxPins: {
                type: Number
            },
            singleOnly: {
                default: false,
                type: Boolean
            },
            currentLocation: {
                type: Object
            },
            parentPins: {
              default: function() {return []},
              type: Array
            },
            parentCenter: {
                default: null,
                type: Object
            }
        },

        data() {
            return {
                isMapVisible: false,
                address: "",
                pins: [],
                center: undefined,
            }
        },

        mounted() {
            if (this.currentLocation) {
                let pin = {
                    lat: this.currentLocation.latitude,
                    lng: this.currentLocation.longitude,
                    name: this.currentLocation.name
                };
                if (this.pins) {
                    this.pins.push(pin);
                }
                this.center = pin;
            }

            if (this.parentCenter) {
                this.center = this.parentCenter;
            }
            if (this.parentPins) {
                this.pins.push(...this.parentPins);
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
                        lng: place.geometry.location.lng(),
                        name: place.formatted_address
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
                this.$refs.mapViewerRef.panToPin(pin);
                this.center = pin;
                this.$emit("child-pins", this.pins);
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