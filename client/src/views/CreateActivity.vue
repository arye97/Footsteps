<template>
    <div>
        <Header />
        <h1><br/><br/></h1>
        <header class="masthead">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light">Welcome to Hakinakina</h1>
                        <p class="lead">Plan your route with the best</p>
                        <h1 class="font-weight-light">
                            Create an activity<br/>
                        </h1>
                        Entries marked with * are required<br/><br/>
                    </div>
                </div>
            </div>
        </header>
        <div>
            <b-form @submit="onSubmit">
                <b-form-group
                        id="input-group-1"
                        label="Name of activity: *"
                        label-for="input-1"
                >
                    <b-form-input
                            id="input-1"
                            v-model="activityName"
                            trim
                            v-on:input="updateNameWordCount"
                            placeholder="Your Activity Name..."
                    ></b-form-input>
                    <div class="word-count">
                        {{ nameCharCount }}/{{ maxNameCharCount }} characters remaining
                    </div>
                </b-form-group>
                <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_activity_name">
                    Field is mandatory and can only contain a maximum of {{ maxNameCharCount }} characters
                </div>

                <b-form-group
                        id="input-group-2"
                        label="Description: *"
                        label-for="input-2"
                >
                    <b-form-textarea
                            id="input-2"
                            v-model="description"
                            rows="5"
                            trim
                            v-on:input="updateDescriptionWordCount"
                            placeholder="Description of your activity..."
                    ></b-form-textarea>
                    <div class="word-count">
                        {{ descriptionCharCount }}/{{ maxDescriptionCharCount }} characters remaining
                    </div>
                </b-form-group>
                <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_description">
                    Field is mandatory and can only contain a maximum of {{ maxDescriptionCharCount }} characters
                </div>

                <b-form-group
                        id="input-group-3"
                        label="Activity Types: *"
                        label-for="input-3"
                >
                    <multiselect v-model="selectedActivityTypes" id="input-3"
                                 :options="activityTypes" :multiple="true" :searchable="true" :close-on-select="false"
                                 placeholder="Select your activity types">
                        <template slot="noResult">Invalid activity type</template>
                    </multiselect>
                </b-form-group>
                <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_activity_types">
                    Field is mandatory and must have at least one activity type
                </div>

                <b-form-group
                        id="input-group-4"
                        label="Activity Length: *"
                        description="Continuous has no beginning and end date"
                >
                    <b-form-checkbox v-model="continuous" name="check-button" switch>
                        <div v-if="continuous">
                            Continuous
                        </div>
                        <div v-else>
                            Duration
                        </div>
                    </b-form-checkbox>
                </b-form-group>


                <div v-if="continuous === false">
                    <b-form-group>
                        <div v-if="has_start_time === false" >
                            <label id="input-start-label" for="input-start">Start Date: *</label>
                            <input type="date" class="form-control" v-model="startTime" id="input-start">
                        </div>
                        <div v-else>
                            <label id="input-start-time-label" for="input-start-time">Start Date: *</label>
                            <input type="datetime-local" class="form-control" v-model="startTime" id="input-start-time">
                        </div>
                        <b-form-checkbox
                                class="checkbox-time"
                                size="sm"
                                v-model="has_start_time"
                                @change="resetStartDate"
                        >
                            Include starting time
                        </b-form-checkbox>
                    </b-form-group>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_start">
                        Field is mandatory, a start date must be set with (optionally) a start time
                    </div>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_start_after_end">
                        A start date cannot be set after the end date
                    </div>

                    <b-form-group>
                        <div v-if="has_end_time === false" >
                            <label id="input-end-label" for="input-end">End Date: *</label>
                            <input type="date" class="form-control" v-model="endTime" id="input-end">
                        </div>
                        <div v-else>
                            <label id="input-end-time-label" for="input-end-time">End Date: *</label>
                            <input type="datetime-local" class="form-control" v-model="endTime" id="input-end-time">
                        </div>
                        <b-form-checkbox
                                class="checkbox-time"
                                size="sm"
                                v-model="has_end_time"
                                @change="resetEndDate"
                        >
                            Include ending time
                        </b-form-checkbox>
                    </b-form-group>

                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_end">
                        Field is mandatory, an end date must be set with (optionally) an end time
                    </div>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_end_before_start">
                        An end date cannot be set before the start date
                    </div>
                </div>






                <b-form-group
                        id="input-group-location"
                        label="Location: *"
                        label-for="input-location"
                >
                    <b-form-input
                            id="input-location"
                            v-model="location"
                            placeholder="Location of your activity..."
                    ></b-form-input>
                </b-form-group>
                <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_location">
                    Field is mandatory and a location must be set
                </div>
                <div>
                    <b-button>Back</b-button>
                    <b-button class="float-right" type="submit" variant="primary" @submit="onSubmit">Submit</b-button>
                </div>
            </b-form>
            <footer class="col-12 text-center">
                Entries marked with * are required
            </footer><br/><br/>
        </div>
    </div>
</template>

