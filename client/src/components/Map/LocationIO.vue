<template>
    <div>
        <b-card class="flex-fill" border-variant="secondary">
            <div v-if="isMapVisible">
                <b-button id="hideMapButton" @click="isMapVisible=false">Hide Map</b-button>
                    <map-viewer
                            id="mapComponent"
                            ref="mapViewerRef"
                            :pins="pins"
                            @pin-move="updatePins"
                            @address-change="(modifiedAddress) => this.address = modifiedAddress"
                    ></map-viewer>
                <div v-if="!viewOnly">
                    <h3 class="font-weight-light"><strong>Search and add a pin</strong></h3>

                    <!--We should add to fields in :options if we want to receive other data from the API-->
                    <gmap-autocomplete
                            id="gmapAutoComplete"
                            :value="address"
                            :options="{fields: ['geometry', 'formatted_address', 'address_components']}"
                            @place_changed="addMarker( placeToPin($event) )"
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

        watch: {
            pins: function () {
                if (this.maxPins) {
                    for (let i=0; i < this.pins.length - this.maxPins; i++) {
                        this.pins.shift();  // Can't use splice.  Causes infinite loop with watcher
                    }
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
            }
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
             * @param pin Object containing lat, lng, name.  (Optional)
             */
            addMarker(pin) {
                console.log(pin);

                if (pin && ["lat", "lng", "name"].every(key => key in pin)) {
                    this.pins.push(pin);

                    // Without this the contents of autocomplete is overwritten by the value of this.address
                    this.address = pin.name;

                } else {
                    pin = {
                        lat: this.$refs.mapViewerRef.currentCenter.lat,
                        lng: this.$refs.mapViewerRef.currentCenter.lng,
                        name: ""
                    };
                    this.pins.push(pin);

                    // Fetches pin.name from API and sets this.address
                    this.$refs.mapViewerRef.repositionPin(pin, this.pins.length - 1);
                }

                this.$refs.mapViewerRef.panToPin(pin);
            },

            /**
             * Syncs this.pins Array with MapViewer.  Called when a pin is repositioned in MapViewer.
             * @param pins Array of pin Objects containing lat, lng, name.
             */
            updatePins(pins) {
                if (pins) this.pins = pins
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
                    pin = {lat: 0, lng: 0, name: ""}
                }
                return pin
            }
        }
    }
</script>

<style scoped>

</style>