// App.vue

<template>
    <div id="app">
        <Notification></Notification>
        <router-view />
    </div>
</template>

<script>
    import Notification from "./components/Notification";
    import api from "./Api";
    import {mutations} from "./store";

    export default {
        components: {
            Notification
        },

        /**
         * This should trigger the first time the website is loaded
         * any important global events that should happen should be
         * written here. -Connor
         */
        beforeMount() {
            this.loadLoggedInUser();
        },

        methods: {
            loadLoggedInUser() {
                api.getProfileId()
                    .then(function (response) {
                        mutations.setLoggedInUser(response.data);
                    })
            }
        }
    }
</script>
