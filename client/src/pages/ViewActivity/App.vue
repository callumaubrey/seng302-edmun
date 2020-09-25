<template>
  <div id="app" v-if="isLoggedIn">
    <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>



    <!-- Main Row of the page -->
    <b-container fluid class="content_container">
      <div v-if="archived">
        <h1 align="center">This activity has been deleted</h1>
      </div>
      <div v-else-if="notFound">
        <h1 align="center">This activity does not exist</h1>
      </div>
      <div v-else-if="!isAuthorized">
        <ForbiddenMessage></ForbiddenMessage>
      </div>
      <b-row v-else-if="!locationDataLoading" class="content_container">
      <!-- Left Side bar -->
      <b-col lg="3" class="pt-4" style="min-width: 480px; overflow-y: auto; min-height: 100% !important; background: #2c3136;">
        <!-- Image and Name -->
        <b-row style="font-size: 6em;" align-h="center">
          <b-col style="max-width: 440px; max-height: 248px; padding: 0em; margin: 0em">
            <UserImage :id="parseInt($route.params.activityId)" is-activity
                       :editable="parseInt(profileId) === parseInt(loggedInId) || loggedInIsAdmin"
                       save-on-change
                       style="min-width: 440px; min-height: 248px;"
                       image-warning="Recommended aspect ratio 16:9. Or it will stretch!"
            ></UserImage>
          </b-col>
        </b-row>
        <b-row align-h="center">
          <h3 style="color: white; padding-top: 15px">{{ activityName }}</h3>
        </b-row>

        <!-- Summary -->
        <FollowerSummary class="text-center"
                         :activityId="parseInt($route.params.activityId)"
                         :key="followSummaryKey"
                         ref="followSummary"
                         style="color: white"></FollowerSummary>

        <!-- Actions -->
        <b-row align-h="center">
          <FollowUnfollow v-bind:activityId="parseInt(this.$route.params.activityId)"
                          v-bind:activityOwnerId="parseInt(this.$route.params.id)"
                          v-bind:loggedInId="loggedInId"
                          v-on:activityFollowed="activityIsFollowed"></FollowUnfollow>
        </b-row>
        <b-row align="center" v-if="parseInt(profileId) === parseInt(loggedInId) || loggedInIsAdmin">
          <b-col>
            <ShareActivity style="padding: 0.4em; cursor: pointer"
                           :modal="parseInt(loggedInId) === parseInt(activityOwner.id)"
                           :visibility="visibility"
                           :profileId="profileId"
                           :activityId="$route.params.activityId"
                           v-on:componentUpdate="forceRerender"
                           :key="shareActivityKey"></ShareActivity>
            <i class="fas fa-edit" style="color: white; font-size: 2em; padding: 0.4em; cursor: pointer" @click="editActivity()"></i>
            <i class="far fa-trash-alt" style="color: white; font-size: 2em; padding: 0.4em; cursor: pointer;" @click="deleteActivity()"></i>
          </b-col>

        </b-row>

