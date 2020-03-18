<template>
    <div>
        <h1>Hi {{firstname + " " + lastname}}!</h1>
        <p>You're logged in to your Hakinakina Account</p>
        <h3>All about you</h3>
        <p v-if="nickname">Nickname: {{ nickname }}</p>
        <p>Gender: {{ gender }}</p>
        <p>Date Of Birth: {{ dob }}</p>
        <p>Email: {{ email }}</p>
        <p v-if="fitnessLevel">Fitness Level: {{ fitnessLevel }}</p>
        <p v-if="passportCountry">Passport Country: {{ passportCountry }}</p>
        <p v-if="bio">Bio: {{ bio }}</p>
    </div>
</template>

<script>
    //import server from '../../';
    export default {
        name: "ViewUser",
        data() {
            return {
                firstname: '',
                middlename: '',
                lastname: '',
                primary_email: '',
                password: '',
                nickname: '',
                gender: '',
                dob: '',
                fitnessLevel: '',
                passportCountry: '',
                bio: ''
            }
        }
    }
</script>
