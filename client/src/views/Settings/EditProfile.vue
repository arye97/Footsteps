<template>
    <div>
        <h1><br/><br/></h1>
        <b-container class="container-fluid contents" fluid>
            <h1><br/></h1>
            <div>
                <div class="container">
                    <div class="row">
                        <div class="col-sm-6 offset-sm-3">
                            <Header/>
                            <router-view></router-view>
                        </div>
                    </div>
                </div>
            </div>
            <header class="masthead">
                <div class="container h-100">
                    <div class="row h-100 align-items-center">
                        <div class="col-12 text-center">
                            <h1 class="font-weight-light"><strong>Edit Profile Details</strong></h1><br/>
                            <template>
                                <b-tabs content-class="mt-3" align="center">
                                    <b-tab title="My Details" active>
                                        <Details/>
                                    </b-tab>
                                    <b-tab title="My Password">
                                        <EditPassword/>
                                    </b-tab>
                                    <b-tab title="My Emails">
                                        <EditEmail/>
                                    </b-tab>
                                </b-tabs>

                            </template>
                        </div>

                    </div>
                </div>
            </header>
        </b-container>
        <br/><br/>
    </div>
</template>

<script>
    import Header from '../../components/Header/Header.vue'
    import Details from './Details'
    import EditEmail from "./EditEmail";
    import EditPassword from "./EditPassword";

    export default {
        name: "EditProfile.vue",
        components: {Header, Details, EditPassword, EditEmail}
    }
</script>
<style scoped>
</style>