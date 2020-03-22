<template>
    <div id="app">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>

        <div class="container">
            <div>
                <b-row>
                    <b-img center v-bind="mainProps" rounded= "circle" src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.f4t8FtSbfNUs94KKgMhhBAHaHr%26pid%3DApi%26h%3D160&f=1" alt="Center image"></b-img>
                </b-row>
                <b-row><h3></h3></b-row>
                <b-row align-h="center">
                    <h3>{{userData.firstname}} {{userData.lastname}} (Admin User)</h3>
                </b-row>
                <b-tabs content-class="mt-3" align="center">
                    <b-tab title="About" active>
                        <b-card style="margin: 1em" title="About:">
                            <b-row>
                                <b-col><strong>Email:</strong></b-col>
                                <b-col v-if="primaryEmail != null"><p>{{primaryEmail}}</p></b-col>
                                <b-col v-else><p>No nickname</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><strong>Roles:</strong></b-col>
                                <b-col>
                                    <div v-bind:key="role" v-for="role in userRoles">
                                        {{ role.roleName }}
                                    </div>
                                </b-col>
                            </b-row>
                        </b-card>
                    </b-tab>

                </b-tabs>
            </div>

        </div>
    </div>
</template>



<script>
    // import api from '../Api';
    import NavBar from "@/components/NavBar.vue"

    const App = {
        name: 'App',
        components: {
            NavBar
        },
        data: function() {
            return {
                userData: '',
                passports: [],
                activites: [],
                additionalEmails: [],
                isLoggedIn: false,
                userName: "",
                locations: [],
                primaryEmail: "",
                userRoles: []
            }
        },
        methods: {
            getUserSession: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profile/user')
                    .then(function (response) {
                        console.log(response.data);
                        currentObj.userData = response.data;
                        currentObj.passports = response.data.passports;
                        currentObj.activites = response.data.activityTypes;
                        currentObj.additionalEmails = response.data.additionalemail;
                        currentObj.isLoggedIn = true;
                        currentObj.userName = response.data.firstname;
                        currentObj.primaryEmail = response.data.email.address;
                        currentObj.userRoles = response.data.roles;
                        let isAdmin = false;
                        for (let i = 0; i < currentObj.userRoles.length; i++) {
                            if (currentObj.userRoles[i].roleName === "ROLE_ADMIN") {
                                isAdmin = true;
                            }
                        }
                        if (isAdmin === false) {
                            window.location.href = '/profile';
                        }
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                        currentObj.isLoggedIn = false;
                        window.location.href = '/login';
                    });
            },
            getLocationData: async function () {
                var locationText = document.getElementById("locationInput").value;
                if (locationText == ''){
                    return
                }
                var locationData = this.axios.create({
                    baseURL: 'https://nominatim.openstreetmap.org/search/city/?q="' + locationText + '"&format=json&limit=5',
                    timeout: 1000,
                    withCredentials: false,
                });
                console.log('https://nominatim.openstreetmap.org/search?q="' + locationText + '"&format=json&limit=5');
                var data = await (locationData.get());
                this.locations = data.data
            },
            setLocationInput: function (location) {
                document.getElementById("locationInput").value = location.display_name;
                this.locations = []
            }
        },
        mounted: function () {
            this.getUserSession();
            this.getLocationData();
        }
    };

    export default App;
</script>
