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
                <label for="first-name">Full Name: *</label>
                <input type="text" class="form-control" v-model="firstname" id="first-name" name="first-name" placeholder="Your First Name..." required><br/>
            </div>
            <div class="form-group">
                <!-- full-name field-->
                <label for="middle-name">Middle Name: </label>
                <input type="text" class="form-control" v-model="middlename" id="middle-name" name="middle-name" placeholder="Your Middle Name..."><br/>
            </div>
            <div class="form-group">
                <!-- full-name field-->
                <label for="last-name">Full Name: *</label>
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
                <select class="form-control" v-model="gender" id="gender" name="gender" required>
                    <option value="" disabled selected hidden>Your Gender... </option>
                    <option value="male">Male</option>
                    <option value="female">Female</option>
                    <option value="non_binary">Non Binary</option>
                </select>
            </div>
            <div class="form-group">
                <!-- date of birth field-->
                <label for="date_of_birth">Date of Birth: *</label>
                <input type="date" class="form-control" v-model="date_of_birth" id="date_of_birth" name="date_of_birth" required>
            </div>
            <div class="form-group">
                <!-- passport country -->
                <label for="passports">Passport Country:</label>
                <select class="form-control" v-model="passports" id="passports" name="passports">
                    <option value="" disabled selected hidden>Select country</option>
                </select>
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
    export default {
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
                passports: [],
                bio: '',
                hasRegistered: false,
                isLoggedIn: false,
                message: ""
            }
        },

        mounted () {
            let select = document.getElementById('passports');
            // Create a request variable and assign a new XMLHttpRequest object to it.
            let request = new XMLHttpRequest();
            //build url
            let restCountriesName = 'https://restcountries.eu/rest/v2/all?fields=name'   ;  //needs to be const somewhere
            let url = new URL(restCountriesName);
            // Open a new connection, using the GET request on the URL endpoint;
            request.open('GET', url, true);

            request.onload = function() {
                if(request.status >= 200 && request.status < 400) {
                    let data = JSON.parse(this.response);
                    data.forEach(country => {
                        // console.log(country.name)
                        let elmt = document.createElement('option');
                        elmt.textContent = country.name;
                        elmt.value = country.name;
                        //console.log(elmt)
                        select.appendChild(elmt)
                    } )
                } else {
                    let elmt = document.createElement('error');
                    elmt.textContent = 'error fetching countries';
                    elmt.value = 'error';
                    select.appendChild(elmt)
                }
            };
            // Send request
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
                server.post(  'http://localhost:9499/profiles',
                    newUser,
                    { headers: { "Access-Control-Allow-Origin": "*", "content-type":"application/json"},
                        withCredentials: true}
                ).then(response => { //If successfully registered the response will have a status of 201
                    if (response.status === 201) {
                        console.log('User Registered Successfully!');
                        this.isLoggedIn = true;
                        this.$router.push('/profile'); //Routes to profile on successful register
                    }
                }).catch(error => {
                    console.log(error.response);
                    //Get alert bar element
                    let errorAlert = document.getElementById("alert");
                    if (error.response.status === 403) { //Error 401: Email already exists or invalid date of birth
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
