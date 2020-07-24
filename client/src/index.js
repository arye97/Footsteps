import Vue from 'vue'
import Router from 'vue-router'
import Login from './views/Login/Login.vue'
import Register from './views/Register/Register.vue'
import Home from './views/Home/Home.vue'
import AdminDashboard from './views/AdminDashboard';
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import ViewUser from "./components/layout/ViewUser.vue";
import CreateActivity from "./views/Activities/CreateActivity";
import EditActivity from "./views/Activities/EditActivity";
import AllActivities from "./views/Activities/AllActivities";
import EditProfile from "./views/Settings/EditProfile";
Vue.use(Router);
Vue.use(BootstrapVue);
Vue.use(IconsPlugin);

export default new Router({
    mode: 'history',
    base: process.env.VUE_APP_BASE_URL,
    routes: [
        {   path: '/',
            name: 'Home',
            component: Home
        },
        {   path: '/login',
            name: 'login',
            component: Login
        },
        {   path: '/register',
            name: 'register',
            component: Register
        },
        {
            path: '/profile',
            name: 'myProfile',
            component: ViewUser
        },
        {
            path: '/profile/:userId',
            name: 'profile',
            component: ViewUser
        },
        {   path: '/admin',
            component: AdminDashboard
        },
        {
            path: '/activities/create',
            name: 'submitCreateActivity',
            component: CreateActivity
        },
        {
            path: '/activities/edit/:activityId',
            name: 'editActivity',
            component: EditActivity
        },
        {
            path: '/activities/',
            name: 'allActivities',
            component: AllActivities
        },
        {
            path: '/profile/:userId/edit',
            name: 'editProfile',
            component: EditProfile
        },
        // otherwise redirect to home
        { path: '/*', redirect: '/login' }
    ]
});