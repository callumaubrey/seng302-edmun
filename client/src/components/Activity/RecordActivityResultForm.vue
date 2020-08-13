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
                       v-on:change="updateInputGroup"></b-form-select>
        <b-form-text>{{ this.result.description }}</b-form-text>


        <label>Special Metric: </label>
        <b-form-select :options="Object.keys(specialMetricDict)" id="select-special-metric"
                       v-model="specialMetricTitle"></b-form-select>
      </b-col>

      <b-col sm="6">
        <label v-if="result.type!=='TimeStartFinish'">Result: </label>
        <b-form-input id="result-input" placeholder="Enter your result"
                      :state="validateResultState('result')"
                      v-if="result.type==='Count' || result.type==='Distance'"
                      v-model="$v.result.result.$model"></b-form-input>


        <b-input-group v-if="result.type==='TimeDuration'">
          <b-form-input :state="validateState('hour')" placeholder="Hours"
                        v-model="$v.hour.$model"></b-form-input>
          <b-form-input :state="validateState('minute')" placeholder="Minutes"
                        v-model="$v.minute.$model"></b-form-input>
          <b-form-input :state="validateState('second')" placeholder="Seconds"
                        v-model="$v.second.$model"></b-form-input>
        </b-input-group>

        <b-input-group id="result-startfinish-feedback" v-if="result.type==='TimeStartFinish'">
          <b-row>
            <label>Start Time: </label>
            <b-form-input placeholder="Enter your start time" type="datetime-local"
                          :state="validateResultState('result_start')"
                          v-model="$v.result.result_start.$model"></b-form-input>
          </b-row>
          <b-row>
            <label style="padding-top: 5px">End Time: </label>
            <b-form-input placeholder="Enter your end time" type="datetime-local"
                          :state="validateResultState('result_finish')"
                          v-model="$v.result.result_finish.$model"></b-form-input>
          </b-row>
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
  props: ['result', 'metricDict', 'isCreateResult'],
  data() {
    return {
      // key (special metric title), value (special metric enum)
      specialMetricDict: {
        "Did not finish": "DidNotFinish",
        "Disqualified": "Disqualified",
        "Technical Failure": "TechnicalFailure"
      },
      resultErrorMessage: null,
      hour: null,
      minute: null,
      second: null,
      specialMetricTitle: null,
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
      },
      result_start: {
      },
      result_finish: {
        resultFinishValidate(val) {
          let startTime = new Date(this.result.result_start);
          let endTime = new Date(val);
          if (endTime < startTime) {
            this.resultErrorMessage = "End date time should be after start date time"
            return false;
          }
          return true;
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
      api.createActivityResult(this.$route.params.id, this.$route.params.activityId, data)
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
      api.updateActivityResult(this.$route.params.id, this.$route.params.activityId, this.result.id,
          data)
      .then(() => {
        this.result.isEditMode = false
        this.$v.result.$reset();
        this.$v.$reset();
        this.$emit('child-to-parent', 'edit')
      })
      .catch(() => {
        this.makeToast("Selected activity result could not be deleted", 'danger')
      })
    },
    /**
     * Calls DELETE activity result endpoint
     */
    removeActivityResult() {
      api.deleteActivityResult(this.$route.params.id, this.$route.params.activityId, this.result.id)
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
    }
  },
  mounted() {
    this.parseToSpecialMetricTitle();
    this.parseDurationStringIntoHMS();
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