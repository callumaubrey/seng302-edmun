<template>
  <div>
    <b-card v-for="activity in activity_data" :key="activity.id"
            @click="gotoActivity(activity.profile_id, activity.id)"
            @mouseenter="$emit('activityHover', activity)"
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
          <b-col class="title">
            {{activity.activityName}}

            <!-- No Location Icon -->
            <span   v-if="activity.location == null"
                    v-b-tooltip.hover title="No Location"
                    class="fa-stack fa-2x" style="font-size: 0.9rem">
              <i class="fas fa-map-marker-alt fa-stack-1x"></i>
              <i class="fas fa-ban fa-stack-2x" style="color:Tomato"></i>
            </span>
          </b-col>

          <b-col v-if="!activity.continuous">
            <span  class="date float-right">
              {{getDurationTimeSummary(activity)}}
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
      gotoActivity(user_id, activity_id) {
        this.$router.push('/profiles/' + user_id + '/activities/' + activity_id);
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