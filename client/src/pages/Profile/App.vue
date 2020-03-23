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
                    <h3>{{userData.firstname}} {{userData.lastname}} </h3>
                </b-row>
                <b-tabs content-class="mt-3" align="center">
                    <b-tab title="About" active>
                        <b-card style="margin: 1em" title="About:">
                            <b-row>
                                <b-col><b>Nickname:</b></b-col>
                                <b-col v-if="userData.nickname != null"><p >{{userData.nickname}}</p></b-col>
                                <b-col v-else><p>No nickname</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><b>Date of Birth:</b></b-col>
                                <b-col><p>{{userData.date_of_birth}}</p></b-col>
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
                                <b-col><p>{{userData.primary_email.address}}</p></b-col>
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
                                <b-col v-if="userData.fitness == 0"><p>Couch potato</p></b-col>
                                <b-col v-if="userData.fitness == 1"><p>Some activity</p></b-col>
                                <b-col v-if="userData.fitness == 2"><p>Average fitness level</p></b-col>
                                <b-col v-if="userData.fitness == 3"><p>Above average fitness</p></b-col>
                                <b-col v-if="userData.fitness == 4"><p>Serious athlete</p></b-col>
                                <b-col v-if="userData.fitness == null"><p>Edit your profile to add a fitness level!</p></b-col>
                            </b-row>
                        </b-card>
                    </b-tab>
                    <b-tab title="Activity Info" >
                        <b-card style="margin: 1em" title="Passport Info:">
                            <b-row>
                                <b-col><b>Your Activities</b></b-col>
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
                activities: [],
                additionalEmails: [],
                isLoggedIn: false,
                userName: "",
            }
        },
        methods: {
            getUserSession: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/' + this.$route.params.id)
                    .then(function (response) {
                        currentObj.userData = response.data;
                        currentObj.passports = response.data.passports;
                        currentObj.activities = response.data.activities;
                        currentObj.additionalEmails = response.data.additional_email;
                        currentObj.isLoggedIn = true;
                        currentObj.userName = response.data.firstname;
                    })
                    .catch(function () {
                        currentObj.isLoggedIn = false;
                        currentObj.$router.push('/login');
                    });
            }
        },
        mounted: function () {
            this.getUserSession();
        }
    };

    export default App;
</script>
