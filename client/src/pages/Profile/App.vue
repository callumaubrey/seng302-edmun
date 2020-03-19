<template>
    <div id="app">
        <NavBar v-bind:isLoggedIn="isLoggedIn"></NavBar>

        <div class="container">
            <div>
                <b-row>
                <b-img center v-bind="mainProps" rounded= "circle" src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.f4t8FtSbfNUs94KKgMhhBAHaHr%26pid%3DApi%26h%3D160&f=1" alt="Center image"></b-img>
                </b-row>
                <b-row><h3></h3></b-row>
                <b-row align-h="center">
                    <h3>{{userData.firstname}} {{userData.lastname}} </h3>
                </b-row>
                <b-tabs content-class="mt-3" align="center">
                    <b-tab title="About" active>
                        <b-card style="margin: 1em" title="About:">
                            <b-row>
                                <b-col><b>Nickname:</b></b-col>
                                <b-col v-if="userData.nickName != null"><p >{{userData.nickName}}</p></b-col>
                                <b-col v-else><p>No nickname</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><b>Date of Birth:</b></b-col>
                                <b-col><p>{{userData.dob}}</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><b>Gender:</b></b-col>
                                <b-col><p>{{userData.gender}}</p></b-col>
                            </b-row>
                        </b-card>
                    </b-tab>

                    <b-tab title="Email">
                        <b-card style="margin: 1em" title="Email(s):">
                            <b-row>
                                <b-col><b>Primary Email:</b></b-col>
                                <b-col><p>{{userData.email.address}}</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><b>Additional Emails:</b></b-col>
                                <b-col v-if="additionalEmails == []">
                                    <p>No additional emails</p>
                                </b-col>
                                <b-col v-else>
                                    <p>
                                    <ul>
                                        <li v-for="item in additionalEmails" :key="item.email">
                                            {{ item.email }}
                                        </li>
                                    </ul>
                                    </p>
                                </b-col>
                            </b-row>
                            <p>(Max 5 emails)</p>
                        </b-card>
                    </b-tab>
                    <b-tab title="Passport Info" >
                        <b-card style="margin: 1em" title="Passport Info:">
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

                    </b-tab>
                    <b-tab title="Fitness Info" >
                        <b-card style="margin: 1em" title="Fitness Info:">
                            <b-row>
                                <b-col><b>Fitness Level:</b></b-col>
                                <b-col v-if="userData.fitness == 1"><p>Couch potato</p></b-col>
                                <b-col v-if="userData.fitness == 2"><p>Some activity</p></b-col>
                                <b-col v-if="userData.fitness == 3"><p>Average fitness level</p></b-col>
                                <b-col v-if="userData.fitness == 4"><p>Above average fitness</p></b-col>
                                <b-col v-if="userData.fitness == 5"><p>Serious athlete</p></b-col>
                                <b-col v-else><p>Edit your profile to add a fitness level!</p></b-col>
                            </b-row>
                        </b-card>
                    </b-tab>

                    <b-tab title="Location Info" >
                        <b-card style="margin: 1em;" title="Location Info:">
                            <b-input id="locationInput" class="form-control" type="text" @keyup.native="getLocationData"></b-input>
                            <div v-for="i in locations" :key="i.place_id">
                                <b-input v-on:click="setLocationInput(i)" type="button" :value=i.display_name></b-input>
                            </div>
                        </b-card>

                    </b-tab>
                    <b-tab title="Activity Info" >
                        <b-card style="margin: 1em" title="Passport Info:">
                            <b-row>
                                <b-col><b>Your Activities</b></b-col>
                                <b-col>
                                    <p>
                                    <ul>
                                        <li v-for="item in activites" :key="item">
                                            {{ item }}
                                        </li>
                                    </ul>
                                    </p>
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
                locations: []
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
                        console.log(currentObj.activites)
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                        currentObj.isLoggedIn = false;
                    });
            },
            getLocationData: async function () {
                var locationText = document.getElementById("locationInput").value
                if (locationText == ''){
                    return
                }
                var locationData = this.axios.create({
                    baseURL: 'https://nominatim.openstreetmap.org/search/city/?q="' + locationText + '"&format=json&limit=5',
                    timeout: 1000,
                    withCredentials: false,
                });
                console.log('https://nominatim.openstreetmap.org/search?q="' + locationText + '"&format=json&limit=5')
                var data = await (locationData.get())
                this.locations = data.data
            },
            setLocationInput: function (location) {
                document.getElementById("locationInput").value = location.display_name
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
