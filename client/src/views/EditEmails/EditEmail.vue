<!--TODO: Make dropdown be able to change the primary email, Make a list of email below that which we can edit/change Have an empty text box we can add new secondary emails into, Have default option in dropdown menu as the primary email-->

<template>
    <div>
    <Sidebar/>
        <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
        <h1><br/></h1>
        <header class="masthead">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light">Edit Emails</h1>
                        <p class="lead">Edit the emails linked to your profile</p><br/>
                    </div>
                </div>
            </div>
        </header>

        <section v-if="error">
            <p>Sorry, looks like we can't get your info! Please try again soon.</p>
            <p>{{ error }}</p>
        </section>

        <section v-else>

            <section v-if="loading">
                <div class="loading">
                    Loading...
                </div>
            </section>

            <section v-else>




                <article class="emails">
                    <section class="primaryEmailDisplay">
                        <table id="primaryEmailTable" class="table table-borderless">
                            <tr>
                                <td>
                                    <p id="primaryEmailLabel">Primary Email:</p>
                                </td>
                                <td>
                                    <p id="primaryEmail">
                                        {{ this.primaryEmail }}
                                    </p>
                                </td>
                            </tr>
                        </table>
                    </section>

                    <hr>

                    <section class="additionalEmailsDisplay">
                        <table id="additionalEmailsTable" class="table table-borderless">
                            <tr v-for="additionalEmail in this.additionalEmails"
                                v-bind:key="additionalEmail">
                                <td> {{ additionalEmail }}
                                    <button type="submit" class="btn btn-primary" v-on:click="setPrimary">Set as Primary</button>
                                    <button type="submit" class="btn btn-danger" v-on:click="deleteEmail">Delete</button>
                                </td>
                            </tr>
                        </table>

                        <form v-on:submit.prevent="addEmail">
                            <label for="newEmail">Add New Email:</label>
                            <input
                                    v-model="inesrtedEmail"
                                    id="newEmail"
                                    placeholder="blah@blah.com"
                            >
                            <button>Add</button>
                        </form>

                    </section>



                </article>



<!--                <div id="primaryEmail">-->
<!--                    <dl>-->
<!--                        <label for="a">Favorite Animal</label><br>-->
<!--                        <dt name="primaryEmail" id="a">-->
<!--                            {{ this.primaryEmail }}-->
<!--                        </dt>-->
<!--                    </dl>-->
<!--                </div>-->




                <div id="addemails-list-example">
                    <form v-on:submit.prevent="addNewTodo">
                        <label for="new-todo">Add a todo</label>
                        <input
                                v-model="newTodoText"
                                id="new-todo"
                                placeholder="E.g. Feed the cat"
                        >
                        <button>Add</button>
                    </form>
                    <ul>
                        <li
                                is="todo-item"
                                v-for="(todo, index) in todos"
                                v-bind:key="todo.id"
                                v-bind:title="todo.title"
                                v-on:remove="todos.splice(index, 1)"
                        ></li>
                    </ul>
                </div>

                <div id="additionalEmails" class="table table-borderless">
                    <form v-on:submit.prevent="addNewTodo">
                        <label for="new-todo">Add a todo</label>
                        <input
                                v-model="newTodoText"
                                id="new-todo"
                                placeholder="E.g. Feed the cat"
                        >
                        <button>Add</button>
                    </form>
                </div>

                <form method="put" v-on:submit.prevent="submitEmail">
                    <dl class="form-group">
                        <span>Primary email address:</span><br/>
                        <dt>
                            <span>{{ this.primaryEmail }}</span>
                            <p class="mt-2"></p>
                        </dt>
                        <dd>
                        <!-- Multiselect for choosing new primary email -->
                        <div class="form-group">
                            <label for="primary_email_select">Edit Additional Emails:</label>
                            <!-- to change "gender" to something else-->
                            <multiselect v-model="selectedEmail" id="primary_email_select" :searchable="false"
                                         :close-on-select="true" :options="additionalEmails" :show-labels="false"
                                         :preselectFirst="true" :allow-empty="false"
                                         placeholder="No Additional Emails">
                            </multiselect>
                        </div>
                        <button type="submit" class="btn btn-secondary" v-on:click="setPrimary">Set as Primary</button>
                        <button type="submit" class="btn btn-secondary" v-on:click="deleteEmail">Delete</button>
                        <br/>
                        <br/>
                        <br/>

                        <table id="additionalEmailsOOOOOOO" class="table table-borderless">
                            <tr>
                                <td scope="row">{{ this.additionalEmails }}</td>
                                <button type="submit" class="btn btn-primary" v-on:click="setPrimary">Make Primary</button>
                                <button type="submit" class="btn btn-secondary" v-on:click="deleteEmail">Delete</button>
                            </tr>
                            <tr>
                                <td scope="row">email1</td>
                                <button type="submit" class="btn btn-primary" v-on:click="setPrimary">Make Primary</button>
                                <button type="submit" class="btn btn-secondary" v-on:click="deleteEmail">Delete</button>
                            </tr>
                            <tr>
                                <td scope="row">email1</td>
                                <button type="submit" class="btn btn-primary" v-on:click="setPrimary">Make Primary</button>
                                <button type="submit" class="btn btn-secondary" v-on:click="deleteEmail">Delete</button>
                            </tr>
                        </table>


                        <div class="form-group">
                            <!-- full-name field-->
                            <label for="add-email">Add email address</label>
                            <input type="email" class="form-control" v-model="addedEmail" id="add-email" name="add-email" placeholder="Email address">
                        </div>


                        <button type="submit" class="btn btn-secondary" v-on:click="addEmail">Add</button>
                        </dd>
                    </dl>
                </form>
                <div class="form-group">
                    <!-- SignIn Button-->
                    <button type="submit" class="btn btn-primary" v-on:click="submitEmail">Submit</button>
                    <router-link to="/" class="btn btn-link">Go Back</router-link>
                </div>
            </section>
        </section>
    </div>
