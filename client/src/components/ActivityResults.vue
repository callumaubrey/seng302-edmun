<template>
  <div>
    <b-row style="margin-bottom:10px;margin-top:5px;">
      <b-col>
        <b-btn :disabled="this.currentMetricIndex == 0  ||
          this.metricIds.length == 0" @click="prevMetric()">
          <b-icon icon="arrow-left" aria-hidden="true"></b-icon>
        </b-btn>
      </b-col>
      <b-col>
        <h3>{{ this.metricTitles[this.currentMetricIndex] }}</h3>
      </b-col>
      <b-bol>
        <b-btn :disabled="this.currentMetricIndex == this.metricIds.length - 1 ||
          this.metricIds.length == 0" @click="nextMetric()">
          <b-icon icon="arrow-right" aria-hidden="true"></b-icon>
        </b-btn>
      </b-bol>
    </b-row>
    <edit-table-result :result="this.selectedResult" :profileId="profileId" :activityId="activityId"
                       v-on:table-update="updateTable"></edit-table-result>
    <b-row>
      <b-col>
        <b-tabs align="center">
          <b-tab title="All Results" active>
            <b-table hover
                     :items="allResults"
                     :fields="allResultsFields"
                     :busy="tableIsLoading"
                     :per-page="perPageAll"
                     :current-page="currentPageAll"
                     @row-clicked="editActivity"
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
                  v-bind="currentPageAll"
                  :total-rows="rowsAll"
                  :per-page="perPageAll"
                  v-on:table-update="updateTable"
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
                     v-on:table-update="updateTable"
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
    </b-row>
  </div>
</template>

<script>
import api from '@/Api'
import EditTableResult from "./EditTableResult";

export default {
  name: "ActivityResults",
  components: {EditTableResult},
  props: ["profileId", "activityId"],
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
      selectedResult: null,
      metricTitleDict: {},
      allResults: [],
      displayEditResult: true,
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
    this.getLoggedInId();
    this.loadResults();
    // listen to create , edit and delete result event
    this.$root.$on('table-update', () => {
      this.getLoggedInId();
      this.loadResults();
    });
  },
  methods: {
    loadResults: function () {
      // load all results and my results in here i guess
      this.tableIsLoading = false;

      /**
       * This function gets all the available metrics of an activity
       * It pushes the IDS, titles and metric types to separate lists
       */
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
    /**
     * This sets the current metric to the next available metric
     */
    nextMetric: function () {
      let index = this.metricIds.indexOf(this.currentMetric);
      if (index >= 0 && index < this.metricIds.length - 1) {
        this.allResults = []
        this.myResults = []
        this.currentMetric = this.metricIds[index + 1];
        this.currentMetricIndex = index + 1;
        this.getAllResults();
        this.getMyResults();
      }
    },
    /**
     * This sets the current metric to the previous available metric
     */
    prevMetric: function () {
      let index = this.metricIds.indexOf(this.currentMetric);
      if (index > 0 && index < this.metricIds.length) {
        this.allResults = []
        this.myResults = []
        this.currentMetric = this.metricIds[index - 1];
        this.currentMetricIndex = index - 1;
        this.getAllResults();
        this.getMyResults();
      }
    },
    /**
     * This function gets a users results from the current metric
     */
    getMyResults: function () {
      api.getAllActivityResultsByProfileId(this.loggedInId, this.activityId, this.currentMetric)
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
    /**
     * This function gets all results for a specified metric
     */
    getAllResults: function () {
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
    /**
     * Formats the date by splitting the date string
     * @param date
     * @returns {string}
     */
    getSplitDate: function (date) {
      if (date == null) {
        return null;
      }
      let dateSplit = date.split("T");
      return dateSplit[0] + " " + dateSplit[1];
    },
    editActivity: function (item) {
      this.selectedResult = item;
      this.$root.$emit('mountEditModal');
      this.$nextTick(() => {
        this.$bvModal.show("editResultModal");
      })
    },
    updateTable: async function () {
      this.getAllResults()
      this.getMyResults()
    },
    getLoggedInId: function () {
      api.getProfileId().then((res) => {
        this.loggedInId = res.data;
      })
          .catch((err) => {
            console.log(err)
            alert("An error has occurred, please refresh the page")
          })
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