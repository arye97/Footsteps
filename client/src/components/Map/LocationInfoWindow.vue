<template>
    <div>
        <h3 v-if="data.title">{{  data.title  }}</h3>
        <div>{{  data.location.name  }}</div>
        <div>
            Latitude: {{  data.location.latitude  }}<br/>
            Longitude: {{  data.location.longitude  }}
        </div>
    </div>
</template>

<script>
export default {
name: "LocationInfoWindow",
    props: {
        data: {
            type: Object
        }
    }
}
</script>

<style scoped>

</style>