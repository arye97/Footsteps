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

let pageSize = 5;

const SEARCH_RESPONSE1 = [
    {
        "firstname": "DJ",
        "lastname": "Roomba",
        "activity_name": "PLACEHOLDER",
        "activityTypes": [
            {
                "activityTypeId": 12,
                "name": "Biking"
            },
            {
                "activityTypeId": 34,
                "name": "Hiking"
            }
        ]
    },
    {
        "firstname": "Akira",
        "lastname": "Kurosawa",
        "activityName": "PLACEHOLDERS",
        "activityTypes": [
            {
                "activityTypeId": 12,
                "name": "Biking"
            },
            {
                "activityTypeId": 34,
                "name": "Hiking"
            }
        ]
    },
    {
        "firstname": "Samantha",
        "lastname": "Saliva",
        "activity_name": "lmao",
        "activityTypes": [
            {
                "activityTypeId": 7,
                "name": "Athletics"
            },
            {
                "activityTypeId": 34,
                "name": "Hiking"
            }
        ]
    },
    {
        "firstname": "Manny",
        "lastname": "Mannamynamo",
        "activity_name": "idk",
        "activityTypes": [
            {
                "activityTypeId": 12,
                "name": "Biking"
            }
        ]
    },
    {
        "firstname": "Jenny",
        "lastname": "Mariam",
        "activity_name": "Another",
        "activityTypes": [
            {
                "activityTypeId": 12,
                "name": "Biking"
            }
        ]
    },
    {
        "firstname": "Mary",
        "lastname": "Sidoarjo",
        "activity_name": "Another Another Kaikoura Coast Track race",
        "activityTypes": [
            {
                "activityTypeId": 34,
                "name": "Hiking"
            }
        ]
    }
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
                activityTitle: ["PLACEHOLDER"],
            });

            return activitySearch.vm.getPaginatedActivitiesByActivityTitle().then(() => {
                expect(activitySearch.vm.api.getActivityByActivityTitle).toHaveBeenCalledWith(["PLACEHOLDER"], activitySearch.vm.$data.currentPage - 1);
            });
        });

    describe("With the '+' method", () => {

        test("Search activity with two activity titles 'PLACEHOLDER' and 'PLACEHOLDERS'", () => {

            api.getActivityByActivityTitle.mockImplementation(() =>
                Promise.resolve({
                    data: SEARCH_RESPONSE1.slice((activitySearch.vm.$data.currentPage - 1) * pageSize, activitySearch.vm.$data.currentPage * pageSize),
                    status: 200,
                    headers: {
                        "total-rows": SEARCH_RESPONSE1.length
                    }
                })
            );

            activitySearch.setData({
                activityTitle: ["PLACEHOLDER + PLACEHOLDERS"],
            });

            return activitySearch.vm.getPaginatedActivitiesByActivityTitle().then(() => {
                expect(activitySearch.vm.api.getActivityByActivityTitle).toHaveBeenCalledWith(["PLACEHOLDER + PLACEHOLDERS"], activitySearch.vm.$data.currentPage - 1);
            });
        });
    });

    describe("With the '-' method", () => {

        test("Search activity with two activity titles 'PLACEHOLDER' excluding 'PLACEHOLDERS'", () => {

            api.getActivityByActivityTitle.mockImplementation(() =>
                Promise.resolve({
                    data: SEARCH_RESPONSE1.slice((activitySearch.vm.$data.currentPage - 1) * pageSize, activitySearch.vm.$data.currentPage * pageSize),
                    status: 200,
                    headers: {
                        "total-rows": SEARCH_RESPONSE1.length
                    }
                })
            );

            activitySearch.setData({
                activityTitle: ["PLACEHOLDER - PLACEHOLDERS"],
            });

            return activitySearch.vm.getPaginatedActivitiesByActivityTitle().then(() => {
                expect(activitySearch.vm.api.getActivityByActivityTitle).toHaveBeenCalledWith(["PLACEHOLDER - PLACEHOLDERS"], activitySearch.vm.$data.currentPage - 1);
            });
        });
    });

    describe('With the "string" method', () => {

        test('Search activity with two activity titles "PLACEHOLDER"', () => {

            api.getActivityByActivityTitle.mockImplementation(() =>
                Promise.resolve({
                    data: SEARCH_RESPONSE1.slice((activitySearch.vm.$data.currentPage - 1) * pageSize, activitySearch.vm.$data.currentPage * pageSize),
                    status: 200,
                    headers: {
                        "total-rows": SEARCH_RESPONSE1.length
                    }
                })
            );

            activitySearch.setData({
                activityTitle: ['"PLACEHOLDER"'],
            });

            return activitySearch.vm.getPaginatedActivitiesByActivityTitle().then(() => {
                expect(activitySearch.vm.api.getActivityByActivityTitle).toHaveBeenCalledWith(['"PLACEHOLDER"'], activitySearch.vm.$data.currentPage - 1);
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

