<template>
    <b-container class="contents">
        <section v-if="loading">
            <div v-if="this.isRedirecting">
                {{ redirectionMessage }}
            </div>
            <div class="loading text-center">
                <b-spinner variant="primary" label="Spinning"></b-spinner>
                <br/>
            </div>
        </section>
        <section v-if="isLoggedIn">
            <div v-if="!locationLoading">
                <div class="location-container">
                    <h2 class="font-weight-bold location-header">My Public Location</h2>
                    <div class="address">
                        <h3 id="public-Name" v-if="publicLocation" class="font-weight-light"> {{publicLocation.name}} </h3>
                        <h3 v-else id="public-Name-Unspecified1" class="font-weight-light">Location not yet specified</h3>
                    </div>
                    <p class="location-description">This is the location that other users will see on your profile.</p>
                    <div class="map-pane">
                        <location-i-o class="input-location"
                                      id="public-location-i-o"
                                      @pin-change="setLocationPublic"
                                      @child-pins="(pins) => pins.length === 0 && setLocationPublic(null)"
                                      :current-location="publicLocation"
                                      :single-only="true"></location-i-o>
                    </div>
                    <label v-if="identicalPublicLocationWarningMessage && identicalPublicLocationWarningMessage.length > 0" class="errorMessage">
                        {{ identicalPublicLocationWarningMessage }}
                    </label>
                </div>
                <hr/>
                <div class="location-container">
                    <h2 class="font-weight-bold location-header">My Private Location</h2>
                    <div class="address">
                        <h3 id="private-Name" v-if="privateLocation" class="font-weight-light"> {{privateLocation.name}} </h3>
                        <h3 v-else id="private-Name-Unspecified2" class="font-weight-light">Location not yet specified</h3>
                    </div>
                    <p class="location-description">This location will only be visible to you.</p>
                    <div class="map-pane">
                        <location-i-o class="input-location"
                                      id="private-location-i-o"
                                      @pin-change="setLocationPrivate"
                                      @child-pins="(pins) => pins.length === 0 && setLocationPrivate(null)"
                                      :current-location="privateLocation"
                                      :single-only="true"></location-i-o>
                    </div>
                    <label v-if="identicalPrivateLocationWarningMessage && identicalPrivateLocationWarningMessage.length > 0" class="errorMessage">
                        {{ identicalPrivateLocationWarningMessage }}
                    </label>
                </div>
            </div>

            <label v-if="!changesMade" class="warningMessage">
                Specify a valid location above to save changes
            </label>

            <b-alert
                    id="locationAlert"
                    :show="dismissCountDown"
                    dismissible
                    :variant="alertVariant"
                    @dismissed="dismissCountDown=0"
                    @dismiss-count-down="countDownChanged"
            >
                {{ message }}
            </b-alert>

            <div id="confirmationButtons">
                <b-button variant="secondary float-left"
                          size="lg" id="back"
                          v-on:click="backToProfile">
                    Back
                </b-button>
                <b-button variant="success float-right"
                          id="save-changes-btn"
                          size="lg" v-on:click="saveChanges"
                          v-bind:disabled="!changesMade">
                    Save Changes
                </b-button>
            </div>
        </section>
    </b-container>
</template>

<script>
import api from "../../Api";
import LocationIO from "../../components/Map/LocationIO";
import { compareObjs } from "../../util"

const TIMEOUT_DURATION = 5;   // Time for error/success messages to disappear

