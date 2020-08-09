<template>
  <div>
    <b-button @click="$bvModal.show('record-result-modal')">Record Activity Result</b-button>

    <b-modal hide-footer id="record-result-modal" size="xl" title="Record Activity Result">
      <RecordActivityResultForm :is-create-result="true" :metric-dict="metricDict"
                                :result="createResultForm"></RecordActivityResultForm>

      <hr>

      <h5>Current Activity Results</h5>
      <div v-if="userHasNoResultsMessage !== null">
        <b-card>
          {{ this.userHasNoResultsMessage }}
        </b-card>
      </div>
      <div v-else>
        <b-card-group :key="index" v-for="(result, index) in resultList">
          <b-card>
            <RecordActivityResultForm :activity-id="activityId" :is-create-result="false"
                                      :metric-dict="metricDict" :result="result"
                                      :user-id="profileId"
                                      v-on:child-to-parent="forceUpdateComponent"></RecordActivityResultForm>
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
      // placeholder. key = metric title, value = metric type
      // metricTitleDict: {
      //   "Eggs collected": "Count",
      //   "Distance flown": "Distance",
      //   "Duration spent trippin": "Duration",
      //   "Time to 10km": "StartFinish"
      // },
      metricDict: {},
      // resultList: [
      //   {
      //     title: "Eggs collected",
      //     type: "Count",
      //     result: 10,
      //     isEditMode: false,
      //     isResultStartFinish: false,
      //   },
      //   {
      //     title: "Time to 10km",
      //     type: "StartFinish",
      //     resultStart: "2018-06-12T19:30",
      //     resultEnd: "2018-06-12T19:30",
      //     isEditMode: false,
      //     isResultStartFinish: false,
      //   }
      // ]
      resultList: []
    }
  },
  methods: {
    getActivityResultsForUser() {

      api.getUserActivityResults(this.profileId, this.activityId).then((res) => {
        let result;
        for (result of res.data) {
          // let resultJSON = {
          //   title: result.title,
          //   result: result.result,
          //   resultStart: result.result_start,
          //   resultEnd: result.result_end,
          //   type: result.type,
          //   specialMetric: result.special_metric,
          //   isEditMode: false
          // }
          result.title = this.metricDict[result.metric_id].title;
          result.description = this.metricDict[result.metric_id].description;
          result.isEditMode = false
          this.resultList.push(result)
        }
        console.log(this.resultList)
      })
      .catch(() => {
        this.userHasNoResultsMessage = "User has no activity results";
      })
      console.log("YEET")
      this.$forceUpdate();
    },
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
    forceUpdateComponent() {
      console.log("HOO")
      this.$forceUpdate();
    }
  },
  mounted() {
    this.getAllMetricsForActivity();
  }

}

</script>

<style>


</style>