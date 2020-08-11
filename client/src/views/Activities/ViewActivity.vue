<template>
    <div id="app">
        <!-- Header -->
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
        <!-- Content container -->
        <b-container class="contents" fluid>
            <div class="row h-100">
                <div class="col-12 text-center">
                    <div v-if="errored">
                        <p class="font-weight-light">{{this.error}}</p>
                        <p class="font-weight-light">{{this.errorMessage}}</p>
                    </div>
                    <div v-else>
                        <div v-if="loading"> Loading... <br/><br/><b-spinner variant="primary" label="Spinning"></b-spinner></div>
                        <div v-else class="font-weight-light">
                            <br/>
                            <h1 class="font-weight-light"><strong>{{this.activityTitle}}</strong></h1>
                            <br/>
                            <h5 class="font-weight-light" v-if="this.description">"{{this.description}}"<br/></h5>
                            <br/>
                        </div>
                    </div>
                </div>
            </div>
        </b-container>
    </div>
</template>

<script>
    import Header from "../../components/Header/Header";
    export default {
        name: "ViewActivity",
        components: {Header},
        data () {
            return {
                errored: false,
                errorMessage: "",
                loading: false,
                activityTitle: "TITLE",
                description: "DESCRIPTION"
            }
        }
    }
</script>