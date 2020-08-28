import "vue-jest"
import {shallowMount} from "@vue/test-utils";
import api from "../Api";
import ActivityForm from "../components/Activities/ActivityForm";



const ACTIVITY1 = {
    profileId: 116,
    activity_name: "Trail Run Arthur's Pass",
    description: "A trail run of Mingha - Deception Route in Arthur's pass.  South to north.",
    selectedActivityTypes: ["Astronomy", "Hiking"],
    continuous: false,
    location: "Arthur's Pass National Park",
    start_time: "2020-12-16T09:00:00+0000",
    end_time: "2020-12-17T17:00:00+0000",
    submitStartTime: "2020-12-16T09:00:00+0000",
    submitEndTime: "2020-12-17T17:00:00+0000"
};

let activityForm;
/**
 * Before each test, mock the necessary properties and methods.
 */
beforeEach(() => {
    activityForm = shallowMount(ActivityForm, {
        propsData: {
            activity: { ACTIVITY1
            }
        },
        mocks: {api},
        methods: {
            }
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
