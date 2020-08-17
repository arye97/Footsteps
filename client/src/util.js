import {nameRegex} from "./constants";
import api from "./Api";

export function validateUser(fieldData, fieldType) {
    const emailRegex = new RegExp(/^[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)+$/);
    const passwordRegex = new RegExp(/(?=.*[0-9])(?=.*[a-zA-Z])(?=\S+$).{8,}/);
    const bioEmailLength = 255;
    const nameLength = 45;
    switch (fieldType) {
        case "bio":
            return {valid: fieldData.length <= bioEmailLength};
        case "nickname":
            return {valid: fieldData.length <= nameLength};
        case "gender":
            return {valid: fieldData === "Male" || fieldData === "Female" || fieldData === "Non-Binary"};
        case "email":
            return {valid: (emailRegex.test(fieldData) && fieldData.length <= bioEmailLength)};
        case "password":
            return {valid: (passwordRegex.test(fieldData))};
        case "middlename":
            return {valid: ((nameRegex.test(fieldData) || fieldData === "" || fieldData == null) && fieldData.length <= nameLength), message: "Middle Name contains numbers or unexpected characters"};
        case "firstname":
            return {valid: (nameRegex.test(fieldData)&& fieldData.length <= nameLength), message: "First Name contains numbers or unexpected characters"};
        case "lastname":
            return {valid: (nameRegex.test(fieldData) && fieldData.length <= nameLength), message: "Last Name contains numbers or unexpected characters"};
        case "date_of_birth":
            return _isValidDOB(fieldData);
        default: return {valid: false};
    }
}

/**
 * Takes a date of birth string and returns true if that date is older than age int variable
 * @param dateStr a string of the form year-month-day  i.e. 1997-02-16
 */
export function _isValidDOB(dateStr) {
    const minDate = new Date();
    minDate.setFullYear(minDate.getFullYear() - 13);
    const maxDate = new Date();
    maxDate.setFullYear(maxDate.getFullYear() - 150);
    let dob = Date.parse(dateStr);
    // Due to differences in implementation of Date.parse() a 'Z' may or may not be required at the end of the date.
    if (Number.isNaN(dob)) {  // If dateStr can't be parsed
        dateStr.endsWith('Z') ? dateStr = dateStr.slice(0, -1) : dateStr += 'Z';  // Remove Z if it exists, add Z if it doesn't exist
        dob = Date.parse(dateStr);    // Parse again
        if (Number.isNaN(dob)) {
            return {valid: false}
        }
    }
    if (dob > minDate) return {valid: false, message: "Given age is considered too young to be registered "};
    if (dob < maxDate) return {valid: false, message: "Given age is considered too old to be registered "};
    return {valid: true}
}


/**
 * Takes a date of birth string and returns a formatted date of birth string
 * @param date_of_birth a string of the form day-month-year  i.e. 15-01-1998
 * @returns {string} formatted date of birth
 */
export function getDateString(date_of_birth) {
    let date = new Date(date_of_birth);
    let offset = date.getTimezoneOffset();
    date.setMinutes(date.getMinutes() - offset);
    const month = date.getMonth() + 1;
    const day = date.getDate();
    return date.getFullYear() + '-'
        + ( month < 10 ? '0' : "" ) + month.toString() + '-'
        + ( day < 10 ?'0' : "" ) + day.toString();
}

/**
 * Return the current time in the backend date format
 * @returns {string} formatted with yyyy-MM-dd'T'HH:mm:ssZ with a timezone designator for the local timezone
 */
export function currentDateBackend() {
    let current = (new Date()).toISOString().substring(0, 16);

    let timeZoneStr = backendTimeZone();
    return current.concat(timeZoneStr);
}

/**
 * Return the current time in the front end date format
 * @returns {string} front end date formatted with yyyy-MM-dd'T'HH: in local time
 */
export function currentDateFrontend() {
    return (new Date()).toISOString().substring(0, 16);
}


/**
 * Converts a back end date of the form 1997-02-11T09:00:00+0000 (yyyy-MM-dd'T'HH:mm:ssZ) to a front end date of
 * the form 1997-02-11T21:00 (yyyy-MM-dd'T'HH:mm) that is in the local timezone of the browser.
 *
 * NOTE: The backend dates come with a timezone attached, usually Greenwich universal time, so they must be converted.
 * @param backEndDateStr string formatted with yyyy-MM-dd'T'HH:mm:ssZ (probably in universal time)
 * @returns string front end date formatted with yyyy-MM-dd'T'HH: in local time
 */
