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
                        :draggable="true"
                        @click="panToPin(pin)"
                        @dragend="repositionPin($event.latLng, pinIndex)"
                />
            </GmapMap>
        </keep-alive>
    </div>
</template>

<script>
    import { gmapApi } from 'gmap-vue';

    export default {
        name: "MapViewer",

        props: {
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
            pins: {
                default() {
                    return [];
                },
                type: Array,
                validator: function (pins) {
                    // Each pin must have a lat, lng and id
                    return pins.every(pin => {return !isNaN(pin.lat) && !isNaN(pin.lng);});
                }
            }
        },
        computed: {
            google: gmapApi
        },

        data() {
            return {
                currentCenter: this.initialCenter  // We shouldn't mutate the prop initialCenter
            }
        },

        async mounted() {

        },

        methods: {

            /**
             * When a pin is dragged to a new location.  This function updates the pin Object in the pins Array with
             * the new lat, lng coordinates and emits the new pin, and its index in the Array, back to the parent.
             * @param movePinEvent the event containing lat, lng
             * @param pinIndex the index of the pin in Array pins
             */
            repositionPin(movePinEvent, pinIndex) {
                if (pinIndex < this.pins.length) {
                    this.pins[pinIndex].lat = movePinEvent.lat();
                    this.pins[pinIndex].lng = movePinEvent.lng();
                    this.$emit("pin-move", this.pins[pinIndex], pinIndex)
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
             * Updates this.currentCenter with the current location from the api.  Its not practical for this to
             * return a value and then set it equal to this.currentCenter because the call to get the center is async.
             */
            updateCenter() {
                this.$refs.mapRef.$mapPromise.then((map) => {
                    const mapBounds = map && map.getBounds();
                    const mapCenter = mapBounds && mapBounds.getCenter();

                    this.currentCenter = {lat: mapCenter && mapCenter.lat(), lng: mapCenter && mapCenter.lng()};
                });
            }
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