<!--        About-->
        <b-card style="margin: 1em; overflow: auto">
          <div v-if="locationDataLoading">
            <div class="text-center text-primary my-2">
              <b-spinner class="align-middle"></b-spinner>
              <strong> Loading...</strong>
            </div>
          </div>
          <div v-else>
            <b-col>
              <label>
                <b>Activity Type(s):</b>
              </label>
              <p>{{ activityTypes }}</p>

              <div v-if="!continuous">
                <label>
                  <b>Start:</b>
                </label>
                <p>{{ startTime }}</p>
                <label>
                  <b>End:</b>
                </label>
                <p>{{ endTime }}</p>
              </div>

              <div v-if="location==null">
                <label>
                  <b>Location:</b>
                </label>
                <p>No location available</p>
              </div>

              <div v-if="location!=null">
                <label>
                  <b>Location:</b>
                </label>
                <p>{{ locationString }}</p>
              </div>

              <div>
                <label>
                  <b>Description:</b>
                </label>
                <p style="text-align: justify;">{{ description }}</p>
              </div>

              <div>
                <label>
                  <b>Hashtags:</b>
                </label>
                <p>
                            <span v-for="hashtag in hashtags" v-bind:key="hashtag">
                              <b-link @click="clickHashtag(hashtag)"
                              >{{ hashtag }}&nbsp;</b-link
                              >
                            </span>
                </p>
              </div>

              <div>
                <label>
                  <b>Activity Creator:</b>
                </label>
                <p>{{ activityOwner.firstname }} {{ activityOwner.lastname }}</p>
              </div>
            </b-col>
          </div>
        </b-card>
        <br>
      </b-col>



          <!-- Tab Content -->
      <b-col class="p-0 pt-4 mh-100">
          <b-tabs pills align="center" style="height: 100%">
            <br>

            <!-- Location Map Tab -->
            <b-tab title="Location"
                   @click="$refs.mapPane.refreshMap()"
                   v-if="this.location || this.activity.path" active
            class="activity-page-content">
              <b-card>
                <h4 v-if="location && location.name" align="center">
                  {{location.name}}
                </h4>
                <MapPane ref="mapPane" :path-overlay="false" :can-hide="true"></MapPane>
              </b-card>
            </b-tab>

            <!-- Participants Tab -->
            <b-tab title="Participants">
                <b-col align="center" class="activity-page-content">
                  <b-card style="margin-top: 1em" :title="'Members of ' + activityName + ':'">
                    <FollowerUserList :activity-id="parseInt($route.params.activityId)"
                                      :logged-in-id="parseInt(loggedInId)"
                                      :activity-creator-id="activityOwner.id"
                                      ref="followerUserList"
                    style="padding-top: 2em"></FollowerUserList>
                  </b-card>
                </b-col>
            </b-tab>

            <!-- Route Tab -->
            <b-tab :disabled="activity.path == null" title="Route" @click="$refs.pathInfoMap.refreshMap()" class="activity-page-content">
              <b-card class="activity-page-content"  title="Activities Route:" align="center">
                <PathInfoMapView ref="pathInfoMap" :path="activity.path"></PathInfoMapView>
              </b-card>
            </b-tab>

            <!-- Results and metrics Tab -->
            <b-tab :disabled="activity.metrics.length == 0" title="Results" class="activity-page-content">
              <b-col cols="9">
                <b-row>
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
              <br>
              <b-card>
                <RecordActivityResultModal :activity-id="this.$route.params.activityId"
                                           :logged-in-id="loggedInId"
                                           :profile-id="profileId"
                                           style="padding-bottom: 1em"></RecordActivityResultModal>
                <ActivityResults :profile-id="profileId"
                                 :activity-id="$route.params.activityId"></ActivityResults>
              </b-card>
            </b-tab>
          </b-tabs>
        </b-col>
    </b-row>
    </b-container>
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
  import PathInfoMapView from "../../components/MapPane/PathInfoMapView";
  import UserImage from "../../components/Activity/UserImage/UserImage";

  const App = {
    name: "App",
    components: {
      UserImage,
      MapPane,
      ActivityResults,
      NavBar,
      FollowUnfollow,
      FollowerUserList,
      FollowerSummary,
      ShareActivity,
      ForbiddenMessage,
      RecordActivityResultModal,
      PathInfoMapView
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
        shareActivityKey: 0,
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
        this.$refs.followerUserList.getMembers();
      },
      async setUpMap() {
        let userLocation = await api.getLocation(this.profileId);
        let map = this.$refs.mapPane;
        // checking if user has a location to put on the map
        if (userLocation.data !== null) {
          map.createMarker(1, 3, userLocation.data.latitude, userLocation.data.longitude,
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
      },
      activityIsFollowed() {
        this.$refs.followSummary.loadFollowerSummary();
        this.$refs.followerUserList.getMembers();
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

  .content_container {
    margin-top: -50px;
    height: calc(100vh - 66px);
    max-height: calc(100vh - 66px);
    /** This is kinda of a dirty way to fill page height. It requires knowing the navbar height. If it was
    to change this page would break. However alternatives would be harder to understand and quite complicated**/
  }

  .activity-page-content {
    padding-right: 2em;
    padding-left: 2em;
  }
</style>

