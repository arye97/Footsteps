import {shallowMount} from '@vue/test-utils'
import Register from '../views/Register/Register.vue'
import each from 'jest-each'


let registerWrapper;

beforeEach(() => {
    registerWrapper = shallowMount(Register);
});


test('Is a vue instance', () => {
    expect(registerWrapper.isVueInstance).toBeTruthy();
});

// ----AC4----
const mandatoryFieldIds = [
    ['first-name-label'],  // A list of all mandatory attributes
    ['last-name-label'],
    ['email-label'],
    ['password-label'],
    ['passwordCheck-label'],
    ['gender-label'],
    ['date_of_birth-label']
];
// Iterate though all mandatory field names (using their id attribute) and check that they contain an asterisk
each(mandatoryFieldIds).test('AC1 %s is marked as a mandatory attribute (with an asterisk)', (field_name) => {
    // Checks that each <label> tag above all mandatory fields ends in an asterisk.
    expect(registerWrapper.find('#' + field_name).text()).toContain("*");
});


