import Vue from 'vue'
import { BootstrapVue, BootstrapVueIcons, IconsPlugin } from 'bootstrap-vue'
import App from './App/App'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import VueLogger from 'vuejs-logger';
import router from './index'

Vue.config.productionTip = false;

Vue.use(VueLogger);
// Install BootstrapVue
Vue.use(BootstrapVue);
Vue.use(BootstrapVueIcons);
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin);

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