/**
 * This tests that all the functions in Api.js can communicate correctly with the backend.
 * It is an end to end test using a live backend (not using mocking).
 * To run this test, start the backend using an in memory database by calling './gradlew bootRun -PspringProfile=local'
 * On subsequent runs of the test, the "Register a new user" test will fail because the user is already in the database
 * (not exactly a stateless test).  For all tests to pass you must restart the server.
 *
 * NOTE: when running the in-memory database you may need to remove the environment variable SPRING_DATASOURCE_URL and
 * then use './gradlew bootRun -PspringProfile=localDev' when running the server normally from your home machine.
 * This is because SPRING_DATASOURCE_URL conflicts with the value in application-local.properties
 */
import "jest"
import api from "../../Api";

// User Id of the user that is being tested.
let USER1_ID = null;

const USER1 = {
    firstname: "John",
    middlename: "Gussy",
    lastname: "Smith",
    nickname: "Meek",
    primary_email: "johnsmith@testmail.com",
    password: "password1",
    date_of_birth: "1997-05-17",
    gender: "Non-Binary",
    bio: "This is my bio",
    fitness: 2,
    passports: [ "New Zealand", "Afghanistan" ],
    activity_types: [ "Archery", "Astronomy", "Rock Climbing" ]
};

const NEW_PASSWORD = "password2";

const ADDITIONAL_EMAILS = ["arthur@pendragon.com", "merlin@beard.com"];

let ACTIVITY_IDS = new Set();

const ACTIVITY1 = {
    activity_name: "Trail Run Arthur's Pass",
    description: "A trail run of Mingha - Deception Route in Arthur's pass.  South to north.",
    activity_type: ["Hiking", "Athletics"],
    continuous: false,
    location: "Arthur's Pass National Park",
    start_time: "2020-12-16T09:00:00+0000",
    end_time: "2020-12-17T17:00:00+0000"
};

/**
 * Differentiates errors.  If the error originates from an api call, then creates a new error with the
 * specific HTTP message.  If the error is from jest or elsewhere, then it returns it without modification.
 * @param error received from a test
 */
function procError(error) {
    try {
        return new Error(error.response.data.message);
    } catch (nestedError) {
        return error;
    }
}


// ---- Preliminary Tests ----

test("Register a new user", () => {
    return api.register(USER1).then((response) => {
        // Store Token
        sessionStorage.setItem("token", response.data.Token);
        // Store User Id
        USER1_ID = response.data.userId;

        expect(response.status).toEqual(201)
    }).catch(err => {
        if (err.response.status === 409) {
            throw new Error("The test user is already in the Database.  Restart the server or remove " + USER1.primary_email + " from the database.")
        } else {
            throw err;
        }
    });
});


test("Logout user", () => {
    return api.logout().then((response) => expect(response.status).toEqual(200)).catch(
        err => {throw procError(err)});
});


test("Login user", () => {
    return api.login({email: USER1.primary_email, password: USER1.password}).then(response => {
        // Store Token
        sessionStorage.setItem("token", response.data.Token);
        // Store User Id
        USER1_ID = response.data.userId;
        expect(response.status).toEqual(201)
    }).catch(
        err => {throw procError(err)});
});


// ---- The Main Tests ----

