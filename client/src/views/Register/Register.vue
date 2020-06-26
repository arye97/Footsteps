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
        <header class="masthead">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light">Welcome to Hakinakina</h1>
                        <p class="lead">Plan your route with the best</p>
                        <h1 class="font-weight-light">
                            Register with us<br/>
                        </h1>
                        Entries marked with * are required<br/><br/>
                    </div>
                </div>
            </div>
        </header>
        <!-- post hides data in the url bar
            v-on:submit.prevent means when you click login then the emit wont only stay for a second
            keeps it in the vue extension logs, wont know until later if we need this to go away
        -->

        <form method="post" v-on:submit.prevent="registerUser">
            <div class="form-group">
                <!-- full-name field-->
                <label id="first-name-label" for="first-name">First Name: *</label>
                <input type="text" class="form-control" v-on:click="unDisplayRules" v-model="firstname" id="first-name" name="first-name" placeholder="Your First Name...">
            </div>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_first_name">
                {{  "Field is mandatory and can only contain letters and spaces"  }}
            </div>
            <div class="form-group">
                <!-- full-name field-->
                <label id="middle-name-label" for="middle-name">Middle Name: </label>
                <input type="text" class="form-control" v-on:click="unDisplayRules" v-model="middlename" id="middle-name" name="middle-name" placeholder="Your Middle Name...">
            </div>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_middle_name">
                {{  "Field can only contain letters and spaces"  }}
            </div>
            <div class="form-group">
                <!-- full-name field-->
                <label id="last-name-label" for="last-name">Last Name: *</label>
                <input type="text" class="form-control" v-on:click="unDisplayRules" v-model="lastname" id="last-name" name="last-name" placeholder="Your Last Name...">
            </div>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_last_name">
                {{  "Field is mandatory and can only contain letters and spaces"  }}
            </div>
            <div class="form-group">
                <!-- email field -->
                <label id="email-label" for="email">Email Address: *</label>
                <input type="email" class="form-control" v-on:click="unDisplayRules" v-model="email" id="email" name="email" placeholder="Your Email Address...">
            </div>
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
            <div class="form-group">
                <!-- password field-->
                <label id="password-label" for="password">Password: *</label>
                <input type="password" class="form-control" v-on:click="displayRules" v-model="password" id="password" name="password" placeholder="Your Password...">
            </div>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_password">
                {{  "Field is mandatory and must not be blank"  }}
            </div>
            <div class="form-group">
                <label id="passwordCheck-label" for="passwordCheck">Retype your Password: *</label>
                <input type="password" class="form-control" v-on:click="displayRules" v-model="passwordCheck" id="passwordCheck" name="passwordCheck" placeholder="Retype Password...">
            </div>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_password_check">
                {{  "Field is mandatory and must not be blank"  }}
            </div>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_password_match">
                {{  'Passwords do not match, please type again'  }}
            </div>
            <div class="form-group">
                <!-- fitness level field -->
                <label id="fitness-label" for="fitness">Fitness Level:</label>
                <multiselect v-model="fitness" id="fitness" v-on:select="unDisplayRules" :options="fitnessOptions" :multiple="false" label="desc" :return="fitnessOptions.desc"
                             placeholder="Please select a fitness level" track-by="value">
                    <template slot="singleLabel" slot-scope="{ option }"><footer> {{ option.desc }}</footer></template>
                </multiselect>
            </div>
            <div class="form-group">
                <!-- nickname field-->
                <label id="nickname-label" for="nickname">Nickname: </label>
                <input type="text" class="form-control" v-on:click="unDisplayRules" v-model="nickname" id="nickname" name="nickname" placeholder="Your Nickname...">
            </div>
            <div class="form-group">
                <!-- gender field -->
                <label id="gender-label" for="gender">Gender: *</label>
                <multiselect v-model="gender" id="gender" v-on:select="unDisplayRules"
                             :options="genders" placeholder="Your gender">
                    <template slot="noResult">Invalid gender</template>
                </multiselect>
            </div>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_gender">
                {{  "Field is mandatory and must not be blank"  }}
            </div>
            <div class="form-group">
                <!-- date of birth field-->
                <label id="date_of_birth-label" for="date_of_birth">Date of Birth: *</label>
                <input type="date" class="form-control" v-on:click="unDisplayRules" v-model="date_of_birth" id="date_of_birth" name="date_of_birth">
            </div>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_dob">
                {{  "Field is mandatory, can not be blank and user must be within 13 - 150 years"  }}
            </div>
            <div class="form-group">
                <!-- passport country -->
                <label id="passportCountries-label" for="passportCountries">Passport Country:</label>
                <multiselect v-model="passports" id="passportCountries" v-on:select="unDisplayRules"
                             :options="countries" :multiple="true" :searchable="true" :close-on-select="false"
                             placeholder="Select your passport countries">
                    <template slot="noResult">Country not found</template>
                </multiselect>
            </div>
            <div class="form-group">
                <!-- user bio -->
                <label id="bio-label" for="bio">Tell us about yourself, your Bio: </label>
                <textarea name="bio" class="form-control" id="bio" v-on:click="unDisplayRules" v-model="bio" cols="30" rows="1" placeholder="Who are you?"></textarea>
            </div>
            <div class="form-group">
                <!-- activity types -->
                <label id="activityTypes-label" for="activityTypes">Activity Types:</label>
                <multiselect v-model="selectedActivityTypes" id="activityTypes" v-on:select="unDisplayRules"
                             :options="activityTypes" :multiple="true" :searchable="true" :close-on-select="false"
                             placeholder="Select your activity types">
                    <template slot="noResult">Invalid activity type</template>
                </multiselect>
            </div>
            <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="alert_form">
                {{  message_form  }}
            </div>
            <div class="form-group">
                <!-- SignIn Button-->
                <button type="submit" class="btn btn-primary">Register</button>
                <router-link to="/login" class="btn btn-link">Login</router-link>
            </div>
        </form>
        <footer class="col-12 text-center">
            Entries marked with * are required
        </footer><br/><br/>
    </div>
