<template>
    <div>
        <h1><br/><br/></h1>
        <div>
            <Header/>
            <router-view></router-view>
        </div>
        <b-container class="container-fluid contents b-container" fluid="lg">
            <div class="row h-100 align-items-center">
                <div class="col-12 text-center">
                    <br/>
                    <h1 class="font-weight-light"><strong>Edit Profile Details</strong></h1><br/>
                </div>
                <div class="col-12 text-center h-100">
                        <b-tabs content-class="mt-3" align="center">
                            <b-tab title="My Details" active>
                                <EditDetails/>
                            </b-tab>
                            <b-tab title="My Password">
                                <EditPassword/>
                            </b-tab>
                            <b-tab title="My Emails">
                                <EditEmail/>
                            </b-tab>
                            <b-tab title="My Location">
                                <EditLocation/>
                            </b-tab>
                        </b-tabs>
                </div>
            </div>
        </b-container>
        <br/><br/>
    </div>
</template>

<script>
    import Header from '../../components/Header/Header.vue'
    import EditDetails from "../../components/Settings/EditDetails";
    import EditEmail from "../../components/Settings/EditEmail";
    import EditPassword from "../../components/Settings/EditPassword";
    import EditLocation from "../../components/Settings/EditLocation";

    export default {
        name: "EditProfile.vue",
        components: {Header, EditDetails, EditPassword, EditEmail, EditLocation}
    }
</script>
<style scoped>

    @media screen and (max-width: 1000px) and (min-width: 600px){
        .b-container {
            margin-left: -7.5em;
            min-width: 175%;
        }
    }

    @media screen and (max-width: 800px) and (min-width: 580px){
        .b-container {
            margin-left: -5em;
            min-width: 175%;
        }
    }
</style>