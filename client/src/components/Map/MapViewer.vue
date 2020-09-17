<template>
    <div id="mapDiv" class="map-pane">
        <keep-alive>
            <GmapMap class="map"
                    ref="mapRef"
                    :center="initialCenter"
                    :zoom="4.5"
                    map-type-id="terrain"
                    style="width: 500px; height: 300px"
                    @dragend="updateCenter"
            >
                <GmapMarker
                        :ref="'marker' + pinIndex"
                        :key="pinIndex"
                        v-for="(pin, pinIndex) in pins"
                        :position="google && new google.maps.LatLng(pin.lat, pin.lng)"
                        :clickable="true"
                        :draggable="draggablePins"
                        :icon="'http://maps.google.com/mapfiles/ms/icons/' + pin.colour.toLowerCase() + '-dot.png'"
                        @click="panToPin(pin)"
                        @dragend="repositionPin({lat: $event.latLng.lat(), lng: $event.latLng.lng()}, pinIndex)"
                />
            </GmapMap>
        </keep-alive>
    </div>
</template>

<script>
    import { gmapApi } from 'gmap-vue';

    /**
     * A map pane that displays draggablePins location pins.
     *
     * Emitted Events:
     * @emits child-pins  -  emitted when internal Array of pins is modified.  Returns the Array.
     * @emits pin-change  -  emitted when a single pin is modified.  Returns the pin Object.
     */
    export default {
        name: "MapViewer",

        props: {
            /**
             * Object {lat, lng} to centre map on.
             */
            initialCenter: {
                default() {
                    return {lat:-40.9006, lng:174.8860};  // Coordinates of New Zealand
                },
                type: Object,
                validator: function (initialCenter) {
                    // Center must have lat and lng
                    return !isNaN(initialCenter.lat) && !isNaN(initialCenter.lng);
                }
            },
            /**
             * Array of pins to display on map.
             */
            pins: {
                default() {
                    return [];
                },
                type: Array,
                validator: function (pins) {
                    // Each pin must have lat and lng
                    return pins.every(pin => {return !isNaN(pin.lat) && !isNaN(pin.lng);});
                }
            },
            /**
             * Pins can be dragged.
             */
            draggablePins: {
                default: true,
                type: Boolean
            }
        },
        computed: {
            google: gmapApi
        },

        data() {
            return {
                currentCenter: this.initialCenter,  // We shouldn't mutate the prop initialCenter
                geoCoder: {geocode: () => {}}
            }
        },

        async mounted() {
            await this.$gmapApiPromiseLazy();  // Without this, google could be null
            this.geoCoder = new this.google.maps.Geocoder();
        },

        methods: {

            /**
             * This function updates the pin Object in the pins Array with new lat, lng coordinates and emits the
             * modified pins Array back to the parent.
             * @param pin Object containing lat, lng
             * @param pinIndex the index of the pin in Array pins
             */
            repositionPin(pin, pinIndex) {
                if (pinIndex < this.pins.length) {
                    this.pins[pinIndex].lat = pin.lat;
                    this.pins[pinIndex].lng = pin.lng;
                    this.generatePinName(this.pins[pinIndex]);

                    this.$emit("child-pins", this.pins);
                }
            },

            /**
             * Centres the map on a pin using a smooth animation
             * @param pin object containing lat, lng
             */
            panToPin(pin) {
                // NOTE there are timing considerations with this function because it's async
                this.$refs.mapRef.$mapPromise.then((map) => {
                    map.panTo(pin)
                });
                this.currentCenter = pin;
            },

            /**
             * Updates this.currentCenter with the current location of the map.
             * NOTE: It's not practical for this to return a value and then set it equal to this.currentCenter because
             * the call to get the center is async.
             */
            updateCenter() {
                this.$refs.mapRef.$mapPromise.then((map) => {
                    const mapBounds = map && map.getBounds();
                    const mapCenter = mapBounds && mapBounds.getCenter();

                    this.currentCenter = {lat: mapCenter && mapCenter.lat(), lng: mapCenter && mapCenter.lng()};
                });
            },

            /**
             * Gives a pin a pin.name.  Mutates the pin.
             * Uses Google Maps API reverse-geocoding.
             * NOTE: Because the API call is async and slow, it is easier to mutate the given pin rather than having a
             * return value.
             * The changed pin (and its name) is emitted as "pin-change"
             * @param pin Object containing lat, lng.
             */
            generatePinName(pin) {
                pin.name = " ";  // This causes the autocomplete box to be blank while the address is loading
                this.$emit("pin-change", pin);

                this.geoCoder.geocode({ location: pin }, (results, status) => {
                    if (status === "OK") {
                        pin.name = results[0].formatted_address
                    } else {
                        pin.name = pin.lat.toFixed(5) + ', ' + pin.lng.toFixed(5);
                    }
                    this.$emit("pin-change", pin)
                });
            },
        }
    }

</script>

<style scoped>
    .map {
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1px outset #6c757e;
    }
    .map-pane {
        display: flex;
        align-items: center;
        justify-content: center;
    }
</style>