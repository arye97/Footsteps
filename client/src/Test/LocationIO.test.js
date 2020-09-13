import {mount, createLocalVue} from '@vue/test-utils'
import LocationIO from '../components/Map/LocationIO.vue'
import {BootstrapVue} from 'bootstrap-vue';
import { gmapApi } from 'gmap-vue';
import "vue-jest"

jest.mock("gmap-vue");

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(gmapApi);

let config;

let locationIO;

const parentPinData = {
    longitude: 0.0,
    latitude: 0.0,
    name: "Activity location"
};
const parentCenterData = {
    longitude: 100.0,
    latitude: 100.0,
}

beforeEach(() => {
    config = {
        localVue
    };
    locationIO = mount(LocationIO, config);
});

test('Is a vue instance', () => {
    expect(locationIO.isVueInstance).toBeTruthy();
});

test('map pane does not exist at default', () => {
    expect(locationIO.find('#mapComponent').exists()).toBeFalsy();
});

test('gmap Auto complete does not exist at default', () => {
    expect(locationIO.find('#gmapAutoComplete').exists()).toBeFalsy();
});

test('Add marker button does not exist at default', () => {
    expect(locationIO.find('#addMarkerButton').exists()).toBeFalsy();
});

test('Hide map button does not exist at default', () => {
    expect(locationIO.find('#hideMapButton').exists()).toBeFalsy();
});

test('Show map button exists at default', () => {
    expect(locationIO.find('#showMapButton').exists()).toBeTruthy();
});

test('Pins obtained from the parent are added to the map', () => {
    locationIO.setProps({
        parentPins: [parentPinData],
    });
    expect(locationIO.vm.$data.pins.includes(parentPinData));
});

test('Pins are empty by default', () => {
    expect(locationIO.vm.$data.pins.size === 0);
});

test('Map is centered on the point specified by the parent', () => {
    locationIO.setProps({
        parentCenter: parentCenterData
    });
    expect(locationIO.vm.$data.center === parentCenterData);
});

test('Map center is undefined by default', () => {
    expect(locationIO.vm.$data.center === undefined);
});