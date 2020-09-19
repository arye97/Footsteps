<template>
    <div>
        <b-card class="flex-fill" border-variant="secondary">
            <div v-if="isMapVisible">
                <b-button id="hideMapButton" variant="info" @click="isMapVisible=false">Hide Map</b-button>
                <div v-if="!viewOnly">
                    <br/>
                </div>
                    <map-viewer
                            id="mapComponent"
                            ref="mapViewerRef"
                            :pins="pins"
                            :draggable-pins="!viewOnly"
                            @child-pins="(newPins) => this.pins = newPins ? newPins : this.pins"
                            @pin-change="pinChanged"
                            :initial-center="center"
                    ></map-viewer>
                <div v-if="!viewOnly">
                    <br/>
                    <h3 class="font-weight-light"><strong>Search and add a pin</strong></h3>
                    <!--We should add to fields in :options if we want to receive other data from the API-->
                    <gmap-autocomplete
                            id="gmapAutoComplete"
                            :value="address"
                            :options="{fields: ['geometry', 'formatted_address', 'address_components']}"
                            @place_changed="(place) => {addMarker(placeToPin(place)); pinChanged(placeToPin(place));}"
                            class="form-control" style="width: 100%">
                    </gmap-autocomplete>
                    <br/>
                    <b-button id='addMarkerButton' variant="primary" block @click="addMarker()">Drop Pin</b-button>
                </div>
            </div>
            <div v-else>
                <b-button id="showMapButton" variant="info" @click="isMapVisible=true">Show Map</b-button>
                <br/>
            </div>
        </b-card>
    </div>

</template>

<script>
    import MapViewer from "../../components/Map/MapViewer";

    /**
     * A component for viewing or editing a location using a textbox and interactive map.  Composes MapViewer.
     *
     * Emitted Events:
     * @emits child-pins  -  emitted when internal Array of pins is modified.  Returns the Array.
     * @emits pin-change  -  emitted when a single pin is modified.  Returns the pin Object.
     */
    export default {
        name: "LocationIO",

        components: {
            MapViewer
        },


        props: {

            /**
             * When true only displays locations and is un-editable.
             */
            viewOnly: {
                default: false,
                type: Boolean
            },
            /**
             * Number of pins to display on the map.  If exceeded, the first pin in the Array is removed.
             */
            maxPins: {
                type: Number
            },
            /**
             * Boolean version of maxPins.  Only allows 1 pin when true.
             */
            singleOnly: {
                default: false,
                type: Boolean
            },
            /**
             * Initial location.
             */
            currentLocation: {
                type: Object
            },
            /**
             * Array of pins to display on map.
             */
            parentPins: {
              default: function() {return []},
              type: Array
            },
            /**
             * Object {lat, lng} to centre map on.
             */
            parentCenter: {
                default: null,
                type: Object
            },
            parentAddress: {
                default: null,
                type: String
            }
        },

        watch: {
            pins: function () {
                if ((this.singleOnly && this.pins.length > 1) || (this.maxPins >= 0 && this.pins.length > this.maxPins)) {
                    this.pins.shift();
                } else {
                    this.$emit("child-pins", this.pins);
                }
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
                    colour: 'red',
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
                this.pins = this.pins.concat(this.parentPins);
            }
            if (this.parentAddress) {
                this.address = this.parentAddress;
            }
        },

        methods: {

            /**
             * Add a marker centred on the coordinates of place, or the center of the map if place is not defined.
             * @param pin Object containing lat, lng, name.  (Optional)
             */
            addMarker(pin) {

                if (pin && ["lat", "lng", "name"].every(key => key in pin)) {
                    this.pins.push(pin);
                    this.address = pin.name;
                    if (!("colour" in pin)) {
                        pin.colour = 'red';
                    }

                } else if (pin === undefined) {

                    pin = {
                        colour: 'red',
                        lat: this.$refs.mapViewerRef.currentCenter.lat,
                        lng: this.$refs.mapViewerRef.currentCenter.lng,
                        name: ""
                    };
                    this.pins.push(pin);

                    // Fetches pin.name from API and sets this.address
                    this.$refs.mapViewerRef.repositionPin(pin, this.pins.length - 1);

                } else {
                    // An error occurred.  This would the place to add a message box "The location can not be found"
                    return;
                }

                this.$refs.mapViewerRef.panToPin(pin);
                this.center = pin;
            },


            /**
             * Convert a google place object to a pin.
             * @param place Object received via the autocomplete component.  Needs fields place.geometry.location.lat(),
             * place.geometry.location.lng() and place.formatted_address.
             * @return Object with properties colour, lat, lng, name
             */
            placeToPin(place) {
                let pin;
                try {
                    pin = {
                        colour: 'red',
                        lat: place.geometry.location.lat(),
                        lng: place.geometry.location.lng(),
                        name: place.formatted_address
                    };
                } catch {
                    pin = null
                }
                return pin
            },


            /**
             * Updates the this.address in gmap-autocomplete and emits the changed pin to the parent component.
             * @param pin Object pin that was changed
             */
            pinChanged(pin) {
                this.address = pin.name;
                this.$emit("pin-change", pin);
            }

        }
    }
</script>

<style scoped>

</style>