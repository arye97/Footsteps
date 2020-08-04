import "vue-jest"
import api from '../Api'
import {shallowMount} from "@vue/test-utils";
import router from "../index";

import UserCard from '../views/Search/UserCard.vue'
import "jest"

jest.mock('../Api');

const DEFAULT_USER_ID = 1;

const USER1 = {
    id: DEFAULT_USER_ID,
    firstname: "John",
    lastname: "Snow",
    primary_email: "roomba@hoover.com",
    bio: "foobar",
    activityTypes: [
        {
            activityTypeId: 5,
            name: "Archery"
        },
        {
            activityTypeId: 47,
            name: "Orienteering"
        },
        {
            activityTypeId: 16,
            name: "Camping"
        }
    ]
};

let userCard;


beforeEach(() => {
    userCard = shallowMount(UserCard, {
        propsData: {
            user: USER1,
            selectedActivityTypes: [ "Archery", "Camping" ]
        },
        router,
        mocks: {api}
    });

});


test('Is a vue instance', () => {
    expect(userCard.isVueInstance).toBeTruthy();
});

describe("The UserCard", () => {

    test('Displays Full Name', () => {
        let fullName = userCard.find('#fullName').text();
        expect(fullName).toContain(USER1.firstname);
        expect(fullName).toContain(USER1.lastname);
    });


    test('Displays Email and Bio', () => {
        let userDetails = userCard.find('#userDetails').text();
        expect(userDetails).toContain(USER1.primary_email);
        expect(userDetails).toContain(USER1.bio);
    });


    test('Displays ActivityTypes that were both searched for, and match the user', () => {
        let activityTypes = userCard.find('#matchingActivityTypes').text();
        expect(activityTypes).toContain("Archery");
        expect(activityTypes).toContain("Camping");
        expect(activityTypes).not.toContain("Orienteering");
    });

});


describe("The View User Modal", () => {

    test('View User Modal title contains Full Name', () => {
        let modal = userCard.find('#modal-view-profile' + DEFAULT_USER_ID);
        expect(modal.attributes().title).toContain(USER1.firstname);
        expect(modal.attributes().title).toContain(USER1.lastname);
    });


    test('View User Modal title contains go to profile button', () => {
        let modal = userCard.find('#modal-view-profile' + DEFAULT_USER_ID);
        expect(modal.find("#goToProfileButton").exists()).toBeTruthy();
    });

});