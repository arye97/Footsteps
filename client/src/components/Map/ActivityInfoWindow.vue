<template>
    <div>
        <b>{{  data.title  }}</b><br/>
        <i>{{  data.location.name  }}</i><br/>
        <div>
            {{  data.description  }}
        </div>
    </div>
</template>

<script>
export default {
name: "ActivityInfoWindow",
    props: {
        data: {
            type: Object
        }
    }
}
</script>

<style scoped>

</style>