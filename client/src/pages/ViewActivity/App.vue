<template>
    <div id = 'app' v-if="isLoggedIn">
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
                <b-row align-h="center">
                    <b-dropdown v-if="profileId == loggedInId" text="Actions" class="m-md-2">
                        <b-dropdown-item  @click="editActivity()">Edit</b-dropdown-item>
                        <b-dropdown-item  @click="deleteActivity()">Delete</b-dropdown-item>
                    </b-dropdown>
                </b-row>
                <b-card style="margin: 1em" title="About:" >
                    <div v-if="locationDataLoading">
                        <div class="text-center text-primary my-2">
                            <b-spinner class="align-middle"></b-spinner>
                            <strong> Loading...</strong>
                        </div>
                    </div>
                    <div v-else>
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
                        <b-row>
                            <b-col><b>Hashtags:</b></b-col>
                            <b-col v-for="hashtag in hashtags" v-bind:key = "hashtag">
                                <b-link @click="clickHashtag(hashtag)">{{hashtag}}</b-link>
                            </b-col>
                        </b-row>
                    </div>
                </b-card>
                <b-card style="margin: 1em" title="Participants:">
                    <div v-if="locationDataLoading">
                        <div class="text-center text-primary my-2">
                            <b-spinner class="align-middle"></b-spinner>
                            <strong> Loading...</strong>
                        </div>
                    </div>
                    <div v-else>
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
                    </div>
                </b-card>
            </div>
        </div>
    </div>
</template>

<script>
    import NavBar from "@/components/NavBar.vue";
    import axios from 'axios'

    const App = {
        name: 'App',
        components: {
            NavBar
        },
        data: function() {
            return {
                //isActivityOwner: false,
                userData: '',
                isLoggedIn: false,
                userName: "",
                loggedInId: null,
                profileId: null,
                activityName: "",
                description: "",
                activityTypes: [],
                continuous: false,
                startTime: "",
                endTime: "",
                location: null,
                hashtags: [],
                activityOwner: null,
                locationString: "",
                locationDataLoading: true
            }
        },
        mounted() {
            this.getUserName();
            this.getActivityData();
            this.getLoggedInId();
            this.setProfileId();
        },
        methods: {
            getUserName: function () {
                let vueObj = this;
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/profiles/firstname')
                .then((res) => {
                    vueObj.userName = res.data;
                })
                .catch(() => {
                    vueObj.isLoggedIn = false;
                    vueObj.$router.push('/login');
                });
            },
            getLoggedInId: function () {
                let vueObj = this;
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/profiles/id')
                    .then((res) => {
                        vueObj.loggedInId = res.data;
                        vueObj.isLoggedIn = true;
                    }).catch(() => {
                    vueObj.isLoggedIn = false;
                    vueObj.$router.push('/login');
                })
            },
            setProfileId: function () {
                let vueObj = this;
                vueObj.profileId = this.$route.params.id;
            },
            deleteActivity() {
                // double check for if user clicks button when hidden.
                if (parseInt(this.profileId) === parseInt(this.loggedInId)) {
                    if (!confirm("Are you sure you want to delete this activity?")) {
                        return;
                    }

                    let profileId = this.$route.params.id;
                    let activityId = this.$route.params.activityId;
                    // alert('http://localhost:9499/profiles/' + profileId + '/activities/' + activityId);
                    axios.delete('http://localhost:9499/profiles/' + profileId + '/activities/' + activityId)
                        .then(() => {
                            this.$router.push('/profiles/' + profileId + '/activities/');
                        })
                        .catch(err => alert(err));
                }
            },
            editActivity() {
                let profileId = this.$route.params.id;
                let activityId = this.$route.params.activityId;
                // double check for it somehow user clicks edit button when hidden.
                console.log("profile Id: " + this.profileId);
                console.log("loggedin Id: " + this.loggedInId);

                if (parseInt(this.profileId) === parseInt(this.loggedInId)) {
                    this.$router.push('/profiles/' + profileId + '/activities/' + activityId + '/edit');
                }
            },
            getActivityData() {
                let vueObj = this;
                let activityId = this.$route.params.activityId;
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/activities/' + activityId)
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
                        if (res.data.tags.length > 0) {
                            for (var i = 0; i < res.data.tags.length; i++) {
                                vueObj.hashtags.push("#" + res.data.tags[i].name);
                            }
                        }
                        console.log(vueObj.hashtags);
                        if (!vueObj.continuous) {
                            this.getCorrectDateFormat(vueObj.startTime, vueObj.endTime, vueObj);
                        }
                        this.getActivityTypeDisplay(vueObj);
                        vueObj.locationDataLoading = false;
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
            },
            clickHashtag(hashtag) {
                hashtag = hashtag.substring(1);
                this.$router.push('/hashtag/' + hashtag);

            }
        }
    };
    export default App;
</script>

