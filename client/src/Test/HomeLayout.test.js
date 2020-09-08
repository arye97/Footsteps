import {mount, createLocalVue} from '@vue/test-utils'
import HomeLayout from '../components/layout/HomeLayout.vue'
import 'vue-jest'
import BootstrapVue from "bootstrap-vue";

const localVue = createLocalVue();
localVue.use(BootstrapVue);

let homeLayout;
let push;
let config;

beforeEach(() => {
    push = jest.fn();
    const $router = {
        push: jest.fn()
    };
    const $route = {
        login: '/login',
        register: '/register'
    };
    config = {
        mocks: {
            $route,
            $router
        },
        localVue
    };
    homeLayout = mount(HomeLayout, config);
});

test('Is a vue instance', () => {
    expect(homeLayout.isVueInstance).toBeTruthy();
});

test('Logo is on the page', () => {
    expect(homeLayout.find('img').exists()).toBeTruthy()
});

// ----AC1----
test('AC1 Has 2 buttons', () => {
    expect(homeLayout.findAll('button')).toHaveLength(2);
});

// ----AC1----
test.each([
    ['register'],
    ['login'],
])('AC1 Has a %s button', (button) => {
    expect(homeLayout.find('#'+ button + 'Button').exists()).toBeTruthy();
});
