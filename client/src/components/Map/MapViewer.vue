<template>
    <div id="mapDiv">
<!--        map goes here-->
    </div>
</template>

<script>
    export default {
        name: "MapViewer.vue",

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