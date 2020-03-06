<template>
  <div id="app">
    <div class="jumbotron">
      <div class="container">
        <div class="row">
          <div class="col-sm-6 offset-sm-3">
            <Header />
            <router-view></router-view>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

  import 'bootstrap/dist/css/bootstrap.css'
  import 'bootstrap-vue/dist/bootstrap-vue.css'
  import Header from '../components/Header/Header';
    export default {
        name:
            "app",
        components: {
            Header
        }
    }

</script>

<style>
</style>
