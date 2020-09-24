import {mount, createLocalVue} from '@vue/test-utils';
import EditPassword from '../components/Settings/EditPassword.vue';
import api from "../Api";
import 'vue-jest';
import router from "../index";
import {BootstrapVue} from "bootstrap-vue";
jest.mock("../Api");

const localVue = createLocalVue();
localVue.use(BootstrapVue);

let editWrapper;
const testUser = {
    id:404,
    firstname:"Testing",
    middlename:"This",
    lastname:"Vue",
    nickname:"tester.boi",
    bio:"I test Vue files",
    date_of_birth:"1999-10-08",
    gender:"Male",
    fitness:0,
    passports:[],
    activityTypes:[]
};
const password = "testpassword1234";



/**
 * A function to cause a delay before a promise is resolved
 * @param milliseconds time to delay
 * @returns {Promise<any>} code to execute after delay
 */
const sleep = (milliseconds) => {
    return new Promise(resolve => setTimeout(resolve, milliseconds))
};

beforeEach(() => {
    return new Promise(resolve => {
        api.getUserData.mockImplementation(() => Promise.resolve({data: testUser, status: 200}));
        api.updatePassword.mockImplementation(() => Promise.resolve({status: 200}));
        let div = document.createElement('div');
        if (document.body) {
            document.body.appendChild(div);
        }
        editWrapper = mount(EditPassword, {
            router,
            attachTo: div,
            mocks: {api},
            localVue
        });
        sleep(150).then(() => resolve());
    });
});

afterEach(() => {
    jest.clearAllMocks();
});

test('Is a vue instance', () => {
    expect(editWrapper.isVueInstance).toBeTruthy();
});

describe('Successful changes', () => {
    it('Normal Password Change', done => {
        editWrapper.vm.$nextTick().then(() => {
            editWrapper.find('#newPass').setValue(password);
            editWrapper.find('#repeatPass').setValue(password);
            editWrapper.find('#oldPass').setValue(password + '5');
            editWrapper.find('#save').trigger('click');
            expect(editWrapper.vm.api.updatePassword).toBeCalledWith(testUser.id, password+'5', password, password);
            done();
        });
    });
    it('Extremely long password', done => {
        editWrapper.vm.$nextTick().then(() => {
            const newPass = password.repeat(100);
            editWrapper.find('#newPass').setValue(newPass);
            editWrapper.find('#repeatPass').setValue(newPass);
            editWrapper.find('#oldPass').setValue(password + '5');
            editWrapper.find('#save').trigger('click');
            expect(editWrapper.vm.api.updatePassword).toBeCalledWith(testUser.id, password+'5', newPass, newPass);
            done();
        });
    });
});

describe('Unsuccessful changes', () => {
    it('Blank new password', done => {
        editWrapper.vm.$nextTick().then(() => {
            editWrapper.find('#newPass').setValue(password);
            editWrapper.find('#repeatPass').setValue(password);
            editWrapper.find('#oldPass').setValue(password);
            editWrapper.find('#save').trigger('click');
            expect(editWrapper.vm.api.updatePassword).toBeCalledTimes(0);
            done();
        });
    });
    it('Blank repeated password', done => {
        editWrapper.vm.$nextTick().then(() => {
            editWrapper.find('#newPass').setValue(password);
            editWrapper.find('#repeatPass').setValue("");
            editWrapper.find('#oldPass').setValue(password);
            editWrapper.find('#save').trigger('click');
            expect(editWrapper.vm.api.updatePassword).toBeCalledTimes(0);
            done();
        });
    });
    it('Short new password', done => {
        editWrapper.vm.$nextTick().then(() => {
            const newPassword = "hi12345";
            editWrapper.find('#newPass').setValue(newPassword);
            editWrapper.find('#repeatPass').setValue(newPassword);
            editWrapper.find('#oldPass').setValue(password);
            editWrapper.find('#save').trigger('click');
            expect(editWrapper.vm.api.updatePassword).toBeCalledTimes(0);
            done();
        });
    });
    it('Numberless new password', done => {
        editWrapper.vm.$nextTick().then(() => {
            const newPassword = "hellotherefriends";
            editWrapper.find('#newPass').setValue(newPassword);
            editWrapper.find('#repeatPass').setValue(newPassword);
            editWrapper.find('#oldPass').setValue(password);
            editWrapper.find('#save').trigger('click');
            expect(editWrapper.vm.api.updatePassword).toBeCalledTimes(0);
            done();
        });
    });
    it('Letterless new password', done => {
        editWrapper.vm.$nextTick().then(() => {
            const newPassword = "123456789";
            editWrapper.find('#newPass').setValue(newPassword);
            editWrapper.find('#repeatPass').setValue(newPassword);
            editWrapper.find('#oldPass').setValue(password);
            editWrapper.find('#save').trigger('click');
            expect(editWrapper.vm.api.updatePassword).toBeCalledTimes(0);
            done();
        });
    });
    it('New password mismatch', done => {
        editWrapper.vm.$nextTick().then(() => {
            editWrapper.find('#newPass').setValue(password + '5');
            editWrapper.find('#repeatPass').setValue(password + '6');
            editWrapper.find('#oldPass').setValue(password + '7');
            editWrapper.find('#save').trigger('click');
            expect(editWrapper.vm.api.updatePassword).toBeCalledTimes(0);
            done();
        });
    });
    it('No password change', done => {
        editWrapper.vm.$nextTick().then(() => {
            editWrapper.find('#newPass').setValue(password + '5');
            editWrapper.find('#repeatPass').setValue(password + '5');
            editWrapper.find('#oldPass').setValue(password + '5');
            editWrapper.find('#save').trigger('click');
            expect(editWrapper.vm.api.updatePassword).toBeCalledTimes(0);
            done();
        });
    });
});