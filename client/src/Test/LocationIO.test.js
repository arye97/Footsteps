import {mount} from '@vue/test-utils'
import LocationIO from '../components/Map/LocationIO.vue'
import "vue-jest"


let locationIO;

beforeEach(() => {
    locationIO = mount(LocationIO);
    jest.mock("gmap-vue")
});

test('Is a vue instance', () => {
    expect(locationIO.isVueInstance).toBeTruthy();
});

test('Location input  exists', () => {
    expect(locationIO.find('#locationInput').exists()).toBeTruthy();
});

test('Location submit button exists', () => {
    expect(locationIO.find('#LocationSubmit').exists()).toBeTruthy();
});