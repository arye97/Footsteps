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
            <div v-if="!locationLoading">
                <div>
                    <h3 class="font-weight-light"><strong>Public Location: </strong></h3><br/>
                    <div class="map-pane">
                        <location-i-o class="input-location"
                                      v-on:child-pins="locationPublicValue"
                                      :current-location="publicLocation"
                                      :single-only=true></location-i-o>
                    </div>
                </div>
                <div>
                    <h3 class="font-weight-light"><strong>Private Location: </strong></h3><br/>
                    <div class="map-pane">
                        <location-i-o class="input-location"
                                      v-on:child-pins="locationPrivateValue"
                                      :current-location="privateLocation"
                                      :single-only=true></location-i-o>
                    </div>
                </div>
            </div>
            <b-button type="submit" variant="success float-right"
                      size="lg"
                      v-on:click="saveChanges">
                Save Changes
            </b-button>

            <!--                      v-bind:disabled="changesHaveBeenMade===false"-->
        </section>
    </b-container>
</template>

<script>
import api from "../../Api";
import LocationIO from "../../components/Map/LocationIO";

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
            // errored: false,
            isRedirecting: false,
            isEditable: false,
            isPublicLocation: true,

            publicLocation: null,
            privateLocation: null,

            inputPublicLocation: null,
            inputPrivateLocation: null
        }
    },
    async mounted() {
        if (this.$route.params.userId !== undefined) {
            await this.validateUserIdWithToken();
        }
        this.populateInputs();

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
                this.processGetError(error);
            });
        },

        /**
         * Populate LocationIO fields to display the user's location.
         */
        async populateInputs() {
            if (!this.isRedirecting) {
                await api.getAllUserData().then(response => {
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
                    }
                    this.locationLoading = false;
                    this.loading = false;
                }).catch(error => {
                    this.processPutError(error);
                });
            }
        },

        /**
         * Submits a PUT request to edit a user's public/private locations.
         */
        async saveChanges() {
            console.log(this.publicLocation)
            console.log(this.privateLocation)

            // If input location is none, then dont modify location
            let editedLocation = {}
            if (this.inputPublicLocation && this.inputPublicLocation !== this.publicLocation) {
                editedLocation['public_location'] = this.inputPublicLocation;
            }
            if (this.inputPrivateLocation && this.inputPrivateLocation !== this.privateLocation) {
                editedLocation['private_location'] = this.inputPrivateLocation;
            }

            console.log(editedLocation)
            // await api.editLocation(editedLocation, this.profileId).then(() => {
            //     // successfully saved modal thingy
            // }).catch(error => {
            //     this.processPutError(error);
            // });
        },

        /**
         * This helper function is called when an error is caught when performing a Get request to the server.<br>
         * Conditions handled are:<br>
         * 401 (UNAUTHORIZED) redirect to login page,<br>
         * 403 (FORBIDDEN) and 404 (NOT_FOUND) redirect to this user's edit profile page,<br>
         * Otherwise unknown error so redirect to user's home page
         */
        processPutError(error) {
            let timeoutTime = 3000;
            this.isLoggedIn = true;
            this.isRedirecting = true;
            if (error.response.status === 401) {
                this.isLoggedIn = false;
                this.redirectionMessage = "Sorry, you are no longer logged in,\n" +
                    "Redirecting to the login page.";
                setTimeout(() => {
                    sessionStorage.clear();
                    this.$router.push('/login');
                }, timeoutTime);
            } else if (error.response.status === 403) {
                this.redirectionMessage = "Sorry, you are not allowed to edit another user's profile,\n" +
                    "Redirecting to your edit profile page.";
                setTimeout(() => {
                    this.$router.push({ name: 'editMyProfile' });
                    // this.init();
                }, timeoutTime);
            } else if (error.response.status === 404) {
                this.redirectionMessage = "Sorry, the user does not exist,\n" +
                    "Redirecting to your edit profile page.";
                setTimeout(() => {
                    this.$router.push({ name: 'editMyProfile' });
                    // this.init();
                }, timeoutTime);
            } else {
                this.redirectionMessage = "Sorry, an unknown error occurred when retrieving profile info,\n" +
                    "Redirecting to your home page.";
                setTimeout(() => {
                    this.$router.push({ name: 'myProfile' });
                }, timeoutTime);
            }
        },

        locationPublicValue: function (params) {
            this.inputPublicLocation = {
                latitude: params[0].lat,
                longitude: params[0].lng,
                name: params[0].name,
            };
        },

        locationPrivateValue: function (params) {
            this.inputPrivateLocation = {
                latitude: params[0].lat,
                longitude: params[0].lng,
                name: params[0].name,
            };
        }
    }
}
</script>

<style scoped>

</style>