</template>

<script>
    import server from '../../Api';
    import {tokenStore} from "../../main";
    import Sidebar from '../../components/layout/ProfileEditSidebar';
    import Multiselect from "vue-multiselect";

    export default {
        name: "EditEmail",
        components: {Sidebar, Multiselect},
        data () {
            return {
                loading: true,
                post: null,
                error: false,
                userId: null,
                primaryEmail: null,
                additionalEmails: null,
                newPrimaryEmail: null,
                newAdditionalEmails: null,
                selectedEmail: null,
                addedEmail: null,
            }
        },
        mounted() {
            server.get(  `/emails`,
                {
                    headers: {
                        'Content-Type': 'application/json',
                               'Token': tokenStore.state.token
                    },
                    withCredentials: true
                }
            ).then(response => {
                if (response.status === 200) {
                    this.userId = response.data["userId"];
                    this.primaryEmail = response.data["primaryEmail"];
                    this.additionalEmails = response.data["additionalEmails"];
                    this.loading = false;
                }
            }).catch(function(error) {
                console.error(error);
                console.error(error.response);
            })
        },
        methods: {
            deleteEmail() {
                //Remove an email
                let selectedIndex = this.additionalEmails.indexOf(this.selectedEmail);
                if (selectedIndex > -1) {
                    this.additionalEmails.splice(selectedIndex, 1);
                }
            },
            setPrimary() {
                let newPrimary = this.selectedEmail;
                let oldPrimary = this.primaryEmail;







                this.deleteEmail();
                this.additionalEmails.unshift(oldPrimary);
                this.primaryEmail = newPrimary;


                console.log('newPrimary: ' + newPrimary + ' Old primary: ' + oldPrimary)
                //}
            },
            addEmail() {
                //Make sure there are no more than 5 emails already and the email is valid
                if (this.additionalEmails.length >= 4) {
                    console.log('Error: maximum number of emails reached. Please remove an email before adding any more (Limit 5)')
                } else if (this.additionalEmails.includes(this.selectedEmail) || (this.primaryEmail == this.selectedEmail)) {
                    //Can't have duplicate emails
                    console.log("Error: Can't add duplicate email");
                } else if (/(.+)@(.+){2,}\.(.+){2,}/.test(this.addedEmail)) {
                    //Email is valid
                    this.additionalEmails.unshift(this.addedEmail);
                } else {
                    //Email's not valid
                    console.log('Error: Invalid email. Please change to proper email format and try again')
                }
            },

            submitEmail() {
                const updateEmail = {
                    primaryEmail: this.primaryEmail,
                    additionalEmails: this.addedEmail
                };
                server.put(`/profiles/${this.userId}/emails`,
                    updateEmail,
                    {
                        headers: {"Access-Control-Allow-Origin": "*",
                                                 "content-type": "application/json",
                                                        "Token": tokenStore.state.token},
                        withCredentials: true
                    }
                ).then(function() {
                    console.log('User Emails updated Successfully!');
                }).catch(error => {
                    console.log(error);
                    //Get alert bar element
                    let errorAlert = document.getElementById("alert");
                    if (error.message === "Network Error") {
                        this.message = error.message;
                    } else if (error.response.status === 403) { //Error 401: Email already exists or invalid date of birth
                        this.message = error.response.data.toString(); //Set alert bar message to error message from server
                    } else if (error.response.status === 400) { //Error 400: Bad request (missing fields)
                        this.message = "An invalid update request has been received please try again"
                    } else {    //Catch for any errors that are not specifically caught
                        this.message = "An unknown error has occurred during update"
                    }
                    errorAlert.hidden = false;          //Show alert bar
                    setTimeout(function () {    //Hide alert bar after ~5000ms
                        errorAlert.hidden = true;
                    }, 5000);
                });
            }
        }
    }
</script>

<style scoped>
    .btn {
        margin-right: 1%;
    }

    .primaryEmailDisplay {

    }

    #primaryEmailTable {
        padding-bottom: 0px;
        margin-bottom: 0px;
        border-collapse: collapse;
        font-size: 20px;
    }

    #primaryEmailTable td {
        padding: 0;
    }

    #primaryEmailLabel {
        text-align: left;
        padding-bottom: 0;
        margin-bottom: 0;
    }

    #primaryEmail {
        text-align: right;
        padding-bottom: 0;
        margin-bottom: 0;
    }





</style>