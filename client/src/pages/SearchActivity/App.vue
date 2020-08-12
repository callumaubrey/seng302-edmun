<template>
  <div>
    <label>Search Activities.</label>
    <b-row>
      <b-col>
        <SearchActivityTag :max-entries="30" :title-label="'Hashtags'" :options="hashtag.options"
                           :values="hashtag.values"
                           :help-text="'Max 30 hashtags'"
                           :input-character-limit="140"
                           :input-placeholder="'hashtag'"
                           v-on:emitInput="autocompleteHashtagInput"
                           v-on:emitTags="manageTags"
                           v-on:changeSearchMethod="manageHashtagMethod"
                           ></SearchActivityTag>
      </b-col>
    </b-row>
    <SearchActivityList v-bind:activity_data="activity_data"></SearchActivityList>
  </div>
</template>

<script>

  import SearchActivityList from "../../components/Activity/SearchActivityList";
  import SearchActivityTag from "../../components/Activity/SearchActivityTag";
  import api from "../../Api";

  export default {
    name: "App.vue",
    components: {SearchActivityList, SearchActivityTag},
    data: function() {
      return {
        activity_data: [],
        hashtag: {
          options: [],
          values: [],
          searchMethod: 'AND'
        },
      }
    },
    methods: {
      getPlaceholderActivities: function() {
        api.getActivities(1001).then((res) => {
          this.activity_data = res.data;
        })
      },
      autocompleteHashtagInput: function (value) {
        let pattern = /^#?[a-zA-Z0-9_]*$/;
        if (!pattern.test(value)) {
          this.hashtag.options = [];
          return;
        }
        if (value[0] == "#") {
          value = value.substr(1);
        }
        if (value.length > 2) {
          let vue = this;
          api.getHashtagAutocomplete(value)
          .then(function (response) {
            let results = response.data.results;
            for (let i = 0; i < results.length; i++) {
              results[i] = "#" + results[i];
            }
            vue.hashtag.options = results;
          })
          .catch(function () {

          });
        } else {
          this.hashtag.options = [];
        }
      },
      manageTags: function (value) {
        this.hashtag.values = value;
        this.hashtag.options = [];
      },
      manageHashtagMethod: function (method) {
        this.hashtag.searchMethod = method;
      }
    },
    mounted() {
      this.getPlaceholderActivities();
    }
  }
</script>

<style scoped>

</style>