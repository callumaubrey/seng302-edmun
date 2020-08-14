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
      manageTags: function (value) {
        this.hashtag.values = value;
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