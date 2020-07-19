<template>
    <div>
        <h1><br/><br/></h1>
        <b-container class="contents" fluid>
            <h1><br/></h1>
            <div class="header-sidebar">

                <div>
                    <div class="container">
                        <div class="row">
                            <div class="col-sm-6 offset-sm-3">
                                <template v-if="loggedIn">
                                    <Header/>
                                </template>
                                <router-view></router-view>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="loggedIn">
            <Sidebar :userId="profileId"/>
                <div class="settings-page" v-if="!this.isRedirecting">
                    <div class="container-fluid" v-if="loggedIn">
                        <div class="form-group">
                            <header class="masthead">
                                <div class="container h-100">
                                    <div class="row h-100 align-items-center">
                                        <div class="col-12 text-center">
                                            <h1 class="font-weight-light"><strong>Edit Profile Details</strong></h1><br/>
                                        </div>
                                    </div>
                                </div>
                            </header>
                            <hr>
                    <b-form-group  label-for="firstname" label="First Name: *">
                        <!-- first-name field-->
                        <div class="edit-area">
                            <b-form-input
                                    class="form-control"
                                    id="firstname"
                                    v-model="firstname"
                                    placeholder="Your First Name..."
                                    trim required>
                            </b-form-input>
                        </div>
                        <div class="form-group">
                            <!-- middle-name field-->
                            <label for="middlename">Middle Name: </label>
                            <div class="edit-area">
                                <input type="text" class="form-control" v-model="middlename" id="middlename" name="middlename"
                                       placeholder="Your Middle Name...">
                            </div>
                        </div>
                    </b-form-group>
                    <b-form-group label-fot="middlename" label="Middle Name:">
                        <!-- middle-name field-->
                        <div class="edit-area">
                            <b-form-input
                                    class="form-control"
                                    id="middlename"
                                    v-model="middlename"
                                    placeholder="Your Middle Name..."
                                    trim>
                            </b-form-input>
                        </div>
                        <div class="form-group">
                            <!-- fitness level field -->
                            <label for="fitness">Fitness Level:</label>
                            <div class="edit-area">
                                <div id="fitnessDiv" class="multiselect multiselect-box">
                                    <multiselect v-model="fitness" id="fitness" :options="fitnessOptions" :multiple="false" label="desc" :return="fitnessOptions.desc"
                                                 placeholder="Please select a fitness level" track-by="value">
                                        <template slot="singleLabel" slot-scope="{ option }"><footer> {{ option.desc }}</footer></template>
                                    </multiselect>
                                </div>
                            </div>
                        </div>
                    </b-form-group>
                    <b-form-group label-for="lastname" label="Last Name: *">
                        <!-- last-name field-->
                        <div class="edit-area">
                            <b-form-input
                                    class="form-control"
                                    id="lastname"
                                    v-model="lastname"
                                    placeholder="Your Last Name..."
                                    trim required>
                            </b-form-input>
                        </div>
                        <div class="form-group">
                            <!-- gender field -->
                            <label for="gender">Gender: *</label>
                            <div class="edit-area">
                                <div id="genderDiv" class="multiselect multiselect-box">
                                    <multiselect v-model="gender" id="gender"
                                                 :options="genders" placeholder="Your gender" required>
                                        <template slot="noResult">Invalid gender</template>
                                    </multiselect>
                                </div>
                            </div>
                        </div>
                    </b-form-group>
                    <b-form-group label-for="fitness" label="Fitness Level:">
                        <!-- fitness level field -->
                        <div class="edit-area">
                            <div id="fitnessDiv" class="multiselect-box">
                                <multiselect v-model="fitness" id="fitness" :options="fitnessOptions" :multiple="false" label="desc" :return="fitnessOptions.desc"
                                             placeholder="Please select a fitness level" track-by="value">
                                    <template slot="singleLabel" slot-scope="{ option }"><footer> {{ option.desc }}</footer></template>
                                </multiselect>
                            </div>
                        </div>
                    </b-form-group>
                    <b-form-group label-for="nickname" label="Nickname:">
                        <!-- nickname field-->
                        <div class="edit-area">
                            <b-form-input
                                    class="form-control"
                                    id="nickname"
                                    v-model="nickname"
                                    placeholder="Your Nickname..."
                                    trim>
                            </b-form-input>
                        </div>
                        <div class="alert alert-danger alert-dismissible fade show sticky-top" role="alert" id="alert_nickname" hidden>
                            <p>Unfortunately your nickname is to long, 45 characters is the limit</p>
                        </div>
                    </b-form-group>
                    <b-form-group label-for="gender" label="Gender: *">
                        <!-- gender field -->
                        <div class="edit-area">
                            <div id="genderDiv" class="multiselect-box">
                                <multiselect v-model="gender" id="gender"
                                             :options="genders" placeholder="Your gender" required>
                                    <template slot="noResult">Invalid gender</template>
                                </multiselect>
                            </div>
                        </div>
                        <div class="alert alert-danger alert-dismissible fade show sticky-top" role="alert" id="alert_gender" hidden>
                            <p>Gender is a mandatory field</p>
                        </div>
                    </b-form-group>
                    <b-form-group label-for="date_of_birth" label="Date of Birth: *">
                        <!-- date-of-birth field-->
                        <div class="edit-area">
                            <input type="date" class="form-control" v-model="date_of_birth" id="date_of_birth" name="date_of_birth" required>
                        </div>
                        <div class="alert alert-danger alert-dismissible fade show sticky-top" role="alert" id="alert_dob" hidden>
                            <p>You must be 13 yrs or older. Older than 150 yrs is invalid</p>
                        </div>
                    </b-form-group>
                    <b-form-group label-for="passports" label="Passport Country:">
                        <!-- passport country -->
                        <div class="edit-area">
                            <div id="passportsDiv" class="multiselect-box">
                                <multiselect v-model="passports" id="passports"
                                             :options="countries" :multiple="true" :searchable="true" :close-on-select="false"
                                             placeholder="Select your passport countries">
                                    <template slot="noResult">Country not found</template>
                                </multiselect>
                            </div>
                        </div>
                    </b-form-group>
                    <b-form-group label-for="bio" label="Tell us about yourself, your Bio:">
                        <!-- user bio -->
                        <div class="edit-area">
                            <b-form-textarea
                                    class="form-control"
                                    id="bio"
                                    v-model="bio"
                                    placeholder="Who are you?"
                                    rows="2"
                                    max-rows="30"
                                    trim>
                            </b-form-textarea>
                        </div>
                        <div class="alert alert-danger alert-dismissible fade show sticky-top" role="alert" id="alert_bio" hidden>
                            <p>Unfortunately your bio is to long, 255 characters is the limit</p>
                        </div>
                    </b-form-group>
                    <b-form-group label-for="activityTypes" label="Activity Types:">
                        <!-- activity types -->
                        <div class="edit-area">
                            <div id="activityTypesDiv" class="multiselect-box">
                                <multiselect v-model="selectedActivityTypes" id="activityTypes"
                                        :options="activityTypes" :multiple="true" :searchable="true" :close-on-select="false"
                                        placeholder="Select your activity types">
                                     <template slot="noResult">Invalid activity type</template>
                                </multiselect>
                            </div>
                        </div>
                    </b-form-group>
                    <div class="alert alert-success alert-dismissible fade show sticky-top" role="alert" id="overall_message" hidden>
                        <p id="alert-message">{{ message }}</p>
                    </div>
                    <div class="text-center">
                        <b-button type="submit" id="back-btn" size="lg" variant="primary float-left" v-on:click="backToProfile">Back</b-button>
                        <b-button type="submit" id="reset-btn" size="lg" variant="primary" v-on:click="updateInputs">Reset</b-button>
                        <b-button type="submit" id="saveChanges-btn" size="lg" variant="success float-right" v-on:click="saveChanges">Save Changes</b-button>
                    </div>
                </div>
            </div>
            <div v-if="this.isRedirecting">
                {{ redirectionMessage }}
                <br/><br/><b-spinner variant="primary" label="Spinning"></b-spinner>
            </div>
        </b-container>
        <br/><br/>
    </div>
