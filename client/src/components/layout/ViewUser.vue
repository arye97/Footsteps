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
                    <div class="col-sm-12 text-center">
                        <h1 class="font-weight-light">Welcome to Hakinakina!</h1>
                        <section v-if="errored">
                            <p class="font-weight-light">{{this.error}}</p>
                            <p class="font-weight-light">Sorry, please log in to access your profile, or try again later.</p>
                        </section>

                        <section v-else>
                            <div v-if="loading"> Loading... <br/><br/><b-spinner variant="primary" label="Spinning"></b-spinner></div>
                            <div v-else class="form-group font-weight-light">
                                <h1 class="font-weight-light">Hi {{this.user.firstname}} {{this.user.middlename}} {{this.user.lastname}}!</h1>
                                <p>You're logged in to your Hakinakina Account</p>
                                <h3 class="font-weight-light">All about you: </h3>
                                <span class="accordion">
                                        <span v-if="this.user.nickname">Nickname: {{ this.user.nickname }}<br/></span>
                                        <span >Gender: {{ this.user.gender }}</span><br/>
                                        <span>Date Of Birth: {{ this.user.date_of_birth }} </span><br/>
                                        <span>Email: {{ this.user.primary_email }}</span><br/>
                                        <span v-if="this.user.additional_email.length >= 1"> Additional Emails: {{ this.user.additional_email.join(", ") }}<br/></span>
                                        <span v-if="this.user.passports.length >= 1">Passports: {{this.user.passports.join(", ")}}<br/></span>
                                        <span v-if="this.user.fitness >= 0">Fitness Level: {{this.fitness}}<br/></span>
                                        <span v-if="this.user.bio">Bio: {{ this.user.bio }}<br/></span>
                                        <span v-if="this.user.activityTypes">Activities I am interested in: {{ this.user.activityTypes.map(at => at.name).join(", ") }}<br/></span>
                                    </span>
                                <button type="submit" class="btn btn-link" v-if="this.isEditable" v-on:click="editProfile" >Edit Profile</button>
                            </div>
                        </section>
                    </div>
                </div>
            </div>
        </div>


    </div>
</template>

<script>
    import server from "../../Api";
    import {fitnessLevels} from '../../constants'
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
                isEditable: true,
                continuousActivities: [],
                discreteActivities: []
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
                this.userId = this.$route.params.userId;
                this.loading = true;
                this.continuousActivities = [];
                this.discreteActivities = [];
                if (this.userId === undefined || isNaN(this.userId)) {
                    this.userId = '';
                }
                await this.editable();
                await this.getProfile();
                await this.getActivities();
                this.loading = false;

            },
            async getProfile() {
              await server.get(  `/profiles/${this.userId}`,
                {headers:
                    {"Access-Control-Allow-Origin": "*", 'Content-Type': 'application/json', 'Token': sessionStorage.getItem("token")}, withCredentials: true
                },
              ).then(response => {
                if (response.status === 200) {
                  //user is set to the user data retrieved
                  this.user = response.data;
                  console.log(this.user);
                  this.userId = this.user.id;
                  this.formattedDate = getDateString(this.user.date_of_birth);
                  for (let i = 0; i < fitnessLevels.length; i++) {
                    if (fitnessLevels[i].value === this.user.fitness) {
                      this.fitness = fitnessLevels[i].desc;
                    }
                  }
                }
              }).catch(error => {
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
            async getActivities() {
                await server.get(  `/profiles/${this.userId}/activities`,
                  {headers:
                    {"Access-Control-Allow-Origin": "*", 'Content-Type': 'application/json', 'Token': sessionStorage.getItem("token")}, withCredentials: true
                },
                ).then(response => {if (response.status === 200) {
                    this.continuousActivities = response.data.filter(e => e.continuous === true);
                    this.discreteActivities = response.data.filter(e => e.continuous === false);
                }}).catch(error => {
                    this.errored = true;
                    this.error = error.response.data.message;
                    if (error.response.data.status === 404 && sessionStorage.getItem('token') !== null) {
                        this.$router.push({ name: 'myProfile' });
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
