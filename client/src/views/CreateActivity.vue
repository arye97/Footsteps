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
                            placeholder="Your Activity Name..."
                    ></b-form-input>
                </b-form-group>
                <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_activity_name">
                    Field is mandatory
                </div>

                <b-form-group
                        id="input-group-2"
                        label="Description: *"
                        label-for="input-2"
                >
                    <b-form-textarea
                            id="input-2"
                            rows="5"
                            trim
                            v-on:input="updateWordCount"
                            v-model="description"
                            placeholder="Description of your activity..."
                    ></b-form-textarea>
                    <div id="word-count">
                        {{ charCount }}/{{ maxCharCount }} characters remaining
                    </div>
                </b-form-group>
                <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_description">
                    Field is mandatory and can only contain a maximum of {{ maxCharCount }} characters
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
                        <label id="input-start-time=label" for="input-start-time">Start Time: *</label>
                        <input type="datetime-local" class="form-control" v-model="startTime" id="input-start-time">
                    </b-form-group>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_start">
                        Field is mandatory, a start date must be set with (optionally) a start time.
                    </div>
                </div>

                <div v-if="continuous === false">
                    <b-form-group>
                        <label id="input-end-time=label" for="input-end-time">End Time: *</label>
                        <input type="datetime-local" class="form-control" v-model="endTime" id="input-end-time">
                    </b-form-group>
                    <div class="alert alert-danger alert-dismissible fade show" hidden role="alert" id="alert_end">
                        Field is mandatory, an end date must be set with (optionally) an end time.
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

                charCount: 0,
                maxCharCount: 280
            }
        },
        mounted() {
            if (!sessionStorage.getItem("token")) {
                this.$router.push('/login'); //Routes to home on logout
            }
            this.fetchActivityTypes();
        },
        methods: {
            onSubmit(evt) {
                evt.preventDefault();
                this.validateActivityInputs();
            },

            validateActivityInputs() {
                if (!this.activityName) {
                    showError('alert_activity_name');
                }

                if (!this.description || this.description.length > this.maxCharCount) {
                    showError('alert_description');
                }

                if (this.selectedActivityTypes.length < 1) {
                    showError('alert_activity_types');
                }

                if (!this.continuous) {
                    if (!this.startTime) {
                        console.log(this.startTime);
                        showError('alert_start');
                    } // penus
                    if (!this.endTime) {
                        console.log(this.endTime);
                        showError('alert_end');
                    } else if (this.endTime > this.startTime) {

                    }
                }
            },

            updateWordCount() {
                console.log(this.description);
                this.charCount = this.description.length;
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

    #word-count {
        padding-top: 7px;
        color: #707070;
        font-size: 0.8em;
    }
</style>