// App.vue

<template>
    <div id="app">
        <Notification></Notification>
        <router-view />
    </div>
</template>

<script>
    import Notification from "./components/Notification";
    export default {
        components: {
            Notification
        }
    }
</script>