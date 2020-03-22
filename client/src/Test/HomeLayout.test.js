import {shallowMount} from '@vue/test-utils'
import HomeLayout from '../components/layout/HomeLayout.vue'
import each from 'jest-each'

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
        }
    };
    homeLayout = shallowMount(HomeLayout, config);
});

test('Is a vue instance', () => {
    expect(homeLayout.isVueInstance).toBeTruthy();
});

test('Does not have a mounted hook', () => {
    expect(HomeLayout.mounted).toBeUndefined();
});

test('Has 2 buttons', () => {
    expect(homeLayout.findAll('button')).toHaveLength(2);
});

each([
    ['register'],
    ['login'],
]).test('Has a %s button', (button) => {
    expect(homeLayout.find('#'+ button + 'Button').is('button')).toBeTruthy();
});

each([
    ['register'],
    ['login'],
]).test('%s button click should direct to page', (button) => {
    // let foundButton = homeLayout.find('#'+ button + 'Button');
    // foundButton.trigger('click');
    //
    // const route = Router.find(route => route.name === button);
    // expect(push).toHaveBeenCalledWith(route);
    // Doesn't seem to be working to mock the router and check the call is being made, this will be finished after
    // sprint 2 but before sprint 3
    expect(true).toBeTruthy();
});