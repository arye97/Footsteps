import Vue from 'vue';
import { BootstrapVue, BootstrapVueIcons, IconsPlugin } from 'bootstrap-vue';
import App from './App/App';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import VueLogger from 'vuejs-logger';
import router from './index';
import * as GmapVue from 'gmap-vue';

// Used by env variables
require('dotenv').config();

Vue.config.productionTip = false;

Vue.use(VueLogger);
// Install BootstrapVue
Vue.use(BootstrapVue);
Vue.use(BootstrapVueIcons);
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin);

Vue.use(GmapVue, {
  load: {
    key: process.env["VUE_APP_GOOGLE_MAPS_API_KEY"],
  },
  installComponents: true
});

/* eslint-disable no-new */
new Vue({
  el: '#app',
  template: '<App/>',
  router,
  components: { App }
});

/* Token for authentication */
export const tokenStore = {
  state: {
    token: null
  },
  setToken(newToken) {
    tokenStore.state.token = newToken
  }
};