<template>
    <div>
        <h1><br/><br/></h1>
        <b-container class="contentsExtendedBottom" fluid>
            <template v-if="userId">
                <div>
                    <div class="container">
                        <div class="row">
                            <div class="col-sm-6 offset-sm-3">
                                <Header :userId="this.userId"/>
                                <router-view></router-view>
                            </div>
                        </div>
                    </div>
                </div>
                <Sidebar :userId="this.userId"/>
            </template>

            <h1><br/></h1>
            <header class="masthead">
                <div class="container h-100">
                    <div class="row h-100 align-items-center">
                        <div class="col-12 text-center">
                            <h1 class="font-weight-light"><strong>Edit Password</strong></h1>
                            <p class="lead">Edit the password linked to your profile</p><br/>
                        </div>
                    </div>
                </div>
            </header>

            <section v-if="loading">
                <div class="loading text-center">
                    <p>Loading...</p>
                </div>
            </section>

            <section v-else>
                <footer class="text-center">
                    <hr/>Password rules:<br/>
                </footer>
                <footer>
                    <ul>
                        <li>Must contain at least 8 characters</li>
                        <li>Must contain at least one letter</li>
                        <li>Must contain at least one number</li>
                    </ul>
                </footer><br/>
                <form v-on:submit.prevent="editPassword">
                    <div class="form-group text-center">
                        <input type="password"
                               v-model="oldPass"
                               class="form-control"
                               id="oldPass"
                               placeholder="Enter Old Password..."
                        >
                    </div>
                    <div class="form-group text-center">
                        <input type="password"
                               v-model="newPass"
                               class="form-control"
                               id="newPass"
                               placeholder="Enter New Password.."
                               @keyup="checkPasswords()"
                        >
                    </div>
                    <div class="form-group text-center">
                        <input type="password"
                               v-model="repeatPass"
                               class="form-control"
                               id="repeatPass"
                               placeholder="Retype New Password.."
                               @keyup="checkPasswords()"
                        >
                    </div>

                    <div class="alert alert-danger alert-dismissible fade show" role="alert" hidden="true" id="form_message">
                        {{  message  }}
                    </div>

                    <div id="confirmationButtons">
                        <b-button type="submit" variant="success float-left"
                                  size="lg" id="back" v-on:click="backToProfile">
                                  Back</b-button>
                        <b-button type="submit" variant="success float-right"
                                  size="lg" v-on:click="editPassword">
                                  Submit</b-button>
                    </div>
                </form>
            </section>
        </b-container>
    </div>
</template>

