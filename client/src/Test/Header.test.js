import {mount, createLocalVue, RouterLinkStub} from '@vue/test-utils';
import Header from '../components/Header/Header.vue';
import "vue-jest";
import {BootstrapVue} from 'bootstrap-vue';
import api from "../Api";

jest.mock("../Api");

const localVue = createLocalVue();
localVue.use(BootstrapVue);

let header;

const USER_DATA  = {
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

beforeEach(() => {
    api.logout.mockImplementation(() => Promise.resolve({status:200}));
    api.getAllUserData.mockImplementation(() => Promise.resolve({data: USER_DATA, status: 200}));
    header = mount(Header, {
        localVue,
        stubs: {
            RouterLink: RouterLinkStub
        }
    });
    sessionStorage.setItem("role", 1);
});

test('Is a vue instance', () => {
    expect(header.isVueInstance).toBeTruthy();
});

test('Logo is on the page', () => {
    expect(header.find('img').exists()).toBeTruthy();
});

test('Toggle button for collapsible nav-bar exists', () => {
    expect(header.find('#togglerButton').exists()).toBeTruthy();
});

test('There are 5 possible collapsible links, when logged in', () => {
    header = mount(Header, {
        data() {
            return {
                isLoggedIn: true
            }
        },
        localVue,
        stubs: {
            RouterLink: RouterLinkStub
        }
    });
    expect(header.vm.isLoggedIn).toBeTruthy();
    const items = header.findAll('.nav-item');
    expect(items.length).toEqual(5);
});

test('There are 2 possible collapsible links, when not logged in', () => {
    // Not logged in by default!
    expect(header.findAll('.nav-item')).toHaveLength(2);
});