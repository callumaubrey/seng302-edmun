<template>
  <div id="app" v-if="isLoggedIn">
    <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"/>
    <div v-if="!authorised">
      <ForbiddenMessage/>
    </div>

    <b-container fluid>
      <b-row align-v="start" align-h="center">
        <b-col cols="8" align-self="center">
          <!-- Title -->
          <b-row align-h="between">
            <b-col>
              <b-button @click="goToActivity()" style="float: right;">View activity
              </b-button>
              <h3>Edit Your Activity: {{ form.name }}</h3>
              <hr>
            </b-col>
          </b-row>

          <b-card no-body>
            <b-tabs card>

              <!-- Activity Info Editing -->
              <b-tab active>
                <template v-slot:title>
                  <b-icon v-if="formError" icon="exclamation-circle-fill" variant="danger"></b-icon>
                  Activity Info
                </template>
                <b-container fluid>

                  <b-form @submit.stop.prevent="onSubmit" novalidate>
                    <b-row>
                      <b-col>
                        <b-form-group>
                          <b-form-radio-group id="duration-type-group" v-model="isContinuous">
                            <b-form-radio name="duration-type" value='0'>Continuous</b-form-radio>
                            <b-form-radio name="duration-type" value='1'>Duration</b-form-radio>
                          </b-form-radio-group>
                        </b-form-group>
                      </b-col>
                    </b-row>

                    <b-row v-if="isContinuous === '1'">
                      <b-col>
                        <b-form-group id="start-date-input-group" label="Start Date"
                                      label-for="start-date-input">
                          <b-form-input
                              :state="validateDurationState('startDate')"
                              aria-describedby="start-date-feedback"
                              id="start-date-input"
                              max="9999-12-31"
                              type="date"
                              v-model="$v.durationForm.startDate.$model"
                          />
                          <b-form-invalid-feedback id="start-date-feedback">This is a required
                            field.
                            Start date
                            must be earlier than end date, and must not be earlier than old start
                            date
                          </b-form-invalid-feedback>
                        </b-form-group>
                      </b-col>
                      <b-col>
                        <b-form-group id="end-date-input-group" label="End Date"
                                      label-for="end-date-input">
                          <b-form-input
                              :state="validateDurationState('endDate')"
                              aria-describedby="end-date-feedback"
                              id="end-date-input"
                              max="9999-12-31"
                              type="date"
                              v-model="$v.durationForm.endDate.$model"
                          />
                          <b-form-invalid-feedback id="end-date-feedback">This is a required field.
                            Start date
                            must be earlier than end date
                          </b-form-invalid-feedback>
                        </b-form-group>
                      </b-col>
                    </b-row>

                    <b-row style="margin-bottom:10px;border-bottom:1px solid #ececec;"
                           v-if="isContinuous === '1'">
                      <b-col>
                        <b-form-group id="start-time-input-group" label="Start Time"
                                      label-for="start-time-input">
                          <b-form-text>Default start time is 12:00 am</b-form-text>
                          <b-form-input
                              :state="validateDurationState('startTime')"
                              id="start-time-input"
                              type="time"
                              v-model="$v.durationForm.startTime.$model"
                          />
                        </b-form-group>
                      </b-col>
                      <b-col>
                        <b-form-group id="end-time-input-group" label="End Time"
                                      label-for="end-time-input">
                          <b-form-text>Default end time is 12:00 am</b-form-text>
                          <b-form-input
                              :state="validateDurationState('endTime')"
                              aria-describedby="end-time-feedback"
                              id="end-time-input"
                              type="time"
                              v-model="$v.durationForm.endTime.$model"
                          />
                          <b-form-invalid-feedback id="end-time-feedback">End time cannot be before
                            or
                            the same as start time.
                          </b-form-invalid-feedback>
                        </b-form-group>
                      </b-col>
                    </b-row>

                    <b-row>
                      <b-col>
                            <span v-if="this.form.selectedActivityTypes.length > 0">
                                Activity Types:
                                <b-form-text>Click on the activity type to remove</b-form-text>
                            </span>
                        <b-list-group horizontal="md" v-if="this.form.selectedActivityTypes">
                          <b-list-group-item :key="activityType"
                                             class="clickable"
                                             v-for="activityType in this.form.selectedActivityTypes"
                                             v-on:click="deleteActivityType(activityType)">
                            {{ activityType }}
                          </b-list-group-item>
                        </b-list-group>
                        <b-form-group id="activity-type-group" label="Add Activity Type"
                                      label-for="activity-type">
                          <b-form-select
                              :options="activityTypes"
                              :state="validateState('selectedActivityType')"
                              aria-describedby="activity-type-feedback"
                              id="activity-type"
                              name="activity-type"
                              v-model="$v.form.selectedActivityType.$model"
                              v-on:change="addActivityType()"
                          />
                          <b-form-invalid-feedback id="activity-type-feedback">Please select an
                            activity type.
                          </b-form-invalid-feedback>
                        </b-form-group>
                        <hr>
                      </b-col>
                    </b-row>

                    <b-row>
                      <b-col>
                        <SearchTag :max-entries="30" :title-label="'Hashtags'"
                                   :options="hashtag.options"
                                   :values="hashtag.values"
                                   :help-text="'Max 30 hashtags'"
                                   :input-character-limit="140"
                                   v-on:emitInput="autocompleteInput"
                                   v-on:emitTags="manageTags"
                        />
                      </b-col>
                    </b-row>
                    <hr>

                    <b-row>
                      <b-col>
                        <b-form-group id="name-input-group" label="Name" label-for="name-input">
                          <b-form-input
                              :state="validateState('name')"
                              aria-describedby="name-feedback"
                              id="name-input"
                              maxlength=128
                              name="name-input"
                              v-model="$v.form.name.$model"
                          />
                          <b-form-invalid-feedback id="name-feedback">This is a required field.
                          </b-form-invalid-feedback>
                        </b-form-group>
                      </b-col>
                    </b-row>

                    <b-row>
                      <b-col>
                        <b-form-group id="description-input-group" label="Description"
                                      label-for="description-input">
                          <b-form-textarea
                              :state="validateState('description')"
                              id="description-input"
                              maxlength=2048
                              name="description-input"
                              placeholder="How did it go?"
                              v-model="$v.form.description.$model"
                          />
                          <b-form-invalid-feedback id="name-feedback">This is a required field.
                          </b-form-invalid-feedback>
                        </b-form-group>
                      </b-col>
                    </b-row>

                  </b-form>
                </b-container>
              </b-tab>

              <b-tab>
                <template v-slot:title>
                  <b-icon v-if="mapError" icon="exclamation-circle-fill" variant="danger"></b-icon>
                  Activity Location
                </template>
                <ActivityLocationTab ref="map"
                                     :can-hide="false"
                                     :user-lat="userLat"
                                     :user-long="userLong"
                                     @locationSelect="updateLocation"
                                     :activity-lat="locationData.latitude"
                                     :activity-long="locationData.longitude"
                />
              </b-tab>


              <!-- Activity Path Editor -->
              <b-tab title="Activity Path">
                <PathInfoMapCreateEdit ref="pathInfoCreateEdit" :profileId = "profileId" :activityId = "activityId" :path = "path"></PathInfoMapCreateEdit>
              </b-tab>

              <!-- Metrics Editor -->
              <b-tab>
                <template v-slot:title>
                  <b-icon v-if="metricError" icon="exclamation-circle-fill"
                          variant="danger"></b-icon>
                  Activity Metrics
                </template>
                <ActivityMetricsEditor ref="metric_editor" :profile-id="profileId"
                                       :activity-id="activityId"></ActivityMetricsEditor>
              </b-tab>
            </b-tabs>


          </b-card>
          <br>
          <b-row>
            <b-col>

              <b-button id="saveButton" type="submit" v-on:click="onSubmit" variant="primary">Save
                changes
              </b-button>
            </b-col>
          </b-row>

        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import SearchTag from "../../components/SearchTag";
