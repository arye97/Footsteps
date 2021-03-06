import {shallowMount} from '@vue/test-utils'
import Register from '../views/Register/Register.vue'
import '../util'
import '../Api'
import 'vue-jest'
import api from "../Api";
import router from '../index'
jest.mock("../Api");
jest.mock('../util', ()=> {
    const actual = jest.requireActual('../util');
    const mocks = {
        ...actual,
        fetchCountries: jest.fn().mockReturnValue(['New Zealand','Australia'])
    };
    return mocks;
});
import { _isValidDOB } from '../util'

let registerWrapper;
let spy;


beforeEach(() => {
    api.register.mockImplementation(() => Promise.resolve({ data: 'ValidToken', status: 201 }));
    spy = jest.spyOn(router, 'push');
    registerWrapper = shallowMount(Register, {router, mocks: {api}});
});



test('Is a vue instance', () => {
    expect(registerWrapper.isVueInstance).toBeTruthy();
});


// ----AC4----
const mandatoryFieldIds = [
    ['first-name'],  // A list of all mandatory attributes
    ['last-name'],
    ['email'],
    ['password'],
    ['passwordCheck'],
    ['gender'],
    ['date_of_birth']
];
// Iterate though all mandatory field names (using their id attribute) and check that they contain an asterisk
test.each(mandatoryFieldIds)('AC4 %s is marked as a mandatory attribute (with an asterisk)', (field_name) => {
    // Checks that each <label> tag above all mandatory fields ends in an asterisk.
    expect(registerWrapper.get('[labelfor="' + field_name + '"]').attributes('label')).toContain("*");
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



// Test isValidDOB() function
describe("isValidDOB checks that a date of birth is older than number of years", () => {
    const current = new Date(Date.now());
    let date;
    let dateStr;
    const minAge = 13;

    // NOTE: it() performs exactly the same as test() but the former reads better
    // Too young
    it("should be false if age is *less* than " + minAge, () => {
        date = new Date(current);
        date.setFullYear(date.getFullYear() - (minAge - 1));
        dateStr = date.toISOString().split('T')[0];
        expect(_isValidDOB(dateStr).vaild).toBeFalsy();
    });

    // Old enough
    it("should be true if age is *greater* than " + minAge, () => {
        date = new Date(current);
        date.setFullYear(date.getFullYear() - (minAge + 1));
        dateStr = date.toISOString().split('T')[0];
        expect(_isValidDOB(dateStr).valid).toBeTruthy();
    });
});


// ----AC9----
test('AC9 User is taken to homepage on register', ()=> {
    const userdata = {
        firstname: 'Test',
        middlename: '',
        lastname: 'Testers',
        password: 'ITestForALiving1',
        gender: 'Male',
        date_of_birth: '10-10-1999',
        fitness: 1,
        nickname: '',
        bio: '',
        passports: [],
        activity_types: []
    };
    const extraData = {
        passwordCheck: userdata.password,
        primary_email: "tester@test.com",
        fitness: {value: 1, desc: "Unfit, no regular exercise, being active is very rare"},
    };
    registerWrapper.setData({...userdata, ...{passwordCheck: extraData.passwordCheck, email: extraData.primary_email, fitness: extraData.fitness}});
    return registerWrapper.vm.registerUser(new Event("dummy")).then(() => {
        expect(registerWrapper.vm.api.register)
            .toHaveBeenCalledWith({...userdata, ...{primary_email: extraData.primary_email}});
        expect(spy).toHaveBeenCalledWith("/");
    });
});
