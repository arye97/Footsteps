<template>
  <div class="settings-page">
    <Sidebar />
    <div class="container-fluid">
      <form>
        <div class="form-group">
          <!-- full-name field-->
          <label for="first-name">First Name: *</label>
          <div class="edit-area">
            <input type="text" class="form-control" v-model="firstname" id="first-name" name="first-name" placeholder="Your First Name..." required disabled>
            <button class="btn btn-primary" id="first-name-btn" v-on:click="mutate" type="button">+</button>
          </div>
        </div>
        <div class="form-group">
          <!-- full-name field-->
          <label for="middle-name">Middle Name: </label>
          <div class="edit-area">
          <input type="text" class="form-control" v-model="middlename" id="middle-name" name="middle-name" placeholder="Your Middle Name..." disabled>
            <button class="btn btn-primary" id="middle-name-btn" v-on:click="mutate" type="button">+</button>
          </div>
        </div>
        <div class="form-group">
          <!-- full-name field-->
          <label for="last-name">Last Name: *</label>
          <div class="edit-area">
          <input type="text" class="form-control" v-model="lastname" id="last-name" name="last-name" placeholder="Your Last Name..." required disabled>
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
            <option value="2">Not overly fit, occasional recreational fitness activity, active a few times a month</option>
            <option value="3">Moderately fit, enjoys fitness activities for recreation, active once or twice a week</option>
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
          <input type="text" class="form-control" v-model="nickname" id="nickname" name="nickname" placeholder="Your Nickname..." disabled>
          <button class="btn btn-primary" id="nickname-btn" v-on:click="mutate" type="button">+</button>
          </div>
        </div>
        <div class="form-group">
          <!-- gender field -->
          <label for="gender">Gender: *</label>
          <div class="edit-area">
          <select class="form-control" v-model="gender" id="gender" name="gender" required disabled>
            <option value="" disabled selected hidden>Your Gender... </option>
            <option value="male">Male</option>
            <option value="female">Female</option>
            <option value="non-binary">Non Binary</option>
          </select>
          <button class="btn btn-primary" id="gender-btn" v-on:click="mutate" type="button">+</button>
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
          <label for="passportCountry">Passport Country:</label>
          <div class="edit-area">
          <select class="form-control" v-model="passportCountry" id="passportCountry" name="passportCountry" disabled>
            <option value="" disabled selected hidden>Select country</option>
          </select>
          <button class="btn btn-primary" id="passportCountry-btn" v-on:click="mutate" type="button">+</button>
          </div>
        </div>
        <div class="form-group">
          <!-- user bio -->
          <label for="bio">Tell us about yourself, your Bio: </label>
          <div class="edit-area">
          <textarea name="bio" class="form-control" id="bio" v-model="bio" cols="30" rows="1" placeholder="Who are you?" disabled></textarea>
          <button class="btn btn-primary" id="bio-btn" v-on:click="mutate" type="button">+</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
  import Sidebar from "../../components/layout/ProfileEditSidebar"
    export default {
        name: "Details.vue",
        components: {
            Sidebar
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
        passportCountry: '',
        bio: ''
      }
    },
      methods: {
          mutate: function (event) {
            let mutateButton = event.target.id;
            let mutateTarget = event.target.id.replace("-btn", "");
            if (event.target.type === "submit") {
              document.getElementById(mutateTarget).setAttribute('disabled', "true");
              document.getElementById(mutateButton).innerText = "+";
              document.getElementById(mutateButton).type = "button";
            } else {
              document.getElementById(mutateTarget).removeAttribute('disabled');
              document.getElementById(mutateButton).innerText = "Save";
              document.getElementById(mutateButton).type = "submit";
            }
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
}
</style>