<template>
  <div>
    <label>Search Activities.</label>
    <SearchActivityList v-bind:activity_data="activity_data"></SearchActivityList>
  </div>
</template>

<script>

  import SearchActivityList from "../../components/Activity/SearchActivityList";
  import api from "../../Api";

  export default {
    name: "App.vue",
    components: {SearchActivityList},
    data: function() {
      return {
        activity_data: []
      }
    },
    methods: {
      getPlaceholderActivities: function() {
        api.getActivities(1001).then((res) => {
          this.activity_data = res.data;
        })
      }
    },
    mounted() {
      this.getPlaceholderActivities();
    }
  }
</script>

<style scoped>

</style>