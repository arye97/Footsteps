import "vue-jest"
import api from '../Api'
import {shallowMount} from "@vue/test-utils";
import router from "../index";

import SearchPage from '../views/Search/Search.vue'
import "jest"

jest.mock('../Api');

const SEARCH_RESPONSE1 = [
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
    },
    {
        "firstname": "Jenny",
        "lastname": "Mariam",
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
        "activityTypes": [
            {
                "activityTypeId": 34,
                "name": "Hiking"
            }
        ]
    }
];

const ACTIVITY_TYPES = [
    "Biking", "Hiking", "Athletics"
];

let pageSize = 5;

let searchPage;


beforeEach(() => {
    searchPage = shallowMount(SearchPage, {
        methods: {
            logout: () => {},
        },
        router,
        mocks: {api}
    });

    api.getActivityTypes.mockImplementation(() =>
        Promise.resolve({
            data: ACTIVITY_TYPES,
            status: 200
        })
    );

    api.getUsersByActivityType.mockImplementation(() =>
        Promise.resolve({
            data: SEARCH_RESPONSE1.slice((searchPage.vm.$data.currentPage - 1) * pageSize, searchPage.vm.$data.currentPage * pageSize),
            status: 200,
            headers: {
                "total-rows": SEARCH_RESPONSE1.length
            }
        })
    );
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


describe("Searching user based on activity types", () => {

    describe("With the 'or' method", () => {

        test("Search user with one activity type 'hiking'", () => {

            api.getUsersByActivityType.mockImplementation(() =>
                Promise.resolve({
                    data: SEARCH_RESPONSE1.slice((searchPage.vm.$data.currentPage  - 1) * pageSize, searchPage.vm.$data.currentPage  * pageSize),
                    status: 200,
                    headers: {
                        "total-rows": SEARCH_RESPONSE1.length
                    }
                })
            );



            searchPage.setData({
                selectedActivityTypes: [ "Hiking" ],
                searchType: "or"
            });

            return searchPage.vm.search().then(() => {
                expect(searchPage.vm.api.getUsersByActivityType).toHaveBeenCalledWith(["Hiking"], "or", searchPage.vm.$data.currentPage - 1);
            });
        });


        test("Search user with two activity types 'hiking' and 'biking'", () => {

            api.getUsersByActivityType.mockImplementation(() =>
                Promise.resolve({
                    data: SEARCH_RESPONSE1.slice((searchPage.vm.$data.currentPage  - 1) * pageSize, searchPage.vm.$data.currentPage  * pageSize),
                    status: 200,
                    headers: {
                        "total-rows": SEARCH_RESPONSE1.length
                    }
                })
            );

            searchPage.setData({
                    selectedActivityTypes: [ "Hiking", "Biking" ],
                    searchType: "or"
            });
            return searchPage.vm.search().then(() => {
                expect(searchPage.vm.api.getUsersByActivityType).toHaveBeenCalledWith(["Hiking", "Biking"], "or", searchPage.vm.$data.currentPage - 1);
            });
        });

    });
});

test('Fetch list of activity types from back-end', () => {
    let activityTypes = [
        {
            activityTypeId: 1,
            name: "4×4 Driving Experience"
        },
        {
            activityTypeId: 2,
            name: "Aeroplane flying and Aerobatics"
        },
        {
            activityTypeId: 3,
            name: "Airsoft"
        }
    ];

    api.getActivityTypes.mockImplementation(() =>
        Promise.resolve({
            data: activityTypes,
            status: 200
        })
    );

    searchPage.setData({
        activityTypes: [
            '4×4 Driving Experience',
            'Aeroplane flying and Aerobatics',
            'Airsoft'
        ],
    });
    return searchPage.vm.fetchActivityTypes().then(() => {
        expect(searchPage.vm.api.getActivityTypes).toHaveBeenCalledWith();
    });
});

/**
 * The below tests are setup to assume usersPerPage has a value of 5 (AS IN THE SERVER).
 * Hence, usersPerPage is set to 5 for these tests.
 * This variable can be changed in Search.vue and will not effect these tests.
 */
describe('Pagination limits the user cards displayed to the user', () => {


    test('First page contains usersPerPage number of users (1st half of users)', async () => {
        window.scrollTo = jest.fn();
        expect(searchPage.vm.$data.userList).toEqual([]);
        await searchPage.vm.api.getUsersByActivityType(["Hiking", "Biking"], "or", searchPage.vm.$data.currentPage - 1)
            .then((response) => {
                expect(response.headers['total-rows']).toBe(6);
            });
        searchPage.vm.getPaginatedUsersByActivityType().then(() => {
            expect(searchPage.vm.api.getUsersByActivityType).toHaveBeenCalledWith(["Hiking", "Biking"], "or", searchPage.vm.$data.currentPage - 1);
            expect(searchPage.vm.$data.userList).toEqual(
                SEARCH_RESPONSE1.slice((searchPage.vm.$data.currentPage - 1) * searchPage.vm.$data.usersPerPage,
                                        searchPage.vm.$data.currentPage * searchPage.vm.$data.usersPerPage));
        });
    });
    test('Second page contains usersPerPage number of users (2nd half of users)', async () => {
        window.scrollTo = jest.fn();
        searchPage.vm.$data.currentPage = 2;
        searchPage.vm.getPaginatedUsersByActivityType().then(() => {
            searchPage.vm.api.getUsersByActivityType(["Hiking", "Biking"], "or", searchPage.vm.$data.currentPage - 1);
            expect(searchPage.vm.$data.userList).toEqual(SEARCH_RESPONSE1.slice(
                (searchPage.vm.$data.currentPage - 1) * searchPage.vm.$data.usersPerPage,
                searchPage.vm.$data.currentPage * searchPage.vm.$data.usersPerPage));
        });
    });
});