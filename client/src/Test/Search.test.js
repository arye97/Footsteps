import Search from '../views/Search';
import "vue-jest"
import api from '../Api'
import {shallowMount} from "@vue/test-utils";
import router from "../index";

import SearchPage from '../views/Search.vue'
import "jest"

jest.mock('../Api');

let searchPage;

beforeEach(() => {
    searchPage = shallowMount(SearchPage);
});

test('Is a vue instance', () => {
    expect(searchPage.isVueInstance).toBeTruthy();
});

test('Activities search bos exists', () => {
    expect(searchPage.find('#searchBoxActivities').exists()).toBeTruthy();
});

test('Search mode selector exists', () => {
    expect(searchPage.find('#searchModeSelect').exists()).toBeTruthy();
});

test('Search button exists', () => {
    expect(searchPage.find('#searchButton').exists()).toBeTruthy();
});

test('And search operator radio button exists', () => {
    expect(searchPage.find('#andRadioButton').exists()).toBeTruthy();
});

test('Or search operator radio button exists', () => {
    expect(searchPage.find('#orRadioButton').exists()).toBeTruthy();
});

let searchWrapper;

beforeEach(() => {
    searchWrapper = shallowMount(Search, { mocks: {api} });
});


describe("Searching user based on activity types", () => {
    describe("With the 'or' method", () => {
        test("Search user with one activity type 'hiking'", () => {
            let response = [
                {
                    "firstname": "DJ",
                    "lastname": "Roomba",
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
                }
            ];
            api.getUsersByActivityType.mockImplementation(() =>
                Promise.resolve({
                    data: response,
                    status: 200
                })
            );
            searchWrapper = shallowMount(Search, {router, mocks: {api}});
            searchWrapper.setData({
                selectedActivityTypes: [ "Hiking" ],
                searchType: "or"
            });
            return searchWrapper.vm.search("Hiking", "or").then(() => {
                expect(searchWrapper.vm.api.getUsersByActivityType).toHaveBeenCalledWith("Hiking", "or");
            });
        });

        test("Search user with two activity types 'hiking' and 'biking'", () => {
            let response = [
                {
                    "firstname": "DJ",
                    "lastname": "Roomba",
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
                    "activityTypes": [
                        {
                            "activityTypeId": 12,
                            "name": "Biking"
                        }
                    ]
                }
            ];

            api.getUsersByActivityType.mockImplementation(() =>
                Promise.resolve({
                    data: response,
                    status: 200
                })
            );
            searchWrapper = shallowMount(Search, {router, mocks: {api}});
            searchWrapper.setData({
                    selectedActivityTypes: [ "Hiking", "Biking" ],
                    searchType: "or"
            });
            return searchWrapper.vm.search("Hiking Biking", "or").then(() => {
                expect(searchWrapper.vm.api.getUsersByActivityType).toHaveBeenCalledWith("Hiking Biking", "or");
            });
        });
    });
});