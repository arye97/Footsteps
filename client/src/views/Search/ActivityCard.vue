<template>
    <div>
        <b-card v-if="errored" id="erroredActivityCard" border-variant="secondary" style="background-color: #f3f3f3">
            {{ errorMessage }}
        </b-card>
        <b-card v-else border-variant="secondary" style="background-color: #f3f3f3" id="activityCard">
            <h5 id="activityName-creatorName" style="text-align: center">
                <strong>{{ activity.activity_name }} |</strong> {{ creatorName }}
            </h5>
            <hr style="border-color: inherit">
            <b-card-text :id="'activity' + activity.id + 'Card'">
                <h6 style="text-align: center">
                    {{ activity.location.name }}
                </h6>
                <hr>
                <div v-if="!activity.continuous" style="text-align: center">
                    {{ getDateTime(activity.start_time) }} - {{ getDateTime(activity.end_time) }}
                    <hr>
                </div>
                <div v-if="activity.description.length <= 200">
                    {{ activity.description }}
                </div>
                <div v-else>
                    {{ activity.description.substring(0,200)+"...." }}
                </div>
            </b-card-text>
            <div id="matchingActivityTypes" v-if="activity.activity_type.length >= 1" align="center">
                <hr>
                <b-button-group v-for="activityType in activity.activity_type" v-bind:key="activityType.name"
                                border-variant="secondary">
                    <!-- Only display queried activity types -->
                    <b-button v-if="activityTypesSearchedFor.includes(activityType.name)" pill variant="secondary"
                              disabled class="font-weight-light activityTypes">
                        {{ activityType.name }}
                    </b-button>
                </b-button-group>
            </div>
            <hr/>
            <fitness-progress-bar
                    :user-fitness-level="myFitness"
                    :activity-fitness-level="activityFitness"
                    v-if="loaded"
            >
            </fitness-progress-bar>
            <b-button id="viewActivityButton" variant="success" block
                      v-on:click="viewActivity()">View Activity
            </b-button>
        </b-card>
    </div>
</template>

<script>
    import api from "../../Api";
    import {formatDateTime} from "../../util";
    import FitnessProgressBar from "../../components/Activities/FitnessProgressBar";

    export default {
        name: "ActivityCard",
        components: {FitnessProgressBar},
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
                activityFitness: null,
                myFitness: null,
                loaded: false
            }
        },

        async mounted() {
            await this.getCreatorName();
            await this.getFitnessLevel();
            this.loaded = true;
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
                if (this.activity.fitness === null) {
                    this.activityFitness = -1; // No fitness level
                } else {
                    this.activityFitness = this.activity.fitness;
                }

                await api.getAllUserData().then(response => {
                    this.myFitness = response.data.fitness;
                }).catch(() => {
                    this.errored = true;
                });
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
    .activityTypes {
        margin: 3px;
        display: inline-block;
    }
</style>