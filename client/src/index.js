import Vue from 'vue'
import Router from 'vue-router'
import Login from './views/Login/Login.vue'
import Register from './views/Register/Register.vue'
import Home from './views/Home/Home.vue'
import AdminDashboard from './views/AdminDashboard';
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import ViewUser from "./components/layout/ViewUser.vue";
import CreateActivity from "./views/Activities/CreateActivity";
import ViewActivity from "./views/Activities/ViewActivity";
import EditActivity from "./views/Activities/EditActivity";
import AllActivities from "./views/Activities/AllActivities";
import EditProfile from "./views/Settings/EditProfile";
import Search from "./views/Search/Search";
Vue.use(Router);
Vue.use(BootstrapVue);
Vue.use(IconsPlugin);

/**
* Guard my route function is a function that checks if a user is 
* authenticated by checking session storage for a token. It will then check the
* associated boolean variable and if this is true it will go to the home page,
* if not it will take you to the login page. This function is specifically built
* for users trying to access the login page whilst being logged in already. But 
* can be refactored to be used as a general router guard with minor changes.
* @param to - the target Route Object being navigated to.
* @param from - the current route being navigated away from.
* @param next - move on to the next hook in the pipeline. If no hooks are left, 
* the navigation is confirmed.
*/
function guardMyroute(to, from, next)
{
    //Boolean variable for authenticated user
    let isAuthenticated = false;
    //uses sessionstorage to figure out if a user is logged in or not, goes through
    //if not assigns false to boolean variable to show that user is not logged in
    if(sessionStorage.getItem("token"))
        isAuthenticated = true;
    else
        isAuthenticated= false;
    //Checks if the user is authenticated, if user is authenticated will go to home page
    //because you shouldn't be able to access the login page if so
    if(!isAuthenticated)
    {
        next(); // allow to enter login route
    }
    else
    {
        next('/'); // go to '/' home page;
    }
}

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
            beforeEnter: guardMyroute,
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
            component: AllActivities,
            props: true,
        },
        {
            path: '/profile/:userId/edit',
            name: 'editProfile',
            component: EditProfile
        },
        {
            path: '/profile/edit',
            name: 'editMyProfile',
            component: EditProfile
        },
        {
            path: '/search/users',
            name: 'searchPage',
            component: Search
        },
        {
            path: '/activity/:activityId',
            name: 'viewActivity',
            component: ViewActivity
        },
        // otherwise redirect to home
        { path: '/*', redirect: '/login' }
    ]
});