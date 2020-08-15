<template>
  <div>
    <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
    <b-container>
      <h1>Search Activities</h1>
      <hr>
      <SearchActivityList v-bind:activity_data="activity_data"></SearchActivityList>
    </b-container>
  </div>
</template>

<script>

  import NavBar from "../../components/NavBar";
  import SearchActivityList from "../../components/Activity/SearchActivityList";
  import Api from "../../Api";


  export default {
    name: "App.vue",
    components: {NavBar, SearchActivityList},
    data: function() {
      return {
        isLoggedIn: false,
        userName: "",

        activity_data: []
      }
    },
    methods: {

      /**
       * Gets current logged in user
       */
      getUser: async function () {
        await Api.getLoggedInProfile()
                .then((res) => {
                  this.userName = res.data.firstname;
                  this.isLoggedIn = true;
                }).catch(err => console.log(err));
      },

      /**
       * Loads activities from the search api based on the form inputs
       */
      loadActivities() {
        // Test Search since no forms exist for the page yet
        Api.getActivitiesBySearch("Activity").then((response) => {
          console.log(response.data.results);
          this.activity_data = response.data.results;
        }).catch((error) => {
          console.log(error);
        });
      }
    },
    mounted() {
      this.getUser();
      this.loadActivities();
    }
  }
</script>

<style scoped>

</style>