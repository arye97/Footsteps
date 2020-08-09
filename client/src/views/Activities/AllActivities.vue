<template>
    <div>
        <header class="masthead">
            <br/><br/><br/>
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <Header/>
                        <br/>
                    </div>
                </div>
            </div>
        </header>
        <b-container class="contents">
            <div class="container h-100">
                <div class="row h-100 align-items-center">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light">
                            <br/>
                            Find something to do!<br/>
                        </h1>
                    </div>
                </div>
            </div>
        <b-alert variant="success" dismissible fade :show=this.alertCount>
            {{this.alertMessage}}
        </b-alert>
        <div style="text-align: center">
            <b-button style="margin-bottom: 1.7em; margin-top: 0.8em" variant="primary" v-on:click="goToPage('/activities/create')">Create New Activity</b-button>
        </div>
        <section>
            <ActivityList v-if="userId !== null" :user_-id="this.userId"/>
        </section>

        </b-container>
    </div>

</template>

<script>
    import Header from '../../components/Header/Header.vue';
    import api from "../../Api";
    import ActivityList from "../../components/Activities/ActivityList";
    export default {
        name: "AllActivities",
        components : {
            ActivityList,
            Header
        },
        data() {
            return {
                userId : null,
                isHovered: false
            }
        },
        props: {
            alertCount: {
                type: Number,
                default: 0
            },
            alertMessage: {
                type: String,
                default: ''
            }
        },
        beforeMount() {
            this.checkLoggedIn();
        },
        async mounted() {
            this.userId = await this.getUserId();
        },
        methods: {
            footerHover(hovered) {
                this.isHovered = hovered;
            },
            checkLoggedIn() {
                if (!sessionStorage.getItem("token")) {
                    this.$router.push("/login");
                }
            },
            /**
             * Get the Id of the current Logged in user.
             * @returns {Promise<*>}
             */
            async getUserId() {
                let userId = null;
                await api.getUserId().then(response => {
                    userId = response.data;
                    this.creatorId = userId;
                });
                return userId
            },
            goToPage(url) {
                this.$router.push(url);
            },
        }
    }
</script>

<style scoped>
    .noMore {
        text-align: center;

    }
    .text-justified {
        text-align: justify;
    }

    .activity-button-group {
        padding-top: 7.5%;
        padding-right: 40%;
        padding-bottom: 7.5%;
    }

    .activity-button-group button {
        margin-left: 35.5%;
        width: 110%;
    }

    .footerButton {
        width: 100%;
    }

    .footSteps {
        width: 7.5%;
        height: 7.5%;
    }

    .footStepsSimplified {
        width: 16%;
        height: 16%;
    }
</style>