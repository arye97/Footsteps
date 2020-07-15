<template>
    <div id="app">
        <div class="container">
            <div class="row">
                <div class="col-sm-12 text-center">
                    <Header/>
                    <br/><br/><br/>
                    <h1 class="font-weight-light">Welcome to Hakinakina!</h1><hr/>
                    <div v-if="this.loading"> {{message}}</div>
                    <div v-else>
                        <h2 class="font-weight-light">You're logged in as an admin!</h2><br/>
                        <h3 class="font-weight-light">The admin dashboard is due in a later release, but you are in the right place!</h3>
                    </div>
                </div>
            </div>
        </div>
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