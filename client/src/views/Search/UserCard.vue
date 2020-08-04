<template>
    <div>
        <b-card border-variant="secondary" style="background-color: #f3f3f3">
            <b-row class="mb-1">
                <b-col>
                    <b-card-text><strong>{{ user.firstname }} {{ user.lastname }}</strong></b-card-text>
                </b-col>
                <b-col>
                    <!-- View user button -->
                    <b-button id="viewProfileButton" style="float: right" variant="primary" v-b-modal="'modal-view-profile' + user.id">View Profile</b-button>

                    <!--View User Details Modal-->
                    <b-modal :id="'modal-view-profile' + user.id" :title="user.firstname + ' ' + user.lastname">
                        <!--The User's Details-->
                        <b-button id="goToProfileButton" style="float: right" variant="primary" v-on:click="viewProfile(user.id)">Go To Profile</b-button>
                        <view-user v-bind:show-header="false"/>

                    </b-modal>
                </b-col>
            </b-row>
            <hr style="border-color: inherit">
            <b-row class="mb-1">
                <b-col>
                    <!-- user.primary_email would be better but is null from BE -->
                    <strong>Email: </strong>{{ user.primary_email }}
                    <br/><br/>
                    <div v-if="user.bio.length <= 75">
                        {{ user.bio }}
                    </div>
                    <div v-else>
                        {{ user.bio.substring(0,75)+"...." }}
                    </div>
                </b-col>
                <b-col v-if="user.activityTypes.length >= 1">
                    <b-list-group>
                        <section v-for="activityType in user.activityTypes" v-bind:key="activityType">
                            <!-- Only display queried activity types -->
                            <b-list-group-item v-if="selectedActivityTypes.indexOf(activityType.name) > -1" variant="primary">
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

    import ViewUser from "../../components/layout/ViewUser.vue";

    export default {
        name: "UserCard",
        components: {ViewUser},
        props: {
            user: {
                id: Number,
                firstname: String,
                lastname: String,
                primary_email: String,
                bio: String,
                activityTypes: Array,
            },
            selectedActivityTypes: {
                default() {
                    return [];
                },
                type: Array
            }
        },

        async mounted() {

        },

        methods: {
            goToPage(url) {
                this.$router.push(url);
            },

            /**
             * Function for redirecting to user profile via userId
             */
            viewProfile(userId) {
                this.goToPage({ name: 'profile', params: {userId: userId} })
            }
        }
    }


</script>

<style scoped>

</style>