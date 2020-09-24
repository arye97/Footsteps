import 'vue-jest'
import {shallowMount, createLocalVue} from '@vue/test-utils'
import CreateActivity from "../views/Activities/CreateActivity";
import api from "../Api";
import router from '../index';
import {BootstrapVue, BootstrapVueIcons, IconsPlugin} from 'bootstrap-vue';
jest.mock("../Api");

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(IconsPlugin);
import EmptyComponent from "./EmptyComponent.vue";
localVue.component('GmapAutocomplete', EmptyComponent);

let createActivity;
let config;
const DEFAULT_USER_ID = 1;
const DEFAULT_ACTIVITY_ID = 1;

let receivedOutcomeRequests = [];

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

const OUTCOME2 = {
    title: "My Awesome Outcome Part too",
    unit_name: "Eggs",
    unit_type: "TEXT"
};

const ACTIVITY_TYPES = [
    {activityTypeId: 1, name: "Hng"},
    {activityTypeId: 2, name: "Attics"}
];

beforeAll(() => {
    config = {
        router,
        localVue,
        data: function() {
            return {
                outcomeList: []
            }
        }
    };
    // This Removes: TypeError: Cannot read property 'then' of undefined
    api.getUserId.mockImplementation(() => Promise.resolve({ data: DEFAULT_USER_ID, status: 200 }));
    api.createOutcome.mockImplementation(outcomeRequest => {
        receivedOutcomeRequests.push(outcomeRequest);
        return Promise.resolve({data: DEFAULT_USER_ID, status: 200});
    });
    api.getUserId.mockImplementation(() => Promise.resolve({data: 1, status: 200}));
    api.getActivityTypes.mockImplementation(() => Promise.resolve({data: ACTIVITY_TYPES, status: 200}));
    createActivity = shallowMount(CreateActivity, config);
});

beforeEach(() => {
    receivedOutcomeRequests = [];
});

test('Is a vue instance', () => {
    expect(createActivity.isVueInstance).toBeTruthy();
});

test('Adds 2 Outcomes and deletes an Outcome to outcomeList', () => {
    expect(createActivity.vm.outcomeList.length).toBe(0);
    createActivity.vm.addOutcome(OUTCOME1);
    createActivity.vm.addOutcome(OUTCOME2);
    expect(createActivity.vm.outcomeList.length).toBe(2);
    createActivity.vm.deleteOutcome(OUTCOME1);
    expect(createActivity.vm.outcomeList.length).toBe(1);
    createActivity.vm.deleteOutcome(OUTCOME2);
    expect(createActivity.vm.outcomeList.length).toBe(0);
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

test('Catches an http status error of 401 or user not authenticated when create activity form is submitted and takes user to login page', async () => {
    createActivity.setProps({
        ACTIVITY1
    });
    await createActivity.vm.$router.push('/activities/create');

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
test('Creates the correct Outcome payload', async () => {
    await createActivity.vm.createAllOutcomes([OUTCOME1], DEFAULT_ACTIVITY_ID);
    expect(receivedOutcomeRequests.length).toBe(1);
    let outcomeRequest = receivedOutcomeRequests[0];

    expect(outcomeRequest.activity_id).toBeDefined();
    expect(outcomeRequest.title).toBeDefined();
    expect(outcomeRequest.unit_name).toBeDefined();
    expect(outcomeRequest.unit_type).toBeDefined();

    expect(Object.keys(outcomeRequest).length).toBe(4);
});