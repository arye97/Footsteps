<template>
    <div>
        <map-viewer
                :center="center"
                :pins="pins"
        ></map-viewer>
        <b-row>
            <b-col>
                <b-form-input
                    id="latInput"
                    v-model="latInput"
                    type="number"
                    placeholder="Input lat here"
                ></b-form-input>
            </b-col>
            <b-col>
                <b-form-input
                        id="lngInput"
                        v-model="lngInput"
                        type="number"
                        placeholder="Input lng here"
                ></b-form-input>
            </b-col>
        </b-row>
        <b-button
            id="LocationSubmit"
            v-on:click="addLocation()"
        >Place pin</b-button>
    </div>

</template>

<script>
    import MapViewer from "../../components/Map/MapViewer";

    export default {
        name: "LocationIO",

        components: {
            MapViewer
        },

        data() {
            return {
                pins:[{id:1, lat:-43.53, lng:172.63}],
                center:{lat:-43.53, lng:172.63},
                latInput: 0,
                lngInput: 0
            }
        },

        methods: {
            addLocation() {
                //todo: get proper id
                let pin = {"id":this.pins.length+1,"lat":parseFloat(this.latInput),"lng":parseFloat(this.lngInput)};
                this.pins.push(pin);
                this.latInput =0;
                this.lngInput = 0;
                console.log(process.env["VUE_APP_GOOGLE_MAPS_API_KEY"]);
            }
        }
    }
</script>

<style scoped>

</style>