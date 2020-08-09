import 'vue-jest'
import {shallowMount} from "@vue/test-utils";
import FeedCard from "../components/HomeFeed/FeedCard";
import "jest"
import api from "../Api";

jest.mock('../Api');

let feedCard;
const EVENT_DELETE = {
  id: 1,
  feedEventType: 'DELETE',
  timeStamp: new Date(),
  activityId: 1,
  userId: 1
};
const EVENT_FOLLOW = {
    id: 1,
    feedEventType: 'FOLLOW',
    timeStamp: new Date(),
    activityId: 1,
    userId: 1
};
const USER1_DATA = {
    id: 1,
    firstname: "Bob",
    middlename: "The",
    lastname: "Cucumber"
};
const EVENT1_DATA = {
    id: 1,
    activity_name: "Go to the zoo"
}

beforeEach(() => {
   feedCard = shallowMount(FeedCard, {
       propsData: {
           event: EVENT_DELETE
       },
       mocks: {api}
   });

   api.getUserData.mockImplementation(() => {
       Promise.resolve({
           data: USER1_DATA,
           status: 200
       })
   });
   api.getActivityData.mockImplementation(() => {
       Promise.resolve({
           data: EVENT1_DATA,
           status: 200
       })
   });
});

test('Is a vue instance', () => {
    expect(feedCard.isVueInstance).toBeTruthy();
});

describe("The feed card", () => {
    test('Displays a description of what happened', () => {
        expect(feedCard.find('#description')).toBeTruthy();
    });

    test('Displays a timestamp', () => {
        expect(feedCard.find('#time')).toBeTruthy();
    });

    test('Does not have any buttons for a delete event', () => {
        expect(feedCard.findAll('b-button')).toHaveLength(0);
    });

    test('Has 2 buttons for a follow event', () => {
        feedCard = shallowMount(FeedCard, {
            propsData: {
                event: EVENT_FOLLOW
            }
        });
        expect(feedCard.findAll('b-button')).toHaveLength(2);
    });
});