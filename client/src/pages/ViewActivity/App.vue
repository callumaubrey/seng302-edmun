<template>
    <div id = 'app'>
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
        <div class="container">
            <div>
                <b-row>
                    <b-img center v-bind="mainProps" rounded= "circle" width ="150px" height="150px" src="https://library.kissclipart.com/20180919/uke/kissclipart-running-clipart-running-logo-walking-8d4133548d1b34c4.jpg" alt="Center image"></b-img>
                </b-row>
                <b-row><h3></h3></b-row>
                <b-row align-h="center">
                    <h3>Activity Name</h3>
                </b-row>
                <b-tabs content-class="mt-3" align="center">
                    <b-tab title="About" active>
                        <b-card style="margin: 1em" title="About:" >
                            <b-row>
                                <b-col><b>Activity Type(s):</b></b-col>
                                <b-col><p>List of activity types</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><b>End Date:</b></b-col>
                                <b-col><p>End date or continuous</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><b>Location:</b></b-col>
                                <b-col><p>Activity location</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><b>Description:</b></b-col>
                                <b-col><p>Activity description</p></b-col>
                            </b-row>
                        </b-card>
                        <b-card style="margin: 1em" title="Participants:">
                            <b-row>
                                <b-col><b>Creator:</b></b-col>
                                <b-col><p>Users name possibly link to profile</p></b-col>
                            </b-row>
                            <b-row>
                                <b-col><b>Other Participants:</b></b-col>
                                <b-col><p>List of other participants possibly with links to profiles</p></b-col>
                            </b-row>
                        </b-card>
                    </b-tab>

                    <b-tab title="Edit" >
                        <b-card style="margin: 1em;" title="Edit:">
                        </b-card>
                        <b-card style="margin: 1em;" title="Delete:">
                        </b-card>
                    </b-tab>
                </b-tabs>
            </div>
        </div>
    </div>
</template>

<script>
    import NavBar from "@/components/NavBar.vue";
    const App = {
        name: 'App',
        components: {
            NavBar
        },
        data: function() {
            return {
                userData: '',
                isLoggedIn: false,
                userName: ""
            }
        }
    };
    export default App;
</script>