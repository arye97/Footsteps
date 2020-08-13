import 'vue-jest'
import {mount} from "@vue/test-utils";
import FeedCard from "../components/HomeFeed/FeedCard";
import "jest"
import api from "../Api";

jest.mock('../Api');

let feedCard;
let now;
const EVENT_DELETE = {
    id: 1,
    feedEventType: 'DELETE',
    timeStamp: new Date(),
    activityId: 1,
    activityName: "Go to the zoo",
    userId: 1
};
// Follow event has the date set as 10m ago
let EVENT_FOLLOW = {
    id: 1,
    feedEventType: 'FOLLOW',
    timeStamp: new Date(),
    activityId: 1,
    activityName: "The First Activity",
    userId: 1
};
const USER1_DATA = {
    id: 1,
    firstname: "Bob",
    lastname: "Cucumber"
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
        api.getUserData.mockImplementation(() => {
            return Promise.resolve({
                data: USER1_DATA,
                status: 200
            })
        });

        feedCard = mount(FeedCard, {
            propsData: {
                event: EVENT_DELETE
            },
            mocks: {api}
        });

        // This causes a delay between beforeEach finishing, and the tests being run.
        // This isn't at all good practice, but its the only way I am able
        // to get EditEmail to fully mount before the tests are run.
        // resolve() signals the above promise to complete
        sleep(150).then(() => resolve());
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
        feedCard = mount(FeedCard, {
            propsData: {
                event: EVENT_FOLLOW
            }
        });
        expect(feedCard.findAll('b-button')).toHaveLength(2);
    });
});

describe("The extract data method", () => {
    test('Sets the time for events that occurred in the last 10s to "Just now"', async () => {
        //EVENT_DELETE set up with the timestamp as now
        await feedCard.vm.extractData();
        expect(feedCard.vm.time).toBe("Just now");
    });

    test.each([
        [1],
        [10],
        [37],
        [59]
    ])('Sets the time for events that occurred %s minutes ago to "%s minutes ago"', async (value) => {
        now = new Date();
        EVENT_FOLLOW.timeStamp = new Date(now - value * 60000);
        feedCard = mount(FeedCard, {
            propsData: {
                event: EVENT_FOLLOW
            }
        });
        await feedCard.vm.extractData();
        expect(feedCard.vm.time).toBe(value + " minutes ago");
    });

    test.each([
        [1],
        [9],
        [17],
        [23]
    ])('Sets the time for events that occurred %s hours ago to "%s hours ago"', async (value) => {
        now = new Date();
        EVENT_FOLLOW.timeStamp = new Date(now - value * 3600000);
        feedCard = mount(FeedCard, {
            propsData: {
                event: EVENT_FOLLOW
            }
        });
        await feedCard.vm.extractData();
        expect(feedCard.vm.time).toBe(value + " hours ago");
    });

    test.each([
        [1],
        [31],
        [157],
        [76543]
    ])('Sets the time for events that occurred %s days ago to "%s days ago"', async (value) => {
        now = new Date();
        now.setDate(now.getDate() - value)
        EVENT_FOLLOW.timeStamp = now;
        feedCard = mount(FeedCard, {
            propsData: {
                event: EVENT_FOLLOW
            }
        });
        await feedCard.vm.extractData();
        expect(feedCard.vm.time).toBe(value + " days ago");
    });

    test('Sets the first name of the person who triggered the event', () => {
        expect(feedCard.vm.firstName).toBe('Bob');
    });

    test('Sets the last name of the person who triggered the event', () => {
        expect(feedCard.vm.lastName).toBe('Cucumber');
    });

    test('Sets the activity title of the event concerned', () => {
        expect(feedCard.vm.event.activityName).toBe("Go to the zoo");
    });
})