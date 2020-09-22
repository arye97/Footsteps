<template>
    <div class="col-sm-6 offset-sm-3">
        <keep-alive include="Search" max="3">
            <router-view :key="$route.fullPath"></router-view>
        </keep-alive>
    </div>
</template>

<script>
    
  import 'bootstrap-vue/dist/bootstrap-vue.css'
    export default {
        name: "app"
    }

</script>

<style>
    body{
        background-image: url('../../assets/jpg/mt1.jpg');
        width: 100%;
        top: 0;
        left: 0;
    }


    .contents{
        border-radius: 5px;
        background-color: white;
        padding-bottom: 12px;
    }

    .contentsExtendedBottom{
        border-radius: 5px;
        background-color: white;
        padding-bottom: 63px;
    }



</style>
