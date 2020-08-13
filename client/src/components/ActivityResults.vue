<template>
  <div>
    <b-card>
      <label>Your Best Results:</label>
      <div v-for="result in this.bestResults" :key="result">
        <b-row>
          <b-col>
            {{ result.metric_id.title }}
          </b-col>
          <b-col>
            result value, might need to be a function to do if statements for which type of value to
            show
          </b-col>
          <b-col>
            Ranking of result compared to all results for that metric
          </b-col>
        </b-row>
      </div>
    </b-card>
    <label>{{ this.metricTitles[this.currentMetricIndex] }}</label>
    <b-row>
      <b-col class="col-tall">
        <b-btn :disabled="this.currentMetricIndex == 0" @click="prevMetric()">
          <b-icon icon="arrow-left" aria-hidden="true"></b-icon>
        </b-btn>
      </b-col>
      <b-col cols="10">
        <b-tabs align="center">
          <b-tab title="All Results" active>
            <b-table hover
                     :items="allResults"
                     :fields="allResultsFields"
                     :busy="tableIsLoading"
                     :per-page="perPageAll"
                     :current-page="currentPageAll"
            >
              <template v-slot:table-busy>
                <div class="text-center text-primary my-2">
                  <b-spinner class="align-middle"></b-spinner>
                  <strong> Loading...</strong>
                </div>
              </template>
            </b-table>
            <b-col>
              <b-pagination
                  v-model="currentPageAll"
                  :total-rows="rowsAll"
                  :per-page="perPageAll"
              ></b-pagination>
            </b-col>
          </b-tab>
          <b-tab title="My Results">
            <b-table hover
                     :items="myResults"
                     :fields="myResultsFields"
                     :busy="tableIsLoading"
                     :per-page="perPageOwn"
                     :current-page="currentPageOwn"
            >
              <template v-slot:table-busy>
                <div class="text-center text-primary my-2">
                  <b-spinner class="align-middle"></b-spinner>
                  <strong> Loading...</strong>
                </div>
              </template>
            </b-table>
            <b-col>
              <b-pagination
                  v-model="currentPageOwn"
                  :total-rows="rowsOwn"
                  :per-page="perPageOwn"
              ></b-pagination>
            </b-col>
          </b-tab>
        </b-tabs>
      </b-col>
      <b-col class="col-tall">
        <b-btn :disabled="this.currentMetricIndex == this.metricIds.length - 1" @click="nextMetric()">
          <b-icon icon="arrow-right" aria-hidden="true"></b-icon>
        </b-btn>
      </b-col>
    </b-row>
  </div>
</template>

<script>
import api from '@/Api'

export default {
  name: "ActivityResults",

  props: {
    profileId: Number,
    activityId: Number
  },

  data() {
    return {
      tableIsLoading: true,
      bestResults: [],
      currentMetric: "",
      currentMetricIndex: 0,
      allResultsFields: [
        {key: 'firstname', tdClass: 'smallCol'},
        {key: 'value', tdClass: 'medCol'},
        {key: 'ranking', tdClass: 'smallCol'}
      ],
      myResultsFields: [
        {key: 'value', label: 'Value', class: 'medCol'},
        {key: 'personal ranking', tdStyle: 'smallCol'},
        {key: 'public ranking', tdClass: 'smallCol'}
      ],
      allResults: [],
      myResults: [],
      countMetrics: 0,
      metricIds: [],
      metricTitles: [],
      metricTypes: [],
      perPageAll: 0,
      currentPageAll: 1,
      rowsAll: 0,
      perPageOwn: 0,
      currentPageOwn: 1,
      rowsOwn: 0
    }
  },
  mounted() {
    this.loadResults();
  },
  methods: {
    loadResults: function () {
      // load all results and my results in here i guess
      this.tableIsLoading = false;

      api.getActivityMetrics(this.profileId, this.activityId)
        .then((res) => {
          this.countMetrics = res.data.length;
          this.currentMetric = res.data[0].id;
          for (let i = 0; i < res.data.length; i++) {
            this.metricIds.push(res.data[i].id);
            this.metricTitles.push(res.data[i].title);
            this.metricTypes.push(res.data[i].type);
          }

          this.getAllResults();
          this.getMyResults();
        })
        .catch(err => console.log(err));
    },
    nextMetric: function () {
      let index = this.metricIds.indexOf(this.currentMetric);
      if (index >= 0 && index < this.metricIds.length - 1) {
        this.currentMetric = this.metricIds[index + 1];
        this.currentMetricIndex = index + 1;
        this.getAllResults();
        this.getMyResults();
      }
    },
    prevMetric: function () {
      let index = this.metricIds.indexOf(this.currentMetric);
      if (index > 0 && index < this.metricIds.length) {
        this.currentMetric = this.metricIds[index - 1];
        this.currentMetricIndex = index - 1;
        this.getAllResults();
        this.getMyResults();
      }
    },
    getMyResults: function() {
      api.getAllActivityResultsByProfileId(this.profileId, this.activityId, this.currentMetric)
          .then((res) => {
            for (let i = 0; i < res.data.length; i++) {
              if (res.data[i].type == "StartFinish") {
                this.myResultsFields = [
                  {key: 'result_start', label: 'Start', class: 'medCol'},
                  {key: 'result_finish', label: 'End', class: 'medCol'},
                  {key: 'personal_ranking', tdStyle: 'smallCol'},
                  {key: 'public_ranking', tdClass: 'smallCol'}
                ];
              } else {
                if (res.data[i].type == "Duration") {
                  res.data[i].value = res.data[i].pretty_result;
                }
                this.myResultsFields = [
                  {key: 'value', label: 'Value', class: 'medCol'},
                  {key: 'personal_ranking', tdStyle: 'smallCol'},
                  {key: 'public_ranking', tdClass: 'smallCol'}
                ]
              }

              res.data[i].personal_ranking = i + 1;
              this.myResults = res.data;

              if (i == res.data.length - 1) {
                this.rowsOwn = this.allResults.length;
              }
            }
          })
          .catch((err) => {
            console.log(err);
          })
    },
    getAllResults: function() {
      api.getAllActivityResultsByMetricId(this.activityId, this.currentMetric)
        .then((res) => {
          for (let i = 0; i < res.data.length; i++) {
            if (res.data[i].type == "StartFinish") {
              this.allResultsFields = [
                {key: 'fullname', tdClass: 'smallCol'},
                {key: 'result_start', label: 'Start', class: 'medCol'},
                {key: 'result_finish', label: 'End', class: 'medCol'},
                {key: 'ranking', tdStyle: 'smallCol'}
              ];
            } else {
              this.allResultsFields = [
                {key: 'fullname', tdClass: 'smallCol'},
                {key: 'value', tdClass: 'medCol'},
                {key: 'ranking', tdClass: 'smallCol'}
              ];
              if (res.data[i].type == "Duration") {
                res.data[i].value = res.data[i].pretty_result;
              }
            }

            res.data[i].ranking = i + 1;
            this.allResults = res.data;

            if (i == res.data.length - 1) {
              this.rowsAll = this.allResults.length;
            }
          }
        })
      .catch((err) => console.log(err));
    }
  }
}
</script>

<style scoped>

.col-tall {
  min-height: 100vh;
  padding-top: 40vh;
}

.table.smallCol {
  max-width: 50px
}

.medCol {
  max-width: 100px
}

</style>