import 'vue-jest'
import {shallowMount} from '@vue/test-utils'
import CreateActivity from "../views/Activities/CreateActivity";
import api from "../Api";
import router from '../index';
jest.mock("../Api");

let createActivity;
let config;
const DEFAULT_USER_ID = 1;
const DEFAULT_ACTIVITY_ID = 1;

let receivedOutcomeRequests = [];

beforeAll(() => {
    config = {
        router
    };
    // This Removes: TypeError: Cannot read property 'then' of undefined
    api.getUserId.mockImplementation(() => Promise.resolve({ data: DEFAULT_USER_ID, status: 200 }));
    api.createOutcome.mockImplementation(outcomeRequest => {
        receivedOutcomeRequests.push(outcomeRequest);
        Promise.resolve({ data: DEFAULT_USER_ID, status: 200 })
    });
    createActivity = shallowMount(CreateActivity, config);
});

beforeEach(() => {
    receivedOutcomeRequests = [];
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

const OUTCOME1 = {
    title: "My Awesome Outcome",
    unit_name: "Distance",
    unit_type: "TEXT"
};

test('Is a vue instance', () => {
    expect(createActivity.isVueInstance).toBeTruthy();
});

test('Catches an http status error of 400 or an invalid activity field when create activity form is submitted and gives user an appropriate alert', () => {
    createActivity.setProps({
        ACTIVITY1
    });

    let networkError = new Error("Mocked Network Error");
    networkError.response = {status: 400};   // Explicitly give the error a response.status
    api.createActivity.mockImplementation(() => Promise.reject(networkError));  // Mocks errors sent from the server

    return createActivity.vm.submitCreateActivity().catch(
        error => expect(error).toEqual(new Error("Entered activity field(s) are invalid")));
});

test('Catches an http status error of 401 or user not authenticated when create activity form is submitted and takes user to login page', () => {
    createActivity.setProps({
        ACTIVITY1
    });

    let networkError = new Error("Mocked Network Error");
    networkError.response = {status: 401};   // Explicitly give the error a response.status
    api.createActivity.mockImplementation(() => Promise.reject(networkError));  // Mocks errors sent from the server
    let spy = jest.spyOn(router, 'push');

    return createActivity.vm.submitCreateActivity().then(() => {
        expect(spy).toHaveBeenCalledWith('/login');
    });

});

test('Catches an http status error of 403 or forbidden access when create activity form is submitted and gives user an appropriate alert', async () => {
    createActivity.setProps({
        ACTIVITY1
    });

    let networkError = new Error("Mocked Network Error");
    networkError.response = {status: 403};   // Explicitly give the error a response.status
    api.createActivity.mockImplementation(() => Promise.reject(networkError));  // Mocks errors sent from the server

    return createActivity.vm.submitCreateActivity().catch(
        error => expect(error).toEqual(new Error("Sorry unable to create this activity (forbidden access)")));
});

test('Catches an http status error that isnt 401, 404, 403 and gives the user an appropriate alert', () => {
    createActivity.setProps({
        ACTIVITY1
    });

    let networkError = new Error("Mocked Network Error");
    networkError.response = {status: 408};   // Explicitly give the error a response.status
    api.createActivity.mockImplementation(() => Promise.reject(networkError));  // Mocks errors sent from the server

    return createActivity.vm.submitCreateActivity().catch(
        error => expect(error).toEqual(new Error("Unknown error has occurred whilst creating this activity")));
});

/**
 * Tests whether a the payload sent to the backend has the required correct fields.
 */
test('Creates the correct Outcome payload', () => {
    createActivity.vm.createAllOutcomes([OUTCOME1], DEFAULT_ACTIVITY_ID);
    expect(receivedOutcomeRequests.length).toBe(1);
    let outcomeRequest = receivedOutcomeRequests[0];

    expect(outcomeRequest.activity_id).toBeDefined();
    expect(outcomeRequest.title).toBeDefined();
    expect(outcomeRequest.unit_name).toBeDefined();
    expect(outcomeRequest.unit_type).toBeDefined();

    expect(Object.keys(outcomeRequest).length).toBe(4);

});


