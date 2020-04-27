import Vue from 'vue'
import App from './App/App'

Vue.config.productionTip = false;
import VueLogger from 'vuejs-logger';
import router from './index'

Vue.config.productionTip = false;

Vue.use(VueLogger);

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
