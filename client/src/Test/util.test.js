import { formateDateTime } from '../util';
import "jest"


describe("Convert ISO8601 into readable date and AM/PM time", () => {

    describe("Receive UTC time from back-end", () => {

        test("Test UTC date formatted with correct single digit AM time", () => {
            const backEndDate = "1997-02-11T09:00:00+0000";
            expect(formateDateTime(backEndDate)).toBe("Tue, 11 Feb 1997 09:00 AM");
        });

        test("Test UTC date formatted with correct single digit PM time", () => {
            const backEndDate = "1997-02-11T21:00:00+0000";
            expect(formateDateTime(backEndDate)).toBe("Tue, 11 Feb 1997 09:00 PM");
        });

        test("Test UTC date formatted with correct double digit 12:00 AM time", () => {
            const backEndDate = "1997-02-11T00:00:00+0000";
            expect(formateDateTime(backEndDate)).toBe("Tue, 11 Feb 1997 12:00 AM");
        });

        test("Test UTC date formatted with correct double digit 12:00 PM time", () => {
            const backEndDate = "1997-02-11T12:00:00+0000";
            expect(formateDateTime(backEndDate)).toBe("Tue, 11 Feb 1997 12:00 PM");
        });
    });

});