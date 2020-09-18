<template>
    <div>
        <b>{{  data.title  }}</b><br/>
        <i v-if="data.location.name">{{  data.location.name  }}</i><br/>
        <div>
            Latitude: {{  data.location.latitude  }}<br/>
            Longitude: {{  data.location.longitude  }}
        </div>
    </div>
</template>

<script>
export default {
name: "InfoWindow",
    props: {
        data: {
            type: Object,
            title: String
        }
    }
}
</script>

<style scoped>

</style>