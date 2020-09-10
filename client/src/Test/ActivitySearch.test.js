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


describe("Searching activity based on activity title", () => {

    describe("With the 'or' method", () => {

        test("Search user with activity title 'PLACEHOLDER'", () => {

            api.getActivityByActivityTitle.mockImplementation(() =>
                Promise.resolve({
                    data: SEARCH_RESPONSE1.slice((activitySearch.vm.$data.currentPage  - 1) * pageSize, activitySearch.vm.$data.currentPage  * pageSize),
                    status: 200,
                    headers: {
                        "total-rows": SEARCH_RESPONSE1.length
                    }
                })
            );



            activitySearch.setData({
                selectedActivityNames: [ "PLACEHOLDER" ],
                searchType: "or"
            });

            return activitySearch.vm.search().then(() => {
                expect(activitySearch.vm.api.getActivityByActivityTitle).toHaveBeenCalledWith(["PLACEHOLDER"], "or", activitySearch.vm.$data.currentPage - 1);
            });
        });


        test("Search activity with two activity titles 'PLACEHOLDER' and 'PLACEHOLDER'", () => {

            api.getActivityByActivityTitle.mockImplementation(() =>
                Promise.resolve({
                    data: SEARCH_RESPONSE1.slice((activitySearch.vm.$data.currentPage  - 1) * pageSize, activitySearch.vm.$data.currentPage  * pageSize),
                    status: 200,
                    headers: {
                        "total-rows": SEARCH_RESPONSE1.length
                    }
                })
            );

            activitySearch.setData({
                selectedActivityTypes: [ "PLACEHOLDER", "PLACEHOLDER" ],
                searchType: "and"
            });
            return activitySearch.vm.search().then(() => {
                expect(activitySearch.vm.api.getActivityByActivityTitle).toHaveBeenCalledWith(["PLACEHOLDER", "PLACEHOLDER"], "and", activitySearch.vm.$data.currentPage - 1);
            });
        });

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

