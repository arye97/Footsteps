import Vue from 'vue'
import Router from 'vue-router'
import Login from './views/Login/Login.vue'
import Register from './views/Register/Register.vue'
import Home from './views/Home/Home.vue'
import EditEmail from "./views/EditEmails/EditEmail";
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import Details from "./views/Settings/Details.vue";
import ViewUser from "./components/layout/ViewUser.vue";
Vue.use(Router);
Vue.use(BootstrapVue);
Vue.use(IconsPlugin);

export default new Router({
  mode: 'history',
  routes: [
  {path: '/', component: Home},
  {path: '/login', component: Login},
  {path: '/register', component: Register},
  {path: '/profile', component: ViewUser},
  {path: '/profile/emails', component: EditEmail},
  {path: "/profile/details", component: Details},

  // otherwise redirect to home
  { path: '/*', redirect: '/login' }
  ]
});