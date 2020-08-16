import ViewActivity from "../views/Activities/ViewActivity";
import {mount} from "@vue/test-utils";
import "jest";
import api from "../Api";

jest.mock('../Api');

let viewActivity;

let CONTINUOUS_ACTIVITY_DATA;
let ACTIVE_USER_DATA;
let SUBSCRIBED_DATA;
let CREATOR_USER_DATA;
let PARTICIPANTS_DATA;
let OUTCOMES_DATA;
let RESULTS_DATA;

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
    CONTINUOUS_ACTIVITY_DATA = {
        activity_name: "My activity",
        description: "An activity description",
        creatorUserId: 18,
        location: "Activity location",
        start_time: new Date(),
        end_time: new Date(),
        activity_type: [
            {"activityTypeId":1, "name":"Free Running"},
            {"activityTypeId":2, "name":"Walking"}
        ],
        continuous: true
    };
    ACTIVE_USER_DATA = 18;
    SUBSCRIBED_DATA = {"subscribed":false};
    CREATOR_USER_DATA = {
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
    PARTICIPANTS_DATA = [
        {"firstname":"S","middlename":"","lastname":"M", "id": 1},
        {"firstname":"Larry", "middlename":"the", "lastname":"Cucumber", "id":2}
    ];
    OUTCOMES_DATA = [{
        "outcome_id": 1,
        "title": "The outcome title",
        "activity_id": 1,
        "unit_name": "The unit name",
        "unit_type": "TEXT"
    }];
    RESULTS_DATA = [{
        "user_id": 1,
        "outcome_id": 1,
        "value": "Some value",
        "did_not_finish": false,
        "comment": "Some comment"
    }];
};

test('Is a vue instance', () => {
    viewActivity = mount(ViewActivity);
    expect(viewActivity.isVueInstance).toBeTruthy();
});

describe('The view activity page', () => {
    beforeEach(() => {
        setValues();
        return new Promise(resolve => {
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
            api.getParticipants.mockImplementation(() => {
                return Promise.resolve({
                    data: PARTICIPANTS_DATA,
                    status: 200
                });
            });
            api.getActivityOutcomes.mockImplementation(() => {
                return Promise.resolve({
                    data: OUTCOMES_DATA,
                    status: 200
                });
            });
            // TODO uncomment this when getOutcomeResults is implemented
            // api.getOutcomeResults.mockImplementation(() => {
            //     return Promise.resolve({
            //         data: RESULTS_DATA,
            //         status: 200
            //     });
            // });
            api.createResult.mockImplementation(() => {
                return Promise.resolve({
                    status: 201
                });
            });
            viewActivity = mount(ViewActivity, {
                mocks: {
                    api,
                    $route
                }
            });

            // This causes a delay between beforeEach finishing, and the tests being run.
            // This isn't at all good practice, but its the only way I am able
            // to get ViewActivity to fully mount before the tests are run.
            // resolve() signals the above promise to complete
            sleep(150).then(() => resolve());
        });
    });

    test('displays the activity title', () => {
        expect(viewActivity.find('#activityTitle').exists()).toBeTruthy();
    });

    test('displays the activity description', () => {
        expect(viewActivity.find('#description').exists()).toBeTruthy();
    });

    test('displays the activity location', () => {
        expect(viewActivity.find('#location').exists()).toBeTruthy();
    });

    test('has a view participants button', () => {
        expect(viewActivity.find('#viewParticipants').exists()).toBeTruthy();
    });

    test('has a view results button', () => {
        expect(viewActivity.find('#viewResults').exists()).toBeTruthy();
    });

    test('shows the activity type', () => {
        expect(viewActivity.find('#activityType').exists()).toBeTruthy();
    });

    test('has a view participants modal', () => {
        expect(viewActivity.find('#viewParticipantsModal').exists()).toBeTruthy();
    });

    test('displays 2 participants', () => {
        // The set up data is set with 2 people participating in this activity
        expect(viewActivity.findAll('#participant').length).toBe(2);
    });

    test('has add result model', () => {
        expect(viewActivity.find('#addResultsModel').exists()).toBeTruthy();
    });

    test('displays 1 outcome card', () => {
        expect(viewActivity.findAll('#outcomeAddResultCard').length).toBe(1);
    });

    test('displays 1 input for the result', () => {
        expect(viewActivity.findAll('#NotSubmittedInputValue1').length).toBe(1);
    });

    test('displays 1 submit result button', () => {
        expect(viewActivity.findAll('#submitResult').length).toBe(1);
    });

});

