import Vue from 'vue'
import Router from 'vue-router'
import Login from './views/Login/Login.vue'
import Register from './views/Register/Register.vue'
import Home from './views/Home/Home.vue'
import ViewUser from './components/layout/ViewUser'
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'

Vue.use(Router);
Vue.use(BootstrapVue);
Vue.use(IconsPlugin)

export default new Router({
  routes: [
  {path: '/', component: Home},
  {path: '/login', component: Login},
  {path: '/register', component: Register},
  {path: '/profile', component: ViewUser},

  // otherwise redirect to home
  { path: '*', redirect: '/login' }
  ]
});