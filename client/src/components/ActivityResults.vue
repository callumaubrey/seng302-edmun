<template>
  <div>
    <b-card>
      <label>Your Best Results:</label>
      <div v-for="result in this.bestResults" :key="result">
        <b-row>
          <b-col>
            {{result.metric_id.title}}
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
    <label>{{this.currentMetric}}</label>
    <b-row>
      <b-col class="col-tall" >
        <b-btn >Left</b-btn>
      </b-col>
      <b-col cols="10">
        <b-tabs align="center">
          <b-tab title="All Results" active>
            <b-table hover
                     :items="allResults"
                     :fields="allResultsFields"
                     :busy="tableIsLoading"
            >
              <template v-slot:table-busy>
                <div class="text-center text-primary my-2">
                  <b-spinner class="align-middle"></b-spinner>
                  <strong> Loading...</strong>
                </div>
              </template>
            </b-table>
          </b-tab>
          <b-tab title="My Results">
            <b-table hover
                     :items="myResults"
                     :fields="myResultsFields"
                     :busy="tableIsLoading"
            >
              <template v-slot:table-busy>
                <div class="text-center text-primary my-2">
                  <b-spinner class="align-middle"></b-spinner>
                  <strong> Loading...</strong>
                </div>
              </template>
            </b-table>
          </b-tab>
        </b-tabs>
      </b-col>
      <b-col class="col-tall">
        <b-btn>Right</b-btn>
      </b-col>
    </b-row>
  </div>
</template>

<script>
  export default {
    name: "ActivityResults",

    props: {
      profileId: Number,
    },

    data() {
      return {
        tableIsLoading: true,
        bestResults: [],
        currentMetric: "",
        allResultsFields: [
          { key: 'user', tdClass:'smallCol'},
          { key: 'value', tdClass:'medCol'},
          { key: 'ranking', tdClass:'smallCol'}
        ],
        myResultsFields: [
          { key: 'value', label: 'Value', class:'medCol'},
          { key: 'personal ranking', tdStyle:'smallCol'},
          { key: 'public ranking', tdClass:'smallCol'}
        ],
        allResults: [],
        myResults: []
      }
    },
    mounted() {
      this.loadResults();
    },
    methods: {
      loadResults: function () {
        // load all results and my results in here i guess
        this.tableIsLoading = false;
      }
    }
  }
</script>

<style scoped>

  .col-tall{
    min-height: 100vh;
    padding-top: 40vh;
  }
  .table.smallCol{
    max-width: 50px
  }
  .medCol{
    max-width: 100px
  }

</style>