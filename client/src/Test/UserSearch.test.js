import "vue-jest"
import api from '../Api'
import {shallowMount} from "@vue/test-utils";
import router from "../index";

import UserSearch from "../views/Search/UserSearch";
import "jest";

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

let userSearch;

beforeEach(() => {
    userSearch = shallowMount(UserSearch, {
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
            data: SEARCH_RESPONSE1.slice((userSearch.vm.$data.currentPage - 1) * pageSize, userSearch.vm.$data.currentPage * pageSize),
            status: 200,
            headers: {
                "total-rows": SEARCH_RESPONSE1.length
            }
        })
    );
});

test('Is a vue instance', () => {
    expect(userSearch.isVueInstance).toBeTruthy();
});

test('Activities search box exists', () => {
    expect(userSearch.find('#searchBoxActivities').exists()).toBeTruthy();
});

test('Search button exists', () => {
    expect(userSearch.find('#searchButton').exists()).toBeTruthy();
});

test('And search operator radio button exists', () => {
    expect(userSearch.find('#andRadioButton').exists()).toBeTruthy();
});

test('Or search operator radio button exists', () => {
    expect(userSearch.find('#orRadioButton').exists()).toBeTruthy();
});


describe("Searching user based on activity types", () => {

    describe("With the 'or' method", () => {

        test("Search user with one activity type 'hiking'", () => {

            api.getUsersByActivityType.mockImplementation(() =>
                Promise.resolve({
                    data: SEARCH_RESPONSE1.slice((userSearch.vm.$data.currentPage  - 1) * pageSize, userSearch.vm.$data.currentPage  * pageSize),
                    status: 200,
                    headers: {
                        "total-rows": SEARCH_RESPONSE1.length
                    }
                })
            );



            userSearch.setData({
                selectedActivityTypes: [ "Hiking" ],
                searchType: "or"
            });

            return userSearch.vm.search().then(() => {
                expect(userSearch.vm.api.getUsersByActivityType).toHaveBeenCalledWith(["Hiking"], "or", userSearch.vm.$data.currentPage - 1);
            });
        });


        test("Search user with two activity types 'hiking' and 'biking'", () => {

            api.getUsersByActivityType.mockImplementation(() =>
                Promise.resolve({
                    data: SEARCH_RESPONSE1.slice((userSearch.vm.$data.currentPage  - 1) * pageSize, userSearch.vm.$data.currentPage  * pageSize),
                    status: 200,
                    headers: {
                        "total-rows": SEARCH_RESPONSE1.length
                    }
                })
            );

            userSearch.setData({
                selectedActivityTypes: [ "Hiking", "Biking" ],
                searchType: "or"
            });
            return userSearch.vm.search().then(() => {
                expect(userSearch.vm.api.getUsersByActivityType).toHaveBeenCalledWith(["Hiking", "Biking"], "or", userSearch.vm.$data.currentPage - 1);
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

    userSearch.setData({
        activityTypes: [
            '4×4 Driving Experience',
            'Aeroplane flying and Aerobatics',
            'Airsoft'
        ],
    });
    return userSearch.vm.fetchActivityTypes().then(() => {
        expect(userSearch.vm.api.getActivityTypes).toHaveBeenCalledWith();
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
        expect(userSearch.vm.$data.userList).toEqual([]);
        await userSearch.vm.api.getUsersByActivityType(["Hiking", "Biking"], "or", userSearch.vm.$data.currentPage - 1)
            .then((response) => {
                expect(response.headers['total-rows']).toBe(6);
            });
        userSearch.vm.getPaginatedUsersByActivityType().then(() => {
            expect(userSearch.vm.api.getUsersByActivityType).toHaveBeenCalledWith(["Hiking", "Biking"], "or", userSearch.vm.$data.currentPage - 1);
            expect(userSearch.vm.$data.userList).toEqual(
                SEARCH_RESPONSE1.slice((userSearch.vm.$data.currentPage - 1) * userSearch.vm.$data.usersPerPage,
                    userSearch.vm.$data.currentPage * userSearch.vm.$data.usersPerPage));
        });
    });
    test('Second page contains usersPerPage number of users (2nd half of users)', async () => {
        window.scrollTo = jest.fn();
        userSearch.vm.$data.currentPage = 2;
        userSearch.vm.getPaginatedUsersByActivityType().then(() => {
            userSearch.vm.api.getUsersByActivityType(["Hiking", "Biking"], "or", userSearch.vm.$data.currentPage - 1);
            expect(userSearch.vm.$data.userList).toEqual(SEARCH_RESPONSE1.slice(
                (userSearch.vm.$data.currentPage - 1) * userSearch.vm.$data.usersPerPage,
                userSearch.vm.$data.currentPage * userSearch.vm.$data.usersPerPage));
        });
    });
});