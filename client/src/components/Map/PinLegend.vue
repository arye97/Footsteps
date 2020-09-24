<template>
    <div>
        <table>
            <tr v-if="redPinDescription">
                <td><span id='red-legend'></span></td>
                <td class="pin-description"><b>{{ redPinDescription }}</b></td>
            </tr>
            <tr v-if="bluePinDescription">
                <td><span id='blue-legend'></span></td>
                <td class="pin-description"><b>{{ bluePinDescription }}</b></td>
            </tr>
            <tr v-if="greenPinDescription">
                <td><span id='green-legend'></span></td>
                <td class="pin-description"><b>{{ greenPinDescription }}</b></td>
            </tr>
        </table>
    </div>
</template>

<script>
    export default {
        name: "PinLegend",
        props: {
            /**
             * Dictates variants of Pin Legends
             * Can be ViewUser, AllActivities, or ActivitySearch, ViewActivity
             * indicating the page that requires Pin Legends
             */
            mode: {
                default: null,
                type: String
            },
        },
        data() {
            return {
                redPinDescription: null,
                bluePinDescription: null,
                greenPinDescription: null,
            }
        },
        mounted() {
            switch(this.mode) {
                case "ViewUser":
                    this.redPinDescription = "Your Location(s)";
                    break;
                case "ActivitySearch":
                    this.redPinDescription = "Your Current Location";
                    this.bluePinDescription = "Your Activities";
                    this.greenPinDescription = "Other Activities";
                    break;
                case "AllActivities":
                    this.redPinDescription = "Your Current Location";
                    this.bluePinDescription = "Your Activities";
                    this.greenPinDescription = "Followed Activities";
                    break;
                case "ViewActivity":
                    this.bluePinDescription = "This Activity";
                    break;
            }
        }
    }
</script>

<style scoped>
    div {
        margin-top: 20px;
        vertical-align:middle
    }

    table {
        display:inline-block;
        text-align: left;
    }

    #red-legend {
        background: url('http://maps.google.com/mapfiles/ms/icons/red-dot.png') no-repeat;
        height: 32px;
        width: 40px;
        display: block;
    }

    #blue-legend {
        background: url('http://maps.google.com/mapfiles/ms/icons/blue-dot.png') no-repeat;
        height: 32px;
        width: 36px;
        display: block;
    }

    #green-legend {
        background: url('http://maps.google.com/mapfiles/ms/icons/green-dot.png') no-repeat;
        height: 32px;
        width: 36px;
        display: block;
    }

    tr {
        height: 40px;
    }
</style>