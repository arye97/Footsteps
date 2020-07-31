import {shallowMount} from '@vue/test-utils'
import EditEmail from '../views/Settings/EditEmail.vue'
import 'vue-jest'

let editEmail;
let push;
let config;
const DEFAULT_USER_ID = 1;

beforeAll(() => {
    push = jest.fn();
    const $router = {
        push: jest.fn()
    };
    const $route = {
        params: {userId : DEFAULT_USER_ID},
    };

    editEmail = shallowMount(EditEmail, {
        methods: {
            editable: async function () {}
        },
        mocks: {
            $route,
            $router
        }
    });
    editEmail.vm.$data.isEditable = true;

});

test('Is a vue instance', () => {
    expect(editEmail.exists()).toBeTruthy();
    expect(editEmail.isVueInstance).toBeTruthy();
});
