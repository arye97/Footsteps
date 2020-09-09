<template>
    <div id="mapDiv" class="map-box">
        <keep-alive>
            <GmapMap class="map"
                    ref="mapRef"
                    :center="center"
                    :zoom="4.5"
                    map-type-id="terrain"
                    style="width: 500px; height: 300px"
            >
                <GmapMarker
                        ref="myMarker"
                        :key="index"
                        v-for="(pin, index) in pins"
                        :position="google && new google.maps.LatLng(pin.lat, pin.lng)"
                        :clickable="true"
                        :draggable="true"
                        @click="center={lat:pin.lat, lng:pin.lng}"
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
    .map-box {
        display: flex;
        align-items: center;
        justify-content: center;
    }
</style>