<script>
    import api from '../../Api';
    import Sidebar from '../../components/layout/ProfileEditSidebar';
    import Header from '../../components/Header/Header.vue'
    import {validateUser} from "../../util";

    const TIMEOUT_DURATION = 5000;   // Time for error/success messages to disappear

    export default {
        name: "EditPassword",
        components: { Sidebar, Header},
        data () {
            return {
                loading: true,
                userId: null,
                user: null,
                message: '',    //Error and success message
                timeoutFlag: 0, //Number of timeout calls currently working
                oldPass: '',
                newPass: '',
                repeatPass: '',
                isEditable: false
            }
        },
        async mounted() {
            await this.init();
        },
        methods: {
            /**
             * Initializes the page.
             */
            async init() {
                //Check if userId is valid on this page, with the given session token.
                this.isEditable = false;
                this.loading = true;
                this.oldPass = '';
                this.newPass = '';
                this.repeatPass = '';
                this.userId = this.$route.params.userId;
                if (this.userId === undefined || isNaN(this.userId)) {
                    this.isEditable = true;
                    this.userId = '';
                } else {
                    await this.editable(); // If allowed to edit, userId is set
                }
                if (this.isEditable) {
                    api.getUserData(this.userId).then(response => {
                        this.loading = false;
                        this.user = response.data;
                        this.userId = this.user.id;
                    }).catch(error => {
                        if (error.response.data.status === 401) {
                            this.logout();
                        } else {
                            this.message = 'Unknown error : ' + error.message;
                            this.showMessage('form_message', true);
                        }
                    });
                }
            },

            /**
             * Shows the alert message via the given alert_name id, but only for 5000ms
             * @param alert_name the id for the alert
             * @param isError true if error alert, otherwise false
             */
            showMessage(alert_name, isError) {
                let self = this;
                let msgAlert = document.getElementById(alert_name);
                if (isError) {       //Set alert to error/danger type if it's an error
                    msgAlert.classList.remove("alert-success");
                    msgAlert.classList.add("alert-danger");
                } else {             //Set alert to success type if it's not an error
                    msgAlert.classList.remove("alert-danger");
                    msgAlert.classList.add("alert-success");
                }
                msgAlert.hidden = false;         //Show alert bar
                this.timeoutFlag += 1;
                setTimeout(function() { //Hide alert bar after ~5000ms
                    if (self.timeoutFlag === 1) {
                        msgAlert.hidden = true;
                    }
                    self.timeoutFlag -= 1;
                }, TIMEOUT_DURATION)
            },
            /**
             * Checks if newPass is valid and if repeatPass is equal to newPass.
             * If problem found set error and message accordingly and call showError
             * @return true if passes, otherwise false
             */
            checkPasswords() {
                let isError = false;
                if (this.repeatPass !== this.newPass) {
                    this.message = "Your New and Repeated passwords don't match.";
                    isError = true;
                }
                if (!validateUser(this.newPass, 'password').valid) {
                    this.message = 'New password is invalid, please try again.';
                    isError = true;
                }
                // old password is checked on server side
                if (isError) {
                    this.showMessage('form_message', isError);
                    return false;
                } else {
                    document.getElementById('form_message').hidden = true;
                }
                return true;
            },
            /**
             * Send the Put request to the server and catch any errors thrown
             */
            editPassword() {
                if (!this.checkPasswords()) {
                    return;
                }
                if (this.oldPass === this.newPass) {
                    this.message = 'Your New password equals your current password. Click the Back button or try again.';
                    this.showMessage('form_message', true);
                    return;
                }
                api.updatePassword(this.userId, this.oldPass, this.newPass, this.repeatPass).then(response => {
                        if (response.status === 200) {
                            this.message = 'Your New password was saved successfully';
                            this.showMessage('form_message', false);
                        }
                    }).catch(error => {
                        if (error.response.data.status === 401) {
                            this.logout();
                        } else if (error.response.status === 400) {
                            this.message = 'The given Old password was incorrect, please try again.';
                        } else if (error.response.data.status === 403) {
                            this.message = 'You are not authorized to edit this user';
                        } else if (error.response.data.status === 404) {
                            this.message = error.response.data.message;
                        } else {
                            this.message = 'Unknown error : ' + error.message;
                        }
                        this.showMessage('form_message', true);
                });
            },

            /**
             * Check if the current userId is allowed to be edited by this user session
             */
            async editable() {
                if (this.userId === '') {
                    this.isEditable = true;
                    return;
                }
                await api.checkProfile(this.userId).then(() => {
                    //Status code 200
                    //User can edit this profile
                    this.isEditable = true;
                }).catch(error => {
                    this.isEditable = false;
                    if (error.response.data.status === 401) {
                        this.logout();
                    } else {
                        this.$router.push({ name: 'passwordNoID' });
                        this.init();
                    }
                });
            },

            /**
             * Logs the user out and clears session token
             */
            logout () {
                api.logout().then(() => {
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
            /**
             * Redirect to view user screen
             */
            backToProfile() {
                this.$router.push({ name: 'profile', params: {userId: this.userId} });
            }
        }
    }
</script>

<style scoped>
    ul {
        display: table;
        margin: 0 auto;
    }
</style>