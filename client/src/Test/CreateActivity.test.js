import 'vue-jest'
import {shallowMount} from '@vue/test-utils'
import CreateActivity from "../views/Activities/CreateActivity";
import api from "../Api";
jest.mock("../Api");

let createActivity;
let push;
let config;
const DEFAULT_USER_ID = 1;

beforeAll(() => {
    push = jest.fn();
    const $router = {
        push: jest.fn()
    };
    const $route = {
        login: '/login',
        register: '/register'
    };
    config = {
        mocks: {
            $route,
            $router
        }
    };
    // This Removes: TypeError: Cannot read property 'then' of undefined
    api.getUserId.mockImplementation(() => Promise.resolve({ data: DEFAULT_USER_ID, status: 200 }));
    createActivity = shallowMount(CreateActivity, config);
});

const ACTIVITY1 = {
    activity_name: "Trail Run Arthur's Pass",
    description: "A trail run of Mingha - Deception Route in Arthur's pass.  South to north.",
    activity_type: ["Hng", "Attics"],
    continuous: false,
    location: "Arthur's Pass National Park",
    start_time: "2020-12-16T09:00:00+0000",
    end_time: "2020-12-17T17:00:00+0000"
};

test('Is a vue instance', () => {
    expect(createActivity.isVueInstance).toBeTruthy();
});

test('Is a vue instance', () => {
    createActivity.setProps({
        ACTIVITY1
    });

    let networkError = new Error("Mocked Network Error");
    networkError.response = {status: 400};   // Explicitly give the error a response.status
    api.createActivity.mockImplementation(() => Promise.reject(networkError));  // Mocks errors sent from the server

    return createActivity.vm.submitCreateActivity().catch(
        error => expect(error).toEqual(new Error("Entered activity field(s) are invalid")));
});