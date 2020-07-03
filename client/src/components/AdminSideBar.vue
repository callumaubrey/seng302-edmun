<template>
    <div>
        <b-button @click="getUserRole()" v-b-toggle.sidebar-1 v-if="loggedInIsAdmin && loggedInId != $route.params.id">
            Admin Controls
        </b-button>
        <b-sidebar id="sidebar-1" :title="'Admin Controls: ' + userData.firstname + ' ' + userData.lastname" shadow>
            <div class="px-3 py-2">
                Currently the user is a: <p v-if="profileIsAdmin">Admin</p>
                <p v-else>Standard User</p>
                Possible actions:
                <b-button v-if="profileIsAdmin">Remove admin privileges</b-button>
                <b-button v-else>Add admin privileges</b-button>
            </div>
        </b-sidebar>
    </div>

</template>

<script>
    import axios from 'axios'

    const AdminSideBar = {
        name: "AdminSideBar",
        props: {
            loggedInIsAdmin: {
                type: Boolean,
                default: null,
            },
            userData: {
                type: Object,
                default: null,
            },
            loggedInId: {
                type: Number,
                default: null
            },
        },
        data: function () {
            return {
                profileIsAdmin:null
            }
        },
        methods: {
            getUserRole: function () {
                const currentObj = this;
                return axios.get('http://localhost:9499/admin/role/' + this.$route.params.id)
                    .then(function (response) {
                        currentObj.profileIsAdmin= false
                        response.data.forEach(function(item) {
                            if (item.roleName === "ROLE_ADMIN") {
                                currentObj.profileIsAdmin = true;
                            }
                        })
                    })
                    .catch(function (error) {
                        console.log(error)
                    });
            },


        }
    }
    export default AdminSideBar

</script>

<style scoped>

</style>