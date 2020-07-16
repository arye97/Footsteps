<template>
    <div id="app">
        <div class="container">
            <div class="row">
                <div class="col-sm-12 ">
                    <template v-if="this.loading === false">
                        <Header :userId="this.userId"/>
                    </template>
                    <h1>
                        <br/><br/>
                    </h1>
                    <b-container class="contents" fluid>
                        <div class="col-sm-12 text-center">
                            <section v-if="errored">
                                <p class="font-weight-light">{{this.error}}</p>
                                <p class="font-weight-light">Sorry, please log in to access your profile, or try again later.</p>
                            </section>
                            <section v-else>
                                <div v-if="loading"> Loading...</div>
                                <div v-else class="form-group font-weight-light">
                                    <br/>
                                    <h1 class="font-weight-light">{{this.user.firstname}} {{this.user.middlename}} {{this.user.lastname}} ({{this.user.nickname}})</h1>
                                    <br/>
                                    <h5 class="font-weight-light" v-if="this.user.bio">"{{ this.user.bio }}"<br/></h5>
                                    <br/>

                                    <b-button type="submit" variant="success" size="med"
                                              v-if="this.isEditable" v-on:click="editProfile">Edit Profile</b-button>


                                    <br/><br/>



                                    <!-- Handling for displaying Passport Countries -->
                                    <h3 class="font-weight-light">Passport countries: </h3><br/>
                                    <span class="accordion">

                                        <b-list-group v-if="this.user.passports.length >= 1">
                                            <b-card v-for="country in this.user.passports" v-bind:key="country" class="flex-fill" border-variant="secondary">
                                                <b-card-text class="font-weight-light">
                                                    {{country}}
                                                </b-card-text>
                                            </b-card>
                                            <br/>
                                        </b-list-group>

                                        <b-list-group v-else horizontal="md">
                                            <b-card class="flex-fill" border-variant="secondary">
                                                <b-card-text class="font-weight-light">
                                                    No selected passport countries
                                                </b-card-text>
                                            </b-card>
                                        </b-list-group>
                                        <br/>


                                    </span>

                                    <h3 class="font-weight-light">Fitness Level: </h3>
                                    <span v-if="this.user.fitness >= 0">{{this.fitness}}<br/></span>
                                    <br/>

                                    <!--Handling for displaying of Emails -->
                                    <h3 class="font-weight-light">Email(s): </h3>
                                    <span v-if="this.user.additional_email.length >= 1">{{ this.user.primary_email }} (Primary), {{this.user.additional_email.join(", ")}}<br/></span>
                                    <span v-else>{{ this.user.primary_email }} (Primary)<br/></span>
                                    <br/>

                                    <h3 class="font-weight-light">Gender: </h3>
                                    <span>{{ this.user.gender }}<br/></span><br/>

                                    <h3 class="font-weight-light">Date of Birth: </h3>
                                    <span>{{ this.formattedDate }} <br/></span>

                                </div>
                            </section>
                        </div>
                    </b-container>
                </div>
            </div>
        </div>


    </div>
</template>

<script>
    import server from "../../Api";
    import {fitnessLevels} from '../../constants'
    import {getDateString} from '../../util'
    import Header from '../../components/Header/Header';
    export default {
        name: "ViewUser",
        components: {
            Header
        },
        data() {
            return {
                user: null,
                loading: true,
                errored: false,
                error: null,
                fitness: null,
                formattedDate: "",
                userId: '',
                isEditable: true
            }
        },
        async mounted() {
            await this.init();
        },
        methods: {
            async init() {
                this.user = null;
                this.errored = false;
                this.error = null;
                this.fitness = null;
                this.formattedDate = "";
                this.userId = this.$route.params.userId;
                this.loading = true;
                if (this.userId === undefined || isNaN(this.userId)) {
                    this.userId = '';
                }
                await this.editable();
                await server.get(  `/profiles/${this.userId}`,
                    {headers:
                            {"Access-Control-Allow-Origin": "*", 'Content-Type': 'application/json', 'Token': sessionStorage.getItem("token")}, withCredentials: true
                    },
                ).then(response => {
                    if (response.status === 200) {
                        //user is set to the user data retrieved
                        this.user = response.data;
                        // If the user is an admin, redirect to the admin dashboard
                        if (this.user.role === 20) {
                            this.$router.push("/admin");
                            return;
                        }
                        this.userId = this.user.id;
                        this.formattedDate = getDateString(this.user.date_of_birth);
                        for (let i = 0; i < fitnessLevels.length; i++) {
                            if (fitnessLevels[i].value === this.user.fitness) {
                                this.fitness = fitnessLevels[i].desc;
                            }
                        }
                        //no longer loading, so show data
                        this.loading = false;
                    }
                }).catch(error => {
                    this.loading = false;
                    this.errored = true;
                    this.error = error.response.data.message;
                    if (error.response.data.status === 404 && sessionStorage.getItem('token') !== null) {
                        this.$router.push({ name: 'myProfile' });
                        this.init();
                    } else {
                        this.logout();
                    }
                });
            },
            logout() {
                server.post('/logout', null,
                    {
                        headers: {"Access-Control-Allow-Origin": "*", "Content-Type": "application/json", 'Token': sessionStorage.getItem("token")},
                        withCredentials: true
                    }
                ).then(() => {
                    sessionStorage.clear();
                    // tokenStore.setToken(null);
                    this.isLoggedIn = (sessionStorage.getItem("token") !== null);
                    this.$forceUpdate();
                    this.$router.push('/login'); //Routes to home on logout
                }).catch(() => {
                    sessionStorage.clear();
                    this.isLoggedIn = (sessionStorage.getItem("token") !== null);
                    this.$forceUpdate();
                    this.$router.push('/login'); //Routes to home on logout
                })
            },
            editProfile () {
                this.$router.push({name: 'details', params:  { userId: this.userId }});
            },

            /**
             * Checks if the user ID currently held, is editable by this user/client
             */
            async editable() {
                if (this.userId === '') {
                    this.isEditable = true;
                    return;
                }
                await server.get(`/check-profile/`.concat(this.userId),
                    {headers: {
                            'Content-Type': 'application/json',
                            'Token': sessionStorage.getItem("token")}}
                ).then(() => {
                    // Status code 200
                    // User can edit this profile
                    // If admin will return 200
                    this.isEditable = true;
                }).catch(error => {
                    this.isEditable = false;
                    if (error.response.data.status === 401) {
                        this.logout();
                    }
                });
            }
        }
    }
</script>
