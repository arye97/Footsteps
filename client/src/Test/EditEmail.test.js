import {shallowMount} from '@vue/test-utils'
import EditEmail from '../views/Settings/EditEmail.vue'
import api from "../Api"
import 'vue-jest'
jest.mock("../Api");

let editEmail;
let push;

const DEFAULT_USER_ID = 1;


beforeEach(async () => {
    push = jest.fn();
    const $router = {
        push: jest.fn()
    };
    const $route = {
        params: {userId : undefined},
    };

    api.getUserData.mockImplementation(
        () => Promise.resolve({ data: {id: DEFAULT_USER_ID}, status: 201 }))

    api.getUserEmails.mockImplementation(
        () => Promise.resolve({
            data: {
                userId: DEFAULT_USER_ID,
                additionalEmails: ["johnhopkins@hotmail.com", "stewart@yahoo.com"],
                primaryEmail: "johntester@tester.com"
            },
            status: 200
        }));

    editEmail = shallowMount(EditEmail, {
        methods: {
            editable: async () => {},
            logout: () => {}
        },
        mocks: {
            $route,
            $router
        }
    });
});

test('Is a vue instance', () => {
    expect(editEmail.exists()).toBeTruthy();
    expect(editEmail.isVueInstance).toBeTruthy();
});


test('Displays Primary Email', () => {
    expect(editEmail.find("#primaryEmail").text()).toBe("johntester@tester.com");
});