export function validateUser(fieldData, fieldType) {

    switch (fieldType) {
        case "gender":
            return {valid: fieldData === "Male" || fieldData === "Female" || fieldData === "Non-Binary"};
        case "password":
            return {valid: fieldData !== ''};
        case "middlename":
            return {valid: !(/\d/.test(fieldData)), message: "Name contains numbers"}
        case "firstname":
        case "lastname":
            return {valid: fieldData !== '' && !(/\d/.test(fieldData)), message: "Name contains numbers"};
        case "date_of_birth":
            return _isValidDOB(fieldData);
        default: return {valid: true};
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
        dateStr.endsWith('Z') ? dateStr = dateStr.slice(0, -1) : dateStr += 'Z'  // Remove Z if it exists, add Z if it doesn't exist
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
 * @param dateOfBirth a string of the form day-month-year  i.e. 15-01-1998
 * @returns {string} formatted date of birth
 */
export function getDateString(dateOfBirth) {
    let date = new Date(dateOfBirth);
    let offset = date.getTimezoneOffset();
    date.setMinutes(date.getMinutes() - offset);
    // return date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear();
    return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();

}