<template>
    <div id="app">
        <div>
            <div class="container">
                <div class="row">
                    <div class="col-sm-6 offset-sm-3">
                        <Header />
                        <router-view></router-view>
                    </div>
                </div>
            </div>
        </div>
        <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
        <h1><br/><br/></h1>


        <!-- contents of the page -->
        <b-container class="contents" fluid>
            <header class="masthead">
                <div class="container h-100">
                    <h1><br/></h1>
                    <div class="row h-100 align-items-center">
                        <div class="col-12 text-center">
                            <h1 class="font-weight-light">Register with us<br/></h1>
                            Entries marked with * are required<br/><br/>
                            <hr><br/>
                        </div>
                    </div>
                </div>
            </header>
            <!-- post hides data in the url bar
                v-on:submit.prevent means when you click login then the emit wont only stay for a second
                keeps it in the vue extension logs, wont know until later if we need this to go away
            -->

        <b-form @submit="registerUser">
            <b-form-group label="First Name: *" label-for="first-name" id="first-name-group">
                <!-- full-name field-->
                <b-form-input
                  type="text" class="form-control" trim v-on:click="unDisplayRules" v-on:input="updateCharCount"
                  v-model="firstname" id="first-name" name="first-name" placeholder="Your First Name..."
                />
                <div class="word-count">
                    {{ firstNameCharCount }}/{{ maxNameCharCount }} characters left
                </div>
            </b-form-group>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_first_name">
                {{  "Field is mandatory and can only contain letters, spaces, hyphens, and apostrophes"  }}
            </div>

            <b-form-group label="Middle Name: " label-for="middle-name">
                <!-- full-name field-->
                <b-form-input type="text" class="form-control" trim v-on:click="unDisplayRules" v-on:input="updateCharCount" v-model="middlename" id="middle-name" name="middle-name" placeholder="Your Middle Name..." />
                <div class="word-count">
                    {{ middleNameCharCount }}/{{ maxNameCharCount }} characters left
                </div>
            </b-form-group>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_middle_name">
                {{  "Field can only contain letters, spaces, hyphens, and apostrophes"  }}
            </div>

            <b-form-group label-for="last-name" label="Last Name: *">
                <!-- full-name field-->
                <b-input type="text" class="form-control" trim v-on:click="unDisplayRules" v-on:input="updateCharCount" v-model="lastname" id="last-name" name="last-name" placeholder="Your Last Name..." />
                <div class="word-count">
                    {{ lastNameCharCount }}/{{ maxNameCharCount }} characters left
                </div>
            </b-form-group>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_last_name">
                {{  "Field is mandatory and can only contain letters, spaces, hyphens, and apostrophes"  }}
            </div>
            <b-form-group label-for="email" label="Email Address: *">
                <!-- email field -->
                <b-input type="email" class="form-control" trim v-on:click="unDisplayRules" v-on:input="updateCharCount" v-model="email" id="email" name="email" placeholder="Your Email Address..." />
                <div class="word-count">
                    {{ emailCharCount }}/{{ maxEmailCharCount }} characters left
                </div>
            </b-form-group>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_email">
                {{  "Field is mandatory, can not be blank and must be a valid email"  }}
            </div>

            <section v-if="isDisplayingRules">
                <footer>Password rules:<br/>
                    <ul>
                        <li>Must contain at least 8 characters</li>
                        <li>Must contain at least one letter</li>
                        <li>Must contain at least one number</li>
                    </ul>
                </footer>
            </section>

            <b-form-group label-for="password" label="Password: *">
                <!-- password field-->
                <b-input type="password" class="form-control" v-on:click="displayRules" v-on:input="updateCharCount" v-model="password" id="password" name="password" placeholder="Your Password..." />
            </b-form-group>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_password">
                {{  "Field is mandatory and must not be blank"  }}
            </div>
            <b-form-group label-for="passwordCheck" label="Retype your Password: *">
                <b-input type="password" class="form-control" v-on:click="displayRules" v-on:input="updateCharCount" v-model="passwordCheck" id="passwordCheck" name="passwordCheck" placeholder="Retype Password..." />
            </b-form-group>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_password_check">
                {{  "Your password doesn't follow the password rules"  }}
            </div>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_password_match">
                {{  'Passwords do not match, please type again'  }}
            </div>

            <b-form-group label-for="fitness" label="Fitness Level:">
                <!-- fitness level field -->
                <multiselect v-model="fitness" id="fitness" v-on:select="unDisplayRules" :options="fitnessOptions" :multiple="false" label="desc" :return="fitnessOptions.desc"
                             placeholder="Please select a fitness level" track-by="value">
                    <template slot="singleLabel" slot-scope="{ option }"><footer> {{ option.desc }}</footer></template>
                </multiselect>
            </b-form-group>

            <b-form-group label-for="nickname" label="Nickname:">
                <!-- nickname field-->
                <b-input type="text" class="form-control" v-on:click="unDisplayRules" v-model="nickname" id="nickname" name="nickname" placeholder="Your Nickname..." />
                <div class="word-count">
                    {{ nicknameCharCount }}/{{ maxNameCharCount }} characters left
                </div>
            </b-form-group>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_nickname">
                {{  'Unfortunately your nickname is to long, 45 characters is the limit'  }}
            </div>

            <b-form-group label-for="gender" label="Gender: *">
                <!-- gender field -->
                <multiselect v-model="gender" id="gender" v-on:select="unDisplayRules"
                             :options="genders" placeholder="Your gender">
                    <template slot="noResult">Invalid gender</template>
                </multiselect>
            </b-form-group>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_gender">
                {{  "Field is mandatory and must not be blank"  }}
            </div>

            <b-form-group label-for="date_of_birth" label="Date of Birth: *">
                <!-- date of birth field-->
                <b-input type="date" name="date_of_birth" class="form-control" v-model="date_of_birth" id="date_of_birth" />
            </b-form-group>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_dob">
                {{  "Field is mandatory and can not be blank. User must be within 13 - 150 years (ie. the date must be in the past)"  }}
            </div>

            <b-form-group label-for="passportCountries" label="Passport Country:">
                <!-- passport country -->
                <multiselect v-model="passports" id="passportCountries" v-on:select="unDisplayRules"
                             :options="countries" :multiple="true" :searchable="true" :close-on-select="false"
                             placeholder="Select your passport countries">
                    <template slot="noResult">Country not found</template>
                </multiselect>
            </b-form-group>

            <b-form-group label-for="bio" label="Tell us about yourself, your Bio:">
                <!-- user bio -->
                <b-form-textarea name="bio" class="form-control" id="bio" v-on:click="unDisplayRules"
                                 v-on:input="updateCharCount" v-model="bio" cols="30" rows="1" placeholder="Who are you?" />
                <div class="word-count">
                    {{ bioCharCount }}/{{ maxBioCharCount }} characters left
                </div>
            </b-form-group>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_bio">
                {{  'Unfortunately your bio is to long, 255 characters is the limit'  }}
            </div>

            <b-form-group label-for="activityTypes" label="Activity Types:">
                <!-- activity types -->
                <multiselect v-model="selectedActivityTypes" id="activityTypes" v-on:select="unDisplayRules"
                             :options="activityTypes" :multiple="true" :searchable="true" :close-on-select="false"
                             placeholder="Select your activity types">
                    <template slot="noResult">Invalid activity type</template>
                </multiselect>
            </b-form-group>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_form">
                {{  message_form  }}
            </div>

            <b-form-group>
                <!-- SignIn Button-->
                <b-button type="submit" variant="primary">Register</b-button>
                <router-link to="/login" class="btn btn-link">Login</router-link>
            </b-form-group>
        </b-form>
        <footer class="col-12 text-center">
            Entries marked with * are required
        </footer><br/>
        </b-container>
        <br/><br/>
    </div>
