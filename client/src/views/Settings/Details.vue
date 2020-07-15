<template>
    <div>
        <div class="header-sidebar">
            <h1><br/><br/></h1>
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
            <div class="alert alert-success alert-dismissible fade show sticky-top" role="alert" id="alert" hidden>
                <p id="alert-message"><strong>{{ code }}</strong>{{ message }}</p>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </div>
        <div v-if="loggedIn">
            <Sidebar :userId="profileId"/>
            <div class="settings-page" v-if="!this.isRedirecting">
                <div class="container-fluid" v-if="loggedIn">
                    <div class="form-group">
                        <!-- first-name field-->
                        <label for="firstname">First Name: *</label>
                        <div class="edit-area">
                            <input type="text" class="form-control" v-model="firstname" id="firstname" name="firstname"
                                   placeholder="Your First Name..." required disabled>
                            <button class="btn btn-primary" id="firstname-btn" v-on:click="mutate" type="button">Edit</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <!-- middle-name field-->
                        <label for="middlename">Middle Name: </label>
                        <div class="edit-area">
                            <input type="text" class="form-control" v-model="middlename" id="middlename" name="middlename"
                                   placeholder="Your Middle Name..." disabled>
                            <button class="btn btn-primary" id="middlename-btn" v-on:click="mutate" type="button">Edit</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <!-- last-name field-->
                        <label for="lastname">Last Name: *</label>
                        <div class="edit-area">
                            <input type="text" class="form-control" v-model="lastname" id="lastname" name="lastname"
                                   placeholder="Your Last Name..." required disabled>
                            <button class="btn btn-primary" id="lastname-btn" v-on:click="mutate" type="button">Edit</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <!-- fitness level field -->
                        <label for="fitness">Fitness Level:</label>
                        <div class="edit-area">
                            <div id="fitnessDiv" class="multiselect--disabled multiselect-box">
                                <multiselect v-model="fitness" id="fitness" :options="fitnessOptions" :multiple="false" label="desc" :return="fitnessOptions.desc"
                                             placeholder="Please select a fitness level" track-by="value">
                                    <template slot="singleLabel" slot-scope="{ option }"><footer> {{ option.desc }}</footer></template>
                                </multiselect>
                            </div>
                            <button class="btn btn-primary" id="fitnessDiv-btn" v-on:click="mutate" type="button">Edit</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <!-- nickname field-->
                        <label for="nickname">Nickname: </label>
                        <div class="edit-area">
                            <input type="text" class="form-control" v-model="nickname" id="nickname" name="nickname"
                                   placeholder="Your Nickname..." disabled>
                            <button class="btn btn-primary" id="nickname-btn" v-on:click="mutate" type="button">Edit</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <!-- gender field -->
                        <label for="gender">Gender: *</label>
                        <div class="edit-area">
                            <div id="genderDiv" class="multiselect--disabled multiselect-box">
                                <multiselect v-model="gender" id="gender"
                                             :options="genders" placeholder="Your gender" required>
                                    <template slot="noResult">Invalid gender</template>
                                </multiselect>
                            </div>
                            <button class="btn btn-primary" id="genderDiv-btn" v-on:click="mutate" type="button">Edit</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <!-- date-of-birth field-->
                        <label for="date_of_birth">Date of Birth: *</label>
                        <div class="edit-area">
                            <input type="date" class="form-control" v-model="date_of_birth" id="date_of_birth" name="date_of_birth" disabled required>
                            <button class="btn btn-primary" id="date_of_birth-btn" v-on:click="mutate" type="button">Edit</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <!-- passport country -->
                        <label for="passports">Passport Country:</label>
                        <div class="edit-area">
                            <div id="passportsDiv" class="multiselect--disabled multiselect-box">
                                <multiselect v-model="passports" id="passports"
                                             :options="countries" :multiple="true" :searchable="true" :close-on-select="false"
                                             placeholder="Select your passport countries">
                                    <template slot="noResult">Country not found</template>
                                </multiselect>
                            </div>
                            <button class="btn btn-primary" id="passportsDiv-btn" v-on:click="mutate" type="button">Edit</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <!-- user bio -->
                        <label for="bio">Tell us about yourself, your Bio: </label>
                        <div class="edit-area">
                            <textarea name="bio" class="form-control" id="bio" v-model="bio" cols="30" rows="2" placeholder="Who are you?"
                                      disabled></textarea>
                            <button class="btn btn-primary" id="bio-btn" v-on:click="mutate" type="button">Edit</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <!-- activity types -->
                        <label for="activityTypes">Activity Types:</label>
                        <div class="edit-area">
                            <div id="activityTypesDiv" class="multiselect--disabled multiselect-box">
                                <multiselect v-model="selectedActivityTypes" id="activityTypes"
                                        :options="activityTypes" :multiple="true" :searchable="true" :close-on-select="false"
                                        placeholder="Select your activity types">
                                     <template slot="noResult">Invalid activity type</template>
                                </multiselect>
                            </div>
                            <button class="btn btn-primary" id="activityTypesDiv-btn" v-on:click="mutate" type="button">Edit</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="this.isRedirecting">
            {{ redirectionMessage }}
            <br/><br/><b-spinner variant="primary" label="Spinning"></b-spinner>
        </div>



    </div>
