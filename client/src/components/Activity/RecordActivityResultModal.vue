<template>
  <div id="app">
    <!-- this block deals with create activity result form -->
    <b-row>
      <b-button id="activity-result-modal-button" class="record-button"
                @click="$bvModal.show('record-result-modal')">
        Record Activity Result
      </b-button>
    </b-row>

    <b-modal hide-footer id="record-result-modal" size="xl" title="Record Activity Result">

      <div v-if="this.activityHasNoMetricsMessage == null">
        <RecordActivityResultForm :is-create-result="true" :metric-dict="metricDict"
                                  :result="createResultForm"
                                  v-on:child-to-parent="refreshComponent"
                                  :profileId="profileId"
                                  :activityId="activityId"></RecordActivityResultForm>
      </div>

      <div v-else>
        <b-card>
          {{ this.activityHasNoMetricsMessage }}
        </b-card>
      </div>


      <hr>


      <h5>Current Activity Results</h5>

      <!-- this block displays 'user has no activity result message' -->
      <div v-if="userHasNoResultsMessage !== null">
        <b-card id="user-no-result-message">
          {{ this.userHasNoResultsMessage }}
        </b-card>
      </div>

      <!-- this block iterates through user's activity result and display them in b-cards for editing and deleting -->
      <div v-else>
        <b-card-group :key="index" v-for="(result, index) in resultList">
          <b-card>
            <RecordActivityResultForm :is-create-result="false"
                                      :metric-dict="metricDict" :result="result"
                                      v-on:child-to-parent="refreshComponent"
                                      :profileId="profileId"
                                      :activityId="activityId"
            ></RecordActivityResultForm>
          </b-card>
        </b-card-group>
      </div>

    </b-modal>
  </div>

</template>

<script>
import RecordActivityResultForm from "@/components/Activity/RecordActivityResultForm";
import api from "@/Api";

export default {
  name: "RecordActivityResultModal",
  components: {
    RecordActivityResultForm
  },
  props: ['profileId', 'activityId', 'loggedInId'],
  data() {
    return {
      createResultForm: {
        metric_id: null,
        user_id: null,
        special_metric: null,
        result: null,
        result_finish: null,
        result_start: null,
        type: null,
        isEditMode: true,
        title: null,
        description: null
      },
      userHasNoResultsMessage: null,
      activityHasNoMetricsMessage: null,
      // key (metric id), value (metric json)
      metricDict: {},
      resultList: []
    }
  },
  methods: {
    /**
     * Calls GET all activity results of user endpoint
     */
    getActivityResultsForUser() {
      for (let metricId of Object.keys(this.metricDict)) {
        api.getUserActivityResults(this.loggedInId, this.activityId, metricId).then((res) => {
          let result;
          for (result of res.data) {
            result.title = this.metricDict[result.metric_id].title;
            result.description = this.metricDict[result.metric_id].description;
            result.isEditMode = false;
            if (result.type === 'TimeDuration') {
              result.result = this.convertToReadableDurationFormat(result.result)
            }
            this.resultList.push(result)
          }
        })
      }
    },
    /**
     * Calls GET all metrics of activity endpoint
     */
    getAllMetricsForActivity() {
      api.getActivityMetrics(this.profileId, this.activityId).then((res) => {
        let metric;
        for (metric of res.data) {
          this.metricDict[metric.id] = metric;
        }
        if (res.data.length === 0) {
          this.activityHasNoMetricsMessage = "Activity does not have any metrics."
        }
        this.getActivityResultsForUser();
      })
    },
    /**
     * Convert Duration type string to readable '1h 2m 3s' format
     * @param val duration type string
     * @returns readable duration format string
     */
    convertToReadableDurationFormat(val) {
      if (val == null) {
        return null;
      }
      let iso8601DurationRegex = /(-)?PT(?:([.,\d]+)H)?(?:([.,\d]+)M)?(?:([.,\d]+)S)?/;
      let matches = val.match(iso8601DurationRegex);
      let hours = matches[2] === undefined ? 0 : matches[2];
      let minutes = matches[3] === undefined ? 0 : matches[3];
      let seconds = matches[4] === undefined ? 0 : matches[4];
      return hours + 'h ' + minutes + 'm ' + seconds + 's';
    },
    /**
     * Calls GET metric and activity results endpoint and creates notification based on the event listened
     */
    refreshComponent(val) {
      this.resultList = []
      this.getAllMetricsForActivity();
      if (val === 'delete') {
        this.makeToast("Selected activity result is successfully deleted", 'success')
      } else if (val === 'edit') {
        this.makeToast("Selected activity result is successfully updated", 'success')
      } else if (val === 'create') {
        this.makeToast("Activity result is successfully created", 'success')
      }
      this.$root.$emit('table-update')
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
  },
  mounted() {
    this.getAllMetricsForActivity();
  }
}
</script>

<style>

.record-button {
  margin-left: 15px;
  background-color: #1c9e34;
  border-color: #1c9e34;
}


</style>