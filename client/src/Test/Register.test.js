import {shallowMount} from '@vue/test-utils'
import Register from '../views/Register/Register.vue'
import "jest"

let registerWrapper;

beforeEach(() => {
    registerWrapper = shallowMount(Register);
});


test('Is a vue instance', () => {
    expect(registerWrapper.isVueInstance).toBeTruthy();
});

// ----S2 AC3----
test('AC3 List of countries from an external api', () => {
    //Tests that xhr is used to make a call to an external api by mocking the xhr class
    const fakeXhr = () => ({
        onload          : () => {throw DOMException},
        open            : jest.fn(),
        send            : function () {this.onload()},
        status          : 200,
        readyState      : 4,
        response        : JSON.stringify([{"name": "New Zealand"}, {"name": "Australia"}])
    });
    let  fakeJestXhr = jest.fn().mockImplementation(fakeXhr);
    window.XMLHttpRequest = fakeJestXhr;
    let testWrapper = shallowMount(Register, {mocks: {global: {XMLHttpRequest: fakeJestXhr}}});
    testWrapper.vm.fetchCountries();
    expect(testWrapper.vm.$data.countries.sort()).toEqual(["New Zealand",  "Australia"].sort());
});






