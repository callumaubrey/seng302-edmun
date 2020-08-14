<template>
  <div>
    <label>{{ this.metricTitles[this.currentMetricIndex] }}</label>
    <b-row>
      <b-col class="col-tall">
        <b-btn :disabled="this.currentMetricIndex == 0  ||
          this.metricIds.length == 0" @click="prevMetric()">
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
        <b-btn :disabled="this.currentMetricIndex == this.metricIds.length - 1 ||
          this.metricIds.length == 0" @click="nextMetric()">
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
      ],
      allResults: [],
      myResults: [],
      countMetrics: 0,
      metricIds: [],
      metricTitles: [],
      metricTypes: [],
      perPageAll: 10,
      currentPageAll: 1,
      rowsAll: 0,
      perPageOwn: 10,
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
              if (res.data[i].type == "TimeStartFinish") {
                this.myResultsFields = [
                  {key: 'result_start', label: 'Start', class: 'medCol'},
                  {key: 'result_finish', label: 'End', class: 'medCol'},
                  {key: 'personal_ranking', tdStyle: 'smallCol'},
                ];
                res.data[i].result_start = this.getSplitDate(res.data[i].result_start);
                res.data[i].result_finish = this.getSplitDate(res.data[i].result_finish);
              } else {
                if (res.data[i].type == "TimeDuration") {
                  res.data[i].value = res.data[i].pretty_result;
                }
                this.myResultsFields = [
                  {key: 'value', label: 'Value', class: 'medCol'},
                  {key: 'personal_ranking', tdStyle: 'smallCol'},
                ]
              }

              res.data[i].personal_ranking = i + 1;
              this.myResults = res.data;
            }

            this.rowsOwn = res.data.length;
          })
          .catch((err) => {
            console.log(err);
          })
    },
    getAllResults: function() {
      api.getAllActivityResultsByMetricId(this.activityId, this.currentMetric)
        .then((res) => {
          for (let i = 0; i < res.data.length; i++) {
            if (res.data[i].type == "TimeStartFinish") {
              this.allResultsFields = [
                {key: 'fullname', tdClass: 'smallCol'},
                {key: 'result_start', label: 'Start', class: 'medCol'},
                {key: 'result_finish', label: 'End', class: 'medCol'},
                {key: 'ranking', tdStyle: 'smallCol'}
              ];
              res.data[i].result_start = this.getSplitDate(res.data[i].result_start);
              res.data[i].result_finish = this.getSplitDate(res.data[i].result_finish);
            } else {
              this.allResultsFields = [
                {key: 'fullname', tdClass: 'smallCol'},
                {key: 'value', tdClass: 'medCol'},
                {key: 'ranking', tdClass: 'smallCol'}
              ];
              if (res.data[i].type == "TimeDuration") {
                res.data[i].value = res.data[i].pretty_result;
              }
            }

            res.data[i].ranking = i + 1;
            this.allResults = res.data;
          }
          this.rowsAll = res.data.length;
        })
      .catch((err) => console.log(err));
    },
    getSplitDate: function(date) {
      let dateSplit = date.split("T");
      return dateSplit[0] + " " + dateSplit[1];
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