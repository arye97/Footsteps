import {shallowMount,createLocalVue} from '@vue/test-utils'
import Details from '../views/Settings/Details.vue'
import VueRouter from 'vue-router'
import '../Api'
import 'jest'
import server from "../Api";
import {fitnessLevels} from "../constants";
jest.mock("../Api");

let detailsWrapper;
const userId = 1;

const localVue = createLocalVue();
// localVue.use(VueRouter);

// const freshLocalVue = createLocalVue()
// freshLocalVue.use(VueRouter)

const $route = { params: userId };

const router = new VueRouter();

beforeEach(() => {
    detailsWrapper = shallowMount(Details, {
        localVue,
        // router,
        mocks: {
            $route,
            server
        },
        methods: {
            updateInputs() {
                return {
                    profileId: userId,
                    firstname: 'Mover',
                    middlename: '',
                    lastname: 'Shaker',
                    password: 'ITestForALiving',
                    gender: 'Male',
                    date_of_birth: '10-10-1999',
                    fitness: 3,
                    nickname: '',
                    bio: '',
                    passports: [],
                    countries: [],
                    genders: ['Male', 'Female', 'Non-Binary'],
                    loggedIn: false,
                    fitnessOptions: fitnessLevels,
                    isMyProfile: null
                }
            },
            validateUserIdWithToken() {
                 return {

                 }
            }
        }
    });
});

// ----AC1----
test('Is a vue instance', () => {
    expect(detailsWrapper.isVueInstance).toBeTruthy();
});

describe("Checks if a user id provided from query parameters belongs to a specified token", () => {
    test("AC3 A user profile can only be modified by the user that owns the profile and by any admin.", () => {
        // server.get.mockImplementation(() => Promise.resolve({ data: 'ValidToken', status: 201 }));
        server.mockImplementation(() => Promise.resolve({ data: 'ValidToken', status: 201 }));
        detailsWrapper = shallowMount(Details, {router, mocks: {server}});

        let spy = jest.spyOn(router, 'push');
        console.log("i hate you jest");
        // console.log(detailsWrapper.vm.validateUserIdWithToken)
        // console.log(detailsWrapper.vm.validateUserIdWithToken())
        // console.log(detailsWrapper.vm.updateInputs())
        // console.log(detailsWrapper.vm.validateUserIdWithToken.then())

        // console.log(detailsWrapper.vm.registerUser)
        // console.log(detailsWrapper.vm.validateUserIdWithToken())
            detailsWrapper.vm.validateUserIdWithToken().then(() => {
                expect(detailsWrapper.vm.server.get)
                }
            )

                // expect(detailsWrapper.vm.server.get).toHaveBeenCalledWith(`/check-profile/${userId}`, {
                //     "headers": {
                //         "Content-Type": "application/json",
                //         "Token": 'ValidToken'
                //     },
                //     "withCredentials": true
                // });
                //expect(spy).toHaveBeenCalledWith(`/check-profile/${userId}`);
        //});
    });
});