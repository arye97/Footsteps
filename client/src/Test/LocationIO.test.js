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
    expect(locationIO.vm.pins[0]).toEqual({lat:0, lng:0})
    expect(locationIO.vm.address).toEqual("Somewhere")
});

test('addMarker converts the center of the map to a pin', () => {
    locationIO.vm.$refs.mapViewerRef = {currentCenter: {lat:0, lng:0}, panToPin: () => {}};

    locationIO.vm.addMarker();
    expect(locationIO.vm.pins.length).toBe(1);
    expect(locationIO.vm.pins[0]).toEqual({lat:0, lng:0})
});

test('updatePins re-positions a pin', () => {
    const origionalAddress = locationIO.vm.address;
    locationIO.vm.pins.push({lat:0, lng:0});

    locationIO.vm.updatePins({lat:1, lng:1}, 0);
    expect(locationIO.vm.pins[0]).toEqual({lat:1, lng:1});
    expect(locationIO.vm.address).not.toEqual(origionalAddress);
});