</template>

<script>
    import server from '../../Api';
    import Multiselect from 'vue-multiselect'
    import Header from '../../components/Header/Header.vue'
    import {getCountryNames, getActivityTypes, fitnessLevels} from '../../constants';
    // import {tokenStore} from '../../main';
    import {validateUser} from "../../util";

    function showError(alert_name) {
        let errorAlert = document.getElementById(alert_name);
        
        errorAlert.hidden = false;          //Show alert bar
        setTimeout(function () {    //Hide alert bar after ~9000ms
            errorAlert.hidden = true;
        }, 9000);
    }

    async function validUser(newUser, passwordCheck) {
        let count = 0; //count of blank fields
        if(!validateUser(newUser.password, "password").valid) {
            showError('alert_password');
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
        return count;
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
                activityTypes: ["Swimming with Elephants"],
                genders: ['Male', 'Female', 'Non-Binary'],
                passports: [],
                selectedActivityTypes: [],
                isDisplayingRules: false
            }
        },

        mounted () {
            this.fetchCountries();
            this.fetchActivityTypes();
        },

        methods: {
            displayRules() {
                this.isDisplayingRules = true;
            },
            unDisplayRules() {
                this.isDisplayingRules = false;
            },
            fetchActivityTypes() {
                this.activityTypes = getActivityTypes.sort();
            },
            async fetchCountries() {
                let select = [];
                // Create a request variable and assign a new XMLHttpRequest object to it.
                let request = new window.XMLHttpRequest();
                //build url
                let url = new URL(getCountryNames);
                // Open a new connection, using the GET request on the URL endpoint;
                request.open('GET', url, true);

                request.onload = function() {
                    // If the request is successful
                    if(request.status >= 200 && request.status < 400) {
                        let data = JSON.parse(this.response);
                        data.forEach(country => {
                            let elmt = country.name;
                            select.push(elmt)
                        } )
                    } else {
                        select = 'List is empty';
                        let errorAlert = document.getElementById("alert_form");
                        this.message_form = 'Error fetching countries';
                        errorAlert.hidden = false;          //Show alert bar
                    }
                };
                // Send request
                this.countries = select;
                request.send()
            },
            async registerUser() {
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
                    activity_type: this.selectedActivityTypes
                };
                if (newUser.fitness === undefined) newUser.fitness = -1;
                let validCount = await validUser(newUser, this.passwordCheck);
                if (validCount === 'password') {
                    this.message_form = "Password and re-typed password do not match. Please try again"
                    showError('alert_form');
                    return;
                } else if (validCount !== 0) {
                    this.message_form = validCount.toString() + " empty or invalid mandatory fields have been found. Please fill them in to register";
                    showError('alert_form');
                    return;
                }
                console.log("The New User\\/");
                console.log(newUser);
                // The HTTP Post Request
                console.log(newUser);
                await server.post('/profiles',
                    newUser,
                    {
                        headers: {"Access-Control-Allow-Origin": "*", "Content-Type": "application/json"},
                        withCredentials: true
                    }
                ).then(response => { //If successfully registered the response will have a status of 201
                    if (response.status === 201) {
                        console.log('User Registered Successfully!');
                        sessionStorage.setItem("token", response.data.Token);
                        // tokenStore.setToken(response.data);
                        this.$router.push('/profile'); //Routes to profile on successful register
                    }
                }).catch(error => {
                    console.log(error);
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

<style src="vue-multiselect/dist/vue-multiselect.min.css">
    .multiselect {
        color: black;
    }
</style>