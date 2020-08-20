import 'vue-jest'
import {mount, createLocalVue} from "@vue/test-utils";
import HomeFeed from "../components/HomeFeed/HomeFeed";
import api from "../Api";
import BootstrapVue from 'bootstrap-vue';

jest.mock('../Api');

const localVue = createLocalVue();
localVue.use(BootstrapVue);

let homeFeed;
// Follow event has the date set as 10m ago
let FEED_EVENT = {
    id: 1,
    feedEventType: 'MODIFY',
    timeStamp: new Date(),
    activityId: 1,
    activityName: "The First Activity",
    userId: 1
};

const USER_DATA = {
    id:404,
    firstname:"Testing",
    middlename:"This",
    lastname:"Vue",
    nickname:"tester.boi",
    bio:"I test Vue files",
    date_of_birth:"1999-10-08",
    gender:"Male",
    fitness:0,
    passports:[],
    activityTypes:[]
};

/**
 * A function to cause a delay before a promise is resolved
 * @param milliseconds time to delay
 * @returns {Promise<any>} code to execute after delay
 */
const sleep = (milliseconds) => {
    return new Promise(resolve => setTimeout(resolve, milliseconds))
};

beforeEach(() => {
    return new Promise(resolve => {
        api.getFeedEvents.mockImplementation(() => {
            return Promise.resolve({
                data: [FEED_EVENT],
                status: 200
            })
        });
        api.getUserId.mockImplementation(() => {
            return Promise.resolve({
                data: 1,
                status: 200
            })
        });
        api.getUserData.mockImplementation(() => {
            return Promise.resolve({
                data: USER_DATA,
                status: 200
            })
        });

        homeFeed = mount(HomeFeed, {
            mocks: {api},
            localVue
        });

        sleep(150).then(() => resolve());
    });
});

test('Check that feed event data is fetched and rendered properly', () => {
    expect(homeFeed.find('#description').text()).toBe('You modified the activity The First Activity')
});