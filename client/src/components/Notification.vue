<template>
    <ul style="max-width: 30em; min-width: 15em; position: fixed; bottom: 0em; right: 2em; list-style-type: none">
        <li v-for="notification in this.notifications" v-bind:key="notification.id">
            <b-alert
                    fade
                    :show="notification.dismissCountDown"
                    dismissible
                    :variant="notification.notificationStyle"
                    @dismissed="notification.dismissCountDown=0"
            >
                {{notification.notificationMessage}}
            </b-alert>
        </li>
    </ul>
</template>

<script>
    import {store} from "../store";

    export default {
        data: function () {
            return {
                notifications: store.notifications
            }
        },
    }
</script>