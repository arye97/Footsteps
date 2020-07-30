import { search } from '../views/Search';
import "jest"


// NOTE jest-setup-global.js explicitly sets the timezone to New Zealand time.
describe("Searching user based on activity types", () => {

    describe("From Back End to Front End", () => {

        test("Back end date in UTC time", () => {
            const backEndDate = "1997-02-11T09:00:00+0000";
            expect(backendDateToLocalTimeZone(backEndDate)).toBe("1997-02-11T21:00");

        });
    });
});
