import 'vue-jest'
import {createLocalVue, mount} from '@vue/test-utils'
import EditActivity from "../views/Activities/EditActivity";
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


let editActivity;
let config;
const DEFAULT_USER_ID = 1;
const DEFAULT_ACTIVITY_ID = 1;
const DEFAULT_OUTCOME_ID = 1;

let receivedOutcomeRequests = [];
let editedOutcomeRequests = [];

const OUTCOME1 = {
    outcome_id: DEFAULT_OUTCOME_ID,
    title: "My Awesome Outcome",
    unit_name: "Distance",
    unit_type: "TEXT"
};
const OUTCOME2 = {
    title: "Another Awesome Outcome",
    unit_name: "Time",
    unit_type: "TEXT"
};
const OUTCOME3 = { // Same outcome as OUTCOME1 just edited
    outcome_id: DEFAULT_OUTCOME_ID,
    title: "Edited My Awesome Outcome",
    unit_name: "Distance",
    unit_type: "TEXT",
    isEdited: true
};
const OUTCOME4 = { // New outcome not yet within DB, and also edited
    title: "Edited My New Outcome",
    unit_name: "Eggs",
    unit_type: "TEXT",
    isEdited: true
}
const ORIGINAL_OUTCOME_LIST = [];
const ACTIVITY_TYPES = [
    {activityTypeId: 1, name: "Hng"},
    {activityTypeId: 2, name: "Attics"}
];
const ACTIVITY_DATA = {
    activity_name: "Activity name",
    creatorUserId: 1,
    continuous: true,
    description: "My activity description",
    location: "My activity location",
    activity_type: ["Hng", "Attics"],
};

beforeEach(() => {
    config = {
        router,
        data: function() {
            return {
                outcomeList: []
            }
        },
        localVue
    };
    // This Removes: TypeError: Cannot read property 'then' of undefined
    api.getUserId.mockImplementation(() => Promise.resolve({ data: DEFAULT_USER_ID, status: 200 }));
    api.getActivityData.mockImplementation(() => Promise.resolve({data: ACTIVITY1, status: 200}));
    api.deleteOutcome.mockImplementation(() => Promise.resolve({data: null, status: 200}));
    // Mock isActivityEditable endpoint to succeed
    api.isActivityEditable.mockImplementation(() => Promise.resolve({ data: null, status: 200 }));
    api.getActivityOutcomes.mockImplementation(() => Promise.resolve({ data: ORIGINAL_OUTCOME_LIST, status: 200 }));
    api.createOutcome.mockImplementation(outcomeRequest => {
        receivedOutcomeRequests.push(outcomeRequest);
        return Promise.resolve({ data: DEFAULT_USER_ID, status: 200 });
    });
    api.updateOutcome.mockImplementation(outcomeRequest => {
        editedOutcomeRequests.push(outcomeRequest);
        return Promise.resolve({data: null, status: 200});
    });
    api.getActivityTypes.mockImplementation(() => Promise.resolve({data: ACTIVITY_TYPES, status: 200}));
    api.getActivityData.mockImplementation(() => Promise.resolve({data: ACTIVITY_DATA, status: 200}));
    editActivity = mount(EditActivity, config);
    if (editActivity.vm.$router.currentRoute.name !== "editActivity") {
        editActivity.vm.$router.push(`/activities/edit/${DEFAULT_ACTIVITY_ID}`);
    }
});

beforeEach(() => {
    receivedOutcomeRequests = [];
    editedOutcomeRequests = [];
    editActivity.vm.outcomeList = [];
});

const ACTIVITY1 = {
    activity_name: "Trail Run Arthur's Pass",
    description: "A trail run of Mingha - Deception Route in Arthur's pass.  South to north.",
    activity_type: ["Astronomy", "Hiking"],
    continuous: false,
    location: "Arthur's Pass National Park",
    start_time: "2020-12-16T09:00:00+0000",
    end_time: "2020-12-17T17:00:00+0000"
};

test('Is a vue instance', () => {
    expect(editActivity.isVueInstance).toBeTruthy();
});

test('Adds and deletes an Outcome to outcomeList', () => {
    expect(editActivity.vm.outcomeList.length).toBe(0);
    editActivity.vm.addOutcome(OUTCOME2);
    expect(editActivity.vm.outcomeList.length).toBe(1);
    editActivity.vm.deleteOutcome(OUTCOME2);
    expect(editActivity.vm.outcomeList.length).toBe(0);
});

test('Catches an http status error of 401 or user not authenticated when edit activity form is submitted and takes user to login page', () => {
    editActivity.setProps({
        ACTIVITY1
    });

    let networkError = new Error("Mocked Network Error");
    networkError.response = {status: 401};   // Explicitly give the error a response.status
    api.updateActivity.mockImplementation(() => Promise.reject(networkError));  // Mocks errors sent from the server
    let spy = jest.spyOn(router, 'push');

    return editActivity.vm.submitEditActivity().then(() => {
        expect(spy).toHaveBeenCalledWith('/login');
    });
});


