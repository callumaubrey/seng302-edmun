<template>

  <!-- This block displays the activity result -->
  <div v-if="!result.isEditMode">
    <b-row>
      <b-col sm="3">
        <b-row>
          <h6>{{ result.title }}</h6>
        </b-row>
        <b-row>
          <p style="font-style: italic;">{{ result.description }}</p>
        </b-row>

      </b-col>
      <b-col sm="4">
        <h6 v-if="result.type!=='TimeStartFinish'">{{ result.result }}</h6>
        <div v-else>
          <h6> Start time: {{ result.result_start }} </h6>
          <h6> End time: {{ result.result_finish }} </h6>
        </div>
      </b-col>

      <b-col sm="2">
        <p style="font-style: italic; color: crimson">{{ specialMetricTitle }}</p>
      </b-col>

      <b-col sm="1">
        <b-button-group>
          <b-button @click="result.isEditMode=true" class="button-group" id="edit-result-button"
                    variant="success">Edit
          </b-button>
          <b-button @click="removeActivityResult" class="button-group" id="delete-result-button"
                    variant="danger">Delete
          </b-button>
        </b-button-group>
      </b-col>
    </b-row>
  </div>

  <!-- This block deals with the creating/editing activity result form -->
  <div v-else>
    <b-row>
      <b-col sm="4">
        <label>Metric Title: </label>
        <b-form-select :options="Object.keys(metricTitleDict)" id="select-metric-title"
                       v-model="result.title"
                       :disabled="specialMetricSelected"
                       v-on:change="updateInputGroup"></b-form-select>
        <b-form-text>{{ this.result.description }}</b-form-text>


        <label>Special Metric: </label>
        <b-form-select :options="Object.keys(specialMetricDict)" id="select-special-metric"
                       v-model="specialMetricTitle"
                       v-on:change="setSpecialMetricSelectedFlag"></b-form-select>
      </b-col>

      <b-col sm="6">
        <label v-if="result.type!=='TimeStartFinish'">Result: </label>
        <b-form-input id="result-input" placeholder="Enter your result"
                      :state="validateResultState('result')"
                      v-if="result.type==='Count' || result.type==='Distance'"
                      :disabled="specialMetricSelected"
                      v-model="$v.result.result.$model"></b-form-input>


        <b-input-group v-if="result.type==='TimeDuration'">
          <b-form-input :state="validateState('hour')" placeholder="Hours"
                        :disabled="specialMetricSelected"
                        v-model="$v.hour.$model"></b-form-input>
          <b-form-input :state="validateState('minute')" placeholder="Minutes"
                        :disabled="specialMetricSelected"
                        v-model="$v.minute.$model"></b-form-input>
          <b-form-input :state="validateState('second')" placeholder="Seconds"
                        :disabled="specialMetricSelected"
                        v-model="$v.second.$model"></b-form-input>
        </b-input-group>

        <b-input-group id="result-startfinish-feedback" v-if="result.type==='TimeStartFinish'">
          <div>
            <b-form-group id="start-date-input-group" label="Start Date"
                          label-for="start-date-input">
              <b-form-input
                  :disabled="specialMetricSelected"
                  :state="validateStartFinishState('startDate')"
                  id="start-date-input"
                  type="date"
                  v-model="$v.startFinish.startDate.$model"
              ></b-form-input>
              <b-form-invalid-feedback id="start-date-feedback">Start date must be in a valid format
              </b-form-invalid-feedback>
            </b-form-group>
            <b-form-group id="start-time-input-group" label="Start Time"
                          label-for="start-time-input">
              <b-form-input
                  :disabled="specialMetricSelected"
                  :state="validateStartFinishState('startTime')"
                  aria-describedby="start-time-feedback"
                  id="start-time-input"
                  type="time"
                  v-model="$v.startFinish.startTime.$model"
              ></b-form-input>
              <b-form-invalid-feedback id="start-time-feedback">Start time must be in a valid format
              </b-form-invalid-feedback>
            </b-form-group>
          </div>
          <div>
            <b-form-group id="end-date-input-group" label="End Date" label-for="end-date-input"
                          style="padding-left: 5px">
              <b-form-input
                  :disabled="specialMetricSelected"
                  :state="validateStartFinishState('endDate')"
                  aria-describedby="end-date-feedback"
                  id="end-date-input"
                  type="date"
                  v-model="$v.startFinish.endDate.$model"
              ></b-form-input>
              <b-form-invalid-feedback id="end-date-feedback">This is a required field and cannot be
                before start date.
              </b-form-invalid-feedback>
            </b-form-group>
            <b-form-group id="end-time-input-group" label="End Time" label-for="end-time-input"
                          style="padding-left: 5px">
              <b-form-input
                  :disabled="specialMetricSelected"
                  :state="validateStartFinishState('endTime')"
                  aria-describedby="end-time-feedback"
                  id="end-time-input"
                  type="time"
                  v-model="$v.startFinish.endTime.$model"
              ></b-form-input>
              <b-form-invalid-feedback id="end-time-feedback">End time cannot be before or the same
                as
                start time.
              </b-form-invalid-feedback>
            </b-form-group>
          </div>
        </b-input-group>
        <p style="color: crimson" v-if="resultErrorMessage!=null">{{ resultErrorMessage }}</p>
      </b-col>

      <b-col sm="1">
        <b-button @click="createActivityResult" id="create-result-button" v-if="isCreateResult">
          Create
        </b-button>
        <b-button-group v-else>
          <b-button @click="editActivityResult" id="save-result-button">Save</b-button>
          <b-button @click="onCancelButtonClick" id="cancel-result-button" variant="danger">
            Cancel
          </b-button>
        </b-button-group>
      </b-col>
    </b-row>
  </div>
