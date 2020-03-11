<template>
    <div id="app">
        <NavBar isLoggedIn=true></NavBar>

        <div class="container">
            <b-container fluid>
                <b-row>
                    <b-col>
                        <b-input :value="userData.firstname"></b-input>
                        <hr>
                    </b-col>
                </b-row>
            </b-container>
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