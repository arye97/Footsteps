import ViewActivity from "../views/Activities/ViewActivity";
import {mount} from "@vue/test-utils";
import "jest";
import api from "../Api";

jest.mock('../Api');

let viewActivity;

const CONTINUOUS_ACTIVITY_DATA = {
    activity_name: "My activity",
    description: "An activity description",
    creatorUserId: 1,
    location: "Activity location",
    start_time: new Date(),
    end_time: new Date(),
    activity_type: [
        {"activityTypeId":1, "name":"Free Running"},
        {"activityTypeId":2, "name":"Walking"}
    ],
    continuous: true
};
const ACTIVE_USER_DATA = 1;
const SUBSCRIBED_DATA = {"subscribed":false};
const CREATOR_USER_DATA = {
    "lastname":"Tomato",
    "firstname":"Bob",
    "middlename":"The",
    "nickname":"Bobby",
    "primary_email":"bob@thetomato.com",
    "additional_email":["bob@bob.com"],
    "activityTypes":[],
    "id":18,
    "passports":["Algeria","Bolivia (Plurinational State of)","Holy See","Honduras"],
    "fitness":-1,
    "gender":"Male",
    "date_of_birth":"1987-04-04",
    "bio":"My name is Bob"
};

beforeEach(() => {
    api.getActivityData.mockImplementation(() => {
        return Promise.resolve({
            data: CONTINUOUS_ACTIVITY_DATA,
            status: 200
        });
    });
    api.getUserId.mockImplementation(() => {
        return Promise.resolve({
            data: ACTIVE_USER_DATA,
            status: 200
        });
    });
    api.getUserSubscribed.mockImplementation(() => {
        return Promise.resolve({
            data: SUBSCRIBED_DATA,
            status: 200
        });
    });
    api.getUserData.mockImplementation(() => {
        return Promise.resolve({
            data: CREATOR_USER_DATA,
            status: 200
        });
    });
    viewActivity = mount(ViewActivity, {
        mocks: {api}
    });
});

test('Is a vue instance', () => {
    expect(viewActivity.isVueInstance).toBeTruthy();
});

describe('The view activity page', () => {
    test('displays the activity title', () => {
        expect(viewActivity.find('#activityTitle')).toBeTruthy();
    });

    test('displays the activity description', () => {
        expect(viewActivity.find('#description')).toBeTruthy();
    });

    test('displays the activity location', () => {
        expect(viewActivity.find('#location')).toBeTruthy();
    });

    // test('does not display the start time for continuous events', () => {
    //     console.log(viewActivity.html());
    //     expect(viewActivity.find('#startTime')).toBeFalsy();
    // });
});