<template>
    <div id="app">
        <div class="container">
            <div class="row">
                <div class="col-sm-12 ">
                    <Header />
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
                            <div v-if="loading"> Loading...</div>
                            <div v-else class="form-group font-weight-light">
                                <h1 class="font-weight-light">Hi {{this.user.firstname}}!</h1>
                                <p>You're logged in to your Hakinakina Account</p>
                                <h3 class="font-weight-light">All about you: </h3>
                                <span class="accordion">
                                        <span v-if="this.user.nickname">Nickname: {{ this.user.nickname }}</span><br/>
                                        <span >Gender: {{ this.user.gender }}</span><br/>
                                        <span>Date Of Birth: {{ this.user.date_of_birth }}</span><br/>
                                        <span>Email: {{ this.user.primary_email }}</span><br/>
                                        <span>Additional Emails: {{ this.user.additional_email.join(", ") }}</span><br/>
                                        <button type="button" class="btn btn-link" v-on:click="editEmail">Edit Emails</button><br/>
                                        <span v-if="this.user.passports">Passports: {{this.user.passports.join(", ")}}</span><br/>
                                        <span v-if="this.user.fitness">Fitness Level: {{this.user.fitness}}</span><br/>
                                        <span v-if="this.user.bio">Bio: {{ this.user.bio }}</span><br/>
                                    </span>
                                <button type="submit" class="btn btn-link" v-on:click="logout" >Logout</button>
                                <button type="submit" class="btn btn-link" v-on:click="editProfile" >Edit Profile</button>
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
    import {tokenStore} from '../../main';
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
                fitness: null
            }
        },
        async mounted() {
            this.loading = true;
            await server.get(  '/profiles',
                {headers:
                        {"Access-Control-Allow-Origin": "*", 'Content-Type': 'application/json', 'Token': tokenStore.state.token}, withCredentials: true
                }, )
            .then(response => {
                if (response.status === 200) {
                    console.log('Status = OK. response.data:');
                    console.log(response.data);
                    //user is set to the user data retrieved
                    this.user = response.data;
                    for (var i = 0; i < fitnessLevels.length; i++) {
                        if (fitnessLevels[i].value == this.user.fitness) {
                            this.fitness = fitnessLevels[i].desc;
                        }
                    }
                    //no longer loading, so show data
                    this.loading = false;
                }
            }).catch(error => {
                this.errored = true;
                console.error(error);
                console.error(error.response);

                })
        },
        methods: {
            editEmail () {
                this.$router.push('/profile/emails');
            },
            logout () {
                server.post('/logout', null,
                    {
                        headers: {"Access-Control-Allow-Origin": "*", "content-type": "application/json", 'Token': tokenStore.state.token},
                        withCredentials: true
                    }
                ).then(response => {
                    console.log(response);
                    console.log('User logged out successfully!');
                    tokenStore.setToken(null);
                    this.$router.push('/'); //Routes to home on logout
                }).catch(error => {
                    console.error(error);
                    console.log('User was already logged out!');
                    this.$router.push('/'); //Routes to home on logout
                })
            },
            editProfile () {
                this.$router.push('/profile/details');
            }
        }
    }
</script>
