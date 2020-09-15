<template>
    <div>
        <h1>{{  activity.activity_name  }}</h1>
        <h2>{{  activity.location.name  }}</h2>
        <div>
            {{  activity.description  }}
        </div>
    </div>
</template>

<script>
export default {
name: "ActivityInfoWindow",
    props: {
        activity: {
            type: Object
        }
    }
}
</script>

<style scoped>

</style>