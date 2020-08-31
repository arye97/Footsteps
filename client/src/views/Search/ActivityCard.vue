<template>
    <div>
        <b-card border-variant="secondary" style="background-color: #f3f3f3" id="activityCard">
            <b-row class="mb-1">
                <b-col>
                    <strong>{{ activity.activity_name }}
                        <div style="text-decoration-color: red">|</div>
                        {{ creatorName }}</strong>
                </b-col>
                <b-col>
                    <b-button id="viewActivityButton" style="float: right" variant="success"
                              v-on:click="viewActivity()">View Activity
                    </b-button>
                </b-col>
            </b-row>
            <b-row no-gutters>
                <b-col md="1">
                    <b-card-text :id="'activity' + activity.id + 'Card'">
                        <strong>Location: </strong>{{ activity.location }}
                        <br>
                        <div v-if="!activity.continuous">
                            <strong>Start Date: </strong>{{ getDateTime(activity.start_time) }}
                            <br/>
                            <strong>End Date: </strong>{{ getDateTime(activity.end_time) }}
                            <br/><br/>
                        </div>
                        <div v-if="activity.description.length <= 75">
                            {{ activity.description }}
                        </div>
                        <div v-else>
                            {{ activity.description.substring(0,75)+"...." }}
                        </div>
                    </b-card-text>
                </b-col>
                <b-col v-if="activity.activity_type.length >= 1">
                    <b-list-group id="matchingActivityTypes">
                        <section v-for="activityType in activity.activity_type" v-bind:key="activityType.name">
                            <!-- Only display queried activity types -->
                            <b-list-group-item v-if="activityTypesSearchedFor.includes(activityType.name)"
                                               variant="success">
                                {{ activityType.name }}
                            </b-list-group-item>
                        </section>
                    </b-list-group>
                </b-col>
            </b-row>
            <br/>
        </b-card>
    </div>
</template>

<script>
    import api from "../../Api";
    import {formatDateTime} from "../../util";

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
                location: String // Todo this will change in a future story
            },
            activeUserId: {
                default: null
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
                errorMessage: 'An error occurred when loading this activity card, please try again',
                loading: true,
                activityName: ''
            }
        },

        async mounted() {
            await this.getCreatorName();
            this.loading = false;
        },

        methods: {
            /**
             * Imports formatDateTime function from util.js
             */
            getDateTime: formatDateTime,
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