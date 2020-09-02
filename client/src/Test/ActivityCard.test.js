import "vue-jest"
import api from '../Api'
import {shallowMount} from "@vue/test-utils";
import router from "../index";
import ActivityCard from '../views/Search/ActivityCard.vue'
import "jest"

jest.mock('../Api');

const ACTIVITY_ID = 1;
const USER_ID = 1;
const USER = {
    firstname: 'Tim',
    lastname: 'Wong'
};

const ACTIVITY = {
    id: ACTIVITY_ID,
    creatorUserId: USER_ID,
    activity_name: 'Snow Race',
    description: 'A fun snow race',
    activity_type: [
        {
            activityTypeId: 5,
            name: "Archery"
        },
        {
            activityTypeId: 47,
            name: "Orienteering"
        },
        {
            activityTypeId: 16,
            name: "Camping"
        }
    ],
    continuous: false,
    start_time: 'Wed, 02 Sep 2020 03:41 PM',
    end_time: 'Wed, 02 Sep 2020 04:43 PM',
    location: 'Queenstown, New Zealand'
};

let activityCard;

beforeEach(() => {
    api.getUserData.mockImplementation(() => Promise.resolve({data: USER, status: 200}));
    activityCard = shallowMount(ActivityCard, {
        propsData: {
            activity: ACTIVITY,
            activityTypesSearchedFor: ["Archery", "Orienteering"]
        },
        router,
        mocks: {api}
    });
});

test('Is a vue instance', () => {
    expect(activityCard.isVueInstance).toBeTruthy();
});

describe("The ActivityCard errors", () => {

    beforeEach(() => {
        let networkError = new Error("Mocked Network Error");
        networkError.response = {status: 404};
        api.getUserData.mockImplementation(() => Promise.reject(networkError));
        activityCard = shallowMount(ActivityCard, {
            propsData: {
                activity: ACTIVITY,
                activityTypesSearchedFor: ["Archery", "Orienteering", 'Gymnastics']
            },
            router,
            mocks: {api}
        });
    });

    test('Displays an error card if creator name is not found', () => {
        expect(activityCard.find('#erroredActivityCard').exists()).toBeTruthy();
        expect(activityCard.find('#activityCard').exists()).toBeFalsy();
    });
});

describe('The ActivityCard elements', () => {

    test('Displays a card with no errors', () => {
        expect(activityCard.find('#erroredActivityCard').exists()).toBeFalsy();
        expect(activityCard.find('#activityCard').exists()).toBeTruthy();
    });

    test('Displays activity name and creator name', () => {
        let names = activityCard.find('#activityName-creatorName').text();
        expect(names).toContain(USER.firstname);
        expect(names).toContain(USER.lastname);
        expect(names).toContain(ACTIVITY.activity_name);
    });

    test('Displays a button for redirecting', () => {
        expect(activityCard.find('#viewActivityButton').exists()).toBeTruthy();
    });

    test('Displays ActivityTypes that were both searched for, and match the activity', () => {
        let activityTypes = activityCard.find('#matchingActivityTypes').text();
        expect(activityTypes).toContain("Archery");
        expect(activityTypes).toContain("Orienteering");
    });

    test('Does not display ActivityTypes that were searched for, and do not match the activity', () => {
        let activityTypes = activityCard.find('#matchingActivityTypes').text();
        expect(activityTypes).not.toContain("Gymnastics");
    });

    test('Displays activity info', () => {
        let names = activityCard.find('#activity' + ACTIVITY_ID + 'Card').text();
        expect(names).toContain(ACTIVITY.location);
        expect(names).toContain(ACTIVITY.start_time);
        expect(names).toContain(ACTIVITY.end_time);
        expect(names).toContain(ACTIVITY.description);
    });
});

//
// describe("The View User Modal", () => {
//
//     test('View User Modal title contains Full Name', () => {
//         let modal = userCard.find('#modal-view-profile' + DEFAULT_USER_ID);
//         expect(modal.attributes().title).toContain(USER1.firstname);
//         expect(modal.attributes().title).toContain(USER1.lastname);
//     });
//
//
//     test('View User Modal title contains go to profile button', () => {
//         let modal = userCard.find('#modal-view-profile' + DEFAULT_USER_ID);
//         expect(modal.find("#goToProfileButton").exists()).toBeTruthy();
//     });
//
// });