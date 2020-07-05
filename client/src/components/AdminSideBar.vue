<template>
    <div>
        <b-button @click="getOtherUserRole()" v-b-toggle.sidebar-1
                  v-if="loggedInIsAdmin && loggedInId != $route.params.id">Admin
        </b-button>
        <b-sidebar id="sidebar-1" :title="'Admin Controls: ' + userData.firstname + ' ' + userData.lastname" shadow>
            <div class="px-3 py-2">
                Currently the user is a: <p v-if="profileIsAdmin">Admin</p>
                <p v-else>Standard User</p>
                Possible actions:
                <br>
                <b-button @click="toggleAdminRights" v-if="profileIsAdmin" variant="danger">Remove Admin Privilege
                </b-button>
                <b-button @click="toggleAdminRights" v-else variant="success">Add Admin Privilege</b-button>
                <p style="color:red;" v-if="roleEditedErrorMsg != null">{{roleEditedErrorMsg}}</p>
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
                profileIsAdmin: null,
                roleEditedErrorMsg: null
            }
        },
        methods: {
            getOtherUserRole: function () {
                const currentObj = this;
                return axios.get('http://localhost:9499/admin/role/' + this.$route.params.id)
                    .then(function (response) {
                        currentObj.profileIsAdmin = false
                        response.data.forEach(function (item) {
                            if (item.roleName === "ROLE_ADMIN" || item.roleName === "ROLE_USER_ADMIN") {
                                currentObj.profileIsAdmin = true;
                            }
                        })
                    })
                    .catch(function (error) {
                        console.log(error)
                    });
            },
            toggleAdminRights: function () {
                let vue = this;
                axios.put('http://localhost:9499/admin/profiles/' + this.$route.params.id + '/role', {
                    role: "ROLE_USER_ADMIN"
                })
                    .then(function () {
                        vue.roleEditedErrorMsg = null;
                        vue.getOtherUserRole();
                    })
                    .catch(function (err) {
                        vue.roleEditedErrorMsg = err.data;
                    })
            }
        }
    }
    export default AdminSideBar

</script>

<style scoped>

</style>