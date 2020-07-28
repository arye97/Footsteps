import {nameRegex} from "./constants";

export function validateUser(fieldData, fieldType) {
    const emailRegex = new RegExp(/^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/);
    const passwordRegex = new RegExp(/(?=.*[0-9])(?=.*[a-zA-Z])(?=\S+$).{8,}/);
    const bioPasswordEmailLength = 255;
    const nameLength = 45;
    switch (fieldType) {
        case "bio":
            return {valid: fieldData.length <= bioPasswordEmailLength};
        case "nickname":
            return {valid: fieldData.length <= nameLength};
        case "gender":
            return {valid: fieldData === "Male" || fieldData === "Female" || fieldData === "Non-Binary"};
        case "email":
            return {valid: (emailRegex.test(fieldData) && fieldData.length <= bioPasswordEmailLength)};
        case "password":
            return {valid: (passwordRegex.test(fieldData) && fieldData.length <= bioPasswordEmailLength)};
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
    //let age = new Date(Date.now() - dob);
    //let ageYear = Math.abs(age.getUTCFullYear() - 1970);
    //console.log(age, ageYear);
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
 * Formats ISO8601 into readable date-time.
 * In addition, converts 24 hour time to AM/PM time.
 * e.g. Mon, 1 Jan 2000 10:00 AM
 * @param dateTime ISO8601 date-time
 * @returns {string} formatted date-time
 */
export function formatDateTime(dateTime) {
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