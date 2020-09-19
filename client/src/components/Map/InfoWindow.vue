<template>
    <div>
        <div v-if="data.error">
            <h3>There was an error loading the info for this pin</h3>
        </div>
        <div v-else>
            <b>{{  data.title  }}</b><br/>
            <i v-if="data.location.name">{{  data.location.name  }}</i><br/>
            <div v-if="data.start_time">
                Starts: {{  this.dateToHRDate(data.start_time)  }}<br/>
                Ends: {{  this.dateToHRDate(data.end_time)  }}
            </div>
            <div>
                Latitude: {{  data.location.latitude  }}<br/>
                Longitude: {{  data.location.longitude  }}
            </div>
        </div>
    </div>
</template>

<script>
import {formatDateTime} from "../../util";

export default {
name: "InfoWindow",
    props: {
        data: {
            type: Object,
        },
    },
    methods: {
        /**
         * Converts a date to a human readable format
         * @param date the  date to convert
         */
        dateToHRDate(date) {
            return formatDateTime(date);
        }
    }
}
</script>

<style scoped>

</style>