describe('As an activity creator', () => {
    test('I cannot see the follow button', () => {
        expect(viewActivity.find('#followButton').exists()).toBeFalsy();
    });

    test('I cannot see the unfollow button', () => {
        expect(viewActivity.find('#unfollowButton').exists()).toBeFalsy();
    });

    test('I can see the edit activity button', () => {
        expect(viewActivity.find('#editActivity').exists()).toBeTruthy();
    });

    test('I can see the add results button', () => {
        expect(viewActivity.find('#addResults').exists()).toBeTruthy();
    });
});

describe("If I'm following an activity", () => {
    beforeEach(() => {
        ACTIVE_USER_DATA = 2;
        SUBSCRIBED_DATA = {"subscribed":true};
        return new Promise(resolve => {
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
                mocks: {
                    api,
                    $route
                }
            });

            // This causes a delay between beforeEach finishing, and the tests being run.
            // This isn't at all good practice, but its the only way I am able
            // to get ViewActivity to fully mount before the tests are run.
            // resolve() signals the above promise to complete
            sleep(150).then(() => resolve());
        });
    });

    test('I can see the unfollow button', () => {
        expect(viewActivity.find('#unfollowButton').exists()).toBeTruthy();
    });

    test("I can't see the follow button", () => {
        expect(viewActivity.find('#followButton').exists()).toBeFalsy();
    });

    test("I can't see the edit activity button", () => {
        expect(viewActivity.find('#editActivity').exists()).toBeFalsy();
    });

    test('I can see the add results button', () => {
        expect(viewActivity.find('#addResults').exists()).toBeTruthy();
    });
});

describe('If I am not following the activity', () => {
    beforeEach(() => {
        ACTIVE_USER_DATA = 2;
        SUBSCRIBED_DATA = {"subscribed":false};
        return new Promise(resolve => {
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
                mocks: {
                    api,
                    $route
                }
            });

            // This causes a delay between beforeEach finishing, and the tests being run.
            // This isn't at all good practice, but its the only way I am able
            // to get ViewActivity to fully mount before the tests are run.
            // resolve() signals the above promise to complete
            sleep(150).then(() => resolve());
        });
    });

    test("I can't see the unfollow button", () => {
        expect(viewActivity.find('#unfollowButton').exists()).toBeFalsy();
    });

    test("I can see the follow button", () => {
        expect(viewActivity.find('#followButton').exists()).toBeTruthy();
    });

    test("I can't see the edit activity button", () => {
        expect(viewActivity.find('#editActivity').exists()).toBeFalsy();
    });

    test("I can't see the add results button", () => {
        expect(viewActivity.find('#addResults').exists()).toBeFalsy();
    });
});

describe('When there are no participants', () => {
    beforeEach(() => {
        PARTICIPANTS_DATA = [];
        return new Promise(resolve => {
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
                mocks: {
                    api,
                    $route
                }
            });

            // This causes a delay between beforeEach finishing, and the tests being run.
            // This isn't at all good practice, but its the only way I am able
            // to get ViewActivity to fully mount before the tests are run.
            // resolve() signals the above promise to complete
            sleep(150).then(() => resolve());
        });
    });

    test("When there are no participants, it displays 'No participants to show'", () => {
        expect(viewActivity.find('#noParticipants').text()).toBe('No participants to show');
    });
});