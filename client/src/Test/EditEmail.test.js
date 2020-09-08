import {mount, createLocalVue} from '@vue/test-utils';
import EditEmail from '../views/Settings/EditEmail.vue';
import api from "../Api";
import 'vue-jest';
import {BootstrapVue, BootstrapVueIcons} from "bootstrap-vue";
jest.mock("../Api");

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);

let editEmail;
let push;

const PAUSE = 150;
const DEFAULT_USER_ID = 1;
const MAX_EMAILS = 5;

/**
 * A function to cause a delay before a promise is resolved
 * @param milliseconds time to delay
 * @returns {Promise<any>} code to execute after delay
 */
const sleep = (milliseconds) => {
    return new Promise(resolve => setTimeout(resolve, milliseconds))
};

/**
 * Before each test, mock the necessary properties and methods.
 */
beforeEach(() => {
    return new Promise(resolve => {
        push = jest.fn();
        const $router = {
            push: jest.fn()
        };
        const $route = {
            params: {userId : undefined},
        };

        api.getUserData.mockImplementation(
            () => Promise.resolve({ data: {id: DEFAULT_USER_ID}, status: 201 })
        );
        api.getUserEmails.mockImplementation(
            () => Promise.resolve({
                data: {
                    userId: DEFAULT_USER_ID,
                    additionalEmails: ["johnhopkins@hotmail.com", "stewart@yahoo.com"],
                    primaryEmail: "johntester@tester.com"
                },
                status: 200
            })
        );
        api.checkProfile.mockImplementation(
            () => Promise.resolve({status: 200})
        );
        api.logout.mockImplementation(
            () => Promise.resolve({status: 200})
        );

        editEmail = mount(EditEmail, {
            mocks: {
                $route,
                $router
            },
            localVue
        });

        // This causes a delay between beforeEach finishing, and the tests being run.
        // This isn't at all good practice, but its the only way I am able
        // to get EditEmail to fully mount before the tests are run.
        // resolve() signals the above promise to complete
        sleep(PAUSE).then(() => resolve());
    });
});


test('Is a vue instance', () => {
    expect(editEmail.exists()).toBeTruthy();
    expect(editEmail.isVueInstance).toBeTruthy();
});


test('Displays Primary Email', () => {
    expect(editEmail.find("#primaryEmail").text()).toBe("johntester@tester.com");
});


test('Displays Secondary Emails', () => {
    let index1 = editEmail.vm.$data.additionalEmails.indexOf("johnhopkins@hotmail.com");
    expect(editEmail.find("#additionalEmail" + index1).text()).toEqual("johnhopkins@hotmail.com");

    let index2 = editEmail.vm.$data.additionalEmails.indexOf("stewart@yahoo.com");
    expect(editEmail.find("#additionalEmail" + index2).text()).toEqual("stewart@yahoo.com");
});


test('Make Secondary Primary', () => {
    let oldPrimary = editEmail.vm.$data.primaryEmail;
    let oldSecondary = editEmail.vm.$data.additionalEmails[0];

    editEmail.find("#primaryButton").trigger('click');
    expect(editEmail.vm.$data.primaryEmail).toEqual(oldSecondary);
    expect(editEmail.vm.$data.additionalEmails).toContain(oldPrimary);
});


test('Add Secondary Email', () => {
    const newEmail1 = "mynewemail1@gmail.com";

    editEmail.vm.$data.insertedEmail = newEmail1;
    editEmail.find("#addEmail").trigger("submit.prevent");

    expect(editEmail.vm.$data.additionalEmails).toContain(newEmail1);
});


test('Add More than 5 Emails', () => {
    let newEmail;
    for (let i=1; i <= MAX_EMAILS + 1; i++) {
        newEmail =  "mynewemail" + i + "@gmail.com";

        editEmail.vm.$data.insertedEmail = newEmail;
        editEmail.find('#addEmail').trigger("submit.prevent");
    }

    expect(editEmail.vm.$data.additionalEmails.length).toBe(4);
    // The emails after i=2 shouldn't be added
    expect(editEmail.vm.$data.additionalEmails).not.toContain("mynewemail3@gmail.com");
});


test('Delete Secondary Email', () => {
    let deletedSecondary = editEmail.vm.$data.additionalEmails[0];

    editEmail.find("#deleteButton").trigger("click");

    expect(editEmail.vm.$data.additionalEmails).not.toContain(deletedSecondary);
});


test('Delete All Secondary Emails', () => {

    for (let i=0; i < MAX_EMAILS; i++) {
        editEmail.find("#deleteButton").trigger("click");
    }

    expect(editEmail.vm.$data.additionalEmails.length).toEqual(0);
});