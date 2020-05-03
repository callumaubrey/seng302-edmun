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
                <b-row align-h="center" v-if="isActivityOwner">
                    <b-dropdown text="Actions" class="m-md-2">
                        <b-dropdown-item>Edit</b-dropdown-item>
                        <b-dropdown-item @click="deleteActivity()">Delete</b-dropdown-item>
                    </b-dropdown>
                </b-row>
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
                isActivityOwner: false,
                userData: '',
                isLoggedIn: false,
                userName: "",
                loggedInId: 0
            }
        },
        mounted() {
            this.getProfileData();
        },
        methods: {
            getProfileData: function () {
                let vueObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/user')
                .then((res) => {
                    vueObj.loggedInId = res.data.id;
                    vueObj.isLoggedIn = true;
                    vueObj.userName = res.data.firstname;
                    this.checkIsOwner();
                })
                .catch(() => {
                    vueObj.isLoggedIn = false;
                    vueObj.$router.push('/login');
                });
            },
            checkIsOwner: function () {
                let vueObj = this;
                console.log(vueObj.loggedInId);
                if (vueObj.loggedInId == vueObj.$route.params.profileId) {
                    vueObj.isActivityOwner = true;
                } else {
                    vueObj.isActivityOwner = false;
                }
            },
            deleteActivity() {
                if (!confirm("Are you sure you want to delete this activity?")) {
                    return;
                }

                let profileId = this.$route.params.profileId;
                let activityId = this.$route.params.activityId;
                alert('http://localhost:9499/profiles/' + profileId + '/activities/' + activityId);
                this.axios.delete('http://localhost:9499/profiles/' + profileId + '/activities/' + activityId)
                .then(() => {
                    this.$router.push('/profile/' + profileId);
                })
                .catch(err => alert(err));
            }
        }
    };
    export default App;
</script>