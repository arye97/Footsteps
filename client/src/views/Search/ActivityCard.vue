<template>
    <div>
        <b-card v-if="errored" id="erroredActivityCard" border-variant="secondary" style="background-color: #f3f3f3">
            {{ errorMessage }}
        </b-card>
        <b-card v-else border-variant="secondary" style="background-color: #f3f3f3" id="activityCard">
            <b-row>
                <b-col id="activityName-creatorName">
                    <strong>{{ activity.activity_name }} | {{ creatorName }}</strong>
                </b-col>
                <b-col>
                    <b-button id="viewActivityButton" style="float: right" variant="success"
                              v-on:click="viewActivity()">View Activity
                    </b-button>
                </b-col>
            </b-row>
            <hr style="border-color: inherit">
            <b-row no-gutters>
                <b-col cols="6">
                    <b-card-text :id="'activity' + activity.id + 'Card'">
                        <strong>Location:</strong>
                        <br>
                        {{ activity.location.name  }}
                        <br/>
                        <div v-if="!activity.continuous">
                            <strong>Start Date:</strong>
                            <br/>
                            {{ getDateTime(activity.start_time) }}
                            <br/>
                            <strong>End Date:</strong>
                            <br/>
                            {{ getDateTime(activity.end_time) }}
                            <br/>
                        </div>
                        <br/>
                        <div v-if="activity.description.length <= 75">
                            {{ activity.description }}
                        </div>
                        <div v-else>
                            {{ activity.description.substring(0,75)+"...." }}
                        </div>
                    </b-card-text>
                </b-col>
                <b-col v-if="activity.activity_type.length >= 1">
                    <div v-if="activity.fitness" style="text-align: left; padding-left: 7px">
                        <strong>Fitness Level:</strong>
                        <br>
                        <b-card-text :id="'fitnessLevel' + activity.id">
                            this.fitness
                        </b-card-text>
                    </div>
                    <b-list-group class="mx-2" id="matchingActivityTypes">
                        <section v-for="activityType in activity.activity_type" v-bind:key="activityType.name">
                            <!-- Only display queried activity types -->
                            <b-list-group-item v-if="activityTypesSearchedFor.includes(activityType.name)"
                                               style="text-align: center" variant="success">
                                {{ activityType.name }}
                            </b-list-group-item>
                        </section>
                    </b-list-group>
                </b-col>
            </b-row>
        </b-card>
    </div>
</template>

<script>
    import api from "../../Api";
    import {formatDateTime} from "../../util";
    import {fitnessLevels} from "../../constants";

    export default {
        name: "ActivityCard",
        props: {
            activity: {
                id: Number,
                creatorUserId: Number,
                activity_name: String,
                description: String,
                activity_type: Array,
                continuous: Boolean,
                start_time: Date,
                end_time: Date,
                location: String,
                fitness: Number
            },
            activityTypesSearchedFor: {
                default() {
                    return [];
                },
                type: Array
            }
        },

        data() {
            return {
                errored: false,
                errorMessage: 'An error occurred when loading this activity, please try again',
                creatorName: '',
                fitness: "",
                myFitness: null
            }
        },

        async mounted() {
            await this.getCreatorName();
            await this.getFitnessLevel();
        },

        methods: {
            /**
             * Imports formatDateTime function from util.js
             */
            getDateTime: formatDateTime,
            /**
             * Function to get the string and colour for fitness level
             */
            async getFitnessLevel() {
                for (let i = 0; i < fitnessLevels.length; i++) {
                    if (fitnessLevels[i].value === this.activity.fitness) {
                        this.fitness = fitnessLevels[i].desc;
                    }
                }
                if (this.fitness === "No fitness level") {
                    return null;
                }
                await api.getAllUserData().then(response => {
                    this.myFitness = response.data.fitness;
                }).catch(() => {
                    this.errored = true;
                });
                this.setFitnessColour();
            },

            /**
             * Updates the colour of a b-card-text element based on the value of this.myFitness
             */
            setFitnessColour() {
                let fitnessLevelsElement = document.getElementById('fitnessLevel' + this.activity.id);
                if (this.myFitness === -1) {
                    fitnessLevelsElement.style["color"] = "black";
                } else if (this.myFitness <= this.fitness) {
                    fitnessLevelsElement.style["color"] = "green";
                } else {
                    fitnessLevelsElement.style["color"] = "red";
                }
            },
            /**
             * Function for redirecting to view activity page via activity's ID
             */
            viewActivity() {
                this.$router.push({name: 'viewActivity', params: {activityId: this.activity.id}})
            },
            /**
             * Obtains the name of the creator of an activity
             */
            async getCreatorName() {
                await api.getUserData(this.activity.creatorUserId).then(response => {
                    this.creatorName = `${response.data.firstname} ${response.data.lastname}`;
                }).catch(error => {
                    if (error.response.status === 401) {
                        this.logout();
                    } else {
                        this.errored = true;
                    }
                });
            },
            /**
             * Logout is used for when an error needs redirection
             * we must actually log out the user rather than just redirect them
             */
            logout() {
                api.logout();
                sessionStorage.clear();
                this.$router.push('/login');
            }
        }
    }
</script>

<style scoped>

</style>