<script>
    import Multiselect from 'vue-multiselect'
    import Header from '../components/Header/Header.vue'
    import server from "../Api";

    function showError(alert_name) {
        let errorAlert = document.getElementById(alert_name);

        errorAlert.hidden = false;          //Show alert bar
        setTimeout(function () {    //Hide alert bar after ~9000ms
            errorAlert.hidden = true;
        }, 9000);
    }

    export default {
        components: { Header, Multiselect },
        name: "CreateActivity",
        data() {
            return {
                profileId: null,
                activityName: null,
                description: null,
                activityTypes: [],
                selectedActivityTypes: [],
                continuous: true,
                startTime: null,
                endTime: null,
                location: null,

                nameCharCount: 0,
                maxNameCharCount: 75,
                descriptionCharCount: 0,
                maxDescriptionCharCount: 1500,

                has_start_time: false,
                has_end_time: false,

                isValidFormFlag: true,
            }
        },
        mounted() {
            if (!sessionStorage.getItem("token")) {
                this.$router.push('/login'); //Routes to home on logout
            }
            this.fetchActivityTypes();
        },
        methods: {
            /**
             * Called when submit button is pressed
             */
            async onSubmit(evt) {
                evt.preventDefault();
                this.isValidFormFlag = true;
                await this.validateActivityInputs()
                if (this.isValidFormFlag) {
                    await this.submitActivity();
                }
            },

            /**
             * Makes a POST request to the back-end to create an activity
             */
            async submitActivity() {
                const activityForm = {
                    activity_name: this.activityName,
                    description: this.description,
                    activity_type: this.selectedActivityTypes,
                    continuous: this.continuous,
                    location: this.location
                };
                if (!this.continuous) {
                    // If no time provided, manually concatenating Thh:mm, which is bad, might use Moment.js instead but will consult team
                    let formattedStartTime = this.startTime;
                    let formattedEndTime = this.endTime;
                    if (this.startTime.length === 10) {
                        formattedStartTime = formattedStartTime.concat('T23:59')
                    }
                    if (this.endTime.length === 10) {
                        formattedEndTime = formattedEndTime.concat('T23:59')
                    }
                    activityForm["start_time"] = formattedStartTime.concat(':00+1300');
                    activityForm["end_time"] = formattedEndTime.concat(':00+1300');
                }

                // Hardcoded id since have no way of obtaining it atm
                // Also does not work if continuous is chosen, because back-end needs tinkering
                await server.post(`/profiles/56/activities`,
                    activityForm, {
                        headers: {"Access-Control-Allow-Origin": "*", "Content-Type": "application/json"},
                        withCredentials: true
                    }).then(response => { //If successfully registered the response will have a status of 201
                        if (response.status === 201) {
                            console.log('Activity Created Successfully!');
                            sessionStorage.setItem("token", response.data.Token);
                            // somehow can't get back to profile
                            // this.$router.push('/profile');
                        }
                    }

                )
            },

            /**
             * Validate activity inputs, called when onSubmit is called
             */
            async validateActivityInputs() {
                if (!this.activityName || this.nameCharCount > this.maxNameCharCount) {
                    showError('alert_activity_name');
                    this.isValidFormFlag = false;
                }

                if (!this.description || this.descriptionCharCount > this.maxDescriptionCharCount) {
                    showError('alert_description');
                    this.isValidFormFlag = false;
                }

                if (this.selectedActivityTypes.length < 1) {
                    showError('alert_activity_types');
                    this.isValidFormFlag = false;
                }

                // If duration is chosen
                if (!this.continuous) {
                    if (!this.startTime) {
                        showError('alert_start');
                        this.isValidFormFlag = false;
                    }
                    else if (this.startTime > this.endTime) {
                        showError('alert_start_after_end');
                        this.isValidFormFlag = false;
                    }
                    if (!this.endTime) {
                        showError('alert_end');
                        this.isValidFormFlag = false;
                    }
                    else if (this.endTime < this.startTime) {
                        showError('alert_end_before_start');
                        this.isValidFormFlag = false;
                    }
                }

                if (!this.location) {
                    showError('alert_location');
                    this.isValidFormFlag = false;
                }
            },

            /**
             * Updates word count for activity name
             */
            updateNameWordCount() {
                this.nameCharCount = this.activityName.length;
            },

            /**
             * Updates word count for description
             */
            updateDescriptionWordCount() {
                this.descriptionCharCount = this.description.length;
            },

            /**
             * Resets start date, called when checkbox is ticked/unticked
             */
            resetStartDate() {
                this.startTime = null
            },

            /**
             * Resets end date, called when checkbox is ticked/unticked
             */
            resetEndDate() {
                this.endTime = null
            },

            /**
             * Fetch all possible activity types from the server
             */
            async fetchActivityTypes() {
                await server.get('activity-types',
                    {headers: {'Content-Type': 'application/json', 'Token': sessionStorage.getItem("token")}
                    }
                ).then(response => {
                    this.activityTypes = response.data.map(activity => activity['name']);
                    this.activityTypes.sort(function (a, b) {
                        return a.toLowerCase().localeCompare(b.toLowerCase());
                    });
                }).catch(error => {
                    this.processGetError(error);
                });
            },
        }
    }

</script>

<style scoped>
    footer {
        padding-top: 55px;
    }

    .word-count {
        padding-top: 7px;
        color: #707070;
        font-size: 0.8em;
    }

    .checkbox-time {
        padding-top: 10px;
    }
</style>