<template>
  <div class="col-12 text-center">
    <b-card class="flex-fill overflow-hidden" border-style="hidden" border-variant="white">
      <div v-if="isMapVisible">
        <b-button id="hideMapButton" variant="info" @click="isMapVisible=false">Hide Map</b-button>
        <br/><br/>
        <map-viewer
                id="mapComponent"
                ref="mapViewerRef"
                :pins="pins"
                :draggable-pins="!viewOnly"
                @child-pins="(newPins) => this.pins = newPins ? newPins : this.pins"
                @pin-change="pinChanged"
                :initial-center="center"
                :zoom="zoom"

        ></map-viewer>
          <br/>
        <b-button-group id="mapButtons" v-if="!viewOnly">
          <b-button id='addMarkerButton' v-b-popover.hover.top="'Place Pin in Exact Center of the Map'" variant="primary" block @click="addMarker()">Drop Pin</b-button>
        </b-button-group>
      </div>
      <div v-else>
        <b-button id="showMapButton" variant="info" @click="isMapVisible=true">Show Map</b-button>
      </div>
        <div v-if="!viewOnly">
            <br/>
            <h3 class="font-weight-light"><strong>Search and add a location</strong></h3>
            <!--We should add to fields in :options if we want to receive other data from the API-->

            <b-container class="input-location-container">
            <b-row no-gutters>
            <b-col>
                <GmapAutocomplete
                    id="gmapAutoComplete"
                    :value="address"
                    :options="{fields: ['geometry', 'formatted_address', 'address_components']}"
                    @change="(place) => {check(place)}"
                    @place_changed="(place) => {addMarker(placeToPin(place)); pinChanged(placeToPin(place));}"
                    @focusin="emitFocus(true)"
                    @focusout="emitFocus(false)"
                    class="form-control">
                </GmapAutocomplete>
            </b-col>
                <b-col cols="1" v-if="!canDelete" >
                <b-button id="clearPinsButton" variant="danger" @click="clearPins" v-bind:disabled="pins.length === 0">
                    <b-icon-trash-fill />
                </b-button>
            </b-col>
            </b-row>
            </b-container>
        </div>
        <pin-legend v-if="pinLegendMode" class="pin-legend" :mode="pinLegendMode"/>
    </b-card>
  </div>

</template>

<script>
    import MapViewer from "../../components/Map/MapViewer";
    import PinLegend from "../Map/PinLegend";

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
            MapViewer,
            PinLegend
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
            },
            pinLegendMode: {
                default: null,
                type: String
            },
            canDelete: {
                default: false,
                type: Boolean
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
                isMapVisible: true,
                address: "",
                pins: [],
                center: undefined,
                zoom: undefined
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
                if (this.pins && pin) {
                    this.pins.push(pin);
                    this.pinChanged(pin);
                }
                this.center = pin;
                this.zoom = 10;
            }

            if (this.parentCenter) {
                this.center = this.parentCenter;
                this.zoom = 10;
            }
            if (this.parentPins) {
                this.pins = this.pins.concat(this.parentPins);
            }
            if (this.parentAddress) {
                this.address = this.parentAddress;
            }
            this.pins.forEach(function (element) {
                element.windowOpen = false;
            });
        },

        methods: {

            /**
             * Add a marker centred on the coordinates of place, or the center of the map if place is not defined.
             * @param pin Object containing lat, lng, name.  (Optional)
             */
            addMarker(pin) {
                if (pin && ["lat", "lng", "name"].every(key => key in pin)) {
                    if (!("colour" in pin)) {
                        pin.colour = 'red';
                    }
                    this.pins.push(pin);
                    this.address = pin.name;

                } else if (pin === undefined) {
                    pin = {
                        colour: 'red',
                        lat: this.$refs.mapViewerRef.currentCenter.lat,
                        lng: this.$refs.mapViewerRef.currentCenter.lng,
                        name: "",
                        windowOpen: false
                    };
                    this.pins.push(pin);

                    // Fetches pin.name from API and sets this.address
                    this.$refs.mapViewerRef.repositionPin(pin, this.pins.length - 1);

                } else {
                    // An error occurred.  This would the place to add a message box "The location can not be found"
                    return;
                }
                if (this.$refs.mapViewerRef) {
                    this.$refs.mapViewerRef.panToPin(pin);
                }
                this.center = pin;
                this.zoom = 10;
            },

            /**
             * Adds an Array of pins to the map
             * @param pins Array of pin Objects containing lat, lng, name.
             */
            addMarkers(pins) {
                for (let pin of pins) {
                    if (pin && ["lat", "lng"].every(key => key in pin)) {
                        if (!("name" in pin)) {
                            pin["name"] = pin.lat.toFixed(5) + ', ' + pin.lng.toFixed(5);
                        }
                        if (!("colour" in pin)) {
                            pin.colour = 'red';
                        }
                        this.pins.push(pin);

                    }
                }
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
                        name: place.formatted_address,
                        windowOpen: false
                    };
                } catch {
                    pin = null
                }
                return pin
            },
            /**
             * Emits an event when the gmap-autocomplete field is focused
             */
            emitFocus(inFocus) {
                this.$emit('locationIO-focus', inFocus);
            },
            /**
             * Updates the this.address in gmap-autocomplete and emits the changed pin to the parent component.
             * @param pin Object pin that was changed
             */
            pinChanged(pin) {
                this.address = pin.name;
                this.$emit("pin-change", pin);
            },


            /**
             * Remove all pins.
             */
            clearPins() {
                this.pins = [];
                this.address = "";
                this.$emit("pin-change", null);
            },

            /**
             * Checks the formatted address from the autocomplete box. If it hasn't been selected from the google autocomplete
             * suggestions it emits an invlaid-location warning to the user
             * @param place the information in the box
             */
            check(place) {
                if (this.canDelete) {
                    if (place.formatted_address === undefined) {
                        this.$emit("invalid-location");
                    }
                }
            }

        }
    }
</script>

<style scoped>
    .pin-legend {
        margin-left: auto;
        margin-right: auto;
    }

    .border-white {
        background-color: white;
    }

    .input-location-container {
    }
</style>