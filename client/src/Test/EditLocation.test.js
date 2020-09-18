import {mount, createLocalVue} from '@vue/test-utils';
import EditLocation from '../components/Settings/EditLocation.vue';
import api from '../Api';
import 'vue-jest';
import "jest";
import router from '../index';
import {BootstrapVue} from 'bootstrap-vue';

jest.mock('../Api');
jest.mock('../index');

const localVue = createLocalVue();
localVue.use(BootstrapVue);

let editLocation;

const USER_ID = 1;

let USER_DATA;

const setValues = () => {
    USER_DATA = {
        id: USER_ID,
        firstname: "Pam",
        lastname: "Ham",
        date_of_birth: "1999-10-08",
        gender: "Female",
        passports: [],
        activityTypes: [],
        private_location: {
            name: 'Hisar, Haryana, India',
            latitude: -40.9006,
            longitude: 174.8860
        },
        public_location: {
            name: 'Windsor, ON, Canada',
            latitude: -40.9006,
            longitude: 174.8860
        }
    }
}

const $route = {
    params: {
        userId: USER_ID
    }
};

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
        setValues();
        api.checkProfile.mockImplementation(() => Promise.resolve({status: 200}));
        api.getUserData.mockImplementation(() => Promise.resolve({data: USER_DATA, status: 200}));
        api.editLocation.mockImplementation(() => Promise.resolve({status: 200}));

        editLocation = mount(EditLocation, {
            mocks: {
                $route
            },
            router,
            localVue
        });
        sleep(150).then(() => resolve());
    });
});

afterEach(() => {
    jest.clearAllMocks();
});

test('Is a vue instance', () => {
    expect(editLocation.isVueInstance).toBeTruthy();
});

describe('User starts with no location data', () => {

    beforeEach(() => {
        return new Promise(resolve => {
            USER_DATA.public_location = null;
            USER_DATA.private_location = null;
            api.getUserData.mockImplementation(() => Promise.resolve({data: USER_DATA, status: 200}));
            editLocation = mount(EditLocation, {
                mocks: {
                    $route
                },
                router,
                localVue
            });
            sleep(150).then(() => resolve());
        });
    });

    test('Displays public location as Not Specified', () => {
        expect(editLocation.find('#public-Name-Unspecified1').text()).toBe('Location not yet specified');
    });

    test('Displays private location as Not Specified', () => {
        expect(editLocation.find('#private-Name-Unspecified2').text()).toBe('Location not yet specified');
    });

    test('User can add a public location', async () => {
        setValues();
        editLocation.vm.inputPublicLocation = USER_DATA.public_location;
        await editLocation.vm.$nextTick().then(() => {
            editLocation.find('#save-changes-btn').trigger('click');
            expect(api.editLocation).toBeCalledTimes(1);

        });
        await editLocation.vm.$nextTick().then(() => {
            expect(editLocation.find('#public-Name').text()).toBe(USER_DATA.public_location.name);
        });
    });

    test('User can add a private location', async () => {
        setValues();
        editLocation.vm.inputPrivateLocation = USER_DATA.private_location;
        await editLocation.vm.$nextTick().then(() => {
            editLocation.find('#save-changes-btn').trigger('click');
            expect(api.editLocation).toBeCalledTimes(1);
        });
        await editLocation.vm.$nextTick().then(() => {
            expect(editLocation.find('#private-Name').text()).toBe(USER_DATA.private_location.name);
        });
    });
});

describe('User starts with location data', () => {

    test("Displays public location as 'Windsor, ON, Canada'", () => {
        expect(editLocation.find('#public-Name').text()).toBe(USER_DATA.public_location.name);
    });

    test("Displays private location as 'Hisar, Haryana, India'", () => {
        expect(editLocation.find('#private-Name').text()).toBe(USER_DATA.private_location.name);
    });

});
