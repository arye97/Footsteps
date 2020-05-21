<template>
    <div id="app">
        <div class="container">
            <div class="row">
                <div class="col-sm-12 text-center">
                    <Header/>
                    <br/><br/><br/>
                    <h1 class="font-weight-light">Welcome to Hakinakina!</h1><hr/>
                    <h2 class="font-weight-light">You're logged in as an admin!</h2><br/>
                    <h3 class="font-weight-light">The admin dashboard is due in a later release, but you are in the right place!</h3>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import Header from '../components/Header/Header';

    export default {
        name: "ViewUser",
        components: {
            Header
        },
        async mounted() {

        }
    }
</script>