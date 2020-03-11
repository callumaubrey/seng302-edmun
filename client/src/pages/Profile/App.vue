<template>
    <div id="app">
        <NavBar isLoggedIn=true></NavBar>

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
                    <b-card style="margin: 1em" title="User Information:">
                        <b-row>
                            <b-col><b>Your Countries:</b></b-col>
                            <b-col><p>{{userData.yourCountries}}</p></b-col>
                        </b-row>
                    </b-card>

                    <b-card style="margin: 1em" title="Email(s):">
                        <b-row>
                            <b-col><b>Primary Email:</b></b-col>
                            <b-col><p>{{userData.email}}</p></b-col>
                        </b-row>
                        <p>(Max 5 emails)</p>
                    </b-card>
                </b-col>

                <b-col>
                    <b-card style="margin: 1em" title="Passport Info:">
                        <b-row>
                            <b-col><b>Your Countries:</b></b-col>
                            <b-col><p>{{userData.yourCountries}}</p></b-col>
                        </b-row>
                    </b-card>

                    <b-card style="margin: 1em" title="Fitness Info:">
                        <b-row>
                            <b-col><b>Primary Email:</b></b-col>
                            <b-col><p>{{userData.fitness}}</p></b-col>
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
    import axios from 'axios'

    const profileData = axios.create({
        baseURL: "http://localhost:9499/profile/1",
        timeout: 1000
    });

    const App = {
        name: 'App',
        components: {
            NavBar
        },
        data: function() {
            return {
                userData: null
            }
        },
        methods: {
            getProfileData: async function() {
                var data = await (profileData.get());
                this.userData = data.data;
            }
        },
        mounted: function () {
            this.getProfileData();
        }
    };

    export default App;
</script>