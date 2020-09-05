<template>
    <div>
        <map-viewer
                :center="center"
                :pins="pins"
        ></map-viewer>
        <b-row>
            <b-col>
                <b-form-input
                    id="locationInput"
                    v-model="locationInput"
                    type="text"
                    placeholder="Input address here"
                ></b-form-input>
            </b-col>
            <b-col>
                <b-button
                        id="LocationSubmit"
                        v-on:click="addLocation()"
                >Place pin</b-button>
            </b-col>
        </b-row>

    </div>

</template>

<script>
    import MapViewer from "../../components/Map/MapViewer";
    import { gmapApi } from 'gmap-vue';

    export default {
        name: "LocationIO",

        components: {
            MapViewer
        },

        computed: {
            google: gmapApi
        },

        data() {
            return {
                pins:[{id:1, lat:-43.53, lng:172.63}],
                center:{lat:-43.53, lng:172.63},
                locations: [],
                locationInput: ""
            }
        },

        methods: {

            addLocation() {
                //todo: validate and format location
                this.locations.push(this.locationInput);
            }
        }
    }
</script>

<style scoped>

</style>