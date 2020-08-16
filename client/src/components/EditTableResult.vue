<template>
  <div>
    <b-modal id="editResultModal">
      <div v-if="loading">

      </div>
      <div v-else>
        <RecordActivityResultForm :is-create-result="false"
                                  :metric-dict="metricDict" :result="result"
                                  v-on:child-to-parent="refreshComponent"
                                  :profileId="profileId"
                                  :activityId="activityId"
                                  :key="loading"
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
    data()  {
      return {
        metricDict: {},
        loading: true,
        loggedInId: null
      }

    },
    methods: {
      async getAllMetricsForActivity() {
        await api.getActivityMetrics(this.loggedInId, this.activityId).then((res) => {
          let metric;
          for (metric of res.data) {
            this.metricDict[metric.id] = metric;
          }
        })
        this.loading = false;
        console.log(this.metricDict);
      },
      async getLoggedInId() {
        await api.getProfileId().then((res) => {
          this.loggedInId = res.data;
        })
        .catch(() => {
          alert("An error has occurred, please refresh the page")
        })
      }
    },
    async mounted() {
      console.log("flag1")
      await this.getLoggedInId();
      await this.getAllMetricsForActivity();
    }
  }
</script>

<style scoped>

</style>