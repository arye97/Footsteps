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
                <label for="fitnessLevel">Fitness Level:</label>
                <select class="form-control" v-model="fitnessLevel" name="fitnessLevel" id="fitnessLevel">
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
                    <option value="non-binary">Non Binary</option>
                </select>
            </div>
            <div class="form-group">
                <!-- date of birth field-->
                <label for="dob">Date of Birth: *</label>
                <input type="date" class="form-control" v-model="dob" id="dob" name="dob" required>
            </div>
            <div class="form-group">
                <!-- passport country -->
                <label for="passportCountry">Passport Country:</label>
                <select class="form-control" v-model="passportCountry" id="passportCountry" name="passportCountry">
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
                <button type="submit" class="btn btn-primary" v-on:click="registerUser">Register</button>
                <router-link to="/login" class="btn btn-link">Login</router-link>
            </div>
            <label v-show="regError" id="error">Error</label>
        </form>
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
                dob: '',
                fitnessLevel: '',
                passportCountry: '',
                bio: '',
                regError: false
            }
        },

        mounted () {
            let select = document.getElementById('passportCountry')
            // Create a request variable and assign a new XMLHttpRequest object to it.
            let request = new XMLHttpRequest()
            //build url
            let restCountriesName = 'https://restcountries.eu/rest/v2/all?fields=name'     //needs to be const somewhere
            let url = new URL(restCountriesName)
            // Open a new connection, using the GET request on the URL endpoint;
            request.open('GET', url, true)

            request.onload = function() {
                if(request.status >= 200 && request.status < 400) {
                    let data = JSON.parse(this.response)
                    data.forEach(country => {
                        // console.log(country.name)
                        let elmt = document.createElement('option')
                        elmt.textContent = country.name
                        elmt.value = country.name
                        //console.log(elmt)
                        select.appendChild(elmt)
                    } )
                } else {
                    let elmt = document.createElement('error')
                    elmt.textContent = 'error fetching countries'
                    elmt.value = 'error'
                    select.appendChild(elmt)
                }
            }
            // Send request
            request.send()
        },

        methods: {
            // Method is called when the register button is selected
            registerUser() {
                // Save the data as a newUser object
                const newUser = {
                    lastname: this.lastname,
                    firstname: this.firstname,
                    middlename: this.middlename,
                    nickname: this.nickname,
                    primary_email: this.email,
                    password: this.password,
                    date_of_birth: this.dob,
                    gender: this.gender,
                    bio: this.bio
                }
                // console.log(newUser)     // view data in console for testing with this
                // The HTTP Post Request
                server.post(  'http://localhost:9499/profiles',
                    newUser
                ).then(function(){
                    console.log('User Registered Successfully!');
                    this.regError = false;
                }
                ).catch(error => {
                    console.log(error.response);
                    let errorLabel = document.getElementById("error");
                    if (error.response.status == 403) {
                        errorLabel.textContent = error.response.data.toString();
                    } else {
                        errorLabel.textContent = "An unknown error has occurred during login"
                    }
                    this.regError = true;
                });
                if (!this.regError) {
                    this.$router.push("/profile");
                }
            }
        }
    }


</script>
