import {shallowMount} from '@vue/test-utils'
import SearchPage from '../views/Search.vue'
import "jest"

let searchPage;

beforeEach(() => {
    searchPage = shallowMount(SearchPage);
});

test('Is a vue instance', () => {
    expect(searchPage.isVueInstance).toBeTruthy();
});

test('Activities search bos exists', () => {
    expect(searchPage.find('#searchBoxActivities').exists()).toBeTruthy();
});

test('Search mode selector exists', () => {
    expect(searchPage.find('#searchModeSelect').exists()).toBeTruthy();
});

test('Search button exists', () => {
    expect(searchPage.find('#searchButton').exists()).toBeTruthy();
});

test('And search operator radio button exists', () => {
    expect(searchPage.find('#andRadioButton').exists()).toBeTruthy();
});

test('Or search operator radio button exists', () => {
    expect(searchPage.find('#orRadioButton').exists()).toBeTruthy();
});



