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
each(mandatoryFieldIds).test('AC4 %s is marked as a mandatory attribute (with an asterisk)', (field_name) => {
    // Checks that each <label> tag above all mandatory fields ends in an asterisk.
    expect(registerWrapper.get('#' + field_name).text()).toContain("*");
});


// ----AC7----
test('AC7 Gender dropdown menu contains “male”, “female”, and “non-binary”', () => {
    // Tests that the genders array contains the right strings.
    // Would be nice to test whether the genders array is used in the dropdown menu
    const expected = [
        expect.stringMatching(/male/i),   // Regular expressions to match values in the genders array
        expect.stringMatching(/female/i),
        expect.stringMatching(/non-binary/i),   // Could change this to accept underscore or hyphen
    ];

    expect(Register.data().genders).toEqual( expect.arrayContaining(expected) );
    expect(Register.data().genders).toHaveLength(3);
});






