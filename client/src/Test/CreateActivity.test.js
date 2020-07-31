import 'vue-jest'
import {shallowMount} from '@vue/test-utils'
import CreateActivity from "../views/Activities/CreateActivity";
import api from "../Api";
jest.mock("../Api");

let createactivity;
let push;
let config;

beforeEach(() => {
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
    createactivity = shallowMount(CreateActivity, config);
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
    expect(createactivity.isVueInstance).toBeTruthy();
});

test('Is a vue instance', () => {
    createactivity.setProps({
        ACTIVITY1
    });
    api.createActivity.mockImplementation(() => Promise.resolve({ status: 400 }));
    expect(() => createactivity.vm.submitCreateActivity()).toThrow("Entered activity field(s) are invalid");
});