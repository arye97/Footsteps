<template>
    <b-container class="contents" fluid>
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
            <h2 class="font-weight-bold location-header">My Location</h2>
            <div v-if="!locationLoading">
                <div class="location-container">
                    <table>
                        <tr>
                            <th class="location-column-label">
                                <h3 class="location-field-header">Public Location:</h3>
                            </th>
                            <th class="location-column-value">
                                <h3 v-if="this.inputPublicLocation" class="font-weight-light"> {{this.inputPublicLocation.name}} </h3>
                                <h3 v-else-if="this.publicLocation" class="font-weight-light"> {{this.publicLocation.name}} </h3>
                                <h3 v-else class="font-weight-light"> Not Specified </h3>
                            </th>
                        </tr>
                    </table>
                    <div class="map-pane">
                        <location-i-o class="input-location"
                                      v-on:child-pins="locationPublicValue"
                                      :current-location="publicLocation"
                                      :single-only=true></location-i-o>
                    </div>
                    <label v-if="this.inputPublicLocation !== this.publicLocation" class="errorMessage">
                        {{ identicalPublicLocationWarningMessage }}
                    </label>
                </div>
                <div class="location-container">
                    <table>
                        <tr>
                            <th class="location-column-label">
                                <h3 class="location-field-header">Private Location:</h3>
                            </th>
                            <th class="location-column-value">
                                <h3 v-if="this.inputPrivateLocation" class="font-weight-light"> {{this.inputPrivateLocation.name}} </h3>
                                <h3 v-else-if="this.privateLocation" class="font-weight-light"> {{this.privateLocation.name}} </h3>
                                <h3 v-else class="font-weight-light"> Not Specified </h3>
                            </th>
                        </tr>
                    </table>
                    <div class="map-pane">
                        <location-i-o class="input-location"
                                      v-on:child-pins="locationPrivateValue"
                                      :current-location="privateLocation"
                                      :single-only=true></location-i-o>
                    </div>
                    <label v-if="this.inputPrivateLocation !== this.privateLocation" class="errorMessage">
                        {{ identicalPrivateLocationWarningMessage }}
                    </label>
                </div>
            </div>

            <label class="errorMessage">
                {{ inputWarningMessage }}
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
                <b-button variant="success float-left"
                          size="lg" id="back"
                          v-on:click="backToProfile">
                    Back
                </b-button>
                <b-button variant="success float-right"
                          size="lg" v-on:click="saveChanges"
                          v-bind:disabled="!checkIfChangesMade">
                    Save Changes
                </b-button>
            </div>
        </section>
    </b-container>
</template>

<script>
import api from "../../Api";
import LocationIO from "../../components/Map/LocationIO";

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
        this.populateInputs();
    },

    computed: {
        /**
         * Computed property that checks if changes have been made
         */
        checkIfChangesMade() {
            return this.getValidatedLocationRequest();
        },
    },

    methods: {
        
        /**
         * Validates changes and submits a PUT request to edit a user's public/private locations.
         * If changes have not been made, SAVE button is disabled.
         */
        async saveChanges() {
            // If input location is none, then dont modify location
            let editedLocationRequest = this.getValidatedLocationRequest();
            if (!editedLocationRequest) {
                this.message = 'Changes have not been made';
                this.alertVariant = 'danger';
                this.showAlert()
            } else {
                await api.editLocation(editedLocationRequest, this.profileId).then(() => {
                    this.publicLocation = this.inputPublicLocation;
                    this.privateLocation = this.inputPrivateLocation;
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
            this.isRedirecting = true;
            if (error.response.status === 400) {
                this.redirectionMessage = "Sorry, the location you saved was invalid,\n" +
                    "Redirecting to your edit locations page.";
                setTimeout(() => {
                    this.$router.go();
                }, this.timeout);
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
        locationPublicValue: function (params) {
            this.inputPublicLocation = {
                latitude: params[0].lat,
                longitude: params[0].lng,
                name: params[0].name,
            };
        },

        /**
         * Function emitted from LocationIO.vue to set inputPrivateLocation
         */
        locationPrivateValue: function (params) {
            this.inputPrivateLocation = {
                latitude: params[0].lat,
                longitude: params[0].lng,
                name: params[0].name,
            };
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
    .location-header {
        margin-top: 25px;
        font-size: 30px;
    }

    .location-container {

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

    #locationAlert {
        margin-top: 10px;
    }
</style>