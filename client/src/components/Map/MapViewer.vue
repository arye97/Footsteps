<template>
    <div id="mapDiv">
        <GmapMap
                ref="mapRef"
                :center="{lat:10, lng:10}"
                :zoom="7"
                map-type-id="terrain"
                style="width: 500px; height: 300px"
        >
            <!--<GmapMarker-->
                    <!--ref="myMarker"-->
                    <!--:key="index"-->
                    <!--v-for="(m, index) in markers"-->
                    <!--:position="m.position"-->
                    <!--:clickable="true"-->
                    <!--:draggable="true"-->
                    <!--@click="center=m.position"-->
            <!--/>-->
            <GmapMarker ref="myMarker" :position="google && new google.maps.LatLng(1.38, 103.8)" />
        </GmapMap>
    </div>
</template>

<script>
    import { gmapApi } from 'gmap-vue';

    export default {
        name: "MapViewer",

        computed: {
            google: gmapApi
        },

        data() {
            return {
                locations: []
            }
        },

        async mounted() {

        },

        mehtods: {

            /**
             * Set the set of locations to be shown on the map
             * @param locationsJson json formatted list of locations to be displayed on the map
             */
            setLocations(locationsList) {
                //todo: check for validity of locationsJson
                this.locations = locationsList;
            },

            /**
             * Adds a list of json format locations to the existing locations list
             * @param locationsList json formatted locations to be added to the current locations list
             */
            addLocations(locationsList) {
                //todo: check for validity of locationsJson
                this.locations.concat(locationsList)
            },

            /**
             * Remove all locations from locations display list
             */
            clearLocations() {
                this.locations = []
            }
        }
    }

</script>

<style scoped>

</style>