<template>
    <div id = 'app'>
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
        <div class="container">
            <div>
                <b-row>
                    <b-img center rounded= "circle" width ="150px" height="150px" src="https://library.kissclipart.com/20180919/uke/kissclipart-running-clipart-running-logo-walking-8d4133548d1b34c4.jpg" alt="Center image"></b-img>
                </b-row>
                <b-row><h3></h3></b-row>
                <b-row align-h="center">
                    <h3>{{activityName}}</h3>
                </b-row>
                <b-row align-h="center" v-if="isActivityOwner">
                    <b-dropdown text="Actions" class="m-md-2">
                        <b-dropdown-item @click="editActivity()">Edit</b-dropdown-item>
                        <b-dropdown-item @click="deleteActivity()">Delete</b-dropdown-item>
                    </b-dropdown>
                </b-row>
                <b-card style="margin: 1em" title="About:" >
                    <b-row>
                        <b-col><b>Activity Type(s):</b></b-col>
                        <b-col><p>{{activityTypes}}</p></b-col>
                    </b-row>
                    <b-row v-if="!continuous">
                        <b-col><b>Start:</b></b-col>
                        <b-col><p>{{startTime}}</p></b-col>
                    </b-row>
                    <b-row v-if="!continuous">
                        <b-col><b>End:</b></b-col>
                        <b-col><p>{{endTime}}</p></b-col>
                    </b-row>
                    <b-row v-if="location==null">
                        <b-col><b>Location:</b></b-col>
                        <b-col><p>No location available</p></b-col>
                    </b-row>
                    <b-row v-if="location!=null">
                        <b-col><b>Location:</b></b-col>
                        <b-col><p>{{locationString}}</p></b-col>
                    </b-row>
                    <b-row>
                        <b-col><b>Description:</b></b-col>
                        <b-col><p>{{description}}</p></b-col>
                    </b-row>
                </b-card>
                <b-card style="margin: 1em" title="Participants:">
                    <b-row>
                        <b-col><b>Creator:</b></b-col>
                        <b-col>
                            <p v-if="activityOwner">{{activityOwner.firstname}} {{activityOwner.lastname}}</p>
                        </b-col>
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
                loggedInId: 0,
                activityName: "",
                description: "",
                activityTypes: [],
                continuous: false,
                startTime: "",
                endTime: "",
                location: null,
                activityOwner: null,
                locationString: "",
            }
        },
        mounted() {
            this.getUserName();
            this.getActivityData();
            this.getUserId();
        },
        methods: {
            getUserName: function () {
                let vueObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/firstname')
                .then((res) => {
                    vueObj.userName = res.data.firstname;
                    this.checkIsOwner();
                })
                .catch(() => {
                    vueObj.isLoggedIn = false;
                    vueObj.$router.push('/login');
                });
            },
            getUserId: function () {
                let vueObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/id')
                    .then((res) => {
                        vueObj.loggedInId = res.data;
                        vueObj.isLoggedIn = true;
                    }).catch(() => {
                    vueObj.isLoggedIn = false;
                    vueObj.$router.push('/login');
                })
            },
            checkIsOwner: function () {
                let vueObj = this;
                console.log("loggedinId is " + vueObj.loggedInId);
                if (vueObj.loggedInId == vueObj.$route.params.id) {
                    vueObj.isActivityOwner = true;
                } else {
                    vueObj.isActivityOwner = false;
                }
            },
            deleteActivity() {
                if (!confirm("Are you sure you want to delete this activity?")) {
                    return;
                }

                let profileId = this.$route.params.id;
                let activityId = this.$route.params.activityId;
                // alert('http://localhost:9499/profiles/' + profileId + '/activities/' + activityId);
                this.axios.delete('http://localhost:9499/profiles/' + profileId + '/activities/' + activityId)
                .then(() => {
                    this.$router.push('/profiles/' + profileId + '/activities/');
                })
                .catch(err => alert(err));
            },
            editActivity() {
                let profileId = this.$route.params.id;
                let activityId = this.$route.params.activityId;
                this.$router.push('/profiles/' + profileId + '/activities/' + activityId + '/edit');
            },
            getActivityData() {
                let vueObj = this;
                let activityId = this.$route.params.activityId;
                let profileId = this.$route.params.id;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/activities/' + activityId)
                    .then((res) => {
                        vueObj.activityOwner = res.data.profile;
                        vueObj.activityName = res.data.activityName;
                        vueObj.description = res.data.description;
                        vueObj.activityTypes = res.data.activityTypes;
                        vueObj.continuous = res.data.continuous;
                        vueObj.startTime = res.data.startTime;
                        vueObj.endTime = res.data.endTime;
                        vueObj.location = res.data.location;
                        if (vueObj.location != null) {
                            vueObj.locationString = vueObj.location.city + ", ";
                            if (vueObj.location.state) {
                                vueObj.locationString += vueObj.location.state + ", ";
                            }
                            vueObj.locationString += vueObj.location.country;
                        }
                        if (vueObj.activityOwner.id != profileId) {
                            vueObj.$router.push('/profiles/' + profileId);
                        }
                        if (!vueObj.continuous) {
                            this.getCorrectDateFormat(vueObj.startTime, vueObj.endTime, vueObj);
                        }
                        this.getActivityTypeDisplay(vueObj);
                    }).catch(() => {
                    let profileId = this.$route.params.id;
                    vueObj.$router.push('/profiles/' + profileId);
                });
            },
            getCorrectDateFormat: function (start, end, currentObj) {
                const startDate = new Date(start);
                const endDate = new Date(end);
                currentObj.startTime = startDate.toString();
                currentObj.endTime = endDate.toString();
            },
            getActivityTypeDisplay: function(currentObj) {
                let result = "";
                for (let i = 0; i < currentObj.activityTypes.length; i++) {
                    result += currentObj.activityTypes[i];
                    if (i + 1 < currentObj.activityTypes.length) {
                        result += ", ";
                    }
                }
                currentObj.activityTypes = result;
            }
        }
    };
    export default App;
</script>

