import {shallowMount, createLocalVue} from '@vue/test-utils'
import MapViewer from '../components/Map/MapViewer'
import {BootstrapVue} from 'bootstrap-vue';
// import { gmapApi } from 'gmap-vue';
import "vue-jest"
import * as GmapMap from "gmap-vue";

jest.mock("gmap-vue");

const localVue = createLocalVue();
localVue.use(BootstrapVue);
// localVue.use(gmapApi);
// localVue.use(GmapVue, { load: { key: '', }, installComponents: false });

let config;

let mapViewer;

beforeEach(() => {
    config = {
        localVue
    };
    mapViewer = shallowMount(MapViewer, config);
});

test('Is a vue instance', () => {
    expect(mapViewer.isVueInstance).toBeTruthy();
});

test('map pane does not exist at default', () => {
    expect(mapViewer.find('#mapComponent').exists()).toBeFalsy();
});

test('gmap Auto complete does not exist at default', () => {
    expect(mapViewer.find('#gmapAutoComplete').exists()).toBeFalsy();
});

test('Add marker button does not exist at default', () => {
    expect(mapViewer.find('#addMarkerButton').exists()).toBeFalsy();
});

test('Hide map button does not exist at default', () => {
    expect(mapViewer.find('#hideMapButton').exists()).toBeFalsy();
});

test('Show map button exists at default', () => {
    expect(mapViewer.find('#showMapButton').exists()).toBeTruthy();
});