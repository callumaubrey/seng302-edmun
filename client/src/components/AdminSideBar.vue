<template>
    <div>
        <b-button v-b-toggle.sidebar-1 v-if="loggedInIsAdmin && loggedInId != $route.params.id" @click="$parent.getUserRole()" >Admin</b-button>
        <b-sidebar id="sidebar-1" :title="'Admin Controls: ' + userData.firstname + ' ' + userData.lastname" shadow>
            <div class="px-3 py-2">
                Currently the user is a:   <p v-if="notLoggedInIsAdmin">Admin</p> <p v-if="notLoggedInIsAdmin == false">Standard User</p>
                Possible actions:
                <b-button v-if="notLoggedInIsAdmin" >Remove admin privileges</b-button>
                <b-button v-else>Add admin privileges</b-button>
            </div>
        </b-sidebar>
    </div>

</template>

<script>
    const AdminSideBar = {
        name: "AdminSideBar",
        props: {
            loggedInIsAdmin: {
                type: Boolean,
                default: null,
            },
            userData: {
            },
            notLoggedInIsAdmin: {
                type: Boolean,
            },
            loggedInId: {
            },
        },
        data: function () {
            return {
            }

        }
    }
    export default AdminSideBar

</script>

<style scoped>

</style>