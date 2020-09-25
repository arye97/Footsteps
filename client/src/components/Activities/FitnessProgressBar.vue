<template>
    <div v-if="!loading" class="fit-bar">
        <b-row class="fit-p">
            <b-col align="left"><p>0</p></b-col>
            <b-col align="left" style="padding-left: 2em; padding-right: 1em"><p>1</p></b-col>
            <b-col align="left" style="padding-left: 2.5em; padding-right: 0"><p>2</p></b-col>
            <b-col align="right" style="padding-left: 0; padding-right: 2.5em"><p>3</p></b-col>
            <b-col align="right" style="padding-left: 1em; padding-right: 2em"><p>4</p></b-col>
            <b-col align="right"><p>5</p></b-col>
        </b-row>
        <b-progress :max="max"
                    v-b-popover.hover.top="'Target fitness level: ' +
                (activityFitnessLevel + 1) +
                ' Your fitness level: ' +
                ((userFitnessLevel < 0) ? 'N/A' : userFitnessLevel + 1)"
        >
            <b-progress-bar variant="success" :value="(isGreen) ? value[0] : 0"></b-progress-bar>
            <b-progress-bar variant="warning" :value="(!isGreen) ? value[0] : 0"></b-progress-bar>

            <b-progress-bar class="white-space" :value="value[1]"></b-progress-bar>

            <b-progress-bar variant="dark" :value="value[2]"></b-progress-bar>

            <b-progress-bar variant="success" :value="(isGreen) ? value[3] : 0"></b-progress-bar>
            <b-progress-bar variant="warning" :value="(!isGreen) ? value[3] : 0"></b-progress-bar>

        </b-progress>
    </div>
</template>

<script>
    export default {
        name: "FitnessProgressBar",
        props: {
            userFitnessLevel: Number,
            activityFitnessLevel: Number
        },
        data() {
            return {
                max: 5,
                isGreen: true,
                value: [0, 0, 0, 0],
                loading: true
            }
        },
        mounted() {
            if (this.activityFitnessLevel < 0 || this.activityFitnessLevel === null) return;
            this.value[2] = 0.05;
            if (this.userFitnessLevel < 0) {
                this.value[1] = this.activityFitnessLevel + 1;
                this.loading = false;
                return
            }

            if (this.userFitnessLevel < this.activityFitnessLevel) {
                this.isGreen = false;
                this.value[0] = this.userFitnessLevel + 1;
                this.value[1] = this.activityFitnessLevel - this.userFitnessLevel;
            }

            if (this.userFitnessLevel >= this.activityFitnessLevel) {
                this.value[0] = this.activityFitnessLevel + 1;
                this.value[3] = this.userFitnessLevel - this.activityFitnessLevel;
            }

            this.loading = false;
        }
    }

</script>

<style scoped>

    .white-space {
        background-color: #e9ecef
    }

    .fit-p p {
        margin-top: 0;
        margin-bottom: 0;
        font-size: 0.8em
    }

    .fit-bar {
        margin-bottom: 1em;
    }

</style>