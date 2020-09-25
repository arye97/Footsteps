<template>
    <div>
        <b-card border-variant="secondary" style="background-color: #f3f3f3">
            <b-row>
                <b-col>
                    <h4 id="fullName" class="font-weight-light" v-if="user.nickname" style="text-align: center;"><strong>{{ user.firstname }} {{ user.lastname }} |</strong> {{user.nickname}}</h4>
                    <h4 id="fullName1" class="font-weight-light" v-else style="text-align: center;"><strong>{{ user.firstname }} {{ user.lastname }}</strong></h4>

                </b-col>
            </b-row>
            <hr style="border-color: inherit">
            <b-row class="mb-1">
                <b-col id="userDetails" class="font-weight-light" style="text-align: center; font-size: x-large;">
                    <!-- user.primary_email would be better but is null from BE -->
                    {{ user.primary_email }}
                </b-col>
            </b-row>
            <hr/>
            <b-row>
                <b-col>
                    <div style="align-items: center; text-align: center;" v-if="this.user.activityTypes.length >= 1">
                        <b-button-group  v-for="activityType in this.user.activityTypes"
                                        v-bind:key="activityType.name" border-variant="secondary">
                            <b-button pill variant="secondary" disabled class="font-weight-light activityTypes">{{activityType.name}}</b-button>
                        </b-button-group>
                    </div>
                </b-col>
            </b-row>
            <b-row v-if="this.user.bio">
                <b-col style="text-align: center;">
                    <hr/>
                    <div v-if="user.bio.length <= 125">
                        {{ user.bio }}
                    </div>
                    <div v-else>
                        <hr/>
                        {{ user.bio.substring(0,125)+"...." }}
                    </div>
                </b-col>
            </b-row>
            <hr/>
            <b-row>
                <b-col>
                    <!-- View user button -->
                    <b-button id="viewProfileButton" style="width: 100%;" variant="success" v-b-modal="'modal-view-profile' + user.id">View Profile</b-button>

                    <!--View User Details Modal-->
                    <b-modal size="lg" :id="'modal-view-profile' + user.id" :title="user.firstname + ' ' + user.lastname" scrollable>
                        <!--The User's Details-->
                        <b-button id="goToProfileButton" style="float: right" variant="primary" v-on:click="viewProfile(user.id)">Go To Profile</b-button>
                        <view-user v-bind:user-id-prop="user.id" v-bind:modalView="true"/>
                    </b-modal>
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
            activityTypesSearchedFor: {
                default() {
                    return [];
                },
                type: Array
            }
        },

        watch: {
            $route: function () {
                this.$bvModal.hide('modal-view-profile' + this.user.id)
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
            },

        }
    }


</script>

<style scoped>


    .activityTypes {
        text-align: center;
        align-items: center;
        margin: 3px;
        display: inline-block;
    }

</style>