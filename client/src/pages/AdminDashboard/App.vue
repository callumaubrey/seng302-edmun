<template>
    <div id="app">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
        <div>
            <b-tabs>
                <b-tab title="Manage Users">
                    <Search></Search>
                </b-tab>

                <b-tab title="Command">
                    <label>Placeholder</label>
                </b-tab>
            </b-tabs>
        </div>
    </div>
</template>

<script>
    import NavBar from "@/components/NavBar.vue";
    import Search from '@/components/Search.vue';
    import axios from "axios";

    const App = {
        name: 'App',
        components: {
            NavBar, Search
        },
        data: function () {
            return {
                isLoggedIn: false,
                userName: "",
                primaryEmail: "",
                userRoles: []
            }
        },
        methods: {
            getUserSession: function () {
                let currentObj = this;
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/profiles/user')
                    .then(function (response) {
                        currentObj.isLoggedIn = true;
                        currentObj.userName = response.data.firstname;
                        currentObj.userRoles = response.data.roles;
                        let isAdmin = false;
                        for (let i = 0; i < currentObj.userRoles.length; i++) {
                            if (currentObj.userRoles[i].roleName === "ROLE_ADMIN") {
                                isAdmin = true;
                            }
                        }
                        if (isAdmin === false) {
                            const profileId = response.data.id.toString();
                            currentObj.$router.push('/profiles/' + profileId)
                        }
                    })
                    .catch(function () {
                        currentObj.isLoggedIn = false;
                        currentObj.$router.push('/');
                    });
            }
        }
    };

    export default App
</script>

<style scoped>

</style>