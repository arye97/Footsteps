import "vue-jest"
import api from '../Api'
import {mount, createLocalVue} from "@vue/test-utils";
import router from "../index";

import ActivitySearch from "../views/Search/ActivitySearch";
import "jest";
import BootstrapVue from "bootstrap-vue";

const localVue = createLocalVue();
localVue.use(BootstrapVue);

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

let activitySearch;

beforeEach(() => {
    activitySearch = mount(ActivitySearch, {
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
            data: SEARCH_RESPONSE1.slice((activitySearch.vm.$data.currentPage - 1) * pageSize, activitySearch.vm.$data.currentPage * pageSize),
            status: 200,
            headers: {
                "total-rows": SEARCH_RESPONSE1.length
            }
        })
    );

    api.logout.mockImplementation(() => Promise.resole({status:200}));
});

test('Is a vue instance', () => {
    expect(activitySearch.isVueInstance).toBeTruthy();
});

test('Activities search box exists', () => {
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

