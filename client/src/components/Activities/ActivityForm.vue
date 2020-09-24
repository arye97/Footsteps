<template>

    <div>
        <header class="masthead">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
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
                            v-model="activity.activityName"
                            trim
                            v-on:input="updateNameWordCount"
                            placeholder="Your Activity Name..."
                    ></b-form-input>
                    <div class="word-count">
                        {{nameCharCount}}/{{maxNameCharCount}} characters left
                    </div>
                </b-form-group>
                <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_activity_name">
                    Field is mandatory and can only contain a maximum of {{maxNameCharCount}} characters
                </div>

                <b-form-group
                        id="input-group-2"
                        label="Description: *"
                        label-for="input-2"
                >
                    <b-form-textarea
                            id="input-2"
                            v-model="activity.description"
                            rows="5"
                            trim
                            v-on:input="updateDescriptionWordCount"
                            placeholder="Description of your activity..."
                    ></b-form-textarea>
                    <div class="word-count">
                        {{descriptionCharCount}}/{{maxDescriptionCharCount}} characters left
                    </div>
                </b-form-group>
                <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_description">
                     Field is mandatory and can only contain a maximum of {{maxDescriptionCharCount}} characters
                </div>

                <b-form-group
                        id="input-group-3"
                        label="Activity Types: *"
                        label-for="input-3"
                >
                    <multiselect v-model="activity.selectedActivityTypes" id="input-3"
                                 :options="activityTypes" :multiple="true" :searchable="true" :close-on-select="false"
                                 placeholder="Select your activity types">
                        <template slot="noResult">Invalid activity type</template>
                    </multiselect>
                </b-form-group>
                <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_activity_types">
                    {{"Field is mandatory and must have at least one activity type"}}
                </div>

                <b-form-group
                        id="input-group-4"
                        label="Activity Length: *"
                        description="Continuous has no beginning and end date"
                >
                    <b-form-checkbox v-model="activity.continuous" name="check-button" switch>
                        <div v-if="activity.continuous">
                            Continuous
                        </div>
                        <div v-else>
                            Duration
                        </div>
                    </b-form-checkbox>
                </b-form-group>


                <div v-if="activity.continuous === false">
                    <b-form-group>
                        <label id="input-start-label">Start Date: *</label>
                        <b-form-datepicker v-model="activity.startDate" class="mb-2"></b-form-datepicker>
                        <b-form-timepicker v-model="activity.startTime" :disabled="!has_start_time" locale="en"></b-form-timepicker>
                        <b-form-checkbox
                                class="checkbox-time"
                                size="sm"
                                v-model="has_start_time"
                                @change="this.disabledStart"
                        >
                            Include starting time
                        </b-form-checkbox>
                    </b-form-group>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_start_valid">
                        {{"Please enter a valid date"}}
                    </div>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_start">
                        {{"Field is mandatory, a start date must be set with (optionally) a start time"}}
                    </div>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_start_after_end">
                        {{"A start date cannot be set after the end date"}}
                    </div>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_start_before_epoch_date">
                        {{"A date cannot be set before 1st Jan, 1970"}}
                    </div>

                    <b-form-group>
                        <label id="input-end-label">End Date: *</label>
                        <b-form-datepicker v-model="activity.endDate" class="mb-2"></b-form-datepicker>
                        <b-form-timepicker v-model="activity.endTime" :disabled="!has_end_time" locale="en"></b-form-timepicker>
                        <b-form-checkbox
                                class="checkbox-time"
                                size="sm"
                                v-model="has_end_time"
                                @change="this.disabledEnd"
                        >
                            Include ending time
                        </b-form-checkbox>
                    </b-form-group>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_end_valid">
                        {{"Please enter a valid date"}}
                    </div>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_end">
                        {{"Field is mandatory, an end date must be set with (optionally) an end time"}}
                    </div>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_end_before_start">
                        {{"An end date cannot be set before the start date"}}
                    </div>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_end_before_epoch_date">
                       {{"A date cannot be set before 1st Jan, 1970"}}
                    </div>
                </div>

                <b-form-group
                        id="input-group-location"
                        label="Location: *"
                        label-for="input-location"
                >
                    <!-- This gets around an issue where vue will throw a warning that location has not been defined -->
                    <!--Without checking that activity.profileId is defined, there will be a race condition, the -->
                    <!--activity data won't have been loaded, and the LocationIO won't receive the location-->
                    <location-i-o v-if="!this.isEdit || this.activity.profileId"
                        id="input-location"
                        @pin-change="locationValue"
                        @invalid-location="invalidLocation"
                        :single-only="true"
                        :canDelete="true"
                        :parent-pins="this.isEdit && this.activity.location ?
                            [{lat: this.activity.location.latitude, lng: this.activity.location.longitude}] :  undefined"

                        :parent-center="this.isEdit && this.activity.location ?
                            {lat: this.activity.location.latitude, lng: this.activity.location.longitude} : undefined"

                        :parent-address="this.isEdit && this.activity.location ?
                            this.activity.location.name : undefined"
                    ></location-i-o>

                </b-form-group>
                <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_location">
                    Field is mandatory and a location must be set
                </div>
                <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="unapproved_location">
                    Please select a location from the autocomplete suggestions
                </div>


                <hr>


                <label>Outcomes: </label>
                <b-row>
                    <b-col>
                        <b-form-group
                                id="input-group-outcome-title"
                        >
                            <b-form-input
                                    id="input-outcome-title"
                                    v-model="activeOutcome.title"
                                    placeholder="Title of your outcome..."
                                    trim
                                    v-on:input="updateOutcomeWordCount"
                            ></b-form-input>
                            <div class="word-count" id="title-word-count">
                                {{outcomeTitleCharCount}}/{{maxOutcomeTitleCharCount}} characters left
                            </div>
                            <b-form-input
                                    id="input-outcome-unit-name"
                                    v-model="activeOutcome.unit_name"
                                    placeholder="Unit of your outcome..."
                                    trim
                                    v-on:input="updateOutcomeWordCount"
                            ></b-form-input>
                            <div class="word-count" id="unit-name-word-count">
                                {{outcomeUnitCharCount}}/{{maxOutcomeUnitCharCount}} characters left
                            </div>
                        </b-form-group>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col align-self="center">
                        <div v-if="errored" class="alert alert-danger alert-dismissible fade show sticky-top" role="alert" id="alert">
                            {{  error_message  }}
                        </div>
                        <b-button
                                v-else
                                v-bind:disabled="!validOutcome"
                                variant="primary"
                                id="addOutcome"
                                v-on:click="addOutcome"
                                class="addOutcomesButton"
                        >
                            Add outcome
                        </b-button>

                    </b-col>
                </b-row>

                <section class="outcomesDisplay">
                    <table id="outcomesTable" class="table table-hover">
                        <tr class="outcomesTable" v-for="(outcome, index) in this.outcomeList"
                            v-bind:key="'outcome' + index"
                            :id="'outcome' + index">
                            <td>
                                <p :id="'title' + index">
                                    {{ outcome.title }}
                                </p>
                            </td>
                            <td>
                                <p :id="'unit_name' + index">
                                    {{ outcome.unit_name }}
                                </p>
                            </td>
                            <!--Only show edit button if this Outcome does not have results-->
                            <td class="tableButtonTd" v-if="editableOutcomes[index]">
                                <b-button variant="danger" :id="'deleteButton' + index" v-on:click="deleteOutcome(index)">
                                    <b-icon-trash-fill></b-icon-trash-fill>
                                </b-button>
                            </td>
                            <td class="tableButtonTd" v-if="editableOutcomes[index]">
                                <b-button variant="primary" :id="'editButton' + index" v-on:click="editOutcome(index)">Edit</b-button>
                            </td>
                        </tr>
                    </table>
                </section>

                <div class="alert alert-danger alert-dismissible fade show sticky-top" role="alert" id="overall_message" hidden>
                    <p id="alert-message">{{ overallMessageText }}</p>
                </div>
                <div>
                    <b-button variant="secondary" v-on:click="$router.back()">Back</b-button>
                    <b-button class="float-right" type="submit" variant="primary" @submit="onSubmit">Submit</b-button>
                </div>
            </b-form>
            <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="any_alert">
                {{"This activity cannot be saved as there is an invalid field"}}
            </div>
            <footer class="col-12 text-center">
                Entries marked with * are required
            </footer><br/><br/>
        </div>
    </div>
