import {shallowMount} from '@vue/test-utils'
import HomeLayout from '../components/layout/HomeLayout.vue'
import each from 'jest-each'

let homeLayout;

beforeEach(() => {
    homeLayout = shallowMount(HomeLayout);
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
]).test('%s button click should direct to page', (button) => {
    console.log(button);
    const spy = jest.fn();
    homeLayout.vm.$on(button, spy);

    let foundButton = homeLayout.find('#'+ button + 'Button');
    foundButton.trigger('click');

    expect(spy).toHaveBeenCalledTimes(1);
});

each([
    ['register'],
    ['login'],
]).test('Has a %s button', (button) => {
    expect(homeLayout.find('#'+ button + 'Button').is('button')).toBeTruthy();
});