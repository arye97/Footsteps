import "vue-jest"
import {mount, createLocalVue} from "@vue/test-utils";
import api from "../Api";
import ActivityForm from "../components/Activities/ActivityForm";
import { BootstrapVue } from 'bootstrap-vue';

jest.mock("../Api");
const localVue = createLocalVue();
localVue.use(BootstrapVue);

const locationData = {
    longitude: 55.0,
    latitude: 72.0,
    name: "Activity location"
};
const ACTIVITY1 = {
    profileId: 116,
    activity_name: "Trail Run Arthur's Pass",
    description: "A trail run of Mingha - Deception Route in Arthur's pass.  South to north.",
    selectedActivityTypes: ["Astronomy", "Hiking"],
    continuous: false,
    location: locationData,
    start_time: "2020-12-16T09:00:00+0000",
    end_time: "2020-12-17T17:00:00+0000",
    submitStartTime: "2020-12-16T09:00:00+0000",
    submitEndTime: "2020-12-17T17:00:00+0000"
};
const ACTIVITY_TYPES = [
    {activityTypeId: 1, name: "Hng"},
    {activityTypeId: 2, name: "Attics"}
];
const pinData = {
    lng: 20.0,
    lat: 35.0,
    name: "Activity location"
};
const pinLocationData = {
    longitude: 20.0,
    latitude: 35.0,
    name: "Activity location"
};

let activityForm;
/**
 * Before each test, mock the necessary properties and methods.
 */
beforeEach(() => {
    api.getActivityTypes.mockImplementation(() => Promise.resolve({data: ACTIVITY_TYPES, status: 200}));
    activityForm = mount(ActivityForm, {
        propsData: {
            activity: ACTIVITY1
        },
        mocks: {api},
        localVue
    });
});

test('Is a vue instance', () => {
    expect(activityForm.isVueInstance).toBeTruthy();
});

test('Outcome title input exists', () => {
    expect(activityForm.find('#input-outcome-title').exists()).toBeTruthy();
});

test('Outcome unit_name input exists', () => {
    expect(activityForm.find('#input-outcome-unit-name').exists()).toBeTruthy();
});

test('Outcome title word count exists', () => {
    expect(activityForm.find('#title-word-count').exists()).toBeTruthy();
});

test('Outcome unit_name word count exists', () => {
    expect(activityForm.find('#unit-name-word-count').exists()).toBeTruthy();
});

test('Add outcome button exists', () => {
    expect(activityForm.find('#addOutcome').exists()).toBeTruthy();
});

test('Outcomes table exists', () => {
    expect(activityForm.find('#outcomesTable').exists()).toBeTruthy();
});

test('Form catches emitted child-pins event', async () => {
    activityForm.find('#input-location').vm.$emit('child-pins', [pinData])
    await activityForm.vm.$nextTick()
    expect(activityForm.vm.$props.activity.location).toStrictEqual(pinLocationData);
});