</template>

<script>
    import Sidebar from "../../components/layout/ProfileEditSidebar.vue"
    import Multiselect from 'vue-multiselect'
    import Header from '../../components/Header/Header.vue'
    import server from "../../Api";
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
                message: '',
                code: '',
                date_of_birth: '',
                countries: [],
                activityTypes: [],
                genders: ['Male', 'Female', 'Non-Binary'],
                loggedIn: false,
                fitnessOptions: fitnessLevels,
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
             * Fetch all possible activity types from the server
             */
            async fetchActivityTypes() {
                this.activityTypes = null;
                await server.get('activity-types',
                    {headers: {'Content-Type': 'application/json', 'Token': sessionStorage.getItem("token")}
                    }
                ).then(response => {
                    console.log(response.data)
                    this.activityTypes = response.data.map(activity => activity['name']);
                    this.activityTypes.sort(function (a, b) {
                        return a.toLowerCase().localeCompare(b.toLowerCase());
                    });
                }).catch(error => {
                    this.processGetError(error);
                });
            },

            mutate: function (event) {
                const alertDiv = document.getElementById("alert");
                //This function is used to swap the purpose of the buttons
                const mutateButton = document.getElementById(event.target.id);
                const mutateTarget = document.getElementById(event.target.id.replace("-btn", ""));
                //Disable the buttons to prevent issues with them being rapidly clicked
                mutateButton.setAttribute('disabled', "true");
                if (event.target.type === "submit") {
                    if (mutateTarget.className !== "multiselect--above multiselect-box") {
                        if (mutateTarget.hasAttribute("required") && mutateTarget.value.trim() === "") {
                            this.message = "This is a required field. Please enter some valid data";
                            alertDiv.classList.remove("alert-success");
                            alertDiv.classList.add("alert-danger");
                            alertDiv.removeAttribute("hidden");
                            setTimeout(function () {
                                alertDiv.hidden = true;
                            }, 5000);

                        } else {
                            const update = {};
                            update[mutateTarget.id] = mutateTarget.value;
                            const validator = validateUser(mutateTarget.value, mutateTarget.id);
                            if (!validator.valid) {
                                this.message = validator.message;
                                alertDiv.classList.remove("alert-success");
                                alertDiv.classList.add("alert-danger");
                                alertDiv.removeAttribute("hidden");
                                setTimeout(function () {
                                    alertDiv.hidden = true;
                                }, 5000);
                            } else if (this.putUpdate(update, alertDiv)) {
                                mutateTarget.setAttribute('disabled', "true");
                                mutateButton.innerText = "Edit";
                                mutateButton.type = "button";
                            }
                        }
                    } else {
                        //Need to fix issues with
                        //I changed it to a switch case.  Slightly more readable.
                        const updateField = document.getElementById(mutateTarget.id.replace("Div", ""));
                        const update = {};

                        switch (updateField.id) {
                            case "gender":
                                update['gender'] = this.gender;
                                break;
                            case "passports":
                                update['passports'] = this.passports;
                                break;
                            case "fitness":
                                update['fitness'] = (this.fitness === null) ? null : this.fitness.value;
                                break;
                            case "activityTypes":
                                update['activityTypes'] = this.selectedActivityTypes;
                                break;
                            default:
                                eval("update['{0}'] = this.{0};".format(updateField.id));

                        }
                        if (this.putUpdate(update, alertDiv)) {
                            mutateTarget.className = "multiselect--disabled multiselect-box";
                            mutateButton.innerText = "Edit";
                            mutateButton.type = "button";
                        }
                        this.message = "This is a required field. Please enter some valid data";
                    }
                } else {
                    if (mutateTarget.className === "multiselect--disabled multiselect-box") {
                        mutateTarget.className = "multiselect--above multiselect-box";
                    } else {
                        mutateTarget.removeAttribute('disabled');
                    }
                    mutateButton.innerText = "Save";
                    mutateButton.type = "submit";
                }
                mutateButton.removeAttribute('disabled');
            },

            putUpdate: async function (update, alertDiv) {
                //Sends the put request to the server to update the user profile
                let result = true;
                await server.put('profiles/'.concat(this.profileId), update,
                    {headers: {'Content-Type': 'application/json', 'Token': sessionStorage.getItem("token")},
                        withCredentials: true
                    }
                ).then(() => {
                    alertDiv.classList.add("alert-success");
                    alertDiv.classList.remove("alert-danger");
                    this.message = "Successfully updated field";
                    this.code = '';
                }).catch(error => {
                    alertDiv.classList.remove("alert-success");
                    alertDiv.classList.add("alert-danger");
                    if (error.response.data.status === 400 || error.response.data.status === 403) {
                        this.message = error.response.data.message.toString();
                        this.code = error.response.data.status;
                        if (error.response.data.status === 400) {
                            result = false;
                        }

                    } else if (error.response.data.status === 401) {
                        this.$router.push("/login");
                    } else {
                        this.message = error.message();
                        this.code = error.code;
                    }

                });
                alertDiv.removeAttribute("hidden");
                setTimeout(function () {
                    alertDiv.hidden = true;
                }, 3000);
                return result;
            },

            /**
             * Updates the input fields to contain the info stored in the database,
             * if allowed to edit the given profileId. If no profileId given edit this user,
             * if invalid profileId is given redirect to this user's detail page.
             */
            async updateInputs() {
                if (!this.isRedirecting) {
                    // If this point is reached user is authorized to edit the profile, and profileId has been set
                    await server.get('profiles/'.concat(this.profileId),
                        {headers: {'Content-Type': 'application/json',
                                                 'Token': sessionStorage.getItem("token")}}
                    ).then(response => {
                        this.loggedIn = true;
                        this.setUserFields(response.data);
                    }).catch(error => {
                        this.processGetError(error);
                    });
                }
            },

            /**
             * This helper function is called when an error is caught when performing a Get request to the server.
             * Conditions handled are:
             * 401 (UNAUTHORIZED) redirect to login page,
             * 403 (FORBIDDEN) and 404 (NOT_FOUND) redirect to this user's edit profile page,
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
                await server.get(`/check-profile/`.concat(this.$route.params.userId),
                    {
                        headers: {
                            'Content-Type': 'application/json',
                            'Token': sessionStorage.getItem("token"),
                        },
                    }
                ).then(() => {
                    // 200
                    // If admin will return 200
                    this.loggedIn = true;
                    this.profileId = this.$route.params.userId;
                }).catch(error => {
                    this.profileId = '';
                    this.processGetError(error);
                });
            }
        }
    }
</script>
<style scoped>
    .form-group {
        padding-top: 15px;
    }

    .edit-area {
        display: flex;
    }

    .form-control {
        max-width: 85%;
        margin-right: 1%;
        min-width: 85%;
    }

    .alert {
        top: 60px;
    }

    .multiselect-box {
        max-width: 85%;
        min-width: 85%;
        margin-right: 1%;
    }
</style>