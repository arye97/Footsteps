import Vue from 'vue'
import Router from 'vue-router'
import Login from './views/Login/Login.vue'
import Register from './views/Register/Register.vue'
import Home from './views/Home/Home.vue'
import EditEmail from "./views/Settings/EditEmail";
import AdminDashboard from './views/AdminDashboard';
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import Details from "./views/Settings/Details.vue";
import ViewUser from "./components/layout/ViewUser.vue";
import EditPassword from "./views/Settings/EditPassword";
import CreateActivity from "./views/Activities/CreateActivity";
import EditActivity from "./views/Activities/EditActivity";
import AllActivities from "./views/Activities/AllActivities";
Vue.use(Router);
Vue.use(BootstrapVue);
Vue.use(IconsPlugin);

export default new Router({
    mode: 'history',
    base: process.env.VUE_APP_BASE_URL,
    routes: [
        {path: '/', component: Home},
        {path: '/login', component: Login},
        {path: '/register', component: Register},
        {
          path: '/profile/:userId/emails',
          name: 'emails',
          component: EditEmail
        },
        {
            path: '/profile/emails',
            name: 'emailsNoId',
            component: EditEmail
        },
        {
            path: '/profile/:userId/password',
            name: 'password',
            component: EditPassword
        },
        {
            path: '/profile/password',
            name: 'passwordNoID',
            component: EditPassword},
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
            name: 'createActivity',
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
        // otherwise redirect to home
        { path: '/*', redirect: '/login' }
    ]
});