describe("Run tests on new user", () => {

    // These beforeEach (should) set the user back to its original state.
    // They are an attempt to make the tests independent and stateless
    afterEach(() => {
        let freshUser = {...USER1};
        freshUser["activityTypes"] = freshUser.activity_types;  // We have a naming problem...
        delete freshUser['activity_types'];
        return api.editProfile(freshUser, USER1_ID).catch(err => console.error(procError(err)));
    });
    afterEach(() => {
        return api.updatePassword(USER1_ID, NEW_PASSWORD, USER1.password, USER1.password).catch(err => {});
    });


    test("Get user role", () => {
        return api.getUserRoles(USER1_ID).then(response => expect(response.data).toEqual(0)).catch(
            err => {throw procError(err)});
    });


    test("Get user id using token", () => {
        return api.getUserId().then(response => expect(response.data).toEqual(USER1_ID)).catch(
            err => {throw procError(err)});
    });


    test("Modify password", () => {
        return api.updatePassword(USER1_ID, USER1.password, NEW_PASSWORD, NEW_PASSWORD).then(response => {
            expect(response.status).toEqual(200);
        }).catch(err => {throw procError(err)});
    });


    test("Edit profile", () => {
        let modifiedUser = {...USER1};
        modifiedUser["activityTypes"] = modifiedUser.activity_types;  // We have a naming problem...
        delete modifiedUser['activity_types'];
        modifiedUser.firstname = "Arthur";
        modifiedUser.fitness = 3;
        return api.editProfile(modifiedUser, USER1_ID).then(response => {
            expect(response.status).toEqual(200);
        }).catch(err => {throw procError(err)});
    });


    test("Get user data by Id", () => {
        return api.getUserData(USER1_ID).then(response => {
            expect(response.status).toEqual(200);

            // Itterate through and compare properties
            for (let propName in USER1) {
                if (propName === "activity_types" || propName === "password") continue;  // Because they're stored a different way
                expect(response.data[propName]).toEqual(USER1[propName]);
            }
        }).catch(err => {throw procError(err)});
    });


    test("Get user data by Token", () => {
        return api.getAllUserData().then(response => {
            expect(response.status).toEqual(200);

            // Itterate through and compare properties
            for (let propName in USER1) {
                if (propName === "activity_types" || propName === "password") continue;  // Because they're stored a different way
                expect(response.data[propName]).toEqual(USER1[propName]);
            }
        }).catch(err => {throw procError(err)});
    });


    test("Test the /check-profile end point", () => {
        return api.checkProfile(USER1_ID).then(response => {
            expect(response.status).toEqual(200);
        }).catch(err => {throw procError(err)});
    });


    // ---- Email Related Tests ----

    describe("Checking user email endpoints", () => {

        afterEach(() => {
            return api.setAdditionalEmails({additional_email: []}, USER1_ID).catch(err => console.error(procError(err)))
        });
        afterEach(() => {
            const resetEmailRequest = {
                primary_email: USER1.primary_email,
                additional_email: []
            };
            return api.setEmails(resetEmailRequest, USER1_ID).catch(err => {/* Do nothing.  Sometimes its ok to have an error here */})
        });


        test("Set additional emails for the user", () => {
            const setEmailRequest = {additional_email: ADDITIONAL_EMAILS}
            return api.setAdditionalEmails(setEmailRequest, USER1_ID).then(response => expect(response.status).toEqual(201)).catch(
                err => {throw procError(err)});
        });


        test("Edit primary and additional emails for the user", () => {
            // Make first secondary primary, and old primary a secondary
            let additionalEmails = ADDITIONAL_EMAILS.slice()
            let newPrimary = additionalEmails.splice(0, 1);
            additionalEmails.push(USER1.primary_email);

            const editEmailRequest = {
                primary_email: newPrimary,
                additional_email: additionalEmails
            };
            return api.setEmails(editEmailRequest, USER1_ID).then(response => expect(response.status).toEqual(201)).catch(
                err => {throw procError(err)});
        });


        test("Check if email is in the database", () => {
            return api.checkUserEmail(USER1.primary_email).then(response => expect(response.status).toEqual(200)).catch(
                err => {throw procError(err)});
        });


        test("Get all emails from user", () => {
            return api.getUserEmails(USER1_ID).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.userId).toEqual(USER1_ID);
                expect(response.data.primaryEmail).toEqual(USER1.primary_email);
                expect(response.data.additionalEmails.length).toBe(0);
            }).catch(err => {throw procError(err)});
        });
    });


    // ---- Activity Related Tests ----

    describe("Checking user activity endpoints", () => {
        function fetchActivities() {  // Helper function to prevent duplicate code
            return api.getUserActivities(USER1_ID).then(response => {
                ACTIVITY_IDS = new Set(response.data.map(activity => activity.id));
            }).catch(err => console.error(procError(err)));
        }

        // Add one activity at the beginning of each test
        beforeEach(() => {
            return api.createActivity(ACTIVITY1, USER1_ID).catch(err => console.error(procError(err)));
        });
        beforeEach(() => {
            return fetchActivities();
        });

        // Remove all Activities at the end of each test
        afterEach(() => {
            return fetchActivities();
        });
        afterEach( () => {
            ACTIVITY_IDS.forEach(async activityId => {
                await api.deleteActivity(USER1_ID, activityId).catch(err => console.error(procError(err)));
            });
            ACTIVITY_IDS.clear();
        });


        test("Create activity", () => {
            return api.createActivity(ACTIVITY1, USER1_ID).then(response => {
                expect(response.status).toEqual(201);
            }).catch(err => {throw procError(err)});
        });


        test("Edit activity", () => {
            let editedActivity = {...ACTIVITY1};
            editedActivity.activity_name = "Hiking in Arthur's Pass";
            editedActivity["id"] = ACTIVITY_IDS.values().next().value;  // Get an id from set
            return api.updateActivity(editedActivity, USER1_ID, editedActivity.id).then(response => {
                expect(response.status).toEqual(200);
            }).catch(
                err => {throw procError(err)});
        });


        test("Get data from activity", () => {
            return api.getActivityData(ACTIVITY_IDS.values().next().value).then(response => {
                expect(response.status).toEqual(200);

                // Itterate through and compare properties
                for (let propName in ACTIVITY1) {
                    if (propName === "activity_type") continue;  // Because they're stored a different way
                    expect(response.data[propName]).toEqual(ACTIVITY1[propName]);
                }
            }).catch(err => {throw procError(err)});
        });


        test("Get all activities from a user", () => {
            return api.getUserActivities(USER1_ID).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length).toBe(1);
            }).catch(err => {throw procError(err)});
        });


        test("Delete an activity", () => {
            let nextActivityId = ACTIVITY_IDS.values().next().value;
            return api.deleteActivity(USER1_ID, nextActivityId).then(response => {
                expect(response.status).toEqual(200);
                ACTIVITY_IDS.delete(nextActivityId)
            }).catch(err => {throw procError(err)});
        });
    });

});


// ---- Other Tests ----

describe("Other miscellaneous tests", () => {

    test("Get all activity types in the database", () => {
        return api.getActivityTypes().then(response => {
            expect(response.status).toEqual(200);
            expect(response.data.length > 0).toBeTruthy();
        }).catch(err => {
            throw procError(err)
        });
    });

});



afterAll(() => {
    api.logout();
});