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
    lng: 0.0,
    lat: 0.0,
    name: "Activity location"
};

const pinGeometry = {
    geometry: {
        location: {
            lat: function () {return 10.0},
            lng: function () {return 25.0},
        }
    },
    formatted_address: "Activity location",
    data: {
        lat: 10.0,
        lng: 25.0,
        name: "Activity location"
    }
};
const altPinGeometry = {
    geometry: {
        location: {
            lat: function () {return 75.0},
            lng: function () {return 100.0},
        }
    },
    formatted_address: "Activity destination",
    data: {
        lat: 75.0,
        lng: 100.0,
        name: "Activity destination"
    }
};

const parentCenterData = {
    lng: 100.0,
    lat: 100.0,
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
    locationIO = mount(LocationIO, {localVue, propsData:{parentPins: [parentPinData]}});
    expect(locationIO.vm.$data.pins).toContain(parentPinData);
});

test('Pins are empty by default', () => {
    expect(locationIO.vm.$data.pins).toHaveLength(0);
});

test('Map is centered on the point specified by the parent', () => {
    locationIO = mount(LocationIO, {localVue, propsData:{parentCenter: parentCenterData}});
    expect(locationIO.vm.$data.center).toBe(parentCenterData)
});

test('Map center is undefined by default', () => {
    expect(locationIO.vm.$data.center).toBeUndefined();
});

test('Only a single pin can be added in single-only mode', () => {
    locationIO = mount(LocationIO, {localVue, propsData: {singleOnly: true}});
    expect(locationIO.vm.$data.pins).toHaveLength(0)
    locationIO.setData({currentPlace: pinGeometry});
    locationIO.vm.addMarker();
    expect(locationIO.vm.$data.pins).toHaveLength(1)
    expect(locationIO.vm.$data.pins).toContainEqual(pinGeometry.data)
    locationIO.setData({currentPlace: altPinGeometry});
    locationIO.vm.addMarker();
    expect(locationIO.vm.$data.pins).toHaveLength(1)
    expect(locationIO.vm.$data.pins).toContainEqual(altPinGeometry.data)
});

test('Add marker function emits an event with the list of pins', () => {
    locationIO.setData({currentPlace: pinGeometry});
    locationIO.vm.addMarker();
    expect(locationIO.emitted('child-pins')).toHaveLength(1)
    expect(locationIO.emitted('child-pins')[0][0]).toContainEqual(pinGeometry.data)
});
