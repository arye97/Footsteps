<template>
  <div class="settings-page">
    <div class="alert alert-success alert-dismissible fade show sticky-top" role="alert" id="alert" hidden>
      <p id="alert-message"><strong>{{ code }}</strong>{{ message }}</p>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
      </button>
    </div>
    <Sidebar/>
    <div class="container-fluid" v-if="loggedIn">
      <div class="form-group">
        <!-- first-name field-->
        <label for="firstname">First Name: *</label>
        <div class="edit-area">
          <input type="text" class="form-control" v-model="firstname" id="firstname" name="firstname"
                 placeholder="Your First Name..." required disabled>
          <button class="btn btn-primary" id="firstname-btn" v-on:click="mutate" type="button">+</button>
        </div>
      </div>
      <div class="form-group">
        <!-- middle-name field-->
        <label for="middlename">Middle Name: </label>
        <div class="edit-area">
          <input type="text" class="form-control" v-model="middlename" id="middlename" name="middlename"
                 placeholder="Your Middle Name..." disabled>
          <button class="btn btn-primary" id="middlename-btn" v-on:click="mutate" type="button">+</button>
        </div>
      </div>
      <div class="form-group">
        <!-- last-name field-->
        <label for="lastname">Last Name: *</label>
        <div class="edit-area">
          <input type="text" class="form-control" v-model="lastname" id="lastname" name="lastname"
                 placeholder="Your Last Name..." required disabled>
          <button class="btn btn-primary" id="lastname-btn" v-on:click="mutate" type="button">+</button>
        </div>
      </div>
      <div class="form-group">
        <!-- fitness level field -->
        <label for="fitness">Fitness Level:</label>
        <div class="edit-area">
          <select class="form-control" v-model="fitness" name="fitness" id="fitness" disabled>
            <option disabled value="">Please select a fitness level</option>
            <option value="1">Unfit, no regular exercise, being active is very rare</option>
            <option value="2">Not overly fit, occasional recreational fitness activity, active a few times a month
            </option>
            <option value="3">Moderately fit, enjoys fitness activities for recreation, active once or twice a week
            </option>
            <option value="4">Fit, may compete occasionally in small scale events, active most days</option>
            <option value="5">Very fit, competitive athlete, extremely active</option>
          </select>
          <button class="btn btn-primary" id="fitness-btn" v-on:click="mutate" type="button">+</button>
        </div>
      </div>
      <div class="form-group">
        <!-- nickname field-->
        <label for="nickname">Nickname: </label>
        <div class="edit-area">
          <input type="text" class="form-control" v-model="nickname" id="nickname" name="nickname"
                 placeholder="Your Nickname..." disabled>
          <button class="btn btn-primary" id="nickname-btn" v-on:click="mutate" type="button">+</button>
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
          <button class="btn btn-primary" id="genderDiv-btn" v-on:click="mutate" type="button">+</button>
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
          <button class="btn btn-primary" id="passportsDiv-btn" v-on:click="mutate" type="button">+</button>
        </div>
      </div>
    <div class="form-group">
      <!-- user bio -->
      <label for="bio">Tell us about yourself, your Bio: </label>
      <div class="edit-area">
        <textarea name="bio" class="form-control" id="bio" v-model="bio" cols="30" rows="1" placeholder="Who are you?"
                  disabled></textarea>
        <button class="btn btn-primary" id="bio-btn" v-on:click="mutate" type="button">+</button>
      </div>
    </div>
  </div>
  </div>
</template>

<script>
    import Sidebar from "../../components/layout/ProfileEditSidebar"
    import Multiselect from 'vue-multiselect'
    import server from "../../Api";
    import {getCountryNames} from '../../constants';
    import router from '../../index';

    export default {
        name: "Details.vue",
        components: {
            Sidebar, Multiselect
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
            message: '',
            code: '',
            countries: [],
            genders: ['Male', 'Female', 'Non-Binary'],
            loggedIn: false
          }
        },
        mounted() {
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

            //Populate input fields with profile data
            this.updateInputs();
        },
        methods: {
            mutate: function (event) {
                const alertDiv = document.getElementById("alert");
                //This function is used to swap the purpose of the buttons
                const mutateButton = document.getElementById(event.target.id);
                const mutateTarget = document.getElementById(event.target.id.replace("-btn", ""));
                //Disable the buttons to prevent issues with them being rapidly clicked
                mutateButton.setAttribute('disabled', "true");
                if (event.target.type === "submit") {
                    if (mutateTarget.className !== "multiselect--above multiselect-box") {
                      if (mutateTarget.hasAttribute("required") && mutateTarget.value === "" || mutateTarget.value === undefined) {
                          this.message = "This is a required field. Please enter some valid data";
                      } else {
                          const update = {};
                          update[mutateTarget.id] = mutateTarget.value;
                          this.putUpdate(update, alertDiv);
                          mutateTarget.setAttribute('disabled', "true");
                          mutateButton.innerText = "+";
                          mutateButton.type = "button";
                      }
                    } else {
                      //Need to fix issues with
                      const updateField = document.getElementById(mutateTarget.id.replace("Div", ""));
                      const update = {};
                      switch (updateField.id) {
                        case "gender":
                          update['gender'] = this.gender.toLowerCase();
                          break;
                        case "passports":
                          update['passports'] = this.passports;
                          break;
                      }
                      this.putUpdate(update, alertDiv);
                      mutateTarget.className = "multiselect--disabled multiselect-box";
                      mutateButton.innerText = "+";
                      mutateButton.type = "button";
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
            putUpdate: function (update, alertDiv) {
              //Sends the put request to the server to update the user profile
                server.put('profiles/'.concat(this.profileId), update,
                  {headers: {'Content-Type': 'application/json'},
                    withCredentials: true}).then(() => {
                    alertDiv.classList.add("alert-success");
                    alertDiv.classList.remove("alert-danger");
                    this.message = "Successfully updated field";
                    this.code = '';
                }).catch(error => {
                  console.log(error);
                    alertDiv.classList.remove("alert-success");
                    alertDiv.classList.add("alert-danger");
                    this.message = error.statusText;
                    this.code = error.code;
                });
                alertDiv.removeAttribute("hidden");
                setTimeout(function () {
                    alertDiv.hidden = true;
                }, 3000);
            },

            updateInputs: function () {
              //Updates the input fields to contain the info stored in the database
              server.get('/profiles', {headers:
              {'Content-Type': 'application/json'}, withCredentials: true
              }, ).then(response => {
                this.profileId = response.data.id;
                this.firstname = response.data.firstname;
                this.middlename = response.data.middlename;
                this.lastname = response.data.lastname;
                this.nickname = response.data.nickname;
                this.gender = response.data.gender;
                this.dob = response.data.dob;
                this.fitnessLevel = response.data.fitnessLevel;
                this.passports = response.data.passports;
                this.bio = response.data.bio;
                this.loggedIn = true;
              }).catch(error => {
                const alertDiv = document.getElementById("alert");
                this.message = error + ". You will be redirected to the home page shortly";
                this.code = error.statusCode;
                alertDiv.classList.remove("alert-success");
                alertDiv.classList.add("alert-danger");
                alertDiv.removeAttribute("hidden");
                setTimeout(function () {
                  router.push("/");
                }, 5000);
              });
            }
        }
    }
</script>
<style>
  .multiselect {
    min-width: 85%;
  }
</style>
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
    max-height: 38px;
  }

  .alert {
    top: 60px;
  }

  .multiselect-box {
    max-width: 85%;
    margin-right: 1%;
    max-height: 38px;
  }
</style>