</template>

<script>
    import api from '../../Api';
    import Multiselect from 'vue-multiselect'
    import Header from '../../components/Header/Header.vue'
    import {fitnessLevels} from '../../constants';
    import {fetchCountries, validateUser} from "../../util";

    function showError(alert_name) {
        let errorAlert = document.getElementById(alert_name);
        
        errorAlert.hidden = false;          //Show alert bar
        setTimeout(function () {    //Hide alert bar after ~9000ms
            errorAlert.hidden = true;
        }, 9000);
    }

    export default {
        components: { Multiselect, Header },
        name: "NewUser",
        data() {
            return {
                firstname: '',
                middlename: '',
                lastname: '',
                email: '',
                password: '',
                passwordCheck: '',
                nickname: '',
                gender: '',
                date_of_birth: '',
                fitness: '',
                fitnessOptions: fitnessLevels,
                bio: '',
                message_form: "",
                message_password_check: '',
                countries: [],
                activityTypes: [],
                genders: ['Male', 'Female', 'Non-Binary'],
                passports: [],
                selectedActivityTypes: [],
                isDisplayingRules: false,
                maxNameCharCount: 45,
                maxBioCharCount: 255,
                maxEmailCharCount: 255,
                firstNameCharCount: 0,
                middleNameCharCount: 0,
                lastNameCharCount: 0,
                nicknameCharCount: 0,
                emailCharCount: 0,
                bioCharCount: 0,
            }
        },

        mounted () {
            this.countries = fetchCountries();
            this.fetchActivityTypes();
        },

        methods: {
            displayRules() {
                this.isDisplayingRules = true;
            },

            unDisplayRules() {
                if(!validateUser(this.password, "password").valid && this.password !== '') {
                    //Password rules not followed
                    showError('alert_password_check');

                } else if (this.password !== this.passwordCheck && (this.password !== '' || this.passwordCheck !== '')) {
                    //Passwords don't match
                    showError('alert_password_match');

                } else {
                    this.isDisplayingRules = false;
                }
            },

            /**
             * Fetch all possible activity types from the server
             */
            async fetchActivityTypes() {
                await api.getActivityTypes().then(response => {
                    this.activityTypes = response.data.map(activity => activity['name']);
                    this.activityTypes.sort(function (a, b) {
                        return a.toLowerCase().localeCompare(b.toLowerCase());
                    });
                });
            },
            async registerUser(evt) {
                evt.preventDefault();
                // Save the data as a newUser object
                const newUser = {
                    lastname: this.lastname.trim(),
                    firstname: this.firstname.trim(),
                    middlename: this.middlename.trim(),
                    nickname: this.nickname,
                    primary_email: this.email.trim(),
                    password: this.password.trim(),
                    date_of_birth: this.date_of_birth,
                    gender: this.gender,
                    bio: this.bio.trim(),
                    fitness: this.fitness.value,
                    passports: this.passports,
                    activity_types: this.selectedActivityTypes
                };
                if (newUser.fitness === undefined) newUser.fitness = -1;
                let validCount = await this.validUser(newUser, this.passwordCheck);
                if (validCount === 'password') {
                    this.message_form = "Password and re-typed password do not match. Please try again"
                    showError('alert_form');
                    return;
                } else if (validCount !== 0) {
                    this.message_form = validCount.toString() + " empty or invalid fields have been found. Please fill them in to register";
                    showError('alert_form');
                    return;
                }
                // The HTTP Post Request
                api.register(newUser).then(response => { //If successfully registered the response will have a status of 201
                    if (response.status === 201) {
                        sessionStorage.setItem("token", response.data.Token);
                        // tokenStore.setToken(response.data);
                        this.$router.push('/'); //Routes to Home page on successful register
                    }
                }).catch(error => {
                    //Get alert bar element
                    if (error.message === "Network Error" || error.message.includes("timeout")) {
                        this.message_form = error.message;
                    } else if (error.response.data.status === 409 || error.response.data.status === 403) { //Error 409: Email already exists, Error 403: Invalid date of birth or invalid name field
                        this.message_form = error.response.data.message.toString(); //Set alert bar message to error message from server
                    } else if (error.response.status === 400) { //Error 400: Bad request (missing fields)
                        this.message_form = "An invalid register request has been received please try again"
                    } else {    //Catch for any errors that are not specifically caught
                        this.message_form = "An unknown error has occurred during register"
                    }
                    showError('alert_form');
                });
            },

            updateCharCount() {
              this.firstNameCharCount = this.firstname.length;
              this.middleNameCharCount = this.middlename.length;
              this.lastNameCharCount = this.lastname.length;
              this.nicknameCharCount = this.nickname.length;
              this.emailCharCount = this.email.length;
              this.bioCharCount = this.bio.length;
            },
            async validUser(newUser, passwordCheck) {
                let count = 0; //count of blank fields
                if(!validateUser(newUser.password, "password").valid) {
                    showError('alert_password_check');
                    count += 1;
                }
                if (newUser.password !== passwordCheck) {
                    showError('alert_password_match');
                    return 'password';
                }
                if(!validateUser(newUser.firstname, "firstname").valid) {
                    showError('alert_first_name');
                    count += 1;
                }
                if(!validateUser(newUser.middlename, "middlename").valid) {
                    showError('alert_middle_name');
                    count += 1;
                }
                if(!validateUser(newUser.lastname, "lastname").valid) {
                    showError('alert_last_name');
                    count += 1;
                }
                if(!validateUser(newUser.date_of_birth, "date_of_birth").valid) {
                    showError('alert_dob');
                    count += 1;
                }
                if(!validateUser(newUser.gender, "gender").valid) {
                    showError('alert_gender');
                    count += 1;
                }
                if(!validateUser(newUser.primary_email, "email").valid) {
                    showError('alert_email');
                    count += 1;
                }
                if(!validateUser(newUser.bio, "bio")) {
                    showError('alert_bio');
                    count += 1;
                }
                if(!validateUser(newUser.nickname, "nickname")) {
                    showError('alert_nickname');
                    count += 1;
                }
                return count;
            }
        },

        computed: {
            value: {
                get () {
                    return this.fitnessOptions.filter(
                        option => this.fitness.includes(option.desc)
                    )
                },
                set (newSelectedOptions) {
                    this.fitness = newSelectedOptions.map(option => option.desc)
                }
            }
        }
    }

</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css" />
<style>
    .multiselect {
        color: black;
    }

    .word-count {
        padding-top: 7px;
        color: #707070;
        font-size: 0.8em;
    }
</style>