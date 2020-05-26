import {shallowMount} from '@vue/test-utils'
import Register from '../views/Register/Register.vue'
import { _isValidDOB } from '../util'
import '../Api'
import 'vue-jest'
import server from "../Api";
import router from '../index'
jest.mock("../Api");

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
test.each(mandatoryFieldIds)('AC4 %s is marked as a mandatory attribute (with an asterisk)', (field_name) => {
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



// Test isValidDOB() function
describe("isValidDOB checks that a date of birth is older than number of years", () => {
    const current = new Date(Date.now())
    console.log("Current " + current)
    let date;
    let dateStr;
    const minAge = 13;

    // NOTE: it() performs exactly the same as test() but the former reads better
    // Too young
    it("should be false if age is *less* than " + minAge, () => {
        date = new Date(current)
        date.setFullYear(date.getFullYear() - (minAge - 1));
        dateStr = date.toISOString().split('T')[0];
        expect(_isValidDOB(dateStr).vaild).toBeFalsy();
    });

    // Old enough
    it("should be true if age is *greater* than " + minAge, () => {
        date = new Date(current)
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
        passports: []
    };
    const extraData = {
        passwordCheck: userdata.password,
        primary_email: "tester@test.com",
        fitness: {value: 1, desc: "Unfit, no regular exercise, being active is very rare"},
    };
    server.post.mockImplementation(() => Promise.resolve({ data: 'ValidToken', status: 201 }));
    let spy = jest.spyOn(router, 'push');
    registerWrapper = shallowMount(Register, {router, mocks: {server}});
    registerWrapper.setData({...userdata, ...{passwordCheck: extraData.passwordCheck, email: extraData.primary_email, fitness: extraData.fitness}});
    return registerWrapper.vm.registerUser().then(() => {
        expect(registerWrapper.vm.server.post)
            .toHaveBeenCalledWith("/profiles",
                {...userdata, ...{primary_email: extraData.primary_email}},
                {"headers": {"Access-Control-Allow-Origin": "*", "Content-Type": "application/json"}, "withCredentials": true});
        expect(spy).toHaveBeenCalledWith("/profile");
    });
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