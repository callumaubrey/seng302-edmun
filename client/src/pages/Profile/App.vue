<template>
    <div id="app">
        <NavBar v-bind:isLoggedIn="isLoggedIn"></NavBar>

        <div class="container">
            <b-container fluid>
                <b-row>
                    <b-col>
                        <h3>{{userData.firstname}} {{userData.lastname}}</h3>
                        <hr>
                    </b-col>
                </b-row>
            </b-container>

            <b-row>
                <b-col>
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
                </b-col>

                <b-col>
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
                </b-col>

            </b-row>



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
                userData: null,
                passports: [],
                additionalEmails: [],
                isLoggedIn: false
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
                        currentObj.additionalEmails = response.data.additionalemail;
                        currentObj.isLoggedIn = true;
                    })
                    .catch(function (error) {
                        console.log(error.response.data);
                        currentObj.isLoggedIn = false;
                    });
            }
        },
        mounted: function () {
            this.getUserSession();
        }
    };

    export default App;
</script>
