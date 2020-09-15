import 'vue-jest'
import {createLocalVue, shallowMount} from "@vue/test-utils";
import {BootstrapVue} from "bootstrap-vue";
import AllActivities from "../views/Activities/AllActivities";
import router from "../index";


const localVue = createLocalVue();
localVue.use(BootstrapVue);

let allActivities;
let config;

beforeAll(() => {
    config = {
        router,
        localVue,
        data: function() {
            return {
                userId : 1
            }
        }
    };
    allActivities = shallowMount(AllActivities, config);
});

test('Is a vue instance', () => {
    expect(allActivities.isVueInstance).toBeTruthy();
});

test('map pane exists on all activities page', () => {
    expect(allActivities.find('#mapComponent').exists()).toBeTruthy();
});

test('activity list exists on all activities page', () => {
    expect(allActivities.find('#activityList').exists()).toBeTruthy();
});

test('create new activity button exists on all activities page', () => {
    expect(allActivities.find('#creatActivityButton').exists()).toBeTruthy();
});

test('header exists on all activities page', () => {
    expect(allActivities.find('#header').exists()).toBeTruthy();
});