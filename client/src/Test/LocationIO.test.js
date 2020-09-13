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

test('placeToPin converts a place object into a pin object', () => {
    const autocompletePlace = {formatted_address: "Somewhere", geometry: {location: {lat: () => 0, lng: () => 0}}};
    const pin = locationIO.vm.placeToPin(autocompletePlace);
    expect(pin).toEqual({lat: 0, lng:0, name: "Somewhere"})
});

test('addMarker adds a valid pin', () => {
    const pin = {lat: 0, lng:0, name: "Somewhere"};
    locationIO.vm.$refs.mapViewerRef = {panToPin: () => {}};

    locationIO.vm.addMarker(pin);
    expect(locationIO.vm.pins.length).toBe(1);
    expect(locationIO.vm.address).toEqual("Somewhere")
});


test('addMarker adds a pin at the centre of the map when given no arguments', () => {
    locationIO.vm.$refs.mapViewerRef = {
        currentCenter: {lat:0, lng:0},
        panToPin: () => {},
        repositionPin: () => {}
    };

    locationIO.vm.addMarker();  // No Args
    expect(locationIO.vm.pins.length).toBe(1);
    expect(locationIO.vm.pins[0].lat).toEqual(0);
    expect(locationIO.vm.pins[0].lng).toEqual(0);
});
