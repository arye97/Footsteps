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
};

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

test('addMarker converts a place object into a pin object', () => {
    const autocompletePlace = {formatted_address: "Somewhere", geometry: {location: {lat: () => 0, lng: () => 0}}};
    locationIO.vm.$refs.mapViewerRef = {panToPin: () => {}};

    locationIO.vm.addMarker(autocompletePlace);
    expect(locationIO.vm.pins.length).toBe(1);
    expect(locationIO.vm.pins[0]).toEqual({lat:0, lng:0, name:"Somewhere"});
    expect(locationIO.vm.address).toEqual("Somewhere")
});

test('addMarker converts the center of the map to a pin', () => {
    locationIO.vm.$refs.mapViewerRef = {currentCenter: {lat:0, lng:0}, panToPin: () => {}};

    locationIO.vm.addMarker();
    expect(locationIO.vm.pins.length).toBe(1);
    expect(locationIO.vm.pins[0]).toEqual({lat:0, lng:0})
});

test('setInputBoxUpdatePins re-positions a pin', () => {
    const origionalAddress = locationIO.vm.address;
    locationIO.vm.pins.push({lat: 0, lng: 0});

    locationIO.vm.setInputBoxUpdatePins({lat: 1, lng: 1}, 0);
    expect(locationIO.vm.pins[0]).toEqual({lat: 1, lng: 1});
    expect(locationIO.vm.address).not.toEqual(origionalAddress);
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

test.each([
    {singleOnly: true},
    {maxPins: 1},
    {singleOnly: true, maxPins: 1},
])('Only a single pin can be added in single-only mode and when max-pin == 1', async (props) => {
    locationIO = mount(LocationIO, {localVue, propsData: props});
    locationIO.vm.$refs.mapViewerRef = {panToPin: () => {}};

    expect(locationIO.vm.$data.pins).toHaveLength(0);
    locationIO.vm.addMarker(pinGeometry);
    await locationIO.vm.$nextTick();  // Need to let the pins watcher update
    expect(locationIO.vm.$data.pins).toHaveLength(1);
    expect(locationIO.vm.$data.pins).toContainEqual(pinGeometry.data);

    locationIO.vm.addMarker(altPinGeometry);
    await locationIO.vm.$nextTick();  // Need to let the pins watcher update
    expect(locationIO.vm.$data.pins).toHaveLength(1);
    expect(locationIO.vm.$data.pins).toContainEqual(altPinGeometry.data)
});



test('Add marker function emits an event with the list of pins', () => {
    locationIO.vm.$refs.mapViewerRef = {panToPin: () => {}};
    locationIO.vm.addMarker(pinGeometry);
    expect(locationIO.emitted('child-pins')).toHaveLength(1);
    expect(locationIO.emitted('child-pins')[0][0]).toContainEqual(pinGeometry.data)
});