</template>

<script>
    import Multiselect from 'vue-multiselect'
    import api from "../../Api";
    import {localTimeZoneToBackEndTime, pinToLocation} from "../../util";
    import LocationIO from "../Map/LocationIO";


    /**
     * Displays an error on the element with id equal to alert_name
     */
    function showError(alert_name) {
        let errorAlert = document.getElementById(alert_name);
        let anyAlert = document.getElementById("any_alert");
        errorAlert.hidden = false;          //Show alert bar
        anyAlert.hidden = false;
        setTimeout(function () {    //Hide alert bar after ~9000ms
            errorAlert.hidden = true;
        }, 9000);
        setTimeout(function () {    //Hide alert bar after ~4500ms
            anyAlert.hidden = true;
        }, 4500);

    }

    /**
     * This component is a form for activity fields with a back button and submit button.  Only performs validation
     * on the form data.  Takes a function in props that is called when the submit button is pressed. by Used by
     * CreateActivity and EditActivity at the time of writing.
     */
    export default {
        components: {LocationIO, Multiselect},
        name: "ActivityForm",
        props: {
            activity: {
                profileId: Number,
                activityName: String,
                description: String,
                selectedActivityTypes: Array,
                continuous: Boolean,
                submitStartTime: String,
                submitEndTime: String,
                location: Object,
                startDate: String,
                startTime: String,
                endDate: String,
                endTime: String,
            },
            outcomeList: {
                default() {
                    return [];
                },
                type: Array
            },
            originalOutcomeList: {
                default() {
                    return [...this.outcomeList];
                },
                type: Array
            },
            submitActivityFunc: Function,
            isEdit:  Boolean,
        },
        data() {
            return {
                inputStartTime: null,
                inputEndTime: null,

                activityTypes: [],
                nameCharCount: 0,
                maxNameCharCount: 75,
                descriptionCharCount: 0,
                maxDescriptionCharCount: 1500,
                defaultTime: "12:00",

                has_start_time: true,
                has_end_time: true,

                isValidFormFlag: true,
                overallMessageText: "",

                activeOutcome: {title:"", unit_name:""},
                validOutcome: false,
                editableOutcomes: [],

                outcomeTitleCharCount: 0,
                maxOutcomeTitleCharCount: 75,
                outcomeUnitCharCount: 0,
                maxOutcomeUnitCharCount: 15,
                errored: false,
                error_message: "Something went wrong",
                submitDisabled: false,
            }
        },
        async created() {
            await this.fetchActivityTypes();
        },
        methods: {
            /**
             * Helper function when checking/unchecking enable start time tick box
             * @param checked status of tick box
             */
            disabledStart(checked) {
                if (!checked) {
                    this.inputStartTime = this.activity.startTime;
                    this.activity.startTime = null;
                } else {
                    if (this.inputStartTime === null) {
                        this.inputStartTime = this.defaultTime;
                    }
                    this.activity.startTime = this.inputStartTime;
                }
            },

            /**
             * Helper function when checking/unchecking enable end time tick box
             * @param checked status of tick box
             */
            disabledEnd(checked) {
                if (!checked) {
                    this.inputEndTime = this.activity.endTime;
                    this.activity.endTime = null;
                } else {
                    if (this.inputEndTime === null) {
                        this.inputEndTime = this.defaultTime;
                    }
                    this.activity.endTime = this.inputEndTime
                }
            },

            /**
             * Called when submit button is pressed.  Validates the form input, then calls the function passed in
             * through props.  This way the form can be used to edit and create activities.
             */
            async onSubmit(evt) {
                evt.preventDefault();

                if (this.submitDisabled) {
                    return;
                }

                this.isValidFormFlag = true;
                await this.validateActivityInputs();
                if (this.isValidFormFlag) {
                    this.formatDurationActivity();
                    try {
                        await this.submitActivityFunc();
                    } catch (errResponse) {
                        this.processPostError(errResponse);
                    }
                }
            },

            /**
             * Handles errors thrown when trying to create an activity
             * status code 401: logs user out
             * status code 403: sends user back to activity list screen and shows them a error message there
             * status code 404: sends user back to activity list screen and shows them a error message there
             * any other errors: sends user back to activity list screen and shows them a error message there
             */
            processPostError(errResponse) {
                if (errResponse.status === 401) {
                    this.logout();
                } else if (errResponse.status === 403) {
                    this.$router.push({name: 'allActivities', params:
                            {alertMessage: "Sorry, you do not have permission to edit or create another user's activity", alertCount: 5}});
                } else if (errResponse.status === 404) {
                    this.$router.push({
                        name: 'allActivities', params:
                            {alertMessage: "Sorry, we couldn't find this activity", alertCount: 5}
                    });
                } else if ('message' in errResponse) {  // If the error is thrown from throwError
                    this.$router.push({name: 'allActivities', params:
                            {alertMessage: errResponse.message, alertCount: 5}});
                } else {
                    this.$router.push({name: 'allActivities', params:
                            {alertMessage: "Sorry, an unknown error occurred while creating this activity", alertCount: 5}});
                }
            },

            /**
             * Logs the user out and clears session token
             */
            logout () {
                api.logout();
                sessionStorage.clear();
                this.isLoggedIn = (sessionStorage.getItem("token") !== null);
                this.$forceUpdate();
                this.$router.push('/login');
            },

            /**
             * Validate activity inputs, called when onSubmit is called
             */
            async validateActivityInputs() {
                if (!this.has_start_time) {
                    this.activity.submitStartTime = this.activity.startDate + "T" + this.defaultTime;
                } else {
                    this.activity.submitStartTime = this.activity.startDate + "T" + this.activity.startTime.substr(0,5);
                }
                let startTime = new Date(this.activity.submitStartTime);

                if (!this.has_end_time) {
                    this.activity.submitEndTime = this.activity.endDate + "T" + this.defaultTime;
                } else {
                    this.activity.submitEndTime = this.activity.endDate + "T" + this.activity.endTime.substr(0,5);
                }
                let endTime = new Date(this.activity.submitEndTime);

                if (!this.activity.activityName || this.nameCharCount > this.maxNameCharCount) {
                    showError('alert_activity_name');
                    this.isValidFormFlag = false;
                }

                if (!this.activity.description || this.descriptionCharCount > this.maxDescriptionCharCount) {
                    showError('alert_description');
                    this.isValidFormFlag = false;
                }

                if (this.activity.selectedActivityTypes.length < 1) {
                    showError('alert_activity_types');
                    this.isValidFormFlag = false;
                }

                // If duration is chosen
                if (!this.activity.continuous) {
                    if (isNaN(startTime.getTime())) {
                        showError('alert_start_valid');
                        this.isValidFormFlag = false;
                    }
                    else if (!this.activity.submitStartTime) {
                        showError('alert_start');
                        this.isValidFormFlag = false;
                    }
                    else if (startTime > endTime) {
                        showError('alert_start_after_end');
                        this.isValidFormFlag = false;
                    }
                    else if (startTime < new Date(0)) {
                        showError('alert_start_before_epoch_date');
                        this.isValidFormFlag = false;
                    }

                    if (isNaN(endTime.getTime())) {
                        showError('alert_end_valid');
                        this.isValidFormFlag = false;
                    }
                    else if (!this.activity.submitEndTime) {
                        showError('alert_end');
                        this.isValidFormFlag = false;
                    }
                    else if (endTime < startTime) {
                        showError('alert_end_before_start');
                        this.isValidFormFlag = false;
                    }
                    else if (endTime < new Date(0)) {
                        showError('alert_end_before_epoch_date');
                        this.isValidFormFlag = false;
                    }
                }

                if (!this.activity.location ||
                    (this.activity.location && !["latitude", "longitude", "name"].every(key => key in this.activity.location))) {
                    showError('alert_location');
                    this.isValidFormFlag = false;
                }
            },

            /**
             * Format the dates correctly for the Backend.
             */
            formatDurationActivity() {
                if (this.activity.submitStartTime && this.activity.submitStartTime.length === 10) {
                    this.activity.submitStartTime = this.activity.submitStartTime.concat('T00:01')
                }
                if (this.activity.submitEndTime && this.activity.submitEndTime.length === 10) {
                    this.activity.submitEndTime = this.activity.submitEndTime.concat('T00:01')
                }

                // These are used if a time is set and then the Include starting/ending time box is un-ticked
                if (!this.has_start_time) {
                    this.activity.submitStartTime = this.activity.submitStartTime.split('T')[0] + 'T12:00';
                }
                if (!this.has_end_time) {
                    this.activity.submitEndTime = this.activity.submitEndTime.split('T')[0] + 'T12:00';
                }

                this.activity.submitStartTime = localTimeZoneToBackEndTime(this.activity.submitStartTime);
                this.activity.submitEndTime = localTimeZoneToBackEndTime(this.activity.submitEndTime);
            },

            /**
             * Updates word count for activity name
             */
            updateNameWordCount() {
                this.nameCharCount = this.activity.activityName.length;
            },

            /**
             * Updates word count for description
             */
            updateDescriptionWordCount() {
                this.descriptionCharCount = this.activity.description.length;
            },

            /**
             * Updates word count for outcome title
             */
            updateOutcomeWordCount() {
                this.errored = false;
                this.outcomeTitleCharCount = this.activeOutcome.title.length;
                this.outcomeUnitCharCount = this.activeOutcome.unit_name.length;
                this.validOutcome = this.outcomeTitleCharCount > 0 && this.outcomeUnitCharCount > 0
                                    && this.outcomeTitleCharCount <= this.maxOutcomeTitleCharCount
                                    && this.outcomeUnitCharCount <= this.maxOutcomeUnitCharCount;
                if (this.validOutcome) {
                    for (let i = 0; i < this.outcomeList.length; i++) {
                        if (this.activeOutcome.title === this.outcomeList[i].title) {
                            this.validOutcome = false;
                            this.errored = true;
                            this.error_message = "That outcome title already exists!";
                            return;
                        }
                    }
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

            /**
             * Adds the current outcome to the outcomeList through parent component
             * to prevent prop mutation.
             * Clears the outcome input fields.
             * (current outcome is the outcome in the input boxes)
             */
            addOutcome() {
                this.$emit("add-outcome", this.activeOutcome);
                this.editableOutcomes.push(true);
                this.activeOutcome = {title:"", unit_name:""};
                this.updateOutcomeWordCount();
            },

            /**
             * Removes a specified outcome from the list of outcomes through parent component
             * to prevent prop mutation.
             * (Active outcome is not part of this list)
             * @param index The index of the outcome, to be deleted, in the outcomeList
             */
            deleteOutcome(index) {
                let outcomeToBeRemoved = this.outcomeList[index];
                this.$emit("delete-outcome", outcomeToBeRemoved);
                this.editableOutcomes.splice(index, 1);
            },

            /**
             * Sets the active outcome to the selected outcome
             * Deletes the to be edited outcome from the outcomeList
             * Updates the outcome input boxes and their respective word counts
             * Sets isEdited to true for when submitting the final outcome list
             * @param index The index of the outcome, to be edited, in the outcomeList
             */
            editOutcome(index) {
                this.activeOutcome = this.outcomeList[index];
                this.activeOutcome.isEdited = true;
                this.deleteOutcome(index);
                this.editableOutcomes.splice(index, 1);
                // The prop outcomeList takes time to update within this child.
                // Therefore, the method updateOutcomeWordCount can't be called as it invalids the outcome each time.
                // Hence, the three lines below are duplicated code from that method.
                this.outcomeTitleCharCount = this.activeOutcome.title.length;
                this.outcomeUnitCharCount = this.activeOutcome.unit_name.length;
                this.validOutcome = true;
            },

            /**
             * Add outcomes which can be edited to the editableOutcomes list
             */
            async checkOutcomesAreEditable() {
                this.editableOutcomes = [];
                for (let i = 0 ; i < this.outcomeList.length ; i++) {
                    this.editableOutcomes.push(await this.outcomeIsEditable(i));
                }
            },

            /**
             * Checks whether or not the outcome at the given index can be edited.
             * @param index The index of the outcome being checked
             * @return true if outcome is editable, otherwise false
             */
            async outcomeIsEditable(index) {
                let result = false;
                let outcome = this.outcomeList[index];
                if (outcome.outcome_id === undefined) {
                    return true;
                }
                await api.getOutcomeResults(outcome.outcome_id).then(response => {
                    if (response.data.length <= 0) {
                        result = true;
                    }
                }).catch(error => {
                    if (error.response.status === 401) {
                        this.logout();
                    } else {
                        this.$router.push({name: 'allActivities', params:
                                {alertMessage: "Sorry, an unknown error occurred while loading the outcomes", alertCount: 5}});
                    }
                });
                return result;
            },
          /**
           * Sets the location of the activity.  Converts a pin to a location object used by the backend.
           * Called when the pins in the LocationIO child component are changed
           * @param pin Object from LocationIO
           */
            locationValue: function (pin) {
                if (pin.name !== "" || pin.name !== null) {
                    let errorAlert = document.getElementById("unapproved_location");
                    errorAlert.hidden = true;
                    this.activity.location = pinToLocation(pin);
                }

            },
            /**
             * Toggles the submitDisabled boolean
             * @param inFocus true if the user is using the gmap-autocomplete field, false otherwise
             */
            setLocationFocus(inFocus) {
                this.submitDisabled = inFocus;
            },

            /**
             * This method is called if an invalid location warning is emitted from LocationIO
             * This displays a warning to the user so they are aware that it is invalid
             */
            invalidLocation() {
                this.activity.location = null;
                showError("unapproved_location");
            }
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

    .tableButtonTd {
        float: right;
    }

    .outcomesTable p {
        margin-top: 5px;
        margin-bottom: 5px;
    }

    .addOutcomesButton {
        margin-bottom: 5px;
    }
</style>