export function backendDateToLocalTimeZone(backEndDateStr) {
    // Check the format
    if (backEndDateStr == null || isNaN((new Date(backEndDateStr)).getTime())) {
        return currentDateFrontend();
    }

    try {
        let timeZoneOffsetMS = (new Date()).getTimezoneOffset() * 60000;   // Timezone offset in milliseconds
        let backEndDate = new Date(backEndDateStr);
        return (new Date(backEndDate - timeZoneOffsetMS)).toISOString().substring(0, 16);

    } catch (err) {
        return currentDateFrontend();
    }
}


/**
 * Get a timezone substring for a backend date of the format :00+0800
 * @returns {string} timezone substring
 */
function backendTimeZone() {
    // Get Timezone offset in minutes
    let timeZoneOffsetMin = -(new Date()).getTimezoneOffset();

    // Construct a string of the form HH
    let hour = (Math.floor(timeZoneOffsetMin / 60)).toString();
    hour = (hour.length < 2 ? '0' : '') + hour;   // Add leading zero

    // Construct a string of the form MM
    let minute = (timeZoneOffsetMin % 60).toString();
    minute = (minute.length < 2 ? '0' : '') + minute;   // Add leading zero


    let timeZoneStr = hour + minute;
    return ":00+" + timeZoneStr;
}

/**
 * Converts a front end date like 1997-02-11T21:00 to a backend date like 1997-02-11T21:00:00+1200.
 * Adds the local time zone onto the end.
 * @param frontEndDate string front end date formatted with yyyy-MM-dd'T'HH: in local time
 * @returns string  formatted with yyyy-MM-dd'T'HH:mm:ssZ with a timezone designator for the local timezone
 */
export function localTimeZoneToBackEndTime(frontEndDate) {
    // Check the format
    if (frontEndDate == null || isNaN((new Date(frontEndDate)).getTime())) {
        return currentDateBackend();
    }

    try {
        let timeZoneStr = backendTimeZone();
        return frontEndDate.concat(timeZoneStr);

    } catch (err) {
        return currentDateBackend();
    }
}

/**
 * Formats ISO8601 into readable date-time.
 * In addition, converts 24 hour time to AM/PM time.
 * e.g. Mon, 1 Jan 2000 10:00 AM
 * @param dateTime ISO8601 date-time
 * @returns {string} formatted date-time
 */
export function formatDateTime(dateTime) {
    dateTime = backendDateToLocalTimeZone(dateTime) + ":00+0000";
    let UTCDateTime = new Date(dateTime).toUTCString().replace("GMT", "").slice(0, -4);
    let date = UTCDateTime.slice(0, UTCDateTime.length - 5);
    let time = UTCDateTime.slice(UTCDateTime.length - 5);
    let hour = time.slice(0, 2);
    let minute = time.slice(3);
    let AMPMTime;

    // Convert 24 hour clock to AM/PM clock
    if (hour < 12) {
        if (hour === '00') {
            AMPMTime = '12' + ":" + minute + " AM"
        } else {
            AMPMTime = hour + ":" + minute + " AM"
        }
    } else {
        if (hour === '12'){
            AMPMTime = '12' + ":" + minute + " PM"
        } else {
            hour -= 12;
            if (hour < 10) {
                hour = '0' + hour;
            }
            AMPMTime = hour + ":" + minute + " PM"
        }
    }
    return date + AMPMTime;
}

/**
 * Fetch the possible passport countries to select from.
 * @returns {(Array|string)} an array of the fetched passport countries, or a string error message
 */
export function fetchCountries() {
    //Fill Passport countries
    let select = [];
    api.getCountries().then(response => {
        response.data.forEach(country => {
            let elmt = country.name;
            select.push(elmt)
        });
    }).catch(() => {
        select = 'List is empty'
    });
    return select;
}

/**
 * A collection of constants.  The same types as UnitType enumeration on the back end.
 * @type {{DATE: string, NUMBER: string, DATE_AND_TIME: string, TEXT: string, TIME: string, BOOLEAN: string}}
 */
export const UnitType = {
    TEXT: "TEXT",
    NUMBER: "NUMBER",
    DATE: "DATE",
    DATE_AND_TIME: "DATE_AND_TIME",
    TIME: "TIME",
    BOOLEAN: "BOOLEAN"
};
