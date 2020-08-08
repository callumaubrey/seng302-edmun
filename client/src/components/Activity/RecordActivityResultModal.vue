<template>
  <div>
    <b-button @click="$bvModal.show('record-result-modal')">Record Activity Result</b-button>

    <b-modal hide-footer id="record-result-modal" size="lg" title="Record Activity Result">
      <RecordActivityResultForm :is-create-result="true" :metric-title-dict="metricTitleDict"
                                :result="createResultForm"></RecordActivityResultForm>

      <hr>

      <h5>Current Activity Results</h5>
      <b-card-group :key="index" v-for="(result, index) in resultList">
        <b-card>
          <RecordActivityResultForm :is-create-result="false" :metric-title-dict="metricTitleDict"
                                    :result="result"></RecordActivityResultForm>
        </b-card>
      </b-card-group>
    </b-modal>
  </div>

</template>

<script>
import RecordActivityResultForm from "@/components/Activity/RecordActivityResultForm";

export default {
  name: "RecordActivityResultModal",
  components: {
    RecordActivityResultForm
  },
  data() {
    return {
      createResultForm: {
        title: null,
        type: null,
        result: null,
        resultStart: null,
        resultEnd: null,
        isEditMode: true
      },
      // placeholder. key = metric title, value = metric type
      metricTitleDict: {
        "Eggs collected": "Count",
        "Distance flown": "Distance",
        "Duration spent trippin": "Duration",
        "Time to 10km": "StartFinish"
      },
      resultList: [
        {
          title: "Eggs collected",
          type: "Count",
          result: 10,
          isEditMode: false,
          isResultStartFinish: false,
        },
        {
          title: "Time to 10km",
          type: "StartFinish",
          resultStart: "2018-06-12T19:30",
          resultEnd: "2018-06-12T19:30",
          isEditMode: false,
          isResultStartFinish: false,
        }
      ]
    }
  },
  methods: {},

}

</script>

<style>


</style>