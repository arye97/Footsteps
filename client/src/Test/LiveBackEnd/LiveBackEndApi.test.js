/**
 * This tests that all the functions in Api.js can communicate correctly with the backend.
 * It is an end to end test using a live backend (not using mocking).
 * To run this test, start the backend using an in memory database by calling './gradlew bootRun -PspringProfile=local'
 * Call 'npm run rest-test' (rest-test and not unit_name-test)
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

let USER2_ID = null;

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
    activity_types: [ "Archery", "Astronomy", "Rock Climbing", "Badminton", "Basketball" ]
};

const USER2 = {
    firstname: "Barry",
    middlename: "Bora",
    lastname: "Bora",
    nickname: "Nancy",
    primary_email: "nancy@testmail.com",
    password: "password2",
    date_of_birth: "1995-05-17",
    gender: "Non-Binary",
    bio: "This is my bio",
    fitness: 2,
    passports: [ "New Zealand", "Afghanistan" ],
    activity_types: [ "Archery", "Hiking", "Badminton", "Basketball" ]
};

const USER3 = {
    firstname: "Phillip",
    middlename: "Ellen",
    lastname: "Wilson",
    nickname: "Mary",
    primary_email: "sandy@testmail.com",
    password: "password3",
    date_of_birth: "2000-05-17",
    gender: "Male",
    bio: "This is my bio",
    fitness: 2,
    passports: [ "New Zealand", "Afghanistan" ],
    activity_types: [ "Hiking", "Rock Climbing", "Astronomy", "Baseball and Softball", "Badminton", "Basketball" ]
};

const USER4 = {
    firstname: "Irrelevant",
    lastname: "User",
    nickname: "Bob",
    primary_email: "bob@testmail.com",
    password: "password4",
    date_of_birth: "2000-05-17",
    gender: "Male",
    activity_types: [ "Badminton", "Basketball" ]
};

const USER5 = {
    firstname: "Irrelevant",
    lastname: "User",
    nickname: "Nancy",
    primary_email: "nancy@testmail.com",
    password: "password4",
    date_of_birth: "2000-05-17",
    gender: "Male",
    activity_types: [ "Badminton", "Basketball" ]
};

const USER6 = {
    firstname: "Irrelevant",
    lastname: "User",
    nickname: "Gob",
    primary_email: "gob@testmail.com",
    password: "password4",
    date_of_birth: "2000-05-17",
    gender: "Male",
    activity_types: [ "Badminton", "Basketball" ]
};

const USER7 = {
    firstname: "Irrelevant",
    lastname: "User",
    nickname: "Sob",
    primary_email: "sob@testmail.com",
    password: "password4",
    date_of_birth: "2000-05-17",
    gender: "Male",
    activity_types: [ "Badminton", "Basketball" ]
};

const NEW_PASSWORD = "password2";

const ADDITIONAL_EMAILS = ["arthur@pendragon.com", "merlin@beard.com"];

let ACTIVITY_IDS = new Set();

const ACTIVITY1 = {
    activity_name: "Trail Run Arthur's Pass",
    description: "A trail run of Mingha - Deception Route in Arthur's pass.  South to north.",
    activity_type: ["Hiking", "Athletics"],
    continuous: false,
    location: {
        latitude: -42.936342,
        longitude: 171.725097,
        name: "Arthur's Pass National Park",
    },
    start_time: "2020-12-16T09:00:00+0000",
    end_time: "2020-12-17T17:00:00+0000"
};

let OUTCOME1_ID = null;

const OUTCOME1 = {
    title: "My Awesome Outcome",
    unit_name: "Distance",
    unit_type: "TEXT"
};

const RESULT1 = {
    value: "Some value",
    did_not_finish: false,
    comment: "Some comment"
};

let PAGE_NUMBER = 0;

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
            return api.checkUserEmail("euanwidjaja@yahoo.com").then(response => expect(response.status).toEqual(200)).catch(
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
            return api.getUserActivities(USER1_ID, 0, "").then(response => {
                ACTIVITY_IDS = new Set(response.data.map(activity => activity.id));
                OUTCOME1.activity_id = ACTIVITY_IDS.values().next().value;
            }).catch(err => console.error(procError(err)));
        }

        // Add one activity at the beginning of each test
        beforeEach(() => {
            return api.createActivity(ACTIVITY1, USER1_ID).catch(err => console.error(procError(err)));
        });
        beforeEach(() => {
            return fetchActivities();
        });
        beforeEach(() => {
            return api.createOutcome(OUTCOME1).catch(err => console.error(procError(err)));
        });
        beforeEach(() => {
            return api.getActivityOutcomes(ACTIVITY_IDS.values().next().value).then(response => {
                OUTCOME1_ID = response.data[0].outcome_id;
            }).catch(err => console.error(procError(err)));
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
                // Iterate through and compare properties
                for (let propName in ACTIVITY1) {
                    if (propName === "activity_type" || propName === "location") continue;  // Because they're stored a different way
                    expect(response.data[propName]).toEqual(ACTIVITY1[propName]);
                }
                for (let propName in ACTIVITY1.location) {
                    expect(response.data.location[propName]).toEqual(ACTIVITY1.location[propName]);
                }
            }).catch(err => {throw procError(err)});
        });


        test("Check if user can edit activity", () => {
            return api.isActivityEditable(ACTIVITY_IDS.values().next().value).then(response => {
                expect(response.status).toEqual(200);
            }).catch(err => {throw procError(err)});
        });

        test("Create activity outcome", () => {
            OUTCOME1.activity_id = ACTIVITY_IDS.values().next().value;  // Create attribute
            return api.createOutcome(OUTCOME1).then(response => {
                expect(response.status).toEqual(201);
            }).catch(err => {throw procError(err)});
        });

        test("Delete activity outcome", () => {
            OUTCOME1.activity_id = ACTIVITY_IDS.values().next().value;  // Create attribute
            return api.deleteOutcome(OUTCOME1.activity_id).then(response => {
                expect(response.status).toEqual(200);
            }).catch(err => {throw procError(err)});
        });

        test("Get an activities outcomes", () => {
            return api.getActivityOutcomes(ACTIVITY_IDS.values().next().value).then(response => {
                expect(response.status).toEqual(200);
            }).catch(err => {throw procError(err)});
        });

        test("Create new result for an outcome", () => {
            api.setUserSubscribed(ACTIVITY_IDS.values().next().value, USER1_ID).catch(err => {
                throw procError(err)});
            return api.createResult(RESULT1, OUTCOME1_ID).then(response => {
                expect(response.status).toEqual(201);
            }).catch(err => {throw procError(err)});
        });

        test("Get all activities from a user", () => {
            return api.getUserActivities(USER1_ID, 0, "").then(response => {
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

        test("Get list of participants for an activity", () => {
            return api.getParticipants(ACTIVITY_IDS.values().next().value).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length).toEqual(0);  // There are no participants, but it will get an empty collection
            }).catch(err => {
                throw procError(err);
            })
        });
    });


    // ---- Activity Participant Follow/Unfollow Related Tests ----

    describe("Checking user feed event endpoints", () => {
        let activityCreatedByUser1 = null;

        function fetchActivities() {  // Helper function to prevent duplicate code
            return api.getUserActivities(USER1_ID, 0, "").then(response => {
                activityCreatedByUser1 = response.data[0];
            }).catch(err => console.error(procError(err)));
        }

        // Add one activity associated with user 1
        beforeEach(() => {
            return api.createActivity(ACTIVITY1, USER1_ID).catch(err => console.error(procError(err)));
        });
        // Gather activities created by by user 1
        beforeEach(() => {
            return fetchActivities();
        });
        // Register user 2
        beforeAll(() => {
            return api.register(USER2).catch(err => console.error(procError(err)));
        });
        // Login as user 2
        beforeEach(() => {
            return api.login({email: USER2.primary_email, password: USER2.password}).then(response => {
                // Store Token
                sessionStorage.setItem("token", response.data.Token);
                // Store User Id
                USER2_ID = response.data.userId;
            })
        });

        test("Get following status for an unsubscribed activity", () => {
            return api.getUserSubscribed(activityCreatedByUser1.id, USER2_ID).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.subscribed).toEqual(false);
            }).catch(err => {throw procError(err)});
        });

        test("Follow an unsubscribed activity", () => {
            return api.setUserSubscribed(activityCreatedByUser1.id, USER2_ID).then(response => {
                expect(response.status).toEqual(201);
            }).catch(err => {throw procError(err)});
        });

        test("Follow an already subscribed activity throws a 400 error", () => {
            return api.setUserSubscribed(activityCreatedByUser1.id, USER2_ID)
                .then(() => {
                    fail("API should have thrown a 400 error")
                }).catch(err => {
                    expect(err.response.data.status).toEqual(400);
                    expect(err.response.data.message).toEqual(
                    "User can't re-follow an event they're currently participating in."
                    );
                    procError(err)
                });
        });

        test("Get following status for a subscribed activity", () => {
            return api.getUserSubscribed(activityCreatedByUser1.id, USER2_ID)
                .then(response => {
                    expect(response.status).toEqual(200);
                    expect(response.data.subscribed).toEqual(true);
                }).catch(err => {throw procError(err)});
        });

        test("Unfollow a subscribed activity", () => {
            return api.deleteUserSubscribed(activityCreatedByUser1.id, USER2_ID)
                .then(response => {
                    expect(response.status).toEqual(200);
                }).catch(err => {throw procError(err)});
        });

        test("Unfollow an already unsubscribed activity throws a 400 error", () => {
            return api.deleteUserSubscribed(activityCreatedByUser1.id, USER2_ID)
                .then(() => {
                    fail("API should have thrown a 400 error")
                }).catch(err => {
                    expect(err.response.data.status).toEqual(400);
                    expect(err.response.data.message).toEqual(
                        "User can't un-follow an event they're not participating in."
                    );
                    procError(err)
                });
        });
    });


    // ---- User Searching Related Tests ----

    describe("Searching for users", () => {

        beforeAll(() => {
            api.register(USER3).catch(err => console.error(procError(err)));
            api.register(USER4).catch(err => console.error(procError(err)));
            api.register(USER5).catch(err => console.error(procError(err)));
            api.register(USER6).catch(err => console.error(procError(err)));
            api.register(USER7).catch(err => console.error(procError(err)));
        });


        test("Get users by one activity type (OR)", () => {
            return api.getUsersByActivityType(["Archery"], "or", PAGE_NUMBER).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length === 2);
            }).catch(err => {
                throw procError(err)
            });
        });


        test("Get users by one activity type (AND)", () => {
            return api.getUsersByActivityType(["Hiking"], "and", PAGE_NUMBER).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length === 2);
            }).catch(err => {
                throw procError(err)
            });
        });


        test("Get users by more than one activity types (OR)", () => {
            return api.getUsersByActivityType(["Archery", "Hiking"], "or", PAGE_NUMBER).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length === 3);
            }).catch(err => {
                throw procError(err)
            });
        });


        test("Get users by more than one activity types (AND)", () => {
            return api.getUsersByActivityType(["Archery", "Hiking"], "and", PAGE_NUMBER).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length === 1);
            }).catch(err => {
                throw procError(err)
            });
        });


        test("Get users with ActivityTypes containing a space (OR)", () => {
            return api.getUsersByActivityType(["Rock Climbing"], "or", PAGE_NUMBER).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length).toEqual(2);
            }).catch(err => {
                throw procError(err)
            });
        });

        test("Get users with ActivityTypes containing >1 spaces (AND)", () => {
            return api.getUsersByActivityType(["Rock Climbing", "Baseball and Softball"], "and", PAGE_NUMBER).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length).toEqual(1);
            }).catch(err => {
                throw procError(err)
            });
        });

        test("Get page 1 of paginated users with ActivityTypes (OR)", () => {
            return api.getUsersByActivityType(["Badminton", "Rock Climbing"], "or", PAGE_NUMBER).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length).toEqual(5);
            }).catch(err => {
                throw procError(err)
            });
        });

        test("Get page 1 of paginated users with ActivityTypes (AND)", () => {
            return api.getUsersByActivityType(["Badminton", "Basketball"], "and", PAGE_NUMBER).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length).toEqual(5);
            }).catch(err => {
                throw procError(err)
            });
        });

        test("Get page 2 of paginated users with ActivityTypes (OR)", () => {
            PAGE_NUMBER = 1;
            return api.getUsersByActivityType(["Badminton", "Rock Climbing"], "or", PAGE_NUMBER).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length).toEqual(1);
            }).catch(err => {
                throw procError(err)
            });
        });

        test("Get page 2 of paginated users with ActivityTypes (AND)", () => {
            return api.getUsersByActivityType(["Badminton", "Basketball"], "and", PAGE_NUMBER).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length).toEqual(1);
            }).catch(err => {
                throw procError(err)
            });
        });

        test("Get non-existent page 3 of paginated users with ActivityTypes", () => {
            PAGE_NUMBER = 2;
            return api.getUsersByActivityType(["Badminton", "Basketball"], "or", PAGE_NUMBER).then(response => {
                expect(response.status).toEqual(200);
                expect(response.data.length).toEqual(0);
            }).catch(err => {
                throw procError(err)
            });
        });

        test("Get paginated users with ActivityTypes and BAD_METHOD", () => {
            PAGE_NUMBER = 0;
            return api.getUsersByActivityType(["Hiking"], "BAD_METHOD", PAGE_NUMBER).then(response => {
                fail("API should have thrown a 400 error")
            }).catch(err => {
                expect(err.response.data.status).toEqual(400);
                expect(err.response.data.message).toEqual(
                    "Method must be specified as either (AND, OR)"
                );
                procError(err)
            });
        });

        test("Get paginated users with ActivityTypes that does not exist", () => {
            PAGE_NUMBER = 0;
            return api.getUsersByActivityType(["Rugby"], "or", PAGE_NUMBER).then(response => {
                fail("API should have thrown a 404 error")
            }).catch(err => {
                expect(err.response.data.status).toEqual(404);
                expect(err.response.data.message).toEqual(
                    "No users have been found"
                );
                procError(err)
            });
        });

        test("Get paginated users with ActivityTypes with BAD PAGE_NUMBER", () => {
            PAGE_NUMBER = 2;
            return api.getUsersByActivityType(["Badminton", "Basketball"], "or", "BAD_NUMBER").then(response => {
                fail("API should have thrown a 400 error")
            }).catch(err => {
                expect(err.response.data.status).toEqual(400);
                expect(err.response.data.message).toEqual(
                    "Page-Number must be an integer"
                );
                procError(err)
            });
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
