<template>
    <b-card class="flex-fill" border-variant="secondary" id="viewParticipantsCard">
        <strong>Participants: </strong><br>
        <b-button-group v-for="participant in participants" :key="participant.id" id="viewParticipantsButtonGroup">
            <b-button
                    class="participantButton" pill variant="success"
                    v-on:click="toUserProfile(participant.id)" id="participant">{{participant.name}}</b-button>
        </b-button-group>
        <p v-if="participants.length === 0" id="noParticipants">No participants to show</p>
    </b-card>
</template>

<script>
    export default {
        name: "ViewParticipants",
        props: {
            participants: Array
        },
        methods: {
            /**
             * Sends you to the profile page of a specific user
             * @param id the user's ID
             */
            toUserProfile(id) {
                this.$router.push({ name: 'profile', params: {userId: id} });
            },
        }
    }
</script>

<style scoped>

</style>