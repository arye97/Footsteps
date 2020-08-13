import 'vue-jest'
import {shallowMount} from '@vue/test-utils'
import ActivityList from "../components/Activities/ActivityList";
import api from "../Api";

// Import bootstrap vue so html that uses bootstrap vue components don't throw error
import Vue from "vue";
import {BootstrapVue} from "bootstrap-vue";
Vue.use(BootstrapVue);

jest.mock("../Api");

let activityListWrapper = null;

const DEFAULT_USER_ID_1 = 1;
const DEFAULT_USER_ID_2 = 2;
const ACTIVITIES = [
    {
        id: 1,
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
        id: 2,
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


/**
 * Mock ActivityList.vue in a separate function to simplify process
 */
async function mockActivities() {  // Helper function to prevent duplicate code
    api.getUserId.mockImplementation(() => Promise.resolve({ data: DEFAULT_USER_ID_1, status: 200 }));
    api.getUserActivities.mockImplementation(
        () => Promise.resolve({
            data: ACTIVITIES,
            status: 200
        })
    );
    api.getUserData.mockImplementation(
        () => Promise.resolve( {
            data: {
                firstname: "CreatorName",
                lastname: "Barbara"
            },
            status: 200
        })
    );
    api.getUserSubscribed.mockImplementation(
        () => Promise.resolve( {
            data: {
                subscribed: true
            }, status: 200
        })
    );

    activityListWrapper = shallowMount(ActivityList, {
        methods: {
            checkLoggedIn: () => {},
        },
    });
}


/**
 * Before each test, mock the necessary properties and methods.
 */
beforeEach(() => {
    return mockActivities();
});


test('Is a vue instance', () => {
    expect(activityListWrapper.isVueInstance).toBeTruthy();
});