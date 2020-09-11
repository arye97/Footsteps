<template>
    <div id="mapDiv" class="map-pane">
        <keep-alive>
            <GmapMap class="map"
                    ref="mapRef"
                    :center="center"
                    :zoom="4.5"
                    map-type-id="terrain"
                    style="width: 500px; height: 300px"
            >
                <GmapMarker
                        :ref="'marker' + pinIndex"
                        :key="pinIndex"
                        v-for="(pin, pinIndex) in pins"
                        :position="google && new google.maps.LatLng(pin.lat, pin.lng)"
                        :clickable="true"
                        :draggable="true"
                        @click="center={lat:pin.lat, lng:pin.lng}"
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
            center: {
                default() {
                    return {lat:-40.9006, lng:174.8860};  // Coordinates of New Zealand
                },
                type: Object,
                validator: function (center) {
                    // Center must have lat and lng
                    return !isNaN(center.lat) && !isNaN(center.lng);
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
            }
        },

        async mounted() {

        },

        methods: {

            /**
             * When a pin is dragged to a new location, this function updates the pin Object in the pins Array with
             * the new lat, lng coordinates.
             * @param movePinEvent the event containing lat, lng
             * @param pinIndex the index of the pin in Array pins
             */
            repositionPin(movePinEvent, pinIndex) {
                if (pinIndex < this.pins.length) {
                    this.pins[pinIndex].lat = movePinEvent.lat();
                    this.pins[pinIndex].lng = movePinEvent.lng();
                }
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