export function validateUser(fieldData, fieldType) {

    switch (fieldType) {
        case "date_of_birth":
            return _isValidDOB(fieldData);
        case "firstname":
        case "middlename":
        case "lastname":
            return {valid: !(/\d/.test(fieldData)), message: "Name contains numbers"};
        default: return {valid: true};
    }
}

/**
 * Takes a date of birth string and returns true if that date is older than age int variable
 * @param dateStr a string of the form year-month-day  i.e. 1997-02-16
 * @param age integer age
 */
export function _isValidDOB(dateStr) {
    const minAge = 13;
    const maxAge = 150;
    let dob = Date.parse(dateStr);
    // Due to differences in implementation of Date.parse() a 'Z' may or may not be required at the end of the date.
    if (Number.isNaN(dob)) {  // If dateStr can't be parsed
        dateStr.endsWith('Z') ? dateStr = dateStr.slice(0, -1) : dateStr += 'Z'  // Remove Z if it exists, add Z if it doesn't exist
        dob = Date.parse(dateStr);    // Parse again
        if (Number.isNaN(dob)) {
            // If still can't parse, fall back to returning true so user can still register.  Backend will still check the date
            return {valid: true}
        }
    }
    let age = new Date(Date.now() - dob);
    let ageYear = Math.abs(age.getUTCFullYear() - 1970);
    if (ageYear >= minAge) return {valid: false, message: "Given age is considered too young to be registered "};
    if (ageYear >= maxAge) return {valid: false, message: "Given age is considered too old to be registered "};
    return {valid: true}
}