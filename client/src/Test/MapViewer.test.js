import {mount, createLocalVue} from '@vue/test-utils';
import MapViewer from '../components/Map/MapViewer';
import {BootstrapVue} from 'bootstrap-vue';
import { gmapApi } from 'gmap-vue';
import "vue-jest";
import api from "../Api";
import router from "../index";

jest.mock("gmap-vue");
jest.mock('../Api');

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(gmapApi);

let config;
let mapViewer;

const pin1 = {
    lat: 10.0,
    lng: 25.0,
    name: "Activity location",
    id: 1,
    pin_type: "ACTIVITY"
};
const homePin = {
    lat: 18.0,
    lng: 90.0,
    name: "User home location",
    id: 1,
    pin_type: "USER"
};

beforeEach(() => {
    config = {
        localVue,
        propsData: {
            pins: [pin1, homePin]
        },
        mocks: {
            api
        },
        router
    };
    mapViewer = mount(MapViewer, config);
});

test('Is a vue instance', () => {
    expect(mapViewer.isVueInstance).toBeTruthy();
});

test('When I click an activity pin, I am directed to that activity page', () => {
    let spy = jest.spyOn(router, 'push');
    mapViewer.vm.directToPage(pin1);
    expect(spy).toHaveBeenCalledWith('/activity/1');
});

// test('When I click on a pin that is not for an activity, I am centered on that pin', () => {
//     let spy = jest.spyOn(router, 'push');
//     mapViewer.vm.directToPage(homePin);
//     expect(spy).not.toHaveBeenCalledWith('/activity/1');
//     expect(mapViewer.currentCenter.lat).toEqual(homePin.lat);
//     expect(mapViewer.currentCenter.lng).toEqual(homePin.lng);
// });