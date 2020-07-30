import {shallowMount} from '@vue/test-utils'
import Header from '../components/Header/Header.vue'
import "jest"

let header;

beforeEach(() => {
    header = shallowMount(Header);
});

test('Is a vue instance', () => {
    expect(header.isVueInstance).toBeTruthy();
});

test('Logo is on the page', () => {
    expect(header.find('#logo').is('img')).toBeTruthy()
});

