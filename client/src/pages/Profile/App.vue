<template>
    <div id="app" v-if="isLoggedIn">
        <NavBar :loggedInIsAdmin="loggedInIsAdmin" v-bind:hideElements="hidden" v-bind:isLoggedIn="isLoggedIn"
                v-bind:loggedInId="loggedInId"></NavBar>
        <div class="container">
            <AdminSideBar :loggedInId="loggedInId" :loggedInIsAdmin="loggedInIsAdmin"
                          :userData="userData" v-if="adminAccess"></AdminSideBar>
            <div>
                <b-row style="font-size: 6em;" align-content="center">
                    <b-col>
                        <UserImage :id="profileId"
                                   :editable="parseInt(profileId) === parseInt(loggedInId) || loggedInIsAdmin"
                                   save-on-change></UserImage>
                    </b-col>
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
                                <b-col v-if="userData.nickname != null"><p>{{userData.nickname}}</p></b-col>
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
                            <b-row v-if="userData.bio != ''">
                                <b-col><b>Bio:</b></b-col>
                                <b-col><p>{{userData.bio}}</p></b-col>
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
                                <b-col v-if="userData.fitness == null"><p>Edit your profile to add a fitness level!</p>
                                </b-col>
                            </b-row>
                        </b-card>

                        <b-card style="margin: 1em" title="Intrested In:">
                            <b-row>
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

                    <b-tab title="Location Info" @click="addUserLocationToMap">
                        <b-card style="margin: 1em">
                            <h4 v-if="location && location.name" class="text-center">
                                {{location.name}}
                            </h4>
                            <map-pane ref="map" :can-hide="true"></map-pane>
                        </b-card>
                    </b-tab>
                    <b-tab style="margin: 1em" title="Activities">

                        <b-row>
                            <b-col>
                                <ActivityList :user_id="parseInt(this.$route.params.id)"></ActivityList>
                            </b-col>
                        </b-row>

                    </b-tab>
                </b-tabs>
            </div>

        </div>
    </div>
</template>


<script>
    import api from '@/Api';
    import NavBar from "@/components/NavBar.vue"
    import AdminSideBar from "@/components/AdminSideBar.vue"
    import AdminMixin from "../../mixins/AdminMixin";
    import {store} from "../../store";
    import ActivityList from "../../components/Activity/ActivityList";
    import MapPane from "../../components/MapPane/MapPane";
    import UserImage from "../../components/Activity/UserImage/UserImage";

    const App = {
        name: 'App',
        components: {
            UserImage,
            ActivityList,
            NavBar,
            AdminSideBar,
            MapPane
        },
        data: function () {
            return {
                loggedInUser: '',
                loggedInId: '',
                userData: '',
                passports: [],
                activities: [],
                myActivities: [],
                additionalEmails: [],
                isLoggedIn: '',
                userName: "",
                locations: [],
                userRoles: [],
                profileId: '',
                location: null,
                dob: '',
                loggedInIsAdmin: false,
                hidden: null
            }
        },
        computed: {
            adminAccess() {
                return store.adminAccess;
            }
        },
        methods: {
            getLoggedInUserData: function () {
                let currentObj = this;
                return api.getLoggedInProfile()
                    .then(function (response) {
                        currentObj.loggedInUser = response.data;
                        currentObj.loggedInId = response.data.id;
                        currentObj.userName = response.data.firstname;
                    })
                    // eslint-disable-next-line no-unused-vars
                    .catch(function (error) {
                        currentObj.isLoggedIn = false;
                        currentObj.$router.push('/login');
                    });
            },
            getProfileData: async function () {
                this.loggedInIsAdmin = await AdminMixin.methods.checkUserIsAdmin();
                let currentObj = this;
                api.getProfile(this.$route.params.id)
                    .then(function (response) {
                        currentObj.profileId = response.data.id;
                        currentObj.userData = response.data;
                        currentObj.passports = response.data.passports;
                        currentObj.activities = response.data.activities;
                        currentObj.additionalEmails = response.data.additional_email;
                        currentObj.isLoggedIn = true;
                        currentObj.userRoles = response.data.roles;
                        currentObj.getCorrectDateFormat(response.data.date_of_birth, currentObj);
                        let profileAdmin = false;
                        for (let i = 0; i < currentObj.userRoles.length; i++) {
                            if (currentObj.userRoles[i].roleName === "ROLE_ADMIN") {
                                profileAdmin = true;
                            }
                        }


                        if (profileAdmin && !currentObj.loggedInIsAdmin) {
                            currentObj.$router.push('/profiles/' + currentObj.loggedInId);
                        }

                        currentObj.location = response.data.location;
                        currentObj.checkHideElements();

                    })
                    // eslint-disable-next-line no-unused-vars
                    .catch(function (error) {
                        if (currentObj.isLoggedIn) {
                            currentObj.$router.go(0);
                        } else {
                            currentObj.$router.push('/');
                        }
                    });
            },
            addUserLocationToMap() {
                if (this.location !== null) {
                    this.$refs.map.createMarker(1, 3, this.location.latitude, this.location.longitude, this.userData, this.userName);
                    this.$refs.map.setMapCenter(this.location.latitude, this.location.longitude);
                }
                this.getMyActivities();
            },
            getCorrectDateFormat: function (date, currentObj) {
                const dobCorrectFormat = new Date(date + "T00:00");
                currentObj.dob = dobCorrectFormat.toLocaleDateString('en-NZ');
            },
            checkHideElements: function () {
                let currentObj = this;
                if (currentObj.loggedInId === currentObj.profileId || store.adminAccess) {
                    currentObj.hidden = false;
                } else {
                    currentObj.hidden = true;
                }
            },
            getUserId: function () {
                let currentObj = this;
                api.getProfileId()
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
            },
            getMyActivities: function () {
                let obj = this;
                let map = obj.$refs.map;
                map.refreshMap();
                const profileId = obj.profileId;
                api.getActivities(profileId)
                    .then(function (response) {
                      obj.myActivities = response.data;
                      for (let i = 0; i < obj.myActivities.length; i++) {
                          let activity = obj.myActivities[i];
                          let routeString = "/profiles/" + profileId + "/activities/" + activity.id;
                          map.createMarker(i, 2, activity.location.latitude, activity.location.longitude, routeString, activity.activityName)
                      }
                    })
            }
        },
        watch: {
            $route: function () {
                this.getProfileData();
            },
            adminAccess: function () {
                this.checkHideElements();
            }
        },
        mounted: function () {
            this.getProfileData();
        },
        beforeMount: async function () {
            await this.getLoggedInUserData();
        }
    };

    export default App;
</script>

<style>
</style>
