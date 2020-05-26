import Vue from 'vue'
import Router from 'vue-router'
import Login from './views/Login/Login.vue'
import Register from './views/Register/Register.vue'
import Home from './views/Home/Home.vue'
import EditEmail from "./views/Settings/EditEmail";
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
        {path: '/profile/emails', component: EditEmail},
        {
            path: '/profile/:userId/details',
            name: 'details',
            component: Details
        },
        {
            path: '/profile/details',
            name: 'detailsNoID',
            component: Details
        },
        {
            path: '/profile', component: ViewUser,
            name: 'myProfile'
        },
        {
            path: '/profile/:userId',
            name: 'profile',
            component: ViewUser
        },
        // otherwise redirect to home
        { path: '/*', redirect: '/login' }
    ]
});