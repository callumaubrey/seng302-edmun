<template>
  <div id="app">
    <b-button @click="$bvModal.show('record-result-modal')">Record Activity Result</b-button>

    <b-modal hide-footer id="record-result-modal" size="xl" title="Record Activity Result">
      <RecordActivityResultForm :is-create-result="true" :metric-dict="metricDict"
                                :result="createResultForm"
                                v-on:child-to-parent="refreshComponent"></RecordActivityResultForm>

      <hr>
      <h5>Current Activity Results</h5>
      <!-- this block displays 'user has no activity result message' -->
      <div v-if="userHasNoResultsMessage !== null">
        <b-card>
          {{ this.userHasNoResultsMessage }}
        </b-card>
      </div>

      <!-- this block iterates through user's activity result and display them in b-cards -->
      <div v-else>
        <b-card-group :key="index" v-for="(result, index) in resultList">
          <b-card>
            <RecordActivityResultForm :is-create-result="false"
                                      :metric-dict="metricDict" :result="result"
                                      v-on:child-to-parent="refreshComponent"></RecordActivityResultForm>
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
      // key (metric id), value (metric json)
      metricDict: {},
      resultList: [],
    }
  },
  methods: {
    /**
     * Calls GET all activity results of user API
     */
    getActivityResultsForUser() {
      api.getUserActivityResults(this.profileId, this.activityId).then((res) => {
        let result;
        for (result of res.data) {
          result.title = this.metricDict[result.metric_id].title;
          result.description = this.metricDict[result.metric_id].description;
          result.isEditMode = false
          if (result.type === 'TimeDuration') {
            result.result = this.convertToReadableDurationFormat(result.result)
          }
          this.resultList.push(result)
        }
      })
      .catch(() => {
        this.userHasNoResultsMessage = "User has no activity results";
      })
    },
    /**
     * Calls GET get all metrics for the activity API
     */
    getAllMetricsForActivity() {
      api.getActivityMetrics(this.loggedInId, this.activityId).then((res) => {
        let metric;
        for (metric of res.data) {
          this.metricDict[metric.id] = metric;
        }
        this.getActivityResultsForUser();
      })
      .catch((err) => {
        console.log(err);
      })
    },
    /**
     * Convert Duration type string to readable '1h 2m 3s' format
     * @param val Duration type string
     * @returns readable duration format string
     */
    convertToReadableDurationFormat(val) {
      let iso8601DurationRegex = /(-)?PT(?:([.,\d]+)H)?(?:([.,\d]+)M)?(?:([.,\d]+)S)?/;
      let matches = val.match(iso8601DurationRegex);
      let hours = matches[2] === undefined ? 0 : matches[2];
      let minutes = matches[3] === undefined ? 0 : matches[3];
      let seconds = matches[4] === undefined ? 0 : matches[4];
      return hours + 'h ' + minutes + 'm ' + seconds + 's';
    },
    /**
     * Calls GET metric and activity results API and force the component to rerender
     */
    refreshComponent(val) {
      this.resultList = []
      this.getAllMetricsForActivity();
      // this.$forceUpdate();
      if (val == 'delete') {
        this.makeToast("Selected activity result is successfully deleted", 'success')
      } else if (val == 'edit') {
        this.makeToast("Selected activity result is successfully updated", 'success')
      } else {
        this.makeToast("Activity result is successfully created", 'success')
      }
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


</style>