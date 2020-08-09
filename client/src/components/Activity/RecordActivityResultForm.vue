<template>

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
        <p style="font-style: italic; color: crimson">{{ result.special_metric }}</p>
      </b-col>
      <b-col sm="1">
        <b-button-group>
          <b-button @click="result.isEditMode=true" class="button-group" id="edit-result-button"
                    variant="success">Edit
          </b-button>
          <b-button class="button-group" variant="danger">Delete</b-button>
        </b-button-group>
      </b-col>
    </b-row>
  </div>

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
                       v-model="result.special_metric"></b-form-select>


      </b-col>
      <b-col sm="6">
        <label v-if="result.type!=='TimeStartFinish'">Result: </label>
        <b-form-input id="result-feedback" placeholder="Enter your result"
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
          <b-button @click="result.isEditMode=false" id="save-result-button">Save</b-button>
          <b-button @click="result.isEditMode=false" id="cancel-result-button" variant="danger">
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
// import {required} from 'vuelidate/lib/validators';

export default {
  name: "RecordActivityResultForm",
  mixins: [validationMixin],
  props: ['result', 'metricDict', 'isCreateResult', 'userId', 'activityId'],
  data() {
    return {
      specialMetricDict: {
        "Did not finish": "DidNotFinish",
        "Disqualified": "Disqualified",
        "Technical Failure": "Technical Failure"
      },
      resultErrorMessage: null,
      resultStartFinishErrorMessage: null,
      hour: null,
      minute: null,
      second: null
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
        // required
      },
      result_finish: {
        resultFinishValidate(val) {
          let startTime = new Date(this.result.result_start);
          let endTime = new Date(val);
          if (endTime < startTime) {
            return false;
          }
          return true;
        }
      }
    }
  },
  computed: {
    metricTitleDict() {
      let metricTitleDict = {}
      for (let metricId in this.metricDict) {
        metricTitleDict[this.metricDict[metricId].title] = metricId;
      }

      return metricTitleDict
    }
  },
  methods: {
    updateInputGroup(val) {
      this.result.type = this.metricDict[this.metricTitleDict[val]].unit;
      this.result.description = this.metricDict[this.metricTitleDict[val]].description;
      this.resultErrorMessage = null
      console.log(this.result.type)
    },
    validateResultState(name) {
      const {$dirty, $error} = this.$v.result[name];
      return $dirty ? !$error : null;
    },
    validateState(name) {
      const {$dirty, $error} = this.$v[name];
      return $dirty ? !$error : null;
    },
    createActivityResult() {
      this.$v.result.$touch();
      this.$v.$touch();
      if (this.$v.result.$anyError) {
        console.log("I was here")
        return;
      }
      if (this.result.type === 'TimeDuration') {
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
      }
      let data = {
        metric_id: this.metricTitleDict[this.result.title],
        value: this.result.result,
        start: this.result.result_start,
        end: this.result.result_finish,
        special_metric: this.specialMetricDict[this.result.special_metric]
      }
      console.log(data);
      api.createActivityResult(this.$route.params.id, this.$route.params.activityId, data)
      .then(() => {

      })
      .catch((err) => {
        console.log(err.response.data);
      })
      this.$emit('child-to-parent')
    }
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