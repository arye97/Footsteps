<template>

    <b-progress :max="max" v-if="!loading"
                v-b-popover.hover.top="'Target fitness level: ' +
                (activityFitnessLevel + 1) +
                ' Your fitness level: ' +
                ((userFitnessLevel < 0) ? 'N/A' : userFitnessLevel + 1)"
    >
        <b-progress-bar variant="success" :value="(isGreen) ? value[0] : 0"></b-progress-bar>
        <b-progress-bar variant="warning" :value="(!isGreen) ? value[0] : 0"></b-progress-bar>

        <b-progress-bar class="white-space" :value="value[1]"></b-progress-bar>

        <b-progress-bar variant="danger" :value="value[2]"></b-progress-bar>

        <b-progress-bar variant="success" :value="(isGreen) ? value[3] : 0"></b-progress-bar>
        <b-progress-bar variant="warning" :value="(!isGreen) ? value[3] : 0"></b-progress-bar>

    </b-progress>

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
        if (this.activityFitnessLevel < 0) return
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

</style>