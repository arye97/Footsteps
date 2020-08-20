import {mount, createLocalVue} from '@vue/test-utils';
import Header from '../components/Header/Header.vue';
import "vue-jest";
import {BootstrapVue} from 'bootstrap-vue';

const localVue = createLocalVue();
localVue.use(BootstrapVue);

let header;

beforeEach(() => {
    header = mount(Header, {
        localVue
    });
});

test('Is a vue instance', () => {
    expect(header.isVueInstance).toBeTruthy();
});

test('Logo is on the page', () => {
    expect(header.find('#logo').is('img')).toBeTruthy();
});

test('Toggle button for collapsible nav-bar exists', () => {
    expect(header.find('#togglerButton')).toBeTruthy();
});

test('There are 5 possible collapsible links, when logged in', () => {
    header = mount(Header, {
        data() {
            return {
                isLoggedIn: true
            }
        },
        localVue
    });
    expect(header.vm.isLoggedIn).toBeTruthy();
    const items = header.findAll('b-nav-item');
    expect(items.length).toEqual(5);
});

test('There are 2 possible collapsible links, when not logged in', () => {
    // Not logged in by default!
    expect(header.findAll('b-nav-item')).toHaveLength(2);
});