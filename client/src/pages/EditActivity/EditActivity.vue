<template>
  <div id="app" v-if="isLoggedIn">
    <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
    <div v-if="!authorised">
      <ForbiddenMessage></ForbiddenMessage>
    </div>

    <b-row class="mb-4" v-else>
      <b-col cols="8" offset="2">
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
            <b-tab active title="Activity Info">
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

                  <b-row v-if="isContinuous == '1'">
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
                        ></b-form-input>
                        <b-form-invalid-feedback id="start-date-feedback">This is a required field.
                          Start date
                          must be earlier than end date, and must not be earlier than old start date
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
                        ></b-form-input>
                        <b-form-invalid-feedback id="end-date-feedback">This is a required field.
                          Start date
                          must be earlier than end date
                        </b-form-invalid-feedback>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row style="margin-bottom:10px;border-bottom:1px solid #ececec;"
                         v-if="isContinuous == '1'">
                    <b-col>
                      <b-form-group id="start-time-input-group" label="Start Time"
                                    label-for="start-time-input">
                        <b-form-input
                            :state="validateDurationState('startTime')"
                            id="start-time-input"
                            type="time"
                            v-model="$v.durationForm.startTime.$model"
                        ></b-form-input>
                      </b-form-group>
                    </b-col>
                    <b-col>
                      <b-form-group id="end-time-input-group" label="End Time"
                                    label-for="end-time-input">
                        <b-form-input
                            :state="validateDurationState('endTime')"
                            aria-describedby="end-time-feedback"
                            id="end-time-input"
                            type="time"
                            v-model="$v.durationForm.endTime.$model"
                        ></b-form-input>
                        <b-form-invalid-feedback id="end-time-feedback">End time cannot be before or
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
                        ></b-form-select>
                        <b-form-invalid-feedback id="activity-type-feedback">Please select an
                          activity type.
                        </b-form-invalid-feedback>
                      </b-form-group>
                      <hr>
                    </b-col>
                  </b-row>

                <b-row>
                    <b-col>
                        <SearchTag :max-entries="30" :title-label="'Hashtags'" :options="hashtag.options"
                                   :values="hashtag.values"
                                   :help-text="'Max 30 hashtags'"
                                   :input-character-limit="140"
                                   v-on:emitInput="autocompleteInput"
                                   v-on:emitTags="manageTags"
                                   ></SearchTag>
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
                        ></b-form-input>
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
                        ></b-form-textarea>
                        <b-form-invalid-feedback id="name-feedback">This is a required field.
                        </b-form-invalid-feedback>
                      </b-form-group>
                    </b-col>
                  </b-row>
                  <b-button id="saveButton" type="submit" variant="primary">Save changes</b-button>
                  <b-form-valid-feedback :state='activityUpdateMessage != ""' class="feedback">
                    {{ activityUpdateMessage }}
                  </b-form-valid-feedback>
                  <b-form-invalid-feedback :state='activityErrorMessage == ""' class="feedback">
                    {{ activityErrorMessage }}
                  </b-form-invalid-feedback>
                </b-form>
              </b-container>
            </b-tab>

            <ActivityLocationTab ref="map"
                                 :can-hide="false"
                                 :user-lat="userLat"
                                 :user-long="userLong"
                                 @locationSelect="updateLocation"
                                 :activity-lat="locationData.latitude"
            ></ActivityLocationTab>

            <!-- Metrics Editor -->
            <b-tab title="Activity Metrics">
              <ActivityMetricsEditor ref="metric_editor"></ActivityMetricsEditor>
            </b-tab>
          </b-tabs>

        </b-card>
      </b-col>
    </b-row>
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

    export default {
      mixins: [validationMixin, locationMixin],
      components: {
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
          locationData: null,
          loggedInIsAdmin: false,
          hashtag: {
            options: [],
            values: []
          },
          authorised: true
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
              if (this.form.selectedActivityTypes.length < 1) {
                return false
              }
              return true
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
              if (startDate > endDate) {
                return false;
              }
              return true;
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
            if (response.data.continuous == false) {
              currentObj.isContinuous = '1';
              [currentObj.durationForm.startDate,
                currentObj.durationForm.startTime] = currentObj.convertISOtoDateTime(
                  response.data.startTime);
              [currentObj.dbStartDate,] = currentObj.convertISOtoDateTime(response.data.startTime);
              [currentObj.durationForm.endDate,
                currentObj.durationForm.endTime] = currentObj.convertISOtoDateTime(
                  response.data.endTime);
            } else {
              currentObj.isContinuous = '0';
            }
            currentObj.locationData = response.data.location;
            if (response.data.tags.length > 0) {
              for (var i = 0; i < response.data.tags.length; i++) {
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
          if (this.form.selectedActivityType == 0) {
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
          if (this.$v.form.$anyError || this.form.selectedActivityTypes < 1
              || !this.$refs.metric_editor.validateMetricData()) {
            return;
          }
          let currentObj = this;
          let data = {
            activity_name: this.form.name,
            description: this.form.description,
            activity_type: this.form.selectedActivityTypes,
            continuous: true,
            location: this.locationData,
            hashtags: this.hashtag.values,
            metrics: this.$refs.metric_editor.getMetricData()
          }
          if (this.isContinuous == '0') {
            api.updateActivity(userId, this.activityId, data)
            .then(function (response) {
              console.log(response);
              currentObj.activityUpdateMessage = "Successfully updated activity: "
                  + currentObj.form.name;
              currentObj.activityErrorMessage = "";
            })
            .catch(function (error) {
              currentObj.activityErrorMessage = "Failed to update activity: " + error.response.data
                  + ". Please try again";
              currentObj.activityUpdateMessage = "";
            });

          } else {
            this.$v.durationForm.$touch();
            if (this.$v.durationForm.$anyError) {
              return;
            }
            const isoDates = this.getISODates();
            let data = {
              activity_name: this.form.name,
              description: this.form.description,
              activity_type: this.form.selectedActivityTypes,
              continuous: false,
              start_time: isoDates[0],
              end_time: isoDates[1],
              location: this.locationData,
              hashtags: this.hashtag.values,
              metrics: this.$refs.metric_editor.getMetricData()
            }
            api.updateActivity(userId, this.activityId, data)
            .then(function (response) {
              console.log(response);
              currentObj.activityUpdateMessage = "Successfully updated activity: "
                  + currentObj.form.name;
              currentObj.activityErrorMessage = "";
            })
            .catch(function (error) {
              currentObj.activityErrorMessage = "Failed to update activity: " + error.response.data
                  + ". Please try again";
              currentObj.activityUpdateMessage = ""
            });

          }
        },
        getISODates: function () {
          console.log(this.durationForm.startDate)
          let startDate = new Date(this.durationForm.startDate);
          let endDate = new Date(this.durationForm.endDate);

          // wind it back to previous date to align with local date time
          startDate.setDate(startDate.getDate() - 1);
          endDate.setDate(endDate.getDate() - 1);

          if (this.durationForm.startTime != "" && this.durationForm.startTime != null) {
            startDate = new Date(
                this.durationForm.startDate + " " + this.durationForm.startTime + " UTC");
          }

          if (this.durationForm.endTime != "" && this.durationForm.startTime != null) {
            endDate = new Date(this.durationForm.endDate + " " + this.durationForm.endTime + " UTC");
          }

          let startDateISO = startDate.toISOString().slice(0, -5);
          let endDateISO = endDate.toISOString().slice(0, -5);

          var currentTime = new Date();
          const offset = (currentTime.getTimezoneOffset());

          const currentTimezone = (offset / 60) * -1;
          if (currentTimezone !== 0) {
            startDateISO += currentTimezone > 0 ? '+' : '';
            endDateISO += currentTimezone > 0 ? '+' : '';
          }
          startDateISO += currentTimezone.toString() + "00";
          endDateISO += currentTimezone.toString() + "00";

          if (this.durationForm.startTime == "" || this.durationForm.startTime == null) {
            startDateISO = startDateISO.substring(0, 11) + "24" + startDateISO.substring(13,
                startDateISO.length);
          }
          if (this.durationForm.endTime == "" || this.durationForm.endTime == null) {
            endDateISO = endDateISO.substring(0, 11) + "24" + endDateISO.substring(13,
                endDateISO.length);
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
        }
      },
      mounted: async function () {
        this.activityId = this.$route.params.activityId;
        await this.checkAuthorized();
        this.getActivity();
        this.getUserId();
      }
    }
</script>

<style scoped>
    [v-cloak] {
      display: none;
    }

    .container {
      background-color: #f2f2f2;
      padding: 20px 20px 20px 20px;
      border: 1px solid lightgrey;
      border-radius: 3px;
    }

    .invisible-btn {
      background-color: Transparent;
      background-repeat: no-repeat;
      border: none;
      cursor: pointer;
      overflow: hidden;
      outline: none;
      color: blue;
      font-size: 14px;
    }

    .feedback {
      padding-bottom: 10px;
    }

    .clickable {
      cursor: pointer;
    }

</style>