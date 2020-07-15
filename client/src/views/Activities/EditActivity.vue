<template>
    <div>
        <Header />
        <h1><br/><br/></h1>
        <header class="masthead">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light">Welcome to Hakinakina</h1>
                        <h1 class="font-weight-light">
                            Edit your activity<br/>
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
                    ></b-form-input>
                </b-form-group>

                <b-form-group
                        id="input-group-3"
                        label="Activity Types: *"
                        label-for="input-3"
                >
                    <multiselect v-model="selectedActivityTypes" id="input-3"
                                 :options="activityTypes" :multiple="true" :searchable="true" :close-on-select="false"
                    >
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
                            required>
                    </b-form-input>
                </b-form-group>
                <div>
                    <b-button variant="secondary" v-on:click="$router.back()">Back</b-button>
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
    import Header from '../../components/Header/Header.vue'
    import server from "../../Api";

    export default {
        components: { Header, Multiselect },
        name: "EditActivity",
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
                location: null,
                activityId: null
            }
        },
        async created() {

            let url = window.location.pathname;
            this.activityId = url.substring(url.lastIndexOf('/') + 1);

            await this.getActivityData();
            await this.fetchActivityTypes();
            console.log(this.activityTypes)


            //Uncomment once the activityTypes/id endpoint is created!
            //this.getActivityData();


        },
        methods: {
            onSubmit(evt) {
                evt.preventDefault();
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

            //Getting the data from the selected activity to update
            async getActivityData() {
                await server.get(`activities/${this.activityId}`,
                    {headers: {'Content-Type' : 'application/json', 'Token' : sessionStorage.getItem('token')}
                    }
                ).then(response => {
                    this.activityName = response.data.activity_name;
                    this.continuous = (response.data.continuous === true);
                    this.description = response.data.description;
                    this.location = response.data.location;
                    this.startTime = response.data.start_time;
                    this.endTime = response.data.end_time;
                    for (let i = 0; i < response.data.activity_type.length; i++) {
                        this.selectedActivityTypes.push(response.data.activity_type[i].name);
                    }
                    //need to also add in the activities activity types
                }).catch(error => {
                    this.processGetError(error);
                })
            }
        }
    }

</script>

<style scoped>
    footer {
        padding-top: 55px;
    }
</style>