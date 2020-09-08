<template>
    <div>
        <section v-for="outcome in this.outcomeList" :key="outcome.outcome_id">
            <b-button :id="'viewResultsBtn' + outcome.outcome_id" v-b-toggle="'collapse' + outcome.outcome_id" variant="success" block>{{outcome.title}}</b-button>
            <b-collapse :id="'collapse' + outcome.outcome_id">
                <div v-if="outcome.results.length > 0">
                    <b-card id="viewResultCard" v-for="result in outcome.results" :key="result.result_id">
                        <div v-if="result.did_not_finish">{{result.user_name}}: Did not finish</div>
                        <div v-else>{{result.user_name}}: {{result.value}} {{outcome.unit_name}}</div>
                        <div v-if="result.comment"> ({{result.comment}})</div>
                    </b-card>
                </div>
                <b-card id="noResultCard" v-else>
                    This outcome currently has no registered results
                </b-card>
            </b-collapse>
            <br/>
        </section>
    </div>
</template>

<script>
    export default {
        name: "ViewResults",
        props: {
            outcomeList: Array
        }
    }
</script>

<style scoped>

</style>