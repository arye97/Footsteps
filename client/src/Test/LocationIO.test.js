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

const pin1 = {
    lat: 10.0,
    lng: 25.0,
    name: "Activity location"
};
const pin2 = {
    lat: 75.0,
    lng: 100.0,
    name: "Activity destination"
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

describe("Test placeToPin", () => {

    test('placeToPin converts a place object into a pin object', () => {
        const autocompletePlace = {formatted_address: "Somewhere", geometry: {location: {lat: () => 0, lng: () => 0}}};
        const pin = locationIO.vm.placeToPin(autocompletePlace);
        expect(pin).toEqual({lat: 0, lng: 0, name: "Somewhere", colour: 'red', windowOpen: false})
    });

});

describe("Test addMarker", () => {

    test('addMarker adds a valid pin', () => {
        const pin = {lat: 0, lng: 0, name: "Somewhere"};
        locationIO.vm.$refs.mapViewerRef = {
            panToPin: () => {
            }
        };

        locationIO.vm.addMarker(pin);
        expect(locationIO.vm.pins.length).toBe(1);
        expect(locationIO.vm.pins[0]).toEqual({lat: 0, lng: 0, name: "Somewhere", colour: 'red'});
        expect(locationIO.vm.address).toEqual("Somewhere")
    });


    test('addMarker adds a pin at the centre of the map when given no arguments', () => {
        locationIO.vm.$refs.mapViewerRef = {
            currentCenter: {lat: 0, lng: 0},
            panToPin: () => {
            },
            repositionPin: () => {
            }
        };

        locationIO.vm.addMarker();  // No Args
        expect(locationIO.vm.pins.length).toBe(1);
        expect(locationIO.vm.pins[0].lat).toEqual(0);
        expect(locationIO.vm.pins[0].lng).toEqual(0);
        expect(locationIO.vm.pins[0].colour).toEqual('red');
    });

    test.each([
        {singleOnly: true},
        {maxPins: 1},
        {singleOnly: true, maxPins: 1},
    ])('Only a single pin can be added in single-only mode and when max-pin == 1', async (props) => {
        locationIO = mount(LocationIO, {localVue, propsData: props});
        locationIO.vm.$refs.mapViewerRef = {panToPin: () => {}};

        expect(locationIO.vm.$data.pins).toHaveLength(0);
        locationIO.vm.addMarker(pin1);
        await locationIO.vm.$nextTick();  // Need to let the pins watcher update
        expect(locationIO.vm.$data.pins).toHaveLength(1);
        expect(locationIO.vm.$data.pins).toContainEqual(pin1);

        locationIO.vm.addMarker(pin2);
        await locationIO.vm.$nextTick();  // Need to let the pins watcher update
        expect(locationIO.vm.$data.pins).toHaveLength(1);
        expect(locationIO.vm.$data.pins).toContainEqual(pin2)
    });

    test('Add marker function emits an event with the list of pins', async () => {
        locationIO.vm.$refs.mapViewerRef = {panToPin: () => {}};
        locationIO.vm.addMarker(pin1);
        await locationIO.vm.$nextTick();

        let numEmits = locationIO.emitted('child-pins').length;
        expect(locationIO.emitted('child-pins')[numEmits-1][0]).toContainEqual(pin1)  // Check the last emitted event
    });

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


