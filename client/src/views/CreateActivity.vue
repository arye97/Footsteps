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
            <b-form @submit="onSubmit" v-if="show">
                <b-form-group
                        id="input-group-1"
                        label="Name of activity: *"
                        label-for="input-1"
                >
                    <b-form-input
                            id="input-1"
                            v-model="activityName"
                            required
                            placeholder="Your Activity Name..."
                    ></b-form-input>
                </b-form-group>

                <b-form-group
                        id="input-group-2"
                        label="Description: *"
                        label-for="input-2"
                >
                    <b-form-input
                            id="input-2"
                            v-model="description"
                            required
                            placeholder="Description of your activity..."
                    ></b-form-input>
                </b-form-group>

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


                <b-form-group v-if="continuous === false">
                    <label id="input-start-time=label" for="input-start-time">Start Time: *</label>
                    <input type="datetime-local" class="form-control" v-model="startTime" id="input-start-time">
                </b-form-group>

                <b-form-group v-if="continuous === false">
                    <label id="input-end-time=label" for="input-end-time">End Time: *</label>
                    <input type="datetime-local" class="form-control" v-model="endTime" id="input-end-time">
                </b-form-group>

                <b-form-group
                        id="input-group-location"
                        label="Location: *"
                        label-for="input-location"
                >
                    <b-form-input
                            id="input-location"
                            v-model="location"
                            required
                            placeholder="Location of your activity..."
                    ></b-form-input>
                </b-form-group>
                <div>
                    <b-button class="float-left">Back</b-button>
                    <b-button class="float-right" type="submit" variant="primary">Submit</b-button>
                    <!--                <b-button class="float-right" type="reset" variant="danger">Reset</b-button>-->
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

    export default {
        components: { Header, Multiselect },
        name: "CreateActivity",
        data() {
            return {
                show: true,

                profileId: null,
                activityName: null,
                description: null,
                activityTypes: [],
                selectedActivityTypes: [],
                continuous: true,
                startTime: null,
                endTime: null,
                location: null
            }
        },
        mounted() {
            this.fetchActivityTypes();
            // if (!sessionStorage.getItem("token")) {
            //     this.$router.push('/login'); //Routes to home on logout
            // } else {
            //
            // }
        },
        methods: {
            onSubmit(evt) {
                evt.preventDefault();

            },
            /**
             * Fetch all possible activity types from the server
             */
            async fetchActivityTypes() {
                this.activityTypes = null;
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
</style>