export default {
    name: "EditLocation",
    components: {
        LocationIO
    },
    data() {
        return {
            profileId: null,
            isLoggedIn: false,
            loading: true,
            locationLoading: true,

            redirectionMessage: "Something went wrong! Try again later",
            isRedirecting: false,
            isEditable: false,
            isPublicLocation: true,

            publicLocation: null,
            privateLocation: null,

            inputPublicLocation: null,
            inputPrivateLocation: null,

            savedPublicLocation: null,
            savedPrivateLocation: null,

            identicalPublicLocationWarningMessage: "",
            identicalPrivateLocationWarningMessage: "",

            inputWarningMessage: "",
            message: null,
            dismissSecs: TIMEOUT_DURATION,
            dismissCountDown: 0,
            alertVariant: null

        }
    },
    async mounted() {
        if (this.$route.params.userId !== undefined) {
            await this.validateUserIdWithToken();
        }
        await this.populateInputs();
        this.identicalPrivateLocationWarningMessage = "";
        this.identicalPublicLocationWarningMessage = "";
    },

    computed: {
        /**
         * Computed property that checks if changes have been made
         */
        changesMade() {
            return !compareObjs(this.inputPublicLocation, this.savedPublicLocation) ||
                !compareObjs(this.inputPrivateLocation, this.savedPrivateLocation);
        }
    },

    methods: {
        /**
         * Checks if a user id from query parameter is logged in with token provided
         * to prevent a user from editing another user's profile.
         */
        async validateUserIdWithToken() {
            await api.checkProfile(this.$route.params.userId).then(() => {
                this.isLoggedIn = true;
                this.profileId = this.$route.params.userId;
            }).catch(error => {
                this.profileId = '';
                this.processError(error);
            });
        },

        /**
         * Populate LocationIO fields to display the user's location.
         */
        async populateInputs() {
            if (!this.isRedirecting) {
                await api.getUserData(this.profileId).then(response => {
                    this.isLoggedIn = true;
                    if (response.data.role === 20) {
                        // Is the global admin
                        this.$router.push('/home');
                    } else {
                        this.publicLocation = response.data.public_location;
                        this.privateLocation = response.data.private_location;
                        if (this.publicLocation) {
                            delete this.publicLocation['id'];
                        }
                        if (this.privateLocation) {
                            delete this.privateLocation['id'];
                        }
                        this.inputPublicLocation = this.publicLocation;
                        this.inputPrivateLocation = this.privateLocation;
                        this.savedPublicLocation = this.publicLocation;
                        this.savedPrivateLocation = this.privateLocation;
                    }
                    this.locationLoading = false;
                    this.loading = false;
                }).catch(error => {
                    this.processError(error);
                });
            }
        },

        /**
         * Obtains a validated edit location request based on input.
         * Returns null if input is empty or identical to original location.
         */
        getLocationRequest() {
            let editedLocation = {};

            editedLocation['public_location'] = this.inputPublicLocation;

            editedLocation['private_location'] = this.inputPrivateLocation;

            return editedLocation;
        },


        /**
         * Validates changes and submits a PUT request to edit a user's public/private locations.
         * If changes have not been made, SAVE button is disabled.
         */
        async saveChanges() {
            // If input location is none, then dont modify location
            let editedLocationRequest = this.getLocationRequest();
            if (!editedLocationRequest) {
                this.message = 'Changes have not been made';
                this.alertVariant = 'danger';
                this.showAlert()
            } else {
                await api.editLocation(editedLocationRequest, this.profileId).then(() => {
                    this.publicLocation = this.inputPublicLocation;
                    this.privateLocation = this.inputPrivateLocation;

                    this.savedPublicLocation = this.publicLocation;
                    this.savedPrivateLocation = this.privateLocation;

                    this.message = 'Changes saved successfully';
                    this.alertVariant = 'success';
                    this.showAlert()
                }).catch(error => {
                    this.message = 'Unknown error : ' + error.message;
                    this.processError(error);
                });
            }
        },

        /**
         * Helper method to dismiss alert bar by countdown
         */
        countDownChanged(dismissCountDown) {
            this.dismissCountDown = dismissCountDown
        },

        /**
         * Helper method to instantiate alert countdown
         */
        showAlert() {
            this.dismissCountDown = this.dismissSecs
        },

        /**
         * This helper function is called when an error is caught when performing a Get request to the server.<br>
         * Conditions handled are:<br>
         * 401 (UNAUTHORIZED) redirect to login page,<br>
         * 403 (FORBIDDEN) and 404 (NOT_FOUND) redirect to this user's edit profile page,<br>
         * Otherwise unknown error so redirect to user's home page
         */
        processError(error) {
            let timeoutTime = 3000;
            this.isLoggedIn = true;
            this.loading = true;
            this.isRedirecting = true;
            if (error.response.status === 400) {
                this.redirectionMessage = "Sorry, the location you saved was invalid,\n" +
                    "Redirecting to your edit locations page.";
                setTimeout(() => {
                    this.$router.go();
                }, timeoutTime);
            } else if (error.response.status === 401) {
                this.isLoggedIn = false;
                this.redirectionMessage = "Sorry, you are no longer logged in,\n" +
                    "Redirecting to the login page.";
                setTimeout(() => {
                    sessionStorage.clear();
                    this.$router.push('/login');
                }, timeoutTime);
            } else if (error.response.status === 403) {
                this.redirectionMessage = "Sorry, you are not allowed to edit another user's profile,\n" +
                    "Redirecting to your edit location page.";
                setTimeout(() => {
                    this.$router.go();
                }, timeoutTime);
            } else if (error.response.status === 404) {
                this.redirectionMessage = "Sorry, the user does not exist,\n" +
                    "Redirecting to your edit location page.";
                setTimeout(() => {
                    this.$router.go();
                    this.$forceUpdate()
                }, timeoutTime);
            } else {
                this.redirectionMessage = "Sorry, an unknown error occurred when retrieving profile info,\n" +
                    "Redirecting to your home page.";
                setTimeout(() => {
                    this.$router.push({ name: 'myProfile' });
                }, timeoutTime);
            }
        },

        /**
         * Function emitted from LocationIO.vue to set inputPublicLocation
         */
        setLocationPublic(pin) {
            if (pin) {
                this.inputPublicLocation = {
                    latitude: pin.lat,
                    longitude: pin.lng,
                    name: pin.name,
                };
            } else {
                this.inputPublicLocation = null;
            }

            this.identicalPublicLocationWarningMessage = null;
            if (compareObjs(this.publicLocation, this.inputPublicLocation)) {
                this.identicalPublicLocationWarningMessage = "This is your current location!";
            }
        },

        /**
         * Function emitted from LocationIO.vue to set inputPrivateLocation
         */
        setLocationPrivate(pin) {
            if (pin) {
                this.inputPrivateLocation = {
                    latitude: pin.lat,
                    longitude: pin.lng,
                    name: pin.name,
                };
            } else {
                this.inputPrivateLocation = null;
            }

            this.identicalPrivateLocationWarningMessage = null;
            if (compareObjs(this.privateLocation, this.inputPrivateLocation)) {
                this.identicalPrivateLocationWarningMessage = "This is your current location!";
            }
        },

        /**
         * Redirect to view user screen
         */
        backToProfile() {
            this.$router.push({ name: 'profile', params: {userId: this.userId} });
        },
    }
}
</script>

<style scoped>

    .address {
        text-align: center;
        width: 100%;
    }

    .location-header {
        margin-top: 25px;
        font-size: 30px;
    }

    .location-container {
        word-break: break-all
    }

    table {
        margin: 30px auto 5px;
        padding: 0;
        height: 50px;
        width: 100%;
    }

    table tr {

    }

    table th {
        height: 20px;
    }


    table h3 {
        font-size: 25px;
        overflow: auto
    }

    .location-column-label {
        text-align: left;
        padding-left: 20px;
        width: 40%;
    }

    .location-column-value {
        text-align: right;
        padding-left: 10px;
        padding-right: 20px;
        width: 100%;
        height: 100%;
    }

    #confirmationButtons {
        width: 100%;
        alignment: center;
        margin-top: 25px;
    }

    .errorMessage {
        margin-top: 10px;
        color: red;
    }

    .warningMessage {
        margin-top: 10px;
        color: yellowgreen;
    }

    #locationAlert {
        margin-top: 10px;
    }

    .location-description {
        color: grey;
        font-size: 15px;
    }
</style>