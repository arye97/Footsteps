import {shallowMount} from '@vue/test-utils'
import HomeLayout from './../components/layout/HomeLayout.vue'

describe('layout/HomeLayout', () => {
    let homeLayout;

    beforeEach(() => {
        homeLayout = shallowMount(HomeLayout);
    });

    test('setup correctly', () => {
        expect(homeLayout).find()
    })
});