</template>

<script>
    import Sidebar from "../../components/layout/ProfileEditSidebar.vue"
    import Multiselect from 'vue-multiselect'
    import Header from '../../components/Header/Header.vue'
    import api from "../../Api";
    import {getCountryNames, fitnessLevels} from '../../constants';
    import {validateUser, getDateString} from "../../util"

    export default {
        name: "Details.vue",
        components: {
            Sidebar, Multiselect, Header
        },
        data() {
            return {
                profileId: '',
                firstname: '',
                middlename: '',
                lastname: '',
                nickname: '',
                gender: '',
                fitness: '',
                passports: '',
                bio: '',
                selectedActivityTypes: '',
                date_of_birth: '',
                countries: [],
                activityTypes: [],
                genders: ['Male', 'Female', 'Non-Binary'],
                loggedIn: false,
                fitnessOptions: fitnessLevels,
                message: '',
                isRedirecting: false,
                redirectionMessage: ''
            }
        },
        async mounted() {
            await this.init();
        },
        methods: {
            /**
             * Initializes the page.
             * Called by mounted and when redirecting to this user's edit profile page.
             * @see processGetError
             */
            async init() {
                this.profileId = '';
                this.loggedIn = false;
                this.isRedirecting = false;
                this.redirectionMessage = '';
                this.fetchCountries();
                await this.fetchActivityTypes();
                if (this.$route.params.userId !== undefined) {
                    await this.validateUserIdWithToken(); // If allowed to edit profileId is set
                }
                await this.updateInputs();//Populate input fields with profile data if allowed to edit
            },

            /**
             * Fetch the possible passport countries to select from.
             */
            fetchCountries: function () {
                //Fill Passport countries
                let select = [];
                // Create a request variable and assign a new XMLHttpRequest object to it.
                let request = new XMLHttpRequest();
                //build url
                let url = new URL(getCountryNames);
                // Open a new connection, using the GET request on the URL endpoint;
                request.open('GET', url, true);

                request.onload = function () {
                    // If the request is successful
                    if (request.status >= 200 && request.status < 400) {
                        let data = JSON.parse(this.response);
                        data.forEach(country => {
                            let elmt = country.name;
                            select.push(elmt)
                        })
                    } else {
                        select = 'List is empty'
                    }
                };
                // Send request
                this.countries = select;
                request.send();
            },

            /**
             * Fetch all possible activity types from the server.
             */
            async fetchActivityTypes() {
                this.activityTypes = null;
                await api.getActivityTypes().then(response => {
                    this.activityTypes = response.data.map(activity => activity['name']);
                    this.activityTypes.sort(function (a, b) {
                        return a.toLowerCase().localeCompare(b.toLowerCase());
                    });
                }).catch(error => {
                    this.processGetError(error);
                });
            },

            /**
             * Validates the changes to user before calling putUpdate.
             * Count of invalid fields is also tracked and presented to the user
             */
            saveChanges() {
                let errorCount = 0; //count of blank fields
                if (!validateUser(this.firstname, "firstname").valid) {
                    this.showError('alert_first_name');
                    errorCount += 1;
                }
                if (!validateUser(this.middlename, "middlename").valid) {
                    this.showError('alert_middle_name');
                    errorCount += 1;
                }
                if (!validateUser(this.lastname, "lastname").valid) {
                    this.showError('alert_last_name');
                    errorCount += 1;
                }
                if (!validateUser(this.date_of_birth, "date_of_birth").valid) {
                    this.showError('alert_dob');
                    errorCount += 1;
                }
                if (!validateUser(this.gender, "gender").valid) {
                    this.showError('alert_gender');
                    errorCount += 1;
                }
                if (!validateUser(this.nickname, "nickname").valid) {
                    this.showError('alert_nickname');
                    errorCount += 1;
                }
                if (!validateUser(this.bio, "bio").valid) {
                    this.showError('alert_bio');
                    errorCount += 1;
                }
                // A count greater than zero means there are invalid fields.
                if (errorCount > 0) {
                    this.message = errorCount + " invalid fields have been entered. You can reset the edited fields with the button below"
                    this.showError('overall_message');
                } else {
                    this.putUpdate();
                }
            },

            /**
             * Shows the error message for the alert name given.
             * Displayed for 9 seconds
             */
            showError(alert_name) {
                let errorAlert = document.getElementById(alert_name);
                errorAlert.classList.add("alert-danger");
                errorAlert.classList.remove("alert-success");
                errorAlert.removeAttribute("hidden");   //Show alert bar
                setTimeout(function () {    //Hide alert bar after ~9000ms
                    errorAlert.hidden = true;
                }, 9000);
            },

            /**
             * Sends the put request to the server to update the user profile
             */
            async putUpdate() {
                let editedUser = {
                    'firstname' : this.firstname,
                    'middlename' : this.middlename,
                    'lastname' : this.lastname,
                    'nickname' : this.nickname,
                    'gender' : this.gender,
                    'fitness' : (this.fitness === null) ? -1 : this.fitness.value,
                    'passports' : this.passports,
                    'bio' : this.bio,
                    'activityTypes' : this.selectedActivityTypes,
                    'date_of_birth' : this.date_of_birth
                };
                let alertDiv = document.getElementById('overall_message');
                await api.editProfile(editedUser, this.profileId).then(() => {
                    alertDiv.classList.add("alert-success");
                    alertDiv.classList.remove("alert-danger");
                    this.message = "Successfully updated field";
                    alertDiv.removeAttribute("hidden");
                    setTimeout(function () {
                        alertDiv.hidden = true;
                    }, 3000);
                }).catch(error => {
                    this.processPutError(error);
                });
            },

            /**
             * Updates the input fields to contain the info stored in the database,
             * if allowed to edit the given profileId. If no profileId given edit this user,
             * if invalid profileId is given redirect to this user's detail page.
             */
            async updateInputs() {
                if (!this.isRedirecting) {
                    // If this point is reached user is authorized to edit the profile, and profileId has been set
                    await api.getUserData(this.profileId).then(response => {
                        this.loggedIn = true;
                        this.setUserFields(response.data);
                    }).catch(error => {
                        this.processGetError(error);
                    });
                }
            },

            /**
             * This helper function is called when an error is caught when performing the put request to the server.<br>
             * Conditions handled are:<br>
             * 401 (UNAUTHORIZED) redirect to login page<br>
             * 403 (FORBIDDEN) redirect to this user's edit profile page<br>
             * 404 (NOT_FOUND) invalid field missed by frontend and caught by backend, present to user<br>
             * Otherwise unknown error to present to user
             */
            processPutError(error) {
                if (error.response.data.status === 401) {
                    this.loggedIn = false;
                    this.isRedirecting = true;
                    this.redirectionMessage = "Sorry, you are no longer logged in,\n" +
                        "Redirecting to the login page.";
                    setTimeout(() => {
                        this.$router.push('/login');
                    }, 4000);
                } else if (error.response.data.status === 403) {
                    this.isRedirecting = true;
                    this.redirectionMessage = "Sorry, you are not allowed to edit another user's profile,\n" +
                        "Redirecting to your edit profile page.";
                    setTimeout(() => {
                        this.$router.push({ name: 'detailsNoID' });
                        this.init();
                    }, 4000);
                } else if (error.response.data.status === 404) {
                    this.isRedirecting = true;
                    this.redirectionMessage = "Sorry, the user does not exist,\n" +
                        "Redirecting to your edit profile page.";
                    setTimeout(() => {
                        this.$router.push({name: 'detailsNoID'});
                        this.init();
                    }, 4000);
                } else if (error.response.data.status === 400) {
                    this.message = error.response.data.message.toString();
                    this.showError('overall_message')
                } else {
                    this.message = "An unknown error has occurred";
                    this.showError('overall_message')
                }
            },

            /**
             * This helper function is called when an error is caught when performing a Get request to the server.<br>
             * Conditions handled are:<br>
             * 401 (UNAUTHORIZED) redirect to login page,<br>
             * 403 (FORBIDDEN) and 404 (NOT_FOUND) redirect to this user's edit profile page,<br>
             * Otherwise unknown error so redirect to user's home page
             */
            processGetError(error) {
                this.loggedIn = true;
                this.isRedirecting = true;
                if (error.response.data.status === 401) {
                    this.loggedIn = false;
                    this.redirectionMessage = "Sorry, you are no longer logged in,\n" +
                        "Redirecting to the login page.";
                    setTimeout(() => {
                        this.$router.push('/login');
                    }, 4000);
                } else if (error.response.data.status === 403) {
                    this.redirectionMessage = "Sorry, you are not allowed to edit another user's profile,\n" +
                        "Redirecting to your edit profile page.";
                    setTimeout(() => {
                        this.$router.push({ name: 'detailsNoID' });
                        this.init();
                    }, 4000);
                } else if (error.response.data.status === 404) {
                    this.redirectionMessage = "Sorry, the user does not exist,\n" +
                        "Redirecting to your edit profile page.";
                    setTimeout(() => {
                        this.$router.push({ name: 'detailsNoID' });
                        this.init();
                    }, 4000);
                } else {
                    this.redirectionMessage = "Sorry, an unknown error occurred when retrieving profile info,\n" +
                        "Redirecting to your home page.";
                    setTimeout(() => {
                        this.$router.push({ name: 'myProfile' });
                    }, 4000);
                }
            },

            /**
             * Sets the input variables to the given users attributes.
             */
            setUserFields(user) {
                this.profileId = user.id;
                this.firstname = user.firstname;
                this.middlename = user.middlename;
                this.lastname = user.lastname;
                this.nickname = user.nickname;
                this.gender = user.gender;
                this.passports = user.passports;
                this.bio = user.bio;
                this.selectedActivityTypes = user.activityTypes.map(at => at.name);
                this.date_of_birth = getDateString(user.date_of_birth);
                for (const option in this.fitnessOptions) {
                    if (this.fitnessOptions[option].value === user.fitness) {
                        this.fitness = this.fitnessOptions[option];
                    }
                }
            },

            /**
             * Checks if a user id from query parameter is logged in with token provided.
             * Called to prevent a user from editing another user's profile.
             * Checks if user can edit this given ID.
             */
            async validateUserIdWithToken() {
                await api.checkProfile(this.profileId).then(() => {
                    // 200
                    // If admin will return 200
                    this.loggedIn = true;
                    this.profileId = this.$route.params.userId;
                }).catch(error => {
                    this.profileId = '';
                    this.processGetError(error);
                });
            },

            /**
             * Redirect to view user screen
             */
            backToProfile() {
                this.$router.push({ name: 'profile', params: {userId: this.profileId} });
            }
        }
    }
</script>
<style scoped>
    .input {
        width:100%;
    }
    .settings-page {
        padding-bottom: 15px;
    }

    .edit-area {
        display: flex;
    }

    .form-control {
        max-width: 100%;
        min-width: 85%;
    }

    .alert {
        top: 60px;
    }

    .multiselect-box {
        min-width: 100%;
        margin-right: 1%;
    }

    .multiselect {
        width: 100%;
    }

    #fitnessDiv {
        width: 50em;
    }

    #genderDiv {
        width: 50em;
    }

    #passportsDiv {
        width: 50em;
    }

    #activityTypesDiv {
        width: 50em;
    }
</style>