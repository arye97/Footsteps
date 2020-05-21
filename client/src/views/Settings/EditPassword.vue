<template>
    <div>
        <h1><br/></h1>
        <div>
            <div class="container">
                <div class="row">
                    <div class="col-sm-6 offset-sm-3">
                        <Header />
                        <router-view></router-view>
                    </div>
                </div>
            </div>
        </div>
        <Sidebar/>

        <h1><br/></h1>
        <header class="masthead">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light">Edit Password</h1>
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
                    <li>Must contain 8 characters</li>
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
    </div>
</template>

<script>
    import server from '../../Api';
    import Sidebar from '../../components/layout/ProfileEditSidebar';
    import {tokenStore} from "../../main";
    import {validateUser} from "../../util";

    export default {
        name: "EditPassword",
        components: { Sidebar },
        data () {
            return {
                loading: true,
                userId: null,
                user: null,
                message: '',    //Error and success message
                timeoutFlag: 0, //Number of timeout calls currently working
                oldPass: '',
                newPass: '',
                repeatPass: ''
            }
        },
        mounted() {
            //TODO when get profile/id is available it should be used, instead of one below. Also must find universal way of getting and storing the subject userId, can be and may not be the client's user.
            //Get the user's id from server
            server.get('profiles/',
                {headers:
                        {'Content-Type': 'application/json',
                                'Token': tokenStore.state.token}
                }
            ).then(response => {
                this.loading = false;
                this.user = response.data;
                this.userId = this.user.id;
            }).catch(error => {
                if (error.response.data.status === 401) {
                    this.$router.push("/login");
                } else {
                    this.message = 'Unknown error : ' + error.message;
                }
                this.showMessage('form_message', true);
            });
        },
        methods: {
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
                }, 5000)
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
                server.put('profiles/'.concat(this.userId).concat('/password'),
                    {'old_password': this.oldPass,
                        'new_password': this.newPass,
                        'repeat_password': this.repeatPass},
                    {headers:
                            {'Content-Type': 'application/json',
                                    'Token': tokenStore.state.token}
                    }).then(response => {
                        if (response.status === 200) {
                            this.message = 'Your New password was saved successfully';
                            this.showMessage('form_message', false);
                        }
                    }).catch(error => {
                        if (error.response.data.status === 401) {
                            this.$router.push("/login");
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
             * Redirect to view user screen
             */
            backToProfile() {
                this.$router.push("/profile");
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