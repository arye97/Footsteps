<template>
    <div>
        <h1 v-if="location.type">{{  location.type  }}</h1>
        <div>{{  location.name  }}</div>
        <div>
            Latitude: {{  location.latitude  }}
            Longitude: {{  location.longitude  }}
        </div>
    </div>
</template>

<script>
export default {
name: "LocationInfoWindow",
    props: {
        location: {
            type: Object
        }
    }
}
</script>

<style scoped>

</style>