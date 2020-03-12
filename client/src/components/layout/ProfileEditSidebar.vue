<template>
    <div class="sidebar">
        <div class="sidebar-header">
            <h2>Edit Profile</h2>
        </div>

        <ul class="sidebar-items">
            <li class>
                <a href="/profile/details" class="sidebar-item">
                    <span class="sidebar-item-name">Details</span>
                </a>
            </li>

            <li class>
                <a href="/profile/email" class="sidebar-item">
                    <span class="sidebar-item-name">Email</span>
                </a>
            </li>

            <!--<li class>
                <a href="/profile/password" class="sidebar-item">
                    <span class="sidebar-item-name">Password</span>
                </a>
            </li>-->

        </ul>
    </div>
</template>

<script>
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

.sidebar-items li a {
    color: #707070;
    display: flex;
    padding-top: 12px;
    padding-bottom: 12px;
    padding-left: 15px;
    padding-right: 15px;
}
</style>