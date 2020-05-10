<template>
    <div id="app">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName" v-bind:hideElements="hidden" v-bind:loggedInId="loggedInID"></NavBar>
        <div class="container">
            <div>
                <b-row>
                <b-img center rounded= "circle" width ="150px" height="150px" src="https://www.signtech.co.nz/wp-content/uploads/2019/08/facebook-blank-face-blank-300x298.jpg" alt="Center image"></b-img>
                </b-row>
                <b-row><h3></h3></b-row>
                <b-row align-h="center">
                    <h3>{{userData.firstname}} {{userData.lastname}} </h3>
                </b-row>
                <b-tabs content-class="mt-3" align="center">
                    <b-tab title="About" active>
                        <b-card style="margin: 1em" title="About:" >
                            <b-row>
                                <b-col><b>Nickname:</b></b-col>
                                <b-col v-if="userData.nickname != null"><p >{{userData.nickname}}</p></b-col>
                                <b-col v-else><p>No nickname</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><b>Date of Birth:</b></b-col>
                                <b-col><p>{{dob}}</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><b>Gender:</b></b-col>
                                <b-col><p>{{userData.gender}}</p></b-col>
                            </b-row>
                        </b-card>
                        <b-card style="margin: 1em" title="Email(s):" v-if="!hidden">
                            <b-row>
                                <b-col><b>Primary Email:</b></b-col>
                                <b-col v-if="userData.primary_email"><p>{{userData.primary_email.address}}</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><b>Additional Emails:</b></b-col>
                                <b-col v-if="additionalEmails == []">
                                    <p>No additional emails</p>
                                </b-col>
                                <b-col v-else>
                                    <p>
                                    <ul>
                                        <li v-for="item in additionalEmails" :key="item.address">
                                            {{item.address}}
                                        </li>
                                    </ul>
                                    </p>
                                </b-col>
                            </b-row>
                            <p>(Max 5 emails)</p>
                        </b-card>
                        <b-card style="margin: 1em" title="Passport Info:" v-if="!hidden">
                            <b-row>
                                <b-col><b>Your Passport Countries:</b></b-col>
                                <b-col>
                                    <p>
                                    <ul>
                                        <li v-for="item in passports" :key="item.countryName">
                                            {{ item.countryName }}
                                        </li>
                                    </ul>
                                    </p>
                                </b-col>
                            </b-row>
                        </b-card>

                        <b-card style="margin: 1em" title="Fitness Info:">
                            <b-row>
                                <b-col><b>Fitness Level:</b></b-col>
                                <b-col v-if="userData.fitness == 0"><p>Couch potato</p></b-col>
                                <b-col v-if="userData.fitness == 1"><p>Some activity</p></b-col>
                                <b-col v-if="userData.fitness == 2"><p>Average fitness level</p></b-col>
                                <b-col v-if="userData.fitness == 3"><p>Above average fitness</p></b-col>
                                <b-col v-if="userData.fitness == 4"><p>Serious athlete</p></b-col>
                                <b-col v-if="userData.fitness == null"><p>Edit your profile to add a fitness level!</p></b-col>
                            </b-row>
                        </b-card>
                    </b-tab>

                    <b-tab title="Location Info" >
                        <b-card style="margin: 1em;" title="Location Info:">
                            <b-col v-if="userData.location">
                                <b-row>
                                    City: {{userData.location.city}}
                                </b-row>
                                <b-row>
                                    State: {{userData.location.state}}
                                </b-row>
                                <b-row>
                                    Country: {{userData.location.country}}
                                </b-row>
                            </b-col>
                        </b-card>
                    </b-tab>
                    <b-tab title="Activity Info" >
                        <b-card style="margin: 1em" title="Activity Info:">
                            <b-row>
                                <b-col><b>Activity Types</b></b-col>
                                <b-col>
                                    <p>
                                    <ul>
                                        <li :key="item" v-for="item in activities">
                                            {{ item }}
                                        </li>
                                    </ul>
                                    </p>
                                </b-col>
                            </b-row>
                            <b-row>
                                <b-col>
                                    <b-link @click="goToActivities">Click Here to view {{userName}}'s Activties</b-link>
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
                loggedInUser: '',
                loggedInID: '',
                userData: '',
                passports: [],
                activities: [],
                additionalEmails: [],
                isLoggedIn: '',
                userName: "",
                locations: [],
                userRoles: [],
                profileId: '',
                location: '',
                dob: '',
                hidden: null
            }
        },
        methods: {
            getLoggedInUserData: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/user')
                    .then(function (response) {
                        console.log("Logged in as:", response.data);
                        currentObj.loggedInUser = response.data;
                        currentObj.loggedInID = response.data.id;
                        currentObj.userName = response.data.firstname;
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                        currentObj.isLoggedIn = false;
                        currentObj.$router.push('/login');
                    });
            },
            getProfileData: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/' + this.$route.params.id)
                    .then(function (response) {
                        console.log("Viewing Profile:", response.data);
                        currentObj.profileId = response.data.id;
                        currentObj.userData = response.data;
                        currentObj.passports = response.data.passports;
                        currentObj.activities = response.data.activities;
                        currentObj.additionalEmails = response.data.additional_email;
                        currentObj.isLoggedIn = true;
                        currentObj.userRoles = response.data.roles;
                        currentObj.getCorrectDateFormat(response.data.date_of_birth, currentObj)
                        let isAdmin = false;
                        for (let i = 0; i < currentObj.userRoles.length; i++) {
                            if (currentObj.userRoles[i].roleName === "ROLE_ADMIN") {
                                isAdmin = true;
                            }
                        }
                        if (isAdmin === false) {
                            currentObj.profileId = response.data.id.toString();
                            currentObj.$router.push('/profiles/' + currentObj.profileId)
                        }

                        currentObj.userName = response.data.firstname;
                        currentObj.location = response.data.location
                        currentObj.checkHideElements();
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                        if (currentObj.isLoggedIn) {
                            // currentObj.$router.push('/profiles/' + currentObj.profileId);
                            currentObj.$router.go(0);
                        } else {
                            currentObj.$router.push('/');
                        }
                    });
            },

            getCorrectDateFormat: function (date, currentObj) {
                const dobCorrectFormat = new Date(date + "T00:00");
                currentObj.dob = dobCorrectFormat.toLocaleDateString();
            },
            checkHideElements: function () {
                let currentObj = this;
                console.log("loggedInID", currentObj.loggedInID)
                console.log("profileId", currentObj.profileId)
                if (currentObj.loggedInID == currentObj.profileId){
                    currentObj.hidden = false;
                }
                else{
                    currentObj.hidden = true;
                }
            },

            getLocationData: async function () {
                var locationText = document.getElementById("locationInput").value;
                if (locationText == ''){
                    return
                }
                var locationData = this.axios.create({
                    baseURL: 'https://nominatim.openstreetmap.org/search/city/?q="' + locationText + '"&format=json&limit=10',
                    timeout: 1000,
                    withCredentials: false,
                });
                console.log('https://nominatim.openstreetmap.org/search?q="' + locationText + '"&format=json&limit=5');
                var data = await (locationData.get());
                this.locations = data.data
            },
            getUserId: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/id')
                    .then(function (response) {
                        currentObj.profileId = response.data;
                        currentObj.isLoggedIn = true;
                    })
                    .catch(function () {
                    });
            },
            goToActivities: function () {
                const profileId = this.$route.params.id;
                this.$router.push('/profiles/' + profileId + '/activities');
            }
        },
        mounted: function () {
            this.getProfileData();
        },
        beforeMount () {
            this.getLoggedInUserData();
            this.getLocationData();

        }
    };

    export default App;
</script>
