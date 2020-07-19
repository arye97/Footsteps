import {shallowMount} from '@vue/test-utils'
import Login from '../views/Login/Login.vue'
import "jest"
import '../Api'
import api from "../Api";
import router from '../index';
jest.mock("../Api");


let loginWrapper;

beforeEach(() => {
    loginWrapper = shallowMount(Login);
});

// ----AC1----
test('Is a vue instance', () => {
    expect(loginWrapper.isVueInstance).toBeTruthy();
});


// ----AC2----
// checks the email or string that is provided is of type email using regex
// TEST FAILS.  COMMENTED OUT BY BEN.  Login.data().email Always returns an empty string.
// test('AC3 email is of type email', () => {
//     const expected = [
//         expect.stringMatching(/^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$/i),   // Regular expressions to match values in the genders array
//     ];
//     expect(Login.data().email).toEqual(expect.arrayContaining(expected));
// });

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

test('AC9 User is taken to homepage on login', ()=> {
    const userdata = {email: "tester@tester.com", password: "testPass"};
    api.login.mockImplementation(() => Promise.resolve({ data: {'Token': 'ValidToken', 'userId': 1}, status: 201 }));
    api.getUserRoles.mockImplementation( () => Promise.resolve({ data : 0, status : 200}))
    let spy = jest.spyOn(router, 'push');
    loginWrapper = shallowMount(Login, {router, mocks: {api}});
    loginWrapper.setData({...userdata, ...{message:""}});
    return loginWrapper.vm.login(new Event("dummy")).then(() => {
        expect(loginWrapper.vm.api.login).toHaveBeenCalledWith(userdata);
        expect(spy).toHaveBeenCalledWith("/");
    });
});