import AdminDashboard from '../views/AdminDashboard.vue'
import {shallowMount} from "@vue/test-utils";

let adminDashboard;

beforeEach(() => {
    adminDashboard = shallowMount(AdminDashboard);
});

test('Is a vue instance', () => {
    expect(adminDashboard.isVueInstance).toBeTruthy();
})