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
        <b-btn :disabled="this.currentMetricIndex == 0" @click="prevMetric()">Left</b-btn>
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
        <b-btn :disabled="this.currentMetricIndex == this.metricIds.length - 1" @click="nextMetric()">Right</b-btn>
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
        {key: 'user', tdClass: 'smallCol'},
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
      this.myResults = [];
      api.getAllActivityResultsByProfileId(this.profileId, this.activityId)
          .then((res) => {
            for (let i = 0; i < res.data.length; i++) {
              if (res.data[i].metric_id == this.currentMetric) {
                this.myResults.push(res.data[i]);
              }

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
      this.allResults = [];
      api.getAllActivityResultsByMetricId(this.activityId, this.currentMetric)
        .then((res) => {
          for (let i = 0; i < res.data.length; i++) {
            let profileId = res.data[i].user_id;
            let _this = this;
            api.getProfile(profileId)
              .then((res2) => {
                _this.allResults.push({
                  "user": res2.data.firstname + " " + res2.data.lastname,
                  "value": res.data[i].value
                });
              })
              .catch((err) => console.log(err));
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