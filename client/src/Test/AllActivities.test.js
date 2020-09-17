import 'vue-jest'
import {createLocalVue, shallowMount} from "@vue/test-utils";
import {BootstrapVue} from "bootstrap-vue";
import AllActivities from "../views/Activities/AllActivities";
import router from "../index";
import api from "../Api";

jest.mock("../Api")

const localVue = createLocalVue();
localVue.use(BootstrapVue);

let allActivities;
let config;

const PINS_PER_BLOCK = 2;
const USER_ID = 1;
const ACTIVITY_ID_1 = 21;
const ACTIVITY_ID_2 = 22;
const ACTIVITY_ID_3 = 23;
const ACTIVITY_ID_4 = 24;
const ACTIVITY_ID_5 = 25;
const USER_PIN = {
    lat: 1.0,
    lng: -1.0,
    colour: 'red',
    pin_type: 'USER',
    id: USER_ID
}
const PINS = [
    {
        lat: 1.0,
        lng: -1.0,
        colour: 'blue',
        pin_type: 'ACTIVITY',
        id: ACTIVITY_ID_1
    },
    {
        lat: 1.0,
        lng: -1.0,
        colour: 'blue',
        pin_type: 'ACTIVITY',
        id: ACTIVITY_ID_2
    },
    {
        lat: 1.0,
        lng: -1.0,
        colour: 'blue',
        pin_type: 'ACTIVITY',
        id: ACTIVITY_ID_3
    },
    {
        lat: 1.0,
        lng: -1.0,
        colour: 'blue',
        pin_type: 'ACTIVITY',
        id: ACTIVITY_ID_4
    },
    {
        lat: 1.0,
        lng: -1.0,
        colour: 'green',
        pin_type: 'ACTIVITY',
        id: ACTIVITY_ID_5
    }
];

/**
 * A function to cause a delay before a promise is resolved
 * @param milliseconds time to delay
 * @returns {Promise<any>} code to execute after delay
 */
const sleep = (milliseconds) => {
    return new Promise(resolve => setTimeout(resolve, milliseconds))
};

beforeAll(() => {
    return new Promise(resolve => {
        api.getUserId.mockImplementation(() => {
            return Promise.resolve({data: USER_ID, status: 200});
        });
        api.getActivityPins.mockImplementation((userId, blockNumber) => {
            if (userId === USER_ID) {
                let pins = [];
                if (blockNumber === 0) {
                    pins.push(USER_PIN) // First block, so include the user
                }
                let leftIndex = blockNumber * PINS_PER_BLOCK;
                let rightIndex = leftIndex + PINS_PER_BLOCK;
                if (rightIndex > PINS.length) {
                    rightIndex = PINS.length;
                }
                pins = pins.concat(PINS.slice(leftIndex, rightIndex));
                let header = {'has-next': rightIndex >= PINS.length ? 'false' : 'true'};
                return Promise.resolve({data: pins, status: 200, headers: header});
            }
        });
        config = {
            router,
            localVue
        };
        allActivities = shallowMount(AllActivities, config);
        sleep(150).then(() => resolve());
    });
});

test('Is a vue instance', () => {
    expect(allActivities.isVueInstance).toBeTruthy();
});

test('Map pane exists on all activities page', () => {
    expect(allActivities.find('#mapComponent').exists()).toBeTruthy();
});

test('Activity list exists on all activities page', () => {
    expect(allActivities.find('#activityList').exists()).toBeTruthy();
});

test('Create new activity button exists on all activities page', () => {
    expect(allActivities.find('#creatActivityButton').exists()).toBeTruthy();
});

test('Header exists on all activities page', () => {
    expect(allActivities.find('#header').exists()).toBeTruthy();
});

test('Receive the user pin', () => {
    expect(allActivities.vm.$data.pins).toContain(USER_PIN);
});

test.each(PINS)('Receive pin %o', (pin) => {
    expect(allActivities.vm.$data.pins).toContain(pin);
});