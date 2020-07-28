import { backendDateToLocalTimeZone , localTimeZoneToBackEndTime} from '../util';
import "jest"


// NOTE jest-setup-global.js explicitly sets the timezone to New Zealand time.
describe("Timezone conversion functions", () => {

    describe("From Back End to Front End", () => {

        test("Back end date in UTC time", () => {
            const backEndDate = "1997-02-11T09:00:00+0000";
            expect(backendDateToLocalTimeZone(backEndDate)).toBe("1997-02-11T21:00");
        });

        test("UTC date at one to midnight", () => {
            const backEndDate = "1997-02-11T23:59:00+0000";
            expect(backendDateToLocalTimeZone(backEndDate)).toBe("1997-02-12T11:59");
        });

        test("UTC date at exactly midnight", () => {
            const backEndDate = "1997-02-11T24:00:00+0000";
            expect(backendDateToLocalTimeZone(backEndDate)).toBe("1997-02-12T12:00");
        });

        test("Invalid UTC date after midnight", () => {
            const backEndDate = "1997-02-11T24:01:00+0000";
            expect(() => backendDateToLocalTimeZone(backEndDate)).toThrow(RangeError);
        });
    });

    describe("From Front End to Back End", () => {

        test("Front end date 9pm", () => {
            const frontEndDate = "1997-02-11T21:00";
            expect(localTimeZoneToBackEndTime(frontEndDate)).toBe("1997-02-11T21:00:00+1200");
        });

        test("Test invalid UTC date after midnight", () => {
            const frontEndDate = "1997-02-11T24:01";
            expect(() => localTimeZoneToBackEndTime(frontEndDate)).toThrow(RangeError);
        });
    });

});