import ForbiddenMessage from "../../components/ForbiddenMessage";
import {validationMixin} from "vuelidate";
import {required} from 'vuelidate/lib/validators';
import locationMixin from "../../mixins/locationMixin";
import AdminMixin from "../../mixins/AdminMixin";
import api from '@/Api'
import ActivityMetricsEditor from "../../components/Activity/Metric/ActivityMetricsEditor";
import ActivityLocationTab from "../../components/Activity/ActivityLocationTab";
import {store} from "../../store";
import PathInfoMapCreateEdit from "../../components/MapPane/PathInfoMapCreateEdit";


  export default {
    mixins: [validationMixin, locationMixin],
    components: {
      PathInfoMapCreateEdit,
      ActivityMetricsEditor,
      SearchTag,
      NavBar,
      ForbiddenMessage,
      ActivityLocationTab
    },
    data() {
      return {
        isLoggedIn: false,
        userName: '',
        isContinuous: '',
        profileId: null,
        activityId: null,
        activityTypes: ["Hike", "Bike", "Run", "Walk", "Swim"],
        activityUpdateMessage: "",
        activityErrorMessage: "",
        userLat: null,
        userLong: null,
        formError: false,
        metricError: false,
        mapError: false,
        form: {
          name: null,
          description: null,
          selectedActivityType: 0,
          selectedActivityTypes: [],
          date: null,
        },
        durationForm: {
          startDate: null,
          endDate: null,
          startTime: null,
          endTime: null
        },
        // previous start date
        dbStartDate: null,
        locationData: {
          latitude: null,
          longitude: null
        },
        loggedInIsAdmin: false,
        hashtag: {
          options: [],
          values: []
        },
        authorised: true,
        path: {}
      }
    },
    validations: {
      form: {
        name: {
          required
        },
        description: {},
        selectedActivityType: {
          required,
          validateActivityType() {
            return this.form.selectedActivityTypes.length >= 1;
          }
        },
        date: {},
      },
      durationForm: {
        startDate: {
          required,
          dateValidate(val) {
            return val >= new Date().toISOString().split('T')[0];
          }
        },
        endDate: {
          required,
          validateDate() {
            let startDate = new Date(this.durationForm.startDate);
            let endDate = new Date(this.durationForm.endDate);
            return startDate <= endDate;
          }
        },
        startTime: {},
        endTime: {
          timeValidate(val) {
            let startTime = this.durationForm.startTime;
            //let startDate = new Date(this.durationForm.startDate);
            //let endDate = new Date(this.durationForm.endDate);
            if (this.durationForm.startDate == this.durationForm.endDate) {
              if (val && startTime) {
                let splitStartTime = startTime.split(":");
                let splitEndTime = val.split(":");
                let startTimeObj = new Date();
                startTimeObj.setHours(splitStartTime[0], splitStartTime[1]);
                let endTimeObj = new Date();
                endTimeObj.setHours(splitEndTime[0], splitEndTime[1]);
                if (endTimeObj <= startTimeObj) {
                  return false;
                }
              }
            }
            return true;
          }
        }
      }
    },
    methods: {
      manageTags: function (value) {
        this.hashtag.values = value;
        this.hashtag.options = [];
      },
      autocompleteInput: function (value) {
        let pattern = /^#?[a-zA-Z0-9_]*$/;
        if (!pattern.test(value)) {
          this.hashtag.options = [];
          return;
        }
        if (value[0] == "#") {
          value = value.substr(1);
        }
        if (value.length > 2) {
          let vue = this;
          api.getHashtagAutocomplete(value)
              .then(function (response) {
                let results = response.data.results;
                for (let i = 0; i < results.length; i++) {
                  results[i] = "#" + results[i];
                }
                vue.hashtag.options = results;
              })
              .catch(function () {

              });
        } else {
          this.hashtag.options = [];
        }
      },
      getActivity: function () {
        let currentObj = this;
        api.getActivity(this.activityId)
            .then(function (response) {
              currentObj.form.name = response.data.activityName;
              currentObj.form.description = response.data.description;
              currentObj.form.selectedActivityTypes = response.data.activityTypes;
              currentObj.path = response.data.path
              currentObj.$refs.pathInfoCreateEdit.setPath(response.data.path)
              if (response.data.continuous === false) {
                currentObj.isContinuous = '1';
                [currentObj.durationForm.startDate,
                  currentObj.durationForm.startTime] = currentObj.convertISOtoDateTime(
                    response.data.startTime);
                [currentObj.dbStartDate,] = currentObj.convertISOtoDateTime(
                    response.data.startTime);
                [currentObj.durationForm.endDate,
                  currentObj.durationForm.endTime] = currentObj.convertISOtoDateTime(
                    response.data.endTime);
              } else {
                currentObj.isContinuous = '0';
              }
              if (response.data.location) {
                currentObj.locationData = response.data.location;
              }
              if (response.data.tags.length > 0) {
                for (let i = 0; i < response.data.tags.length; i++) {
                  currentObj.hashtag.values.push("#" + response.data.tags[i].name);
                }
              }
              currentObj.hashtag.values.sort();

              currentObj.$refs.metric_editor.loadMetricData(response.data.metrics);
            })
            .catch(function (error) {
              console.log(error.response);
            });
      },
      validateState(name) {
        const {$dirty, $error} = this.$v.form[name];
        return $dirty ? !$error : null;
      },
      validateDurationState(name) {
        const {$dirty, $error} = this.$v.durationForm[name];
        return $dirty ? !$error : null;
      },
      addActivityType() {
        if (this.form.selectedActivityType === 0) {
          return;
        }
        if (!this.form.selectedActivityTypes.includes(this.form.selectedActivityType)) {
          this.form.selectedActivityTypes.push(this.form.selectedActivityType);
        }
      },
      deleteActivityType(index) {
        this.form.selectedActivityTypes.splice(index, 1);
      },
      onSubmit() {
        this.activityErrorMessage = "";
        this.activityUpdateMessage = "";
        this.$v.form.$touch();
        let userId = this.profileId;
        if (this.loggedInIsAdmin) {
          userId = this.$route.params.id;
        }
        if (this.$v.form.$anyError || this.form.selectedActivityTypes < 1) {
          this.formError = true;
        } else {
          if (this.isContinuous === '0') {
            this.formError = false;
          }
        }
        if (this.isContinuous !== '0') {
          this.$v.durationForm.$touch();
          this.formError = !!this.$v.durationForm.$anyError;
        }
        this.mapError = !this.$refs.map.validLocation;
        this.metricError = !this.$refs.metric_editor.validateMetricData();
        if (this.formError || this.mapError || this.metricError) {
          this.$bvToast.toast('There are errors in the form, please check all tabs', {
            toaster: "b-toaster-bottom-center",
            variant: "danger",
            solid: true
          })
          return;
        }
        let currentObj = this;
        console.log(this.locationData);
        let data = {
          activity_name: this.form.name,
          description: this.form.description,
          activity_type: this.form.selectedActivityTypes,
          continuous: true,
          location: this.locationData,
          hashtags: this.hashtag.values,
          metrics: this.$refs.metric_editor.getMetricData()
        };
        if (this.isContinuous === '0') {
          api.updateActivity(userId, this.activityId, data)
              .then(() => {
                currentObj.updatePath().then(()=> {
                  store.newNotification('Activity updated successfully', 'success', 4);
                  currentObj.$router.push('/profiles/' + userId + '/activities/' + this.activityId);
                }).catch((err) => {
                  console.error(err);
                  store.newNotification('Activity updated successfully, path could not be updated. Try again later.', 'warning', 4);
                  currentObj.$router.push('/profiles/' + userId + '/activities/' + this.activityId);
                });
              })
              .catch(function () {
                currentObj.$bvToast.toast('Failed to update activity, server error', {
                  toaster: "b-toaster-bottom-center",
                  variant: "danger",
                  solid: true
                })
              });

        } else {
          this.$v.durationForm.$touch();
          if (this.$v.durationForm.$anyError) {
            this.formError = true;
            return;
          } else {
            this.formError = false;
          }
          const isoDates = this.getISODates();
          data = {
            activity_name: this.form.name,
            description: this.form.description,
            activity_type: this.form.selectedActivityTypes,
            continuous: false,
            start_time: isoDates[0],
            end_time: isoDates[1],
            location: this.locationData,
            hashtags: this.hashtag.values,
            metrics: this.$refs.metric_editor.getMetricData()
          };
          api.updateActivity(userId, this.activityId, data)
              .then(() => {
                currentObj.updatePath().then(()=> {
                  store.newNotification('Activity updated successfully', 'success', 4);
                  currentObj.$router.push('/profiles/' + userId + '/activities/' + this.activityId);
                }).catch((err) => {
                  console.error(err);
                  store.newNotification('Activity updated successfully, path could not be updated. Try again later.', 'warning', 4);
                  currentObj.$router.push('/profiles/' + userId + '/activities/' + this.activityId);
                });
              })
              .catch(function () {
                currentObj.$bvToast.toast('Failed to update activity, server error', {
                  toaster: "b-toaster-bottom-center",
                  variant: "danger",
                  solid: true
                })
              });
        }
      },

      // updatePath: function() {
      //   return this.$refs.path_editor.updatePathInActivity(this.profileId, this.activityId);
      // },

      getISODates: function () {
        let startDateISO;
        if (this.durationForm.startTime === "00:00" || this.durationForm.startTime == null
            || this.durationForm.startTime === "") {
          startDateISO = this.durationForm.startDate + "T" + "00:00" + ":00+1200"
        } else {
          startDateISO = this.durationForm.startDate + "T" + this.durationForm.startTime
              + ":00+1200";
        }

        let endDateISO;
        if (this.durationForm.endTime === "00:00" || this.durationForm.endTime == null
            || this.durationForm.endTime === "") {
          endDateISO = this.durationForm.endDate + "T" + "00:00" + ":00+1200"
        } else {
          endDateISO = this.durationForm.endDate + "T" + this.durationForm.endTime + ":00+1200";
        }

        return [startDateISO, endDateISO];
      },
      convertISOtoDateTime: function (ISODate) {
        const date = new Date(ISODate.year + "-" + ISODate.monthValue + '-'
            + ISODate.dayOfMonth).toISOString().substring(0, 10);

        let hour = ISODate.hour;
        let minute = ISODate.minute;
        if (ISODate.hour.toString().length === 1) {
          hour = '0' + ISODate.hour;
        }
        if (ISODate.minute.toString().length === 1) {
          minute = '0' + ISODate.minute;
        }
        let time = hour + ':' + minute;
        if (time === "00:00") {
          time = null;
        }
        return [date, time]
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
      /**
       * Gets the users location details, if any, for the map tab marker
       * @returns {Promise<void>}
       */
      getUserLocation: async function () {
        let currentObj = this;
        await api.getLocation(this.profileId)
            .then(function (response) {
              currentObj.userLat = response.data.latitude;
              currentObj.userLong = response.data.longitude;
            }).catch(function () {
            });
      },
      /**
       * sets the location data for the activity from the coords emitted
       * @param coords emitted from map component
       */
      updateLocation: function (coords) {
        this.locationData = {
          latitude: coords.lat,
          longitude: coords.lng
        };
      },
      checkAuthorized: async function () {
        let currentObj = this;
        this.loggedInIsAdmin = await AdminMixin.methods.checkUserIsAdmin();
        return api.getProfileId()
            .then(function (response) {
              currentObj.profileId = response.data;
              if (parseInt(currentObj.profileId) !== parseInt(currentObj.$route.params.id)
                  && !currentObj.loggedInIsAdmin) {
                currentObj.authorised = false
              }
            })
            .catch(function () {
              currentObj.authorised = false
            });
      },
      goToActivity: function () {
        this.$router.push('/profiles/' + this.profileId + '/activities/' + this.activityId);
      },
      onChildClick: function (val) {
        this.selectedVisibility = val
      },

      // loadActivityPath: function() {
      //   this.$refs.path_editor.getPathFromActivity(this.profileId, this.activityId);
      // }
    },
    mounted: async function () {
      this.activityId = this.$route.params.activityId;
      await this.checkAuthorized();
      this.getActivity();
      await this.getUserId();
      await this.getUserLocation();
      // this.loadActivityPath();
    }
  }
</script>

<style scoped>
  [v-cloak] {
    display: none;
  }

  .clickable {
    cursor: pointer;
  }

</style>