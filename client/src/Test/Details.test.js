import {shallowMount} from '@vue/test-utils'
import Details from '../views/Settings/Details.vue'
import '../Api'
import 'jest'
import server from "../Api";
import router from "../index";
jest.mock("../Api");

let detailsWrapper;

beforeEach(() => {
    detailsWrapper = shallowMount(Details)
});

test('Is a vue instance', () => {
    expect(detailsWrapper.isVueInstance.toBeTruthy());
});

describe("validateUserIdWithToken which checks if a user id provided from query parameters belongs to a specified token", () => {
    test("it should filter by a search term (link)", () => {
        const userId = 1;
        server.get.mockImplementation(() => Promise.resolve({ data: 'ValidToken', status: 201 }));
        let spy = jest.spyOn(router, 'push');
        detailsWrapper = shallowMount(Details, {router, mocks: {server}});
        console.log(detailsWrapper.vm);
        detailsWrapper.vm.validateUserIdWithToken() //.then(() => {
            expect(detailsWrapper.vm.server.get).toHaveBeenCalledWith(`/check-profile/${userId}`, {
                "headers": {
                    "Content-Type": "application/json",
                    "Token": 'ValidToken'
                },
                "withCredentials": true
            });
            expect(spy).toHaveBeenCalledWith(`/check-profile/${userId}`);
        //});
    });
});