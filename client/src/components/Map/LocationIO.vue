<template>
    <div>
        <b-card class="flex-fill" border-variant="secondary">
            <div v-if="isMapVisible">
                <b-button id="hideMapButton" @click="isMapVisible=false">Hide Map</b-button>
                    <map-viewer
                            id="mapComponent"
                            :center="center"
                            :pins="pins"
                    ></map-viewer>
                <div v-if="!viewOnly">
                    <h3 class="font-weight-light"><strong>Search and add a pin</strong></h3>

                    <!--We should add to fields in :options if we want to receive other data from the API-->
                    <gmap-autocomplete
                            id="gmapAutoComplete"
                                :options="{fields: ['geometry', 'formatted_address', 'address_components']}"
                            @place_changed="getPlaceField"
                            class="form-control" style="width: 100%">
                    </gmap-autocomplete>

                    <b-button id='addMarkerButton' block @click="addMarker">Add</b-button>
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

        props: {
            viewOnly: {
                default: false,
                type: Boolean
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
                pins: [],
                center:undefined,
                currentPlace: null
            }
        },


        watch: {
            /**
             * Watches the props so that they can be updated after mounting
             */
            parentPins() {
                if (this.parentPins) {
                    this.pins.push(...this.parentPins);
                }
            },
            parentCenter() {
                if (this.parentCenter) {
                    this.center = this.parentCenter;
                }
            }
        },

      methods: {

            /**
             * Receives a place object via the autocomplete component
             * @param place response object from Google API that includes latitude and longitude
             */
            getPlaceField(place) {
                this.currentPlace = place;
            },

            /**
             * Add a marker centred on the coordinates of this.currentPlace
             */
            addMarker() {
                if (this.currentPlace) {
                    const pin = {
                        lat: this.currentPlace.geometry.location.lat(),
                        lng: this.currentPlace.geometry.location.lng()
                    };
                    this.pins.push(pin);
                    this.center = pin;
                    this.currentPlace = null;
                }
            }
        }
    }
</script>

<style scoped>

</style>