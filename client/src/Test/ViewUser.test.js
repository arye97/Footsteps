import ViewUser from "../components/layout/ViewUser"
import {createLocalVue, RouterLinkStub, shallowMount} from "@vue/test-utils";
import "jest";
import api from "../Api";
import { BootstrapVue } from 'bootstrap-vue';
import router from '../index';

const localVue = createLocalVue();
localVue.use(BootstrapVue);

jest.mock('../Api');
jest.mock('../index');
jest.mock('gmap-vue')

let viewUser;

let ACTIVE_USER_ID;
let USER_DATA;
let ACTIVITY_TYPES;
let USER_DATA_PRIVATE_LOCATION;
let USER_DATA_LOCATION;

let config;
const $route = {
    params: {
        activityId: 1
    }
};

/**
 * A function to cause a delay before a promise is resolved
 * @param milliseconds time to delay
 * @returns {Promise<any>} code to execute after delay
 */
const sleep = (milliseconds) => {
    return new Promise(resolve => setTimeout(resolve, milliseconds))
};

/**
 * Sets the values for the api response mocks
 */
const setValues = () => {
    ACTIVE_USER_ID = 18;
    USER_DATA = {
        "lastname":"Tomato",
        "firstname":"Bob",
        "middlename":"The",
        "nickname":"Bobby",
        "primary_email":"bob@thetomato.com",
        "additional_email":["bob@bob.com"],
        "activityTypes":[],
        "passports":["Algeria","Bolivia (Plurinational State of)","Holy See","Honduras"],
        "fitness":-1,
        "gender":"Male",
        "date_of_birth":"1987-04-04",
        "bio":"My name is Bob"
    };
    USER_DATA_LOCATION = {
        ...USER_DATA,
        public_location: {
            name: 'Hisar, Haryana, India',
            latitude: -40.9006,
            longitude: 174.8860
        }
    };
    USER_DATA_PRIVATE_LOCATION = {
        ...USER_DATA,
        private_location: {
            name: 'Windsor, ON, Canada',
            latitude: -40.9006,
            longitude: 174.8860
        }
    };
    ACTIVITY_TYPES = [
        {activityTypeId: 1, name: "Hng"},
        {activityTypeId: 2, name: "Attics"}
    ];
};

beforeEach(() => {
    setValues();
    api.getUserData.mockImplementation(() => {
        return Promise.resolve({
            data: USER_DATA,
            status: 200
        });
    });
    api.getUserId.mockImplementation(() => {
        return Promise.resolve({
            data: ACTIVE_USER_ID,
            status: 200
        });
    });
    api.getActivityTypes.mockImplementation(() => {
        return Promise.resolve({
            data: ACTIVITY_TYPES,
            status: 200
        });
    });
    config = {
        localVue,
        mocks: {
            $route
        },
        stubs: {
            RouterLink: RouterLinkStub
        },
        router,
    };
    return new Promise(resolve => {
        viewUser = shallowMount(ViewUser, config);
        // This causes a delay between beforeEach finishing, and the tests being run.
        // This isn't at all good practice, but its the only way I am able
        // to get ViewActivity to fully mount before the tests are run.
        // resolve() signals the above promise to complete
        sleep(150).then(() => resolve());
    });
});

test('Is a vue instance', () => {
    expect(viewUser.isVueInstance).toBeTruthy();
});

test('Location is hidden when no location exists', () => {
    expect(viewUser.find('#location').exists()).toBeFalsy();
});

test('Location is visible when only private location is defined', async () => {
    viewUser.setData({user: USER_DATA_PRIVATE_LOCATION});
    await viewUser.vm.$nextTick();
    expect(viewUser.find('#location').exists()).toBeTruthy();
});

test('Location is visible when public location is defined', async () => {
    viewUser.setData({user: USER_DATA_LOCATION});
    await viewUser.vm.$nextTick();
    expect(viewUser.find('#location').exists()).toBeTruthy();
});
