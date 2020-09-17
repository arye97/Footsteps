<template>
    <div>
        <b>{{  data.title  }}</b><br/>
        <i>{{  data.location.name  }}</i><br/>
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
            type: Object
        }
    }
}
</script>

<style scoped>

</style>