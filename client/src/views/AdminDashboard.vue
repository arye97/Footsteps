<template>
    <div id="app">
        <h1><br/><br/></h1>
        <b-container class="contents" fluid>
            <div class="container">
                <Header/>
                <div class="row h-100">
                    <div class="col-12 text-center">
                        <h1 class="font-weight-light"><strong>Welcome to Hakinakina</strong></h1>
                        <div v-if="this.loading">
                            <p class="lead">{{message}}</p><br/>
                        </div>
                        <div v-else>
                            <p class="lead">You're logged in as an admin!</p>
                            <p class="lead">The admin dashboard is due in a later release, but you are in the right place!</p>
                        </div>
                    </div>
                </div>
            </div>
        </b-container>
    </div>
</template>

<script>
    import Header from '../components/Header/Header';
    import server from "../Api";
    export default {
        name: "ViewUser",
        components: {
            Header
        },
        data() {
            return {
                adminData: null,
                loading: true,
                message: 'Loading...'
            }
        },
        async mounted() {
            await this.init();
        },
        methods: {
            async init() {
                await server.get(  '/profiles',
                    {headers:
                            {"Access-Control-Allow-Origin": "*", 'Content-Type': 'application/json', 'Token': sessionStorage.getItem("token")}, withCredentials: true
                    }, )
                    .then(response => {
                        this.adminData = response.data;
                        // Check if the user returned is an admin
                        if (this.adminData.role === 20 || this.adminData.role === 10) {
                            // The user is an admin: show them the admin dashboard!
                            this.loading = false;
                        }
                        else {
                            this.adminData = null;
                            // This user is not an admin and so cannot see the admin dashboard -> redirect to home
                            this.$router.push("/profile");
                        }
                    }).catch(error => {
                        if (error.response.status === 401 || error.response.status === 404) {
                            this.$router.push("/login");
                        } else {
                            this.message = "An error has occurred, please try again later";
                        }
                    });
            }
        }
    }
</script>