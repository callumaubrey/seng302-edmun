<template>
  <div id="app" v-if="isLoggedIn">
    <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
    <div v-if="archived">
      <h1 align="center">This activity has been deleted</h1>
    </div>
    <div v-else-if="notFound">
      <h1 align="center">This activity does not exist</h1>
    </div>
    <div v-else-if="!isAuthorized">
      <ForbiddenMessage></ForbiddenMessage>
    </div>
    <div v-else-if="!locationDataLoading" class="container">
      <div>
        <!-- Image and Name -->
        <b-row>
          <b-img center rounded="circle" width="150px" height="150px"
                 src="https://library.kissclipart.com/20180919/uke/kissclipart-running-clipart-running-logo-walking-8d4133548d1b34c4.jpg"
                 alt="Center image"></b-img>
        </b-row>
        <b-row align-h="center">
          <h3>{{ activityName }}</h3>
        </b-row>

        <!-- Summary -->
        <FollowerSummary class="text-center"
                         :activityId="parseInt($route.params.activityId)"
                         :key="followSummaryKey"></FollowerSummary>
        <b-row align-h="center">
          <ShareActivity :modal="parseInt(loggedInId) === parseInt(activityOwner.id)"
                         :visibility="visibility"
                         :profileId="profileId"
                         :activityId="$route.params.activityId"
                         v-on:componentUpdate="forceRerender"
                         :key="shareActivityKey"></ShareActivity>
        </b-row>

        <!-- Actions -->
        <b-row align-h="center">
          <FollowUnfollow v-bind:activityId="parseInt(this.$route.params.activityId)"
                          v-bind:activityOwnerId="parseInt(this.$route.params.id)"
                          v-bind:loggedInId="loggedInId"></FollowUnfollow>
        </b-row>
        <b-row align-h="center">
          <b-dropdown text="Actions"
                      v-if="parseInt(profileId) === parseInt(loggedInId) || loggedInIsAdmin"
                      class="m-md-2">
            <b-dropdown-item @click="editActivity()">Edit</b-dropdown-item>
            <b-dropdown-item @click="deleteActivity()">Delete</b-dropdown-item>
          </b-dropdown>
        </b-row>

        <!-- Content -->
        <b-tabs content-class="mt-3" align="center">
          <b-tab title="About" active>
            <b-row align-h="center">
              <b-col cols="9">
                <b-card style="margin: 1em" title="About:">
                  <div v-if="locationDataLoading">
                    <div class="text-center text-primary my-2">
                      <b-spinner class="align-middle"></b-spinner>
                      <strong> Loading...</strong>
                    </div>
                  </div>
                  <div v-else>
                    <b-row>
                      <b-col cols="3"><b>Activity Type(s):</b></b-col>
                      <b-col><p>{{ activityTypes }}</p></b-col>
                    </b-row>
                    <b-row v-if="!continuous">
                      <b-col cols="3"><b>Start:</b></b-col>
                      <b-col><p>{{ startTime }}</p></b-col>
                    </b-row>
                    <b-row v-if="!continuous">
                      <b-col cols="3"><b>End:</b></b-col>
                      <b-col><p>{{ endTime }}</p></b-col>
                    </b-row>
                    <b-row v-if="location==null">
                      <b-col cols="3"><b>Location:</b></b-col>
                      <b-col><p>No location available</p></b-col>
                    </b-row>
                    <b-row v-if="location!=null">
                      <b-col cols="3"><b>Location:</b></b-col>
                      <b-col><p>{{ locationString }}</p></b-col>
                    </b-row>
                    <b-row>
                      <b-col cols="3"><b>Description:</b></b-col>
                      <b-col><p>{{ description }}</p></b-col>
                    </b-row>
                    <b-row>
                      <b-col cols="3"><b>Hashtags:</b></b-col>
                      <b-col>
                        <p>
                            <span v-for="hashtag in hashtags" v-bind:key="hashtag">
                              <b-link @click="clickHashtag(hashtag)"
                              >{{ hashtag }}&nbsp;</b-link
                              >
                            </span>
                        </p>
                      </b-col>
                    </b-row>
                  </div>
                </b-card>
              </b-col>
            </b-row>

            <b-row align-h="center" v-if="metrics.length > 0">
              <b-col cols="9">
                <b-row class="horizontal-scroll">
                  <div class="d-flex flex-row flex-nowrap">
                    <div v-for="(metric, i) in metrics" v-bind:key="metric">
                      <b-card v-if="i == metrics.length - 1" :title="metric.title"
                              class="metric-card"
                              style="margin-right:0;">
                        <b-card-body>
                          <p style="font-size:14px;">{{ metric.description }}</p>
                        </b-card-body>
                      </b-card>
                      <b-card v-else :title="metric.title" class="metric-card">
                        <b-card-body>
                          <p style="font-size:14px;">{{ metric.description }}</p>
                        </b-card-body>
                      </b-card>
                    </div>
                  </div>
                </b-row>
              </b-col>
            </b-row>
            <b-row v-else align-h="center">
              <b-col cols="9">
                <b-card style="margin: 1em">
                  <b-card-body>
                    No metrics available
                  </b-card-body>
                </b-card>
              </b-col>
            </b-row>
            <b-row align-h="center">
              <b-col cols="9">
                <b-card style="margin: 1em">
                  <b-row align-h="center">
                    <b-col>
                      <h4 v-if="location && location.name">
                        {{location.name}}
                      </h4>
                      <MapPane ref="mapPane" v-if="this.location || this.activity.path"></MapPane>
                      <b-card-body v-else>No Location</b-card-body>
                    </b-col>
                  </b-row>
                </b-card>
              </b-col>
            </b-row>
          </b-tab>
          <b-tab title="Participants">
            <b-row align-h="center">
              <b-col cols="9">
                <b-card style="margin: 1em" title="Participants:">
                  <FollowerUserList ref="followUserList" :activity-id="parseInt($route.params.activityId)"
                                    :logged-in-id="loggedInId"
                                    :activity-creator-id="activityOwner.id"></FollowerUserList>
                </b-card>
              </b-col>
            </b-row>
          </b-tab>
          <b-tab title="Results">
            <RecordActivityResultModal :activity-id="this.$route.params.activityId"
                                       :logged-in-id="loggedInId"
                                       :profile-id="profileId"
                                       style="padding-bottom: 10px"></RecordActivityResultModal>
            <ActivityResults :profile-id="profileId"
                             :activity-id="$route.params.activityId"></ActivityResults>
          </b-tab>
        </b-tabs>
      </div>
    </div>
  </div>
