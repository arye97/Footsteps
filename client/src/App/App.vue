<template>
    <div class="col-sm-6 offset-sm-3">
        <router-view></router-view>
    </div>
</template>

<script>
    
  import 'bootstrap-vue/dist/bootstrap-vue.css'
    export default {
        name:
            "app"
    }

</script>

<style>
    body{
        background-image: url('../../assets/jpg/mt1.jpg');

        background-size: cover;
    }


    .contents{
        background-color: white;
        padding-bottom: 12px;
    }

    .contentsExtendedBottom{
        background-color: white;
        padding-bottom: 63px;
    }



</style>
