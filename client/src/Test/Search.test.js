import "vue-jest";
import api from '../Api';
import {shallowMount} from "@vue/test-utils";
import router from "../index";

import SearchPage from '../views/Search/Search.vue';
import "jest";

jest.mock("../Api.js");

let searchPage;

const DEFAULT_ACTIVITY_TYPES =  [
    {
        activityTypeId: 1,
        name: "bobsledding"
    },
];

beforeEach(() => {
    api.logout.mockImplementation(() => Promise.resolve({status: 200}));
    api.getUserId.mockImplementation(() => Promise.resolve({ data: 1, status: 200}));
    api.getActivityTypes.mockImplementation(() => Promise.resolve({data: DEFAULT_ACTIVITY_TYPES, status: 200}));
    searchPage = shallowMount(SearchPage, {
        mocks: {
            api
        },
        router
    });

});

test('Is a vue instance', () => {
    expect(searchPage.isVueInstance).toBeTruthy();
});

test('Search mode selector exists', () => {
    expect(searchPage.find('#searchModeSelect').exists()).toBeTruthy();
});