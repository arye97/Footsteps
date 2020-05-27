<template>
    <div>
    <div class="container">
        <div class="row">
            <div class="col-sm-6 offset-sm-3">
<!--                <Header />-->
            </div>
        </div>
    </div>
    <div class="sidebar">
        <div class="sidebar-header">
            <h2>Edit Profile</h2>
        </div>

        <ul class="sidebar-items">
            <li class>
                <router-link
                        :to="{ name: 'details',
                               params: { userId: this.userId } }"
                        class="nav-link">
                    <span class="sidebar-item-name">Details</span>
                </router-link>
            </li>
            <li class>
                <router-link
                        :to="{ name: 'emails',
                               params: { userId: this.userId } }"
                        class="nav-link">
                    <span class="sidebar-item-name">Email</span>
                </router-link>
            </li>
            <li class>
                <router-link :to="{ name: 'password',
                               params: { 'userId': this.userId } }" class="sidebar-item">
                    <span class="sidebar-item-name">Password</span>
                </router-link>
            </li>

        </ul>
        </div>
    </div>
</template>

<script>
    // import Header from '../Header/Header';
    export default {
        name: "Sidebar",
        // components: {
        //     Header
        // },
        props: ['userId'],
    }
</script>

<style scoped>
.sidebar {
    top: 56px;
    left: 0;
    bottom: 0;
    position: fixed;
    width: 220px;
    overflow: auto;
    background-color: #fafafa;
}

.sidebar-header {
    width: 100%;
    text-align: left;
    align-items: center;
    padding: 10px 16px 10px 10px;
}

.sidebar-items {
    list-style-type: none;
    align-items: center;
    padding-left: 0;
}

.sidebar-items li a:hover {
    background-color: #e6e6e6;
    padding-left: 20px;
}

.router-link-active {
    background-color: #f0f0f0;
}

.sidebar-items li a {
    color: #707070;
    display: flex;
    padding-top: 12px;
    padding-bottom: 12px;
    padding-left: 15px;
    padding-right: 15px;
}
</style>