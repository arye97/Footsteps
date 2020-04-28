<template>
    <div id="app">
        <HomeLayout></HomeLayout>
    </div>
</template>

<script>
    import HomeLayout from '../../components/layout/HomeLayout.vue'
    export default {
        name: "Home",
        components: {HomeLayout},
        data () {
            return {
                isloggedIn: Log.loggedIn
            }
        }
   }
</script>

<style scoped>
</style>