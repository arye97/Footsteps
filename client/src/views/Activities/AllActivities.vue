<template>
    <div>
    <header class="masthead">
        <br/><br/><br/>
        <div class="container h-100">
            <div class="row h-100 align-items-center">
                <div class="col-12 text-center">
                    <Header/>
                    <h1 class="font-weight-light">Welcome to Hakinakina</h1>
                    <br/>
                    <br/>
                    <h1 class="font-weight-light">
                        Find something to do!<br/>
                    </h1>
                </div>
            </div>
        </div>
    </header>
    <section>
        <!-- Search features -->
    </section>
    <hr/>
    <div>
        <b-tabs content-class="mt-4" justified>
            <b-tab title="Continous" active>
                <section v-for="activity in this.activityList" :key="activity.id">
                    <!-- Activity List -->
                    <b-card v-if="activity.continuous">
                        <b-row no-gutters>
                            <b-col md="6">
                                <b-card-text>
                                    <br/>
                                    Name: {{activity.activity_name}}
                                    <br/><br/>
                                    Description: {{activity.description}}
                                </b-card-text>
                            </b-col>
                            <b-col md="6">
                                <b-card-body class="cardButtons">
                                    <b-button variant="outline-primary" v-on:click="goToPage(`/activities/edit/${activity.id}`)">Edit</b-button>
                                </b-card-body>
                                <b-card-body class="cardButtons">
                                    <b-button variant="outline-primary" v-on:click="goToPage(`/activities/${activity.id}`)">Details</b-button>
                                </b-card-body>
                            </b-col>


                        </b-row>
                    </b-card>
                </section>
                <hr/>
                <footer class="noMore">No more activities to show</footer>
            </b-tab>
            <b-tab title="Non-Continous">
                <section v-for="activity in this.activityList" :key="activity.id">
                    <br/>
                    <!-- Activity List -->
                    <b-card v-if="!activity.continuous">
                            <b-row no-gutters>
                                <b-col md="6">
                                    <b-card-text>
                                        Name: {{activity.activity_name}} <br/><br/>
                                        Description: {{activity.description}} <br/><br/>
                                        Start Date: {{new Date(activity.start_time).toDateString()}} <br/><br/>
                                        End Date: {{new Date(activity.end_time).toDateString()}} <br/><br/>
                                        Duration: {{Math.floor(((new Date(activity.end_time) - new Date(activity.start_time))/1000/60/60/24))}} Days
                                        {{Math.floor(((new Date(activity.end_time) - new Date(activity.start_time))/1000/60/60/365))}} Hours
                                    </b-card-text>
                                </b-col>
                                <b-col md="6">
                                    <b-card-body class="cardButtons">
                                        <b-button variant="outline-primary" v-on:click="goToPage(`/activities/edit/${activity.id}`)">Edit</b-button>
                                    </b-card-body>
                                    <b-card-body class="cardButtons">
                                        <b-button variant="outline-primary" v-on:click="goToPage(`/activities/${activity.id}`)">Details</b-button>
                                    </b-card-body>
                                </b-col>
                            </b-row>
                    </b-card>
                </section>
                <hr/>
                <footer class="noMore">No more activities to show</footer>
            </b-tab>

        </b-tabs>
    </div>

    <br/>
    </div>

</template>

<script>
    import Header from '../../components/Header/Header.vue'
    import server from "../../Api";
    export default {
        name: "AllActivities",
        components : {
            Header
        },
        data() {
            return {
                activityList : [],
                userId : null,
                noMore: false
            }
        },
        async mounted() {
            await this.getUserId();
            this.checkLoggedIn();
            await this.getListOfActivities();
        },
        methods: {
            checkLoggedIn() {
                if (!sessionStorage.getItem("token")) {
                    this.$router.push("/login");
                }
            },
            async getUserId() {
                await server.get(`/profiles/userId`, {
                    headers: {"Access-Control-Allow-Origin": "*", "Content-Type" : "application/json",
                        "Token" : sessionStorage.getItem("token")},
                        withCredentials: true
                }).then(response => {
                    this.userId = response.data;
                }).catch(error => {
                    console.error(error);
                })
            },
            async getListOfActivities() {
                await server.get(`/profiles/${this.userId}/activities`, {
                    headers: {"Access-Control-Allow-Origin": "*", "Content-Type": "application/json",
                        "Token" : sessionStorage.getItem("token")},
                        withCredentials: true
                    }).then(response => { //If successfully registered the response will have a status of 201

                        if (response.data.length === 0) {
                            this.noMore = true;
                        }
                        this.activityList = response.data;
                    }).catch(error => {
                        console.error(error);
                })
            },
            goToPage(url) {
                this.$router.push(url);
            }
        }
    }
</script>

<style scoped>
    .noMore {
        text-align: center;

    }
    .cardButtons {
        display: flex;
        align-items: center;
        justify-content: center;
    }
</style>