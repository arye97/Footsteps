<template>
    <div>
        <b-card class="flex-fill" border-variant="secondary">
            <div v-if="isMapVisible">
                <b-button id="hideMapButton" variant="info" @click="isMapVisible=false">Hide Map</b-button>
                    <map-viewer
                            id="mapComponent"
                            ref="mapViewerRef"
                            :pins="pins"
                            @pin-move="(newPins) => this.pins = newPins ? newPins : this.pins"
                            @pin-change="pinChanged"
                            :initial-center="center"
                    ></map-viewer>
                <div v-if="!viewOnly">
                    <h3 class="font-weight-light"><strong>Search and add a pin</strong></h3>

                    <!--We should add to fields in :options if we want to receive other data from the API-->
                    <gmap-autocomplete
                            id="gmapAutoComplete"
                            :value="address"
                            :options="{fields: ['geometry', 'formatted_address', 'address_components']}"
                            @place_changed="placeChangedAutocomplete"
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
                } else {
                    this.$emit("child-pins", this.pins);
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
             * @param pin Object containing lat, lng, name.  (Optional)
             */
            addMarker(pin) {

                if (pin && ["lat", "lng", "name"].every(key => key in pin)) {
                    this.pins.push(pin);
                    this.address = pin.name;

                } else if (pin === undefined) {

                    pin = {
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
             * @return Object with properties lat, lng, name
             */
            placeToPin(place) {
                let pin;
                try {
                    pin = {
                        lat: place.geometry.location.lat(),
                        lng: place.geometry.location.lng(),
                        name: place.formatted_address
                    };
                } catch {
                    pin = null
                }
                return pin
            },

            placeChangedAutocomplete(place) {
                let pin = this.placeToPin(place);
                this.addMarker(pin);
                this.$emit("pin-change", pin);
            },

            pinChanged(pin) {
                this.address = pin.name;
                this.$emit("pin-change", pin);
            }

        }
    }
</script>

<style scoped>

</style>