</template>

<script>
import api from "@/Api";
import {validationMixin} from "vuelidate";

let durationRegex = /(\d+)h (\d+)m (\d+)s/;

export default {
  name: "RecordActivityResultForm",
  mixins: [validationMixin],
  props: ['result', 'metricDict', 'isCreateResult', 'profileId', 'activityId'],
  data() {
    return {
      // key (special metric title), value (special metric enum)
      specialMetricDict: {
        "None": null,
        "Did not finish": "DidNotFinish",
        "Disqualified": "Disqualified",
        "Technical Failure": "TechnicalFailure"
      },
      resultErrorMessage: null,
      hour: null,
      minute: null,
      second: null,
      specialMetricTitle: null,
      specialMetricSelected: false,
      startFinish: {
        startDate: null,
        startTime: null,
        endDate: null,
        endTime: null
      }
    }
  },
  validations: {
    hour: {
      validateHour(val) {
        this.hour = val;
        let integerRegex = new RegExp("^\\d+$");
        if (!integerRegex.test(val)) {
          this.resultErrorMessage = "Hours must be an integer"
          return false
        } else {
          this.resultErrorMessage = null
          return true
        }
      }
    },
    minute: {
      validateMinute(val) {
        this.minute = val;
        let integerRegex = new RegExp("^\\d+$");
        if (!integerRegex.test(val)) {
          this.resultErrorMessage = "Minutes must be an integer"
          return false
        } else {
          this.resultErrorMessage = null
          return true
        }
      }
    },
    second: {
      validateSecond(val) {
        this.second = val;
        let integerRegex = new RegExp("^\\d+$");
        if (!integerRegex.test(val)) {
          this.resultErrorMessage = "Seconds must be an integer"
          return false
        } else {
          this.resultErrorMessage = null
          return true
        }
      }
    },
    startFinish: {
      startDate: {},
      endDate: {
        dateValidate(val) {
          let startDate = new Date(this.startDate);
          let endDate = new Date(val);
          if (endDate < startDate) {
            return false;
          }
          return true;
        }
      },
      startTime: {},
      endTime: {
        timeValidate(val) {
          let startTime = this.startTime;
          if (this.startDate == this.endDate) {
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
    },
    result: {
      result: {
        validateResult(val) {
          if (this.result.type === 'Count') {
            let integerRegex = new RegExp("^[0-9]*[1-9][0-9]*$");
            if (!integerRegex.test(val)) {
              this.resultErrorMessage = "Count should be an integer value"
              return false
            } else {
              this.resultErrorMessage = null
              return true
            }
          } else if (this.result.type === 'Distance') {
            let floatRegex = new RegExp("^(?=.)([+-]?([0-9]*)(\\.([0-9]+))?)$");
            if (!floatRegex.test(val)) {
              this.resultErrorMessage = "Distance should be a float value"
              return false
            } else {
              this.resultErrorMessage = null
              return true
            }
          } else {
            return true
          }
        }
      }
    }
  },
  computed: {
    // key (metric title), value (metric id)
    metricTitleDict() {
      let metricTitleDict = {}
      for (let metricId in this.metricDict) {
        metricTitleDict[this.metricDict[metricId].title] = metricId;
      }
      return metricTitleDict
    },

  },
  methods: {
    /**
     * Update result type and description to interchange input group,
     * Reset validation highlights,
     * Reset user input
     * @param val metric title
     */
    updateInputGroup(val) {
      this.result.type = this.metricDict[this.metricTitleDict[val]].unit;
      this.result.description = this.metricDict[this.metricTitleDict[val]].description;

      this.$v.result.$reset();
      this.$v.$reset();

      this.resultErrorMessage = null;
      this.result.result = null;
      this.result.result_start = null;
      this.result.result_finish = null;
    },
    validateResultState(name) {
      const {$dirty, $error} = this.$v.result[name];
      return $dirty ? !$error : null;
    },
    validateStartFinishState(name) {
      const {$dirty, $error} = this.$v.startFinish[name];
      return $dirty ? !$error : null;
    },
    validateState(name) {
      const {$dirty, $error} = this.$v[name];
      return $dirty ? !$error : null;
    },
    /**
     * Calls POST activity result endpoint, and also resets the form upon success
     */
    createActivityResult() {
      if (this.result.type === 'TimeDuration') {
        this.$v.$touch()
        if (this.$v.$anyError) {
          return;
        }
        this.convertToDurationStringFormat();
      } else if (this.result.type === 'TimeStartFinish') {
        this.$v.startFinish.$touch();
        if (this.$v.startFinish.$anyError) {
          return;
        }
        this.parseDateTimeInputIntoISODateTimeString();
      } else {
        this.$v.result.$touch();
        if (this.$v.result.$anyError) {
          return;
        }
      }
      let data = {
        metric_id: this.metricTitleDict[this.result.title],
        value: this.result.result,
        start: this.result.result_start,
        end: this.result.result_finish,
        special_metric: this.specialMetricDict[this.specialMetricTitle]
      }
      api.createActivityResult(this.profileId, this.activityId, data)
      .then((res) => {
        this.resultErrorMessage = null;
        this.result.id = res.data;
        this.resetForm();
        this.$emit('child-to-parent', 'create')
      })
      .catch(() => {
        this.makeToast("Activity result could not be created", 'danger')
      })
    },
    /**
     * Calls PUT activity result endpoint
     */
    editActivityResult() {
      if (this.result.type === 'TimeDuration') {
        this.$v.$touch()
        if (this.$v.$anyError) {
          return;
        }
        this.convertToDurationStringFormat();
      } else if (this.result.type === 'TimeStartFinish') {
        this.$v.startFinish.$touch();
        if (this.$v.startFinish.$anyError) {
          return;
        }
        this.parseDateTimeInputIntoISODateTimeString();
      } else {
        this.$v.result.$touch();
        if (this.$v.result.$anyError) {
          return;
        }
      }
      let data = {
        metric_id: this.metricTitleDict[this.result.title],
        value: this.result.result,
        start: this.result.result_start,
        end: this.result.result_finish,
        special_metric: this.specialMetricDict[this.specialMetricTitle]
      }
      api.updateActivityResult(this.profileId, this.activityId, this.result.id,
          data)
      .then(() => {
        this.result.isEditMode = false
        this.$v.result.$reset();
        this.$v.$reset();
        this.$emit('child-to-parent', 'edit')
      })
      .catch(() => {
        this.makeToast("Selected activity result could not be edited", 'danger')
      })
    },
    /**
     * Calls DELETE activity result endpoint
     */
    removeActivityResult() {
      api.deleteActivityResult(this.profileId, this.activityId, this.result.id)
      .then(() => {
        this.$emit('child-to-parent', 'delete')
      }).catch(() => {
        this.makeToast("Activity result could not be deleted", 'danger')
      })
    },
    /**
     * Makes a toast notification
     * @param message the notification message
     * @param variant the colour of the notification based on variant (see Bootstrap Vue variants)
     * @param delay the milliseconds that the toast would stay on the screen
     */
    makeToast(message = 'EDMUN', variant = null, delay = 5000,) {
      this.$bvToast.toast(message, {
        variant: variant,
        solid: true,
        autoHideDelay: delay
      })
    },
    /**
     * Combine hour, minute and second variables to form a duration string
     */
    convertToDurationStringFormat() {
      if (this.hour.length === 1) {
        this.hour = '0' + this.hour;
      }
      if (this.minute.length === 1) {
        this.minute = '0' + this.minute;
      }
      if (this.second.length === 1) {
        this.second = '0' + this.second;
      }
      this.result.result = this.hour + ':' + this.minute + ':' + this.second
    },
    /**
     * Combine date time input and convert them into ISO date time string to be sent to backend
     */
    parseDateTimeInputIntoISODateTimeString() {
      let startDate = new Date(this.startFinish.startDate);
      let endDate = new Date(this.startFinish.endDate);

      if (this.startFinish.startTime !== "" && this.startFinish.startTime != null) {
        startDate = new Date(this.startFinish.startDate + " " + this.startFinish.startTime);
      }

      if (this.startFinish.endTime !== "" && this.startFinish.endTime != null) {
        endDate = new Date(this.startFinish.endDate + " " + this.startFinish.endTime);
      }
      console.log(startDate);
      this.result.result_start = startDate.toISOString();
      this.result.result_finish = endDate.toISOString();
    },
    /**
     * Break ISO Date time string down into its respective date time input boxes
     */
    parseISODateTimeStringIntoDateTimeInput() {
      if (this.result.type === 'TimeStartFinish') {
        this.startFinish.startDate = this.result.result_start.substring(0, 10);
        this.startFinish.endDate = this.result.result_finish.substring(0, 10);

        this.startFinish.startTime = this.result.result_start.substring(11, 16);
        this.startFinish.endTime = this.result.result_finish.substring(11, 16);
        if (this.startFinish.startTime == "24:00") {
          this.startFinish.startTime = null;
        }
        if (this.startFinish.endTime == "24:00") {
          this.startFinish.endTime = null;
        }
      }

    },
    /**
     * Parse an example of '1h 2m 3s' string into its respective variables
     */
    parseDurationStringIntoHMS() {
      if (this.result.type === 'TimeDuration' && this.result.result !== null) {
        let matches = this.result.result.match(durationRegex);
        if (matches) {
          this.hour = matches[1] === undefined ? 0 : matches[1]
          this.minute = matches[2] === undefined ? 0 : matches[2]
          this.second = matches[3] === undefined ? 0 : matches[3]
        }
      }
    },
    /**
     * Reset create activity result form
     */
    resetForm() {
      this.result.metric_id = null;
      this.result.user_id = null;
      this.result.special_metric = null;
      this.result.result = null;
      this.result.result_finish = null;
      this.result.result_start = null;
      this.result.type = null
      this.result.isEditMode = true;
      this.result.title = null;
      this.result.description = null;
      this.$v.result.$reset();

      this.hour = null;
      this.minute = null;
      this.second = null;
      this.specialMetricTitle = null;
      this.$v.$reset();
    },
    /**
     * Compute special metric title by using special metric enum and specialMetricDict
     * @returns {null}
     */
    parseToSpecialMetricTitle() {
      if (this.result.special_metric !== null) {
        for (let specialMetricTitle in this.specialMetricDict) {
          if (this.specialMetricDict[specialMetricTitle] == this.result.special_metric) {
            this.specialMetricTitle = specialMetricTitle;
          }
        }
      }
    },
    /**
     * On 'Cancel' button click, edit mode is turned off, and modal is refreshed to get the unchanged data
     */
    onCancelButtonClick() {
      this.result.isEditMode = false;
      this.$emit('child-to-parent');
    },
    /**
     * Set specialMetricSelected attribute to true if the form is only used for editing and special metric is selected
     */
    setSpecialMetricSelectedFlag() {
      if (!this.isCreateResult && this.specialMetricTitle !== 'None') {
        this.specialMetricSelected = true
      } else {
        this.specialMetricSelected = false
      }
    }
  },
  mounted() {
    this.parseToSpecialMetricTitle();
    this.parseDurationStringIntoHMS();
    this.parseISODateTimeStringIntoDateTimeInput();
    this.setSpecialMetricSelectedFlag();
  }
}
</script>

<style>
.button-group {
  margin-right: 5px;
}

#select-metric-title {
  margin-bottom: 5px;
}

#cancel-result-button {
  margin-left: 5px;
}
</style>