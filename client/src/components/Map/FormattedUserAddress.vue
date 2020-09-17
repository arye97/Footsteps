<template>
    <div>
        <div class="address" v-if="publicLocation">
            <h3 v-if="publicLocation" class="font-weight-light"> {{publicLocation.name}} </h3>
        </div>
        <div class="address" v-else-if="privateLocation">
            <h3 class="font-weight-light"> {{privateLocation.name}} </h3>
        </div>
        <div v-else>
            <h3 class="font-weight-light"> Location Not Yet Specified </h3>
        </div>
    </div>
</template>

<script>
export default {
    name: "FormattedAddress.vue",
    props: {
        publicLocation: {
            default: null,
            type: Object
        },
        privateLocation: {
            default: null,
            type: Object
        }
    },
    watch: {
        publicLocation: function() {
            if (this.publicLocation) {
                this.$forceUpdate();
            }
        },
        privateLocation: function() {
            if (this.privateLocation) {
                this.$forceUpdate();
            }
        }
    }

}
</script>

<style scoped>

    .address {
        text-align: center;
        width: 100%;
        padding-right: 20px;
    }

</style>