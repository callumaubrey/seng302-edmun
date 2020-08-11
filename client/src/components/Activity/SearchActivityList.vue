<template>
  <div>
    <b-card v-for="activity in activity_data" :key="activity.id"
            @click="gotoActivity(activity.id)"
            class="activity-card rounded mb-3">
      <b-card-body class="activity-card-body">
        <b-card-title>

          <!-- Type Icon -->
          <b-icon v-if="activity.continuous"
                  icon="arrow-repeat" class="rounded-circle bg-info p-2" variant="light" font-scale="1.5"
                  v-b-tooltip.hover title="Continuous"></b-icon>
          <b-icon v-else
                  icon="stopwatch-fill" class="rounded-circle bg-info p-2" variant="light" font-scale="1.5"
                  v-b-tooltip.hover title="Duration"></b-icon>

          <!-- Title -->
          {{activity.activityName}}

          <!-- Location -->
          <span v-if="activity.location" class="float-right text-secondary activity-location-text">
                    {{activity.location.city}}, {{activity.location.state}}, {{activity.location.country}}
                </span>

        </b-card-title>
        <!-- Duration Start and End Date -->
        <b-card-sub-title v-if="!activity.continuous" class="date">
          {{getDurationTimeSummary(activity)}}
        </b-card-sub-title>

        <!-- Hashtags -->
        <b-col cols="8" class="hashtags">
                        <span v-for="tag in activity.tags" :key="tag.name" class="pr-1">
                            <router-link :to="{path: '/hashtag/' + tag.name}">#{{tag.name}}</router-link>
                        </span>
        </b-col>
        <b-col cols="3" class="activity-type-icon-collection float-right">
          <ActivityTypeIcon v-for="type in activity.activityTypes" :key="type"
                            :type_name="type" class="float-right"></ActivityTypeIcon>
        </b-col>

        <!-- Activity Icons -->
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
      activity_data: []
    },

    methods: {
      gotoActivity(id) {
        this.$router.push('/profiles/' + this.user_id + '/activities/' + id);
      },

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
  .activity-card-body {
    cursor: pointer;
    padding: 5px;
    margin: 0px;
  }
  .date {
    padding-top: 6px;
    padding-left: 3px;
  }
</style>