<template>
    <div>
        <b-alert dismissible variant="danger" id="addResultAlert"
                 :show="dismissCountDown"
                 @dismissed="dismissCountDown=0"
                 @dismiss-count-down="countDownChanged">
            {{ this.errorMessage }}
        </b-alert>
        <section v-for="outcome in this.outcomeList" :key="outcome.outcome_id">
            <b-card id="outcomeAddResultCard" :title="outcome.title">
                <div v-if="outcome.activeUsersResult.submitted">
                    <div style="color:red" v-if="outcome.activeUsersResult.did_not_finish">
                        Did not Finish
                    </div>
                    <b-input-group
                            :append="outcome.unit_name">
                        <b-input :id="'submittedInputValue' + outcome.outcome_id"
                                 v-model="outcome.activeUsersResult.value"
                                 disabled></b-input>
                    </b-input-group>
                </div>
                <div v-else>
                    <b-input-group
                            :append="outcome.unit_name">
                        <b-input :id="'NotSubmittedInputValue' + outcome.outcome_id"
                                 v-model="outcome.activeUsersResult.value"
                                 placeholder="Input your result here..."></b-input>
                    </b-input-group>
                    <b-form-checkbox
                            :id="'checkboxDNF' + outcome.outcome_id"
                            v-model="outcome.activeUsersResult.did_not_finish">
                        I did not finish
                    </b-form-checkbox>
                    <b-button block :id="'submitResult' + outcome.outcome_id" variant="success"
                              @click="submitOutcomeResult(outcome.outcome_id)">
                        Submit
                    </b-button>
                </div>
            </b-card>
        </section>
    </div>
</template>

<script>
    export default {
        name: "AddResults",
        props: {
            outcomeList: Array,
            dismissCountDown: Number,
            errorMessage: String
        },
        data() {
            return {
                dismissSecs: 5
            }
        },
        methods: {
            /**
             * Update dismissCountDown
             */
            countDownChanged(dismissCountDown) {
                this.$emit('count-down-changed', dismissCountDown);
            },
            /**
             * Send the outcome result to the parent component
             * @param outcomeId
             */
            submitOutcomeResult(outcomeId) {
                this.$emit('submit-result', outcomeId);
            }
        }
    }
</script>

<style scoped>

</style>