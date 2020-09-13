import "vue-jest"
import api from '../Api'
import {shallowMount, createLocalVue} from "@vue/test-utils";
import router from "../index";

import ActivitySearch from "../views/Search/ActivitySearch";
import "jest";
import BootstrapVue from "bootstrap-vue";

const localVue = createLocalVue();
localVue.use(BootstrapVue);

jest.mock('../Api');

const ACTIVITY_TYPES = [
    "Biking", "Hiking", "Athletics"
];

let activitySearch;

beforeEach(() => {
    api.getActivityTypes.mockImplementation(() =>
        Promise.resolve({
            data: ACTIVITY_TYPES,
            status: 200
        })
    );

    api.getUserId.mockImplementation(() => Promise.resolve({status: 200}));

    api.logout.mockImplementation(() => Promise.resolve({status:200}));

    activitySearch = shallowMount(ActivitySearch, {
        router,
        mocks: {api},
        localVue
    });
});

test('Is a vue instance', () => {
    expect(activitySearch.isVueInstance).toBeTruthy();
});

test('Activity type search box exists', () => {
    expect(activitySearch.find('#searchBoxActivities').exists()).toBeTruthy();
});

test('Search button exists', () => {
    expect(activitySearch.find('#searchButton').exists()).toBeTruthy();
});

test('And search operator radio button exists', () => {
    expect(activitySearch.find('#andRadioButton').exists()).toBeTruthy();
});

test('Or search operator radio button exists', () => {
    expect(activitySearch.find('#orRadioButton').exists()).toBeTruthy();
});

