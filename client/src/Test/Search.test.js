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
    }
];

const ACTIVITY_TYPES = [
    "Biking", "Hiking", "Athletics"
];

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
                    data: SEARCH_RESPONSE1,
                    status: 200
                })
            );



            searchPage.setData({
                selectedActivityTypes: [ "Hiking" ],
                searchType: "or"
            });
            return searchPage.vm.search().then(() => {
                expect(searchPage.vm.api.getUsersByActivityType).toHaveBeenCalledWith(["Hiking"], "or");
            });
        });


        test("Search user with two activity types 'hiking' and 'biking'", () => {

            api.getUsersByActivityType.mockImplementation(() =>
                Promise.resolve({
                    data: SEARCH_RESPONSE1,
                    status: 200
                })
            );

            searchPage.setData({
                    selectedActivityTypes: [ "Hiking", "Biking" ],
                    searchType: "or"
            });
            return searchPage.vm.search().then(() => {
                expect(searchPage.vm.api.getUsersByActivityType).toHaveBeenCalledWith(["Hiking", "Biking"], "or");
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