// App.vue

<template>
    <div id="app">
        <router-view />
    </div>
</template>

<script>
    import api from "./Api";
    import {mutations} from "./store";

    export default {

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