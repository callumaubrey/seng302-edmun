<template>

  <div v-if="!result.isEditMode">
    <b-row>
      <b-col sm="4">
        <b-row>
          <h6>{{ result.title }}</h6>
        </b-row>
        <b-row>
          <h6 style="font-style: italic;">{{ result.special_metric }}</h6>
        </b-row>

      </b-col>
      <b-col sm="5">
        <h6 v-if="result.type!=='StartFinish'">{{ result.result }}</h6>
        <div v-else>
          <h6> Start time: {{ result.result_start }} </h6>
          <h6> End time: {{ result.result_end }} </h6>
        </div>
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
        <b-row>
          <b-form-select :options="Object.keys(metricTitleDict)" id="select-metric-title"
                         v-model="result.title"
                         v-on:change="updateInputGroup"></b-form-select>
        </b-row>
        <b-row>
          <b-form-select :options="Object.keys(specialMetricDict)" id="select-special-metric"
                         v-if="!isCreateResult"
                         v-model="result.special_metric"></b-form-select>
        </b-row>

      </b-col>
      <b-col sm="6">
        <b-form-input placeholder="Enter your result" v-if="result.type!=='StartFinish'"
                      :state="validateResultState('result')"
                      v-model="$v.result.result.$model"></b-form-input>
        <b-input-group v-else>
          <b-row>
            <label>Start Time: </label>
            <b-form-input placeholder="Enter your start time" type="datetime-local"
                          :state="validateResultState('result_start')"
                          v-model="$v.result.result_start.$model"></b-form-input>
          </b-row>
          <b-row>
            <label style="padding-top: 5px">End Time: </label>
            <b-form-input placeholder="Enter your end time" type="datetime-local"
                          :state="validateResultState('result_end')"
                          v-model="$v.result.result_end.$model"></b-form-input>
          </b-row>
        </b-input-group>
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
import {required} from 'vuelidate/lib/validators';

export default {
  name: "RecordActivityResultForm",
  mixins: [validationMixin],
  props: ['result', 'metricDict', 'isCreateResult', 'userId', 'activityId'],
  data() {
    return {
      specialMetricDict: {
        "Select a special metric": null,
        "Did not finish": "DidNotFinish",
        "Disqualified": "Disqualified",
        "Technical Failure": "Technical Failure"
      }
    }
  },
  validations: {
    result: {
      result: {
        required,
        validateResult(val) {
          if (this.result.type === 'Count') {
            let integerRegex = new RegExp("^d+$");
            return integerRegex.test(val);
          } else if (this.result.type === 'Duration' || this.result.type === 'Distance') {
            let floatRegex = new RegExp("^(?=.)([+-]?([0-9]*)(\\.([0-9]+))?)$");
            return floatRegex.test(val);
          } else {
            return false;
          }
        }
      },
      result_start: {
        required
      },
      result_end: {
        required,
        // resultEndValidate(val) {
        //   let startTime = new Date(this.result.result_start);
        //   let endTime =  new Date(val);
        //   if (endTime < startTime) {
        //     return false;
        //   }
        //   return true;
        // }
      }
    }
  },
  computed: {
    metricTitleDict() {
      let metric;
      let metricTitleDict = {}
      metricTitleDict["Select a metric title"] = null;
      for (metric in this.metricDict) {
        metricTitleDict[metric.title] = metric.id;
      }
      return metricTitleDict
    }
  },
  methods: {
    updateInputGroup(val) {
      if (val !== "Select a metric title") {
        this.result.type = this.metricDict[this.metricTitleDict[val]].unit;
      }
    },
    validateResultState(name) {
      const {$dirty, $error} = this.$v.result[name];
      return $dirty ? !$error : null;
    },
    createActivityResult() {
      this.$v.result.$touch();
      if (this.$v.result.$anyError) {
        return;
      }
      let data = {
        metric_id: this.metricTitleDict[this.result.title],
        value: this.result.result,
        start: this.result.result_start,
        end: this.result.result_end,
      }
      api.createActivityResult(this.userId, this.activityId, data)
      .then(() => {
        this.$emit('childToParent', this.result)
      })
      .catch((err) => {
        console.log(err);
      })
    }
  }
}

</script>

<style>
.button-group {
  margin-right: 5px;
}
</style>