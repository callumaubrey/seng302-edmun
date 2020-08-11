<template>
  <div>
    <b-card v-for="activity in activity_data" :key="activity.id"
            @click="gotoActivity(activity.id)"
            class="activity-card rounded mb-3">
      <b-card-body class="activity-card-body" >
        <b-row>

          <b-col cols="0.2">
            <!-- Type Icon -->
            <b-icon v-if="activity.continuous"
                    icon="arrow-repeat" class="rounded-circle bg-info p-2" variant="light" font-scale="2.5"
                    v-b-tooltip.hover title="Continuous"></b-icon>
            <b-icon v-else
                    icon="stopwatch-fill" class="rounded-circle bg-info p-2" variant="light" font-scale="2.5"
                    v-b-tooltip.hover title="Duration"></b-icon>
          </b-col>
          <!-- Title -->
          <b-col class="title">{{activity.activityName}}</b-col>

          <b-col>
            <span v-if="!activity.continuous" class="date float-right">
              {{getDurationTimeSummary(activity)}}
            </span>
          </b-col>

          <b-col>
            <!-- Location -->
            <span v-if="activity.location" class="float-right text-secondary activity-location-text">
                      {{activity.location.city}}, {{activity.location.state}}, {{activity.location.country}}
                  </span>
          </b-col>
          <b-col cols="0.2" class="activity-type-icon-collection float-right">
            <ActivityTypeIcon v-for="type in activity.activityTypes" :key="type"
                              :type_name="type" class="float-right"></ActivityTypeIcon>
          </b-col>

        </b-row>
        <!-- Hashtags -->
        <b-col cols="8" class="hashtags">
                        <span v-for="tag in activity.tags" :key="tag.name" class="pr-1">
                            <router-link :to="{path: '/hashtag/' + tag.name}">#{{tag.name}}</router-link>
                        </span>
        </b-col>

      </b-card-body>
    </b-card>
  </div>
</template>

<script>
  import ActivityTypeIcon from "./ActivityType/ActivityTypeIcon";

  export default {
    name: "SearchActivityList",
    components: {ActivityTypeIcon},
    data: function() {
      return {

      }
    },

    props: {
      activity_data: {
        type: Array
      }
    },

    methods: {
      gotoActivity(id) {
        this.$router.push('/profiles/' + this.user_id + '/activities/' + id);
      },
      getDurationTimeSummary(activity) {
        // Returns a pretty print summary of different types of durations
        // StartDate - EndDate
        // StartDate Time - EndDate Time
        // StartDate Time - EndTime

        var start_date = new Date(activity.startTime);
        var end_date = new Date(activity.endTime);
        var start_has_time = (start_date.getHours() + start_date.getMinutes() + start_date.getSeconds()) > 0;
        var end_has_time = (end_date.getHours() + end_date.getMinutes() + end_date.getSeconds()) > 0;


        // No time added
        if(!start_has_time && !end_has_time) {
          return start_date.toLocaleDateString() + ' - ' + end_date.toLocaleDateString();
        }

        // Same day
        if(start_date.toLocaleDateString() === end_date.toLocaleDateString()) {
          return start_date.toLocaleString() + ' - ' + end_date.toLocaleTimeString();
        }

        // Default:
        return start_date.toLocaleString() + ' - ' + end_date.toLocaleString();
      },

      gotoCreateActivity() {
        this.$router.push('/profiles/' + this.user_id + '/activities/create');
      }

    }

  }
</script>

<style scoped>
  .activity-location-text {
    font-size: 1rem;
    padding: 0px;
    margin: 0px
  }

  .activity-card {
    cursor: pointer;
    padding: 0px;
    margin: 0px;
    min-height: 80px;
  }

  .activity-type-icon-collection {
    font-size: 0.50rem;
    padding: 0px;
    margin: 0px;
  }

  .create_button {
    cursor: pointer;
  }
  .hashtags {
    padding-top: 2px;
    padding-left: 3px;
  }
  .card-body {
    cursor: pointer;
    padding-left: 15px;
    padding-right: 15px;
    padding-top: 4px;
    padding-bottom: 4px;
    margin: 0px;
  }
  .date {
    padding-top: 6px;
    padding-left: 60px;
    font-size: 90%;
    font-weight: lighter;
  }
  .title {
    font-size: 150%;
  }

</style>