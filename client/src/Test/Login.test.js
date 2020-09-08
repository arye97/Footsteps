import {shallowMount} from '@vue/test-utils'
import Login from '../views/Login/Login.vue'
import 'vue-jest'
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


test('AC9 User is taken to homepage on login', async ()=> {
    const userdata = {email: "tester@tester.com", password: "testPass"};
    api.login.mockImplementation(() => Promise.resolve({ data: {'Token': 'ValidToken', 'userId': 1}, status: 201 }));
    api.getUserRoles.mockImplementation( () => Promise.resolve({ data : 0, status : 200}))
    let spy = jest.spyOn(router, 'push');
    loginWrapper = shallowMount(Login, {router, mocks: {api}});
    await loginWrapper.vm.$router.push('/login');
    loginWrapper.setData({...userdata, ...{message:""}});
    return loginWrapper.vm.login(new Event("dummy")).then(() => {
        expect(loginWrapper.vm.api.login).toHaveBeenCalledWith(userdata);
        expect(spy).toHaveBeenCalledWith("/");
    });
});

test('AC9 Admin is taken to dashboard on login', ()=> {
    const userdata = {email: "tester@tester.com", password: "testPass"};
    api.login.mockImplementation(() => Promise.resolve({ data: {'Token': 'ValidToken', 'userId': 1}, status: 201 }));
    api.getUserRoles.mockImplementation( () => Promise.resolve({ data : 20, status : 200}))
    let spy = jest.spyOn(router, 'push');
    loginWrapper = shallowMount(Login, {router, mocks: {api}});
    loginWrapper.setData({...userdata, ...{message:""}});
    return loginWrapper.vm.login(new Event("dummy")).then(() => {
        expect(loginWrapper.vm.api.login).toHaveBeenCalledWith(userdata);
        expect(spy).toHaveBeenCalledWith("/admin");
    });
});