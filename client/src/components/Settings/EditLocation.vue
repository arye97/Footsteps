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
            <!-- ToDo: put location things in here!-->
        </section>
    </b-container>
</template>

<script>
import api from "../../Api";

export default {
    name: "EditLocation",
    data() {
        return {
            profileId: null,
            isLoggedIn: false,
            loading: true,
            redirectionMessage: "Something went wrong! Try again later",
            // errored: false,
            isRedirecting: false,
            isEditable: false,
            publicLocation: null,
            privateLocation: null
        }
    },
    async mounted() {
        if (this.$route.params.userId !== undefined) {
            await this.validateUserIdWithToken();
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
                    this.loading = false;
                    if (response.data.role === 20) {
                        // Is the global admin
                        this.$router.push('/home');
                    } else {
                        this.publicLocation = response.data.publicLocation;
                        this.privateLocation = response.data.privateLocation;
                    }
                }).catch(error => {
                    this.processGetError(error);
                });
            }
        },

        /**
         * Submits a PUT request to edit a user's public/private locations.
         */
        async updateLocation() {
            let editedLocation = {
                'public_location': this.publicLocation,
                'private_location': this.privateLocation,
            };
            await api.editLocation(editedLocation, this.profileId).then(() => {
                // alertDiv.classList.add("alert-success");
                // alertDiv.classList.remove("alert-danger");
                // this.message = "Successfully updated field";
                // alertDiv.removeAttribute("hidden");
                // setTimeout(function () {
                //     alertDiv.hidden = true;
                // }, 3000);
            }).catch(error => {
                this.processPutError(error);
            });
        },


        /**
         * This helper function is called when an error is caught when performing a Get request to the server.<br>
         * Conditions handled are:<br>
         * 401 (UNAUTHORIZED) redirect to login page,<br>
         * 403 (FORBIDDEN) and 404 (NOT_FOUND) redirect to this user's edit profile page,<br>
         * Otherwise unknown error so redirect to user's home page
         */
        processGetError(error) {
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

        // check if my id
        // check if logged in
        // function with error handling

    }
}
</script>

<style scoped>

</style>