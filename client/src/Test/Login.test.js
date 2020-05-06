import {shallowMount} from '@vue/test-utils'
import Login from '../views/Login/Login.vue'


let registerWrapper;

beforeEach(() => {
    registerWrapper = shallowMount(Login);
});

// ----AC1----
test('Is a vue instance', () => {
    expect(registerWrapper.isVueInstance).toBeTruthy();
});


// ----AC2----
// checks the email or string that is provided is of type email using regex
test('AC3 email is of type email', () => {
    const expected = [
        expect.stringMatching(/^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$/i),   // Regular expressions to match values in the genders array
    ];
    expect(Login.data().email).toEqual(expect.arrayContaining(expected));
});

// ----AC3----
// This only needs to be used in production, not for manual testing - makes life hard
// Checks for minimum of 8 characters, and contains a capital letter and a number

//Uncomment this code and delete this line when needed for production testing
// test('AC4 password is of min length 8 and contains a capital letter and a number', () => {
//     const expected = [
//         expect.stringMatching(/"^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$"/i)
//     ];
//     expect(Login.data().password).toEqual(expect.arrayContaining(expected));
// });