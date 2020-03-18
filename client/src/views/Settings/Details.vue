<template>
  <div class="settings-page">
    <div class="alert alert-success alert-dismissible fade show sticky-top" role="alert" id="alert" hidden>
      <p id="alert-message"><strong>{{ code }}</strong>{{ message }}</p>
      <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
      </button>
    </div>
    <Sidebar/>
    <div class="container-fluid">
      <div class="form-group">
        <!-- first-name field-->
        <label for="first-name">First Name: *</label>
        <div class="edit-area">
          <input type="text" class="form-control" v-model="firstname" id="first-name" name="first-name"
                 placeholder="Your First Name..." required disabled>
          <button class="btn btn-primary" id="first-name-btn" v-on:click="mutate" type="button">+</button>
        </div>
      </div>
      <div class="form-group">
        <!-- middle-name field-->
        <label for="middle-name">Middle Name: </label>
        <div class="edit-area">
          <input type="text" class="form-control" v-model="middlename" id="middle-name" name="middle-name"
                 placeholder="Your Middle Name..." disabled>
          <button class="btn btn-primary" id="middle-name-btn" v-on:click="mutate" type="button">+</button>
        </div>
      </div>
      <div class="form-group">
        <!-- last-name field-->
        <label for="last-name">Last Name: *</label>
        <div class="edit-area">
          <input type="text" class="form-control" v-model="lastname" id="last-name" name="last-name"
                 placeholder="Your Last Name..." required disabled>
          <button class="btn btn-primary" id="last-name-btn" v-on:click="mutate" type="button">+</button>
        </div>
      </div>
      <div class="form-group">
        <!-- fitness level field -->
        <label for="fitnessLevel">Fitness Level:</label>
        <div class="edit-area">
          <select class="form-control" v-model="fitnessLevel" name="fitnessLevel" id="fitnessLevel" disabled>
            <option disabled value="">Please select a fitness level</option>
            <option value="1">Unfit, no regular exercise, being active is very rare</option>
            <option value="2">Not overly fit, occasional recreational fitness activity, active a few times a month
            </option>
            <option value="3">Moderately fit, enjoys fitness activities for recreation, active once or twice a week
            </option>
            <option value="4">Fit, may compete occasionally in small scale events, active most days</option>
            <option value="5">Very fit, competitive athlete, extremely active</option>
          </select>
          <button class="btn btn-primary" id="fitnessLevel-btn" v-on:click="mutate" type="button">+</button>
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
        <!-- date of birth field-->
        <label for="dob">Date of Birth: *</label>
        <div class="edit-area">
          <input type="date" class="form-control" v-model="dob" id="dob" name="dob" required disabled>
          <button class="btn btn-primary" id="dob-btn" v-on:click="mutate" type="button">+</button>
        </div>
      </div>
      <div class="form-group">
        <!-- passport country -->
        <label for="passportCountries">Passport Country:</label>
        <div class="edit-area">
          <div id="passportCountriesDiv" class="multiselect--disabled multiselect-box">
          <multiselect v-model="passportCountries" id="passportCountries"
                       :options="countries" :multiple="true" :searchable="true" :close-on-select="false"
                       placeholder="Select your passport countries">
            <template slot="noResult">Country not found</template>
          </multiselect>
          </div>
          <button class="btn btn-primary" id="passportCountriesDiv-btn" v-on:click="mutate" type="button">+</button>
        </div>
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
</template>

<script>
    import Sidebar from "../../components/layout/ProfileEditSidebar"
    import Multiselect from 'vue-multiselect'
    import server from "../../Api";
    import {getCountryNames} from '../../constants';

    export default {
        name: "Details.vue",
        components: {
            Sidebar, Multiselect
        },
        mounted() {
            let select = []
            // Create a request variable and assign a new XMLHttpRequest object to it.
            let request = new XMLHttpRequest()
            //build url
            let url = new URL(getCountryNames)
            // Open a new connection, using the GET request on the URL endpoint;
            request.open('GET', url, true)

            request.onload = function () {
                // If the request is successful
                if (request.status >= 200 && request.status < 400) {
                    let data = JSON.parse(this.response)
                    data.forEach(country => {
                        let elmt = country.name;
                        select.push(elmt)
                    })
                } else {
                    select = 'List is empty'
                }
            }
            // Send request
            this.countries = select
            request.send()
        },
        data() {
            return {
                firstname: '',
                middlename: '',
                lastname: '',
                nickname: '',
                gender: '',
                dob: '',
                fitnessLevel: '',
                passportCountries: '',
                bio: '',
                message: '',
                code: '',
                countries: [],
                genders: ['Male', 'Female', 'Non-Binary'],
            }
        },
        methods: {
            mutate: function (event) {
                //const alertDiv = document.getElementById("alert");
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
                          //mutateTarget.value;
                          mutateTarget.setAttribute('disabled', "true");
                          mutateButton.innerText = "+";
                          mutateButton.type = "button";
                      }
                    } else {
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
                server.put(update).then(function (response) {
                    alertDiv.addClass("alert-success");
                    alertDiv.removeClass("alert-danger");
                    this.message = response.statusText;
                    this.code = '';
                }).catch(function (response) {
                    alertDiv.remove("alert-success");
                    alertDiv.addClass("alert-danger");
                    this.message = response.statusText;
                    this.code = response.code;
                });
                alertDiv.removeAttribute("hidden");
                setTimeout(function () {
                    alertDiv.hidden = true;
                }, 3000);
            }
        }
    }
</script>
<style>
  .multiselect-box {
    max-width: 85%;
    margin-right: 1%;
    max-height: 38px;
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
</style>