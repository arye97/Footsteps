<template>
    <div id="app">
        <div>
            <div class="container">
                <div class="row">
                    <div class="col-sm-6 offset-sm-3">
                        <Header />
                        <router-view></router-view>
                    </div>
                </div>
            </div>
        </div>
        <HomeLayout></HomeLayout>
    </div>
</template>

<script>
    import HomeLayout from '../../components/layout/HomeLayout.vue'
    import Header from '../../components/Header/Header.vue'
    export default {
        name: "Home",
        components: {HomeLayout, Header}
   }
</script>

<style scoped>
</style>