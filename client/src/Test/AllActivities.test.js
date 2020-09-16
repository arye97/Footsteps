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
const PINS = [
    {
        lat: 1.0,
        lng: -1.0,
        colour: 'red',
        pin_type: 'USER',
        id: USER_ID
    },
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

beforeAll(() => {
    api.getUserId.mockImplementation(() => {
        return Promise.resolve({data: USER_ID, status: 200});
    });
    api.getActivityPins.mockImplementation((userId, blockNumber) => {
        if (userId === USER_ID) {
            let pins = [];
            if (blockNumber === 0) {
                pins.push(PINS[0]) // First block, so include the user
            }
            let leftIndex = blockNumber * PINS_PER_BLOCK + 1;
            let rightIndex = leftIndex + PINS_PER_BLOCK;
            pins = pins.concat(PINS.slice(leftIndex, rightIndex))
            return Promise.resolve({data: pins, status: 200, headers: {'total-rows': PINS.length}});
        }
    });
    config = {
        router,
        localVue,
        data() {
            return {
                pinsPerBlock: PINS_PER_BLOCK
            }
        }
    };
    allActivities = shallowMount(AllActivities, config);
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

test.each(PINS)('Receive pin %o', (pin) => {
    expect(allActivities.vm.$data.pins).toContain(pin);
});