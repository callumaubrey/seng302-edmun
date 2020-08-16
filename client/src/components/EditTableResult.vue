<template>
  <div>
    <b-modal id="editResultModal" size="xl">
      <div v-if="loading">

      </div>
      <div v-else>
        <RecordActivityResultForm :activityId="activityId" :is-create-result="false"
                                  :key="loading" :metric-dict="metricDict"
                                  :profileId="profileId"
                                  :result="resultData"
                                  v-if="isMounted"
                                  v-on:child-to-parent="refreshComponent"
        ></RecordActivityResultForm>
      </div>

    </b-modal>

  </div>

</template>

<script>
import api from "@/Api.js"
import RecordActivityResultForm from "@/components/Activity/RecordActivityResultForm"

export default {
  name: "EditTableResult",
  components: {RecordActivityResultForm},
  props: ["profileId", "activityId", "result"],
  data() {
    return {
      metricDict: {},
      loading: true,
      loggedInId: null,
      resultData: null,
      isMounted: false
    }

  },
  methods: {
    getActivityResultsForUser() {
      this.resultData = {
        id: this.result.id,
        metric_id: this.result.metric_id,
        user_id: this.result.user_id,
        special_metric: this.result.special_metric,
        result: this.result.result,
        result_finish: this.result.result_finish,
        result_start: this.result.result_start,
        type: this.result.type,
        isEditMode: true,
        title: this.metricDict[this.result.metric_id].title,
        description: this.metricDict[this.result.metric_id].description
      };
      if (this.resultData.type === 'TimeDuration') {
        this.resultData.result = this.convertToReadableDurationFormat(this.resultData.result)
      }
      this.isMounted = true
    },
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
      this.loading = false;
    },
    getLoggedInId() {
      api.getProfileId().then((res) => {
        this.loggedInId = res.data;
        this.getAllMetricsForActivity();
      })
      .catch(() => {
        alert("An error has occurred, please refresh the page")
      })
    },
    /**
     * Convert Duration type string to readable '1h 2m 3s' format
     * @param val duration type string
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
     * Calls GET metric and activity results endpoint and creates notification based on the event listened
     */
    refreshComponent(val) {
      // this.getAllMetricsForActivity();
      if (val === 'delete') {
        this.makeToast("Selected activity result is successfully deleted", 'success')
      } else if (val === 'edit') {
        this.makeToast("Selected activity result is successfully updated", 'success')
      } else if (val === 'create') {
        this.makeToast("Activity result is successfully created", 'success')
      }
      this.$bvModal.hide('editResultModal');
      this.$emit('table-update');
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
    this.$root.$on('mountEditModal', () => {
      this.resultData = null;
      this.isMounted = false;
      this.getLoggedInId();
    });
  }
}
</script>

<style scoped>

</style>