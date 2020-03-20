<template>
    <div>
        <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
        <h1><br/></h1>
        <header class="masthead">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light">Welcome to Hakinakina</h1>
                        <p class="lead">Plan your route with the best</p><br/>
                    </div>
                </div>
            </div>
        </header>
        <!-- post hides data in the url bar
            v-on:submit.prevent means when you click login then the emit wont only stay for a second
            keeps it in the vue extension logs, wont know until later if we need this to go away
        -->
        <h1>
            Register with us<br/>
        </h1>
        <form method="post" v-on:submit.prevent="registerUser">

            <div class="form-group">
                <!-- full-name field-->
                <label for="first-name">First Name: *</label>
                <input type="text" class="form-control" v-model="firstname" id="first-name" name="first-name" placeholder="Your First Name..." required><br/>
            </div>
            <div class="form-group">
                <!-- full-name field-->
                <label for="middle-name">Middle Name: </label>
                <input type="text" class="form-control" v-model="middlename" id="middle-name" name="middle-name" placeholder="Your Middle Name..."><br/>
            </div>
            <div class="form-group">
                <!-- full-name field-->
                <label for="last-name">Last Name: *</label>
                <input type="text" class="form-control" v-model="lastname" id="last-name" name="last-name" placeholder="Your Last Name..." required><br/>
            </div>
            <div class="form-group">
                <!-- email field -->
                <label for="email">Email Address: *</label>
                <input type="email" class="form-control" v-model="email" id="email" name="email" placeholder="Your Email Address..." required>
            </div>
            <div class="form-group">
                <!-- password field-->
                <label for="password">Password: *</label>
                <input type="password" class="form-control" v-model="password" id="password" name="password" placeholder="Your Password..." required>
            </div>
            <div class="form-group">
                <label for="passwordCheck">Retype your Password: *</label>
                <input type="password" class="form-control" v-model="passwordCheck" id="passwordCheck" name="passwordCheck" placeholder="Retype Password..." required>
            </div>
            <div class="form-group">
                <!-- fitness level field -->
                <label for="fitness">Fitness Level:</label>
                <select class="form-control" v-model="fitness" name="fitness" id="fitness">
                    <option disabled value="">Please select a fitness level</option>
                    <option value="1">Unfit, no regular exercise, being active is very rare</option>
                    <option value="2">Not overly fit, occasional recreational fitness activity, active a few times a month</option>
                    <option value="3">Moderately fit, enjoys fitness activities for recreation, active once or twice a week</option>
                    <option value="4">Fit, may compete occasionally in small scale events, active most days</option>
                    <option value="5">Very fit, competitive athlete, extremely active</option>
                </select>
            </div>
            <div class="form-group">
                <!-- nickname field-->
                <label for="nickname">Nickname: </label>
                <input type="text" class="form-control" v-model="nickname" id="nickname" name="nickname" placeholder="Your Nickname...">
            </div>
            <div class="form-group">
                <!-- gender field -->
                <label for="gender">Gender: *</label>
                <multiselect v-model="gender" id="gender"
                             :options="genders" placeholder="Your gender" required>
                    <template slot="noResult">Invalid gender</template>
                </multiselect>
            </div>
            <div class="form-group">
                <!-- date of birth field-->
                <label for="date_of_birth">Date of Birth: *</label>
                <input type="date" class="form-control" v-model="date_of_birth" id="date_of_birth" name="date_of_birth" required>
            </div>
            <div class="form-group">
                <!-- passport country -->
                <label for="passportCountries">Passport Country:</label>
                <multiselect v-model="passports" id="passportCountries"
                             :options="countries" :multiple="true" :searchable="true" :close-on-select="false"
                             placeholder="Select your passport countries">
                    <template slot="noResult">Country not found</template>
                </multiselect>
            </div>
            <div class="form-group">
                <!-- user bio -->
                <label for="bio">Tell us about yourself, your Bio: </label>
                <textarea name="bio" class="form-control" id="bio" v-model="bio" cols="30" rows="1" placeholder="Who are you?"></textarea>
            </div>
            <div class="form-group">
                <!-- SignIn Button-->
                <button type="submit" class="btn btn-primary">Register</button>
                <router-link to="/login" class="btn btn-link">Login</router-link>
            </div>
        </form>
        <div class="alert alert-danger alert-dismissible fade show sticky-top" role="alert" hidden="true" id="alert">
            {{  message  }}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <footer>
            Entries marked with * are required
        </footer>
    </div>
</template>

<script>
    import server from '../../Api';
    import Multiselect from 'vue-multiselect'
    import {getCountryNames} from '../../constants';

    export default {
        components: { Multiselect },
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
                bio: '',
                message: "",
                countries: [],
                genders: ['male', 'female', 'non_binary'],
                passports: []
            }
        },

        mounted () {
            let select = []
            // Create a request variable and assign a new XMLHttpRequest object to it.
            let request = new XMLHttpRequest();
            //build url
            let url = new URL(getCountryNames)
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
                    select = 'List is empty'
                    let errorAlert = document.getElementById("alert");
                    this.message = 'Error fetching countries';
                    errorAlert.hidden = false;          //Show alert bar
                }
            };
            // Send request
            this.countries = select
            request.send()
        },

        methods: {

            async registerUser() {
                // Save the data as a newUser object
                const newUser = {
                    lastname: this.lastname,
                    firstname: this.firstname,
                    middlename: this.middlename,
                    nickname: this.nickname,
                    primary_email: this.email,
                    password: this.password,
                    date_of_birth: this.date_of_birth,
                    gender: this.gender,
                    bio: this.bio,
                    fitness: this.fitness,
                    // passports: this.passports
                };
                // The HTTP Post Request
                server.post('/profiles',
                    newUser,
                    {
                        headers: {"Access-Control-Allow-Origin": "*", "content-type": "application/json"},
                        withCredentials: true
                    }
                ).then(response => { //If successfully registered the response will have a status of 201
                    if (response.status === 201) {
                        console.log('User Registered Successfully!');
                        this.$router.push('/profile'); //Routes to profile on successful register
                    }
                }).catch(error => {
                    console.log(error);
                    //Get alert bar element
                    let errorAlert = document.getElementById("alert");
                    if (error.message === "Network Error" || error.message.includes("timeout")) {
                        this.message = error.message;
                    } else if (error.response.status === 403) { //Error 401: Email already exists, invalid date of birth or invalid name field
                        this.message = error.response.data.toString(); //Set alert bar message to error message from server
                    } else if (error.response.status === 400) { //Error 400: Bad request (missing fields)
                        this.message = "An invalid register request has been received please try again"
                    } else {    //Catch for any errors that are not specifically caught
                        this.message = "An unknown error has occurred during register"
                    }
                    errorAlert.hidden = false;          //Show alert bar
                    setTimeout(function () {    //Hide alert bar after ~5000ms
                        errorAlert.hidden = true;
                    }, 5000);
                });
            }
        }
    }

</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css">
    .multiselect {
        color: black;
    }
</style>