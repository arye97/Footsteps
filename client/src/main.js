import Vue from 'vue'
import App from './App/App'

Vue.config.productionTip = false;
import VueLogger from 'vuejs-logger';
import router from './index'
import VueSession from "vue-session";


Vue.config.productionTip = false;

const options = {
  isEnabled: true,
  logLevel : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : false,
  separator: '|',
  showConsoleColors: true
};

Vue.use(VueLogger, options);

Vue.use(VueSession, {persist: true});

/* eslint-disable no-new */
new Vue({
  el: '#app',
  template: '<App/>',
  router,
  components: { App }
});
