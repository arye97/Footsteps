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
        <Header/>
        <br/>
        <div class="container h-100">
            <b-row>
                <b-col cols="8">
                    <multiselect v-model="selectedActivityTypes" id="activityTypes" v-on:select="unDisplayRules"
                                 :options="activityTypes" :multiple="true" :searchable="true" :close-on-select="false"
                                 placeholder="Select your activity types">
                        <template slot="noResult">Invalid activity type</template>
                    </multiselect>
                </b-col>
                <b-col cols="4">
                    <b-form-select v-model="searchMode" :options="searchModes"></b-form-select>
                </b-col>
            </b-row>
            <b-row style="margin-bottom: 1.7em; margin-top: 0.8em">
                <b-col cols="2" align-self="center">
                    <b-button variant="primary" v-on:click="search()">
                        Search</b-button>
                </b-col>
                <b-col cols="4" align-self="center">
                    <b-form-radio v-model="searchType" name="andType" value="and">Search including all</b-form-radio>
                </b-col>
                <b-col cols="4" align-self="center">
                    <b-form-radio v-model="searchType" name="orType" value="or">Search including some</b-form-radio>
                </b-col>
            </b-row>
        </div>
        <div style="text-align: center">
            <b-card>
                <b-card-text>{{ searchMode }}</b-card-text>
                <b-card-text>{{ selectedActivityTypes }}</b-card-text>
                <b-card-text>{{ searchType }}</b-card-text>
            </b-card>

        </div>
    </b-container>
    </div>
</template>

<script>
    import Header from '../components/Header/Header.vue';
    import Multiselect from 'vue-multiselect'

    export default {
        name: "Search",
        components: {
            Header,
            Multiselect
        },

        data() {
            return {
                searchMode: 'activityType',
                searchModes: [
                    { value: 'activityType', text: 'Activity Type'}
                ],
                selectedActivityTypes : [],
                activityTypes: [
                    "Archery",
                    "E-sports",
                    "Fencing",
                    "Boating"
                ],
                searchType: "and"
            }
        },

        methods: {
            goToPage(url) {
                this.$router.push(url);
            },

            search() {
                // called when search button is hit
                //todo: add functionality
            }
        }
    }


</script>

<style scoped>

</style>