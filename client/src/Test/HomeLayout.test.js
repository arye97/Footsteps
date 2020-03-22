import {shallowMount} from '@vue/test-utils'
import HomeLayout from '../components/layout/HomeLayout.vue'

describe('HomeLayout', () => {
    let homeLayout;

    beforeEach(() => {
        homeLayout = shallowMount(HomeLayout);
    });

    test('Home Layout is a vue instance', () => {
        expect(homeLayout.isVueInstance).toBeTruthy();
    });

    test('Home Layout does not have a mounted hook', () => {
        expect(typeof HomeLayout.mounted).toBe('undefined');
    });

    test('Home Layout has 2 buttons', () => {
        let length = homeLayout.findAll('button').length;
        expect(length).toBe(2);
    })
});