</template>

<script>
  import NavBar from "@/components/NavBar.vue";
  import FollowUnfollow from "@/components/FollowUnfollow.vue";
  import FollowerSummary from "../../components/Activity/FollowerSummary.vue";
  import FollowerUserList from "../../components/Activity/FollowerUserList";
  import ShareActivity from "@/components/SharingActivity/ShareActivity.vue";
  import ForbiddenMessage from "@/components/ForbiddenMessage.vue";
  import RecordActivityResultModal from "@/components/Activity/RecordActivityResultModal";
  import api from '@/Api'
  import AdminMixin from "../../mixins/AdminMixin";
  import ActivityResults from "../../components/ActivityResults";
  import MapPane from "../../components/MapPane/MapPane";

  const App = {
    name: "App",
    components: {
      MapPane,
      ActivityResults,
      NavBar,
      FollowUnfollow,
      FollowerUserList,
      FollowerSummary,
      ShareActivity,
      ForbiddenMessage,
      RecordActivityResultModal
    },
    data: function () {
      return {
        activity: null,
        userData: "",
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
        locationDataLoading: true,
        archived: false,
        notFound: false,
        visibility: null,
        loggedInIsAdmin: false,
        isAuthorized: true,
        metrics: [],
        followSummaryKey: 0,
        shareActivityKey: 0
      }
    },
    mounted() {
      this.getUserName();
      this.getActivityData();
      this.getLoggedInId();
      this.setProfileId();
      this.checkIsAdmin();
    },
    methods: {
      getUserName: function () {
        let vueObj = this;

        api.getFirstName()
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

        api.getProfileId()
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
        if (parseInt(this.profileId) === parseInt(this.loggedInId) || this.loggedInIsAdmin) {
          if (!confirm("Are you sure you want to delete this activity?")) {
            return;
          }

          let profileId = this.$route.params.id;
          let activityId = this.$route.params.activityId;
          api.deleteActivity(profileId, activityId)
              .then(() => {
                this.$router.push("/profiles/" + profileId + "/activities/");
              })
              .catch(err => alert(err));
        }
      },
      editActivity() {
        let profileId = this.$route.params.id;
        let activityId = this.$route.params.activityId;

        if (this.loggedInIsAdmin || (parseInt(this.profileId) === parseInt(this.loggedInId))) {
          this.$router.push(
              "/profiles/" + profileId + "/activities/" + activityId + "/edit"
          );
        }
      },
      getActivityData() {
        let vueObj = this;
        let activityId = this.$route.params.activityId;

        api.getActivity(activityId)
            .then((res) => {
              if (res.data == "Activity is archived") {
                this.archived = true;
              } else {
                vueObj.activity = res.data;
                vueObj.activityOwner = res.data.profile;
                vueObj.activityName = res.data.activityName;
                vueObj.description = res.data.description;
                vueObj.activityTypes = res.data.activityTypes;
                vueObj.continuous = res.data.continuous;
                vueObj.startTime = res.data.startTime;
                vueObj.endTime = res.data.endTime;
                vueObj.location = res.data.location;
                vueObj.visibility = res.data.visibilityType;
                if (res.data.visibilityType == null) {
                  vueObj.visibility = "Public"
                }
                if (res.data.tags.length > 0) {
                  for (var i = 0; i < res.data.tags.length; i++) {
                    vueObj.hashtags.push("#" + res.data.tags[i].name);
                  }
                }
                if (vueObj.activityOwner.id != this.profileId && vueObj.visibility == 'Private') {
                  vueObj.$router.push('/profiles/' + this.profileId);
                }
                vueObj.hashtags.sort();
                if (!vueObj.continuous) {
                  this.getCorrectDateFormat(vueObj.startTime, vueObj.endTime, vueObj);
                }
                this.getActivityTypeDisplay(vueObj);
                this.getMetrics(res.data.profile.id);
                if (vueObj.location) {
                  this.locationString = vueObj.location.name
                  this.$nextTick(function () {
                    this.setUpMap();
                  });
                }
              }
              vueObj.locationDataLoading = false;
            }).catch((err) => {
          console.log(err)
          if (err.response && err.response.status == 404) {
            this.notFound = true;
          } else if (err.response && (err.response.status == 401 || err.response.status == 403)) {
            this.isAuthorized = false;
          } else {
            let profileId = this.$route.params.id;
            vueObj.$router.push("/profiles/" + profileId);
          }
        });
      },
      getMetrics(ownerId) {
        let activityId = this.$route.params.activityId;
        api.getActivityMetrics(ownerId, activityId)
            .then((res) => {
              this.metrics = res.data;
            })
            .catch((err) => {
              console.log(err);
            });
      },
      checkIsAdmin: async function () {
        this.loggedInIsAdmin = await AdminMixin.methods.checkUserIsAdmin();
      },
      getCorrectTimeFormat(dateObject) {
        let hour = dateObject.hour;
        let minute = dateObject.minute;
        if (dateObject.hour.toString().length === 1) {
          hour = '0' + dateObject.hour;
        }
        if (dateObject.minute.toString().length === 1) {
          minute = '0' + dateObject.minute;
        }
        let time = hour + ':' + minute;
        return time;
      },
      getCorrectDateFormat: function (start, end, currentObj) {
        let startTime = this.getCorrectTimeFormat(start)
        let startDate = new Date(start.year + "-" + start.monthValue + '-'
            + start.dayOfMonth + ' ' + startTime).toString();
        if (startTime == null) {
          startDate = new Date(start.year + "-" + start.monthValue + '-'
              + start.dayOfMonth + ' ' + '00:00').toString();
        }
        currentObj.startTime = startDate;
        currentObj.activity.startTime = startDate;

        let endTime = this.getCorrectTimeFormat(end)
        let endDate = new Date(end.year + "-" + end.monthValue + '-'
            + end.dayOfMonth + ' ' + this.getCorrectTimeFormat(end)).toString();
        if (endTime == null) {
          endDate = new Date(end.year + "-" + end.monthValue + '-'
              + end.dayOfMonth + ' ' + '00:00').toString();
        }
        currentObj.endTime = endDate;
      },
      getActivityTypeDisplay: function (currentObj) {
        let result = "";
        for (let i = 0; i < currentObj.activityTypes.length; i++) {
          result += currentObj.activityTypes[i];
          if (i + 1 < currentObj.activityTypes.length) {
            result += ", ";
          }
        }
        currentObj.activityTypes = result;
        currentObj.activity.activityTypes = result;
      },
      clickHashtag(hashtag) {
        hashtag = hashtag.substring(1);
        let queryString = "?hashtags=%23" + hashtag + "&hashtags-method=AND&offset=1&limit=10"
        this.$router.push("/activities/search" + queryString);
      },
      forceRerender(value) {
        this.visibility = value;
        this.shareActivityKey += 1;
        this.followSummaryKey += 1;
        this.$refs.followUserList.getMembers();
      },
      async setUpMap() {
        let userLocation = await api.getLocation(this.profileId);
        let map = this.$refs.mapPane;
        // checking if user has a location to put on the map
        if (userLocation.data !== null) {
          map.createMarker(1, 1, userLocation.data.latitude, userLocation.data.longitude,
              this.profileId, this.activityOwner.firstname);
        }
        // checking if activity has a location to put on the map
        if (this.location !== null) {
          let content = {
            activityTypes: this.activity.activityTypes.split(', '),
            startTime: this.activity.startTime
          };

          map.createMarker(2, 2, this.location.latitude, this.location.longitude, content,
              this.activityName);
          map.setMapCenter(this.location.latitude, this.location.longitude);
        }
      this.$refs.followUserList.getMembers();

        if (this.activity.path !== null) {
          map.setPath(this.activity.path, true, true);
        }
      }

    }
  };
  export default App;
</script>

<style>
  .horizontal-scroll {
    flex-wrap: nowrap;
    white-space: nowrap;
    overflow: auto;
    margin-left: 15px;
    margin-right: 15px;
  }

  .metric-card {
    width: 250px;
    margin-right: 10px;
  }

  .metric-card .card-body {
    white-space: pre-line;
    padding: 10px;
  }

  .metric-card .card-title {
    font-size: 18px;
    margin-left: 7px;
  }
</style>

