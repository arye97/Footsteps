import 'vue-jest'
import {shallowMount} from '@vue/test-utils'
import ActivityList from "../components/Activities/ActivityList";
import api from "../Api";
import router from '../index';
import EditEmail from "../views/Settings/EditEmail";
jest.mock("../Api");

let activityList;
let push;

let config;
const PAUSE = 150;
const SUBSCRIBED_STATUS = false;
const DEFAULT_USER_ID_1 = 1;
const DEFAULT_USER_ID_2 = 2;
const ACTIVITIES = [
    {
        activity_name: "Trail Run Arthur's Pass",
        description: "A trail run of Mingha - Deception Route in Arthur's pass.  South to north.",
        activity_type: ["Astronomy", "Hiking"],
        continuous: false,
        location: "Arthur's Pass National Park",
        start_time: "2020-12-16T09:00:00+0000",
        end_time: "2020-12-17T17:00:00+0000",
        creatorUserId: DEFAULT_USER_ID_2
    },
    {
        activity_name: "Cancelling Marmite",
        description: "I hate marmite. I'd rather go on a juice cleanse with debra and bob.",
        activity_type: ["Archery"],
        continuous: false,
        location: "Nana's house",
        start_time: "2020-12-16T09:00:00+0000",
        end_time: "2020-12-17T17:00:00+0000",
        creatorUserId: DEFAULT_USER_ID_2
    }
];

// beforeAll(() => {
//     config = {
//         router
//     };
//     // This Removes: TypeError: Cannot read property 'then' of undefined
//     api.getUserId.mockImplementation(() => Promise.resolve({ data: DEFAULT_USER_ID, status: 200 }));
//     // api.getUserSubscribed.mockImplementation(() => Promise.resolve( { data: SUBSCRIBED_STATUS, status: 200 }));
//     // api.setUserSubscribed.mockImplementation(() => Promise.resolve( { status: 200 }));
//     // api.deleteUserSubscribed.mockImplementation(() => Promise.resolve( { status: 200}));
//     activityList = shallowMount(ActivityList, config);
// });

/**
 * A function to cause a delay before a promise is resolved
 * @param milliseconds time to delay
 * @returns {Promise<any>} code to execute after delay
 */
const sleep = (milliseconds) => {
    return new Promise(resolve => setTimeout(resolve, milliseconds))
};

/**
 * Before each test, mock the necessary properties and methods.
 */
beforeEach(() => {
    return new Promise(resolve => {
        // push = jest.fn();
        // const $router = {
        //     push: jest.fn()
        // };
        // const $route = {
        //     params: {activityList : undefined},
        // };

        api.getUserId.mockImplementation(() => Promise.resolve({ data: DEFAULT_USER_ID_1, status: 200 }));
        api.getUserActivities.mockImplementation(
            () => Promise.resolve({
                data: {
                    ACTIVITIES
                },
                status: 200
            }));


        activityList = shallowMount(ActivityList, {
            data() {
                return {
                    activityList: ACTIVITIES
                }
            }
        });
        console.log(activityList);

        // This causes a delay between beforeEach finishing, and the tests being run.
        // This isn't at all good practice, but its the only way I am able
        // to get EditEmail to fully mount before the tests are run.
        // resolve() signals the above promise to complete
        sleep(PAUSE).then(() => resolve());
    });
});


test('Is a vue instance', () => {
    expect(activityList.isVueInstance).toBeTruthy();
});


