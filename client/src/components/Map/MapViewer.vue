<template>
    <div id="mapDiv">
        <GmapMap
                ref="mapRef"
                :center="currentCenter"
                :zoom="4.5"
                map-type-id="terrain"
                style="width: 500px; height: 300px"
        >
            <GmapMarker
                    ref="myMarker"
                    :key="pin.id"
                    v-for="pin in pins"
                    :position="google && new google.maps.LatLng(pin.lat, pin.lng)"
                    :clickable="true"
                    :draggable="true"
                    @click="currentCenter={lat:pin.lat, lng:pin.lng}"
            />
        </GmapMap>
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
                    return pins.every(pin => {return !isNaN(pin.lat) && !isNaN(pin.lng) && !isNaN(pin.id);});
                }
            }
        },
        computed: {
            google: gmapApi
        },

        data() {
            return {
                currentCenter: this.center
            }
        },

        async mounted() {

        },

        methods: {

        }
    }

</script>

<style scoped>

</style>