test('Catches an http status error of 403 or forbidden access when edit activity form is submitted and gives user an appropriate alert', () => {
    editActivity.setProps({
        ACTIVITY1
    });

    let networkError = new Error("Mocked Network Error");
    networkError.response = {status: 403};   // Explicitly give the error a response.status
    api.updateActivity.mockImplementation(() => Promise.reject(networkError));  // Mocks errors sent from the server

    return editActivity.vm.submitEditActivity().catch(
        error => expect(error).toEqual(new Error("Sorry unable to edit this activity (forbidden access)")));
});

test('Checking 200 response status of edit activity function', () => {
    editActivity.setProps({
        ACTIVITY1
    });

    api.updateActivity.mockImplementation(() => Promise.resolve({ data: {'Token': 'ValidToken', 'userId': 1}, status: 200 }));  // Mocks errors sent from the server
    let spy = jest.spyOn(router, 'push');

    return editActivity.vm.submitEditActivity().then(() => {
        expect(spy).toHaveBeenCalledWith({name: 'allActivities', params: {alertMessage: 'Activity modified successfully', alertCount: 5}});
    })

});

test('Catches an http status error that isnt 401, 404, 403 and gives the user an appropriate alert', () => {
    editActivity.setProps({
        ACTIVITY1
    });

    let networkError = new Error("Mocked Network Error");
    networkError.response = {status: 408};   // Explicitly give the error a response.status
    api.updateActivity.mockImplementation(() => Promise.reject(networkError));  // Mocks errors sent from the server

    return editActivity.vm.submitEditActivity().catch(
        error => expect(error).toEqual(new Error("Unknown error has occurred whilst editing this activity")));
});

test('Catches an http status error of 401 when activity data is coming back from the server get endpoint', () => {
    editActivity.setProps({
        ACTIVITY1
    });

    let networkError = new Error("Mocked Network Error");
    networkError.response = {status: 401};   // Explicitly give the error a response.status
    api.getActivityData.mockImplementation(() => Promise.reject(networkError));  // Mocks errors sent from the server
    let spy = jest.spyOn(router, 'push');


    return editActivity.vm.getActivityData().then(() => {
        expect(spy).toHaveBeenCalledWith('/login');
    });
});

test('Catches an http status error of 403 when checking if activity can be edited by user', () => {
    editActivity.setProps({
        ACTIVITY1
    });

    let networkError = new Error("Mocked Network Error");
    networkError.response = {status: 403};   // Explicitly give the error a response.status
    api.isActivityEditable.mockImplementation(() => Promise.reject(networkError));  // Mocks errors sent from the server
    let spy = jest.spyOn(router, 'push');


    return editActivity.vm.getActivityData().then(() => {
        expect(spy).toHaveBeenLastCalledWith(
            { name: 'allActivities', params: {alertCount: 5, alertMessage: "Activity is not editable"} });
    });
});

test('Catches an http status error that isnt 401 or 403 when activity data is coming back from the server get endpoint', () => {
    editActivity.setProps({
        ACTIVITY1
    });


    let networkError = new Error("Mocked Network Error");
    networkError.response = {status: 408};   // Explicitly give the error a response.status
    api.getActivityData.mockImplementation(() => Promise.reject(networkError));  // Mocks errors sent from the server
    let spy = jest.spyOn(router, 'push');


    return editActivity.vm.getActivityData().then(() => {
        expect(spy).toHaveBeenLastCalledWith(
            { name: 'allActivities', params: {alertCount: 5, alertMessage: "Can't get Activity data"} });
    });
});

test('Creates the correct Outcome payload and creates only new Outcomes', async () => {
    await editActivity.vm.editAllOutcomes([OUTCOME1, OUTCOME2], [OUTCOME1], DEFAULT_ACTIVITY_ID);
    expect(receivedOutcomeRequests.length).toBe(1);  // Should only make a request for the new Outcome
    let outcomeRequest = receivedOutcomeRequests[0];

    expect(outcomeRequest.title).toEqual(OUTCOME2.title);  // Should request OUTCOME2

    expect(outcomeRequest.activity_id).toBeDefined();
    expect(outcomeRequest.title).toBeDefined();
    expect(outcomeRequest.unit_name).toBeDefined();
    expect(outcomeRequest.unit_type).toBeDefined();

    expect(Object.keys(outcomeRequest).length).toBe(4);

});

test('Creates no requests when no new Outcomes', () => {
    editActivity.vm.editAllOutcomes([OUTCOME1, OUTCOME2], [OUTCOME1, OUTCOME2], DEFAULT_ACTIVITY_ID);
    expect(receivedOutcomeRequests.length).toBe(0);  // Should only make a request for the new Outcome
});

test('Edits outcome with an ID and isEdited set to true', () => {
    editActivity.vm.editAllOutcomes([OUTCOME3], ORIGINAL_OUTCOME_LIST, DEFAULT_ACTIVITY_ID);
    expect(receivedOutcomeRequests.length).toBe(0); // api.createOutcome is not called
    expect(editedOutcomeRequests.length).toBe(1); // api.updateOutcome is called once
});

test('Creates outcome with no ID and isEdited set to true', () => {
    editActivity.vm.editAllOutcomes([OUTCOME1, OUTCOME4], ORIGINAL_OUTCOME_LIST, DEFAULT_ACTIVITY_ID);
    expect(receivedOutcomeRequests.length).toBe(1); // api.createOutcome is called once
    expect(editedOutcomeRequests.length).toBe(0); // api.updateOutcome is not called
});