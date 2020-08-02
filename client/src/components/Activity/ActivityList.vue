<template>
    <div>
        <!-- Nav Type Selection -->
        <b-nav pills class="mb-4">
            <b-nav-item :active="display_mode==='all'" @click="display_mode='all'">All</b-nav-item>
            <b-nav-item :active="display_mode==='continuous'" @click="display_mode='continuous'">Continuous</b-nav-item>
            <b-nav-item :active="display_mode==='duration'" @click="display_mode='duration'">Duration</b-nav-item>
        </b-nav>

        <!-- Create Button -->
        <b-row v-if="login_user_id===user_id">
            <b-col cols="12" class="d-flex mb-4">
                <b-icon icon="plus" class="create_button rounded-circle bg-success p-2 m-auto" variant="light" font-scale="3"
                        @click="gotoCreateActivity"
                        v-b-tooltip.hover title="Create"></b-icon>
            </b-col>
        </b-row>


        <!-- Error Message -->
        <h2 v-if='show_error_message' class="text-danger text-center">
            {{error_message}}
        </h2>

        <!-- Activity List -->
        <b-card v-for="activity in filtered_activity_data" :key="activity.id"
                @click="gotoActivity(activity.id)"
                class="activity-card rounded mb-3">
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
            <b-card-sub-title v-if="!activity.continuous">
                {{getDurationTimeSummary(activity)}}
            </b-card-sub-title>


            <b-row>
                <!-- Hashtags -->
                <b-col cols="8" class="p-2">
                    <span v-for="tag in activity.tags" :key="tag.name" class="pr-1">
                        <router-link :to="{path: '/hashtag/' + tag.name}">#{{tag.name}}</router-link>
                    </span>
                </b-col>

                <!-- Activity Icons -->
                <b-col cols="4" class="activity-type-icon-collection">
                    <ActivityTypeIcon v-for="type in activity.activityTypes" :key="type"
                                      :type_name="type" class="float-right"></ActivityTypeIcon>
                </b-col>
            </b-row>

        </b-card>
    </div>
</template>

<script>
    import api from "../../Api";
    import ActivityTypeIcon from "./ActivityType/ActivityTypeIcon";

    export default {
        name: "ActivityList.vue",
        components: {ActivityTypeIcon},
        data: function() {
            return {
                display_mode: 'all',
                activity_data: [],

                login_user_id: -1,

                show_error_message: false,
                error_message: ''
            }
        },

        props: {
            user_id: {
                type: Number,
                default: 0
            }
        },

        computed: {
            filtered_activity_data: function () {
                return this.activity_data.filter(this.shouldDisplay);
            }
        },

        mounted() {
            this.loadActivities();
            this.getLoggedInId();
        },

        methods: {
            loadActivities() {
                api.getActivities(this.user_id).then((res) => {
                    this.show_error_message = false;
                    this.activity_data = res.data;
                }).catch(() => {
                    this.show_error_message = true;
                    this.error_message = "Could not load activities. Try again later.";
                })
            },

            getLoggedInId: function () {
                // This method really shouldn't be here.
                // A better alternative would be us storing the logged in user id in the store
                // so it could be accessed at all times, however that require a fair amount of re-engineering.
                // So this has to do for now.

                api.getProfileId()
                    .then((res) => {
                        this.login_user_id = res.data;
                    })
            },

            shouldDisplay(activity) {
                if (this.display_mode === 'all') return true;
                if(activity.continuous && this.display_mode === 'continuous') return true;
                if(!activity.continuous && this.display_mode === 'duration') return true;

                return false;
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

                console.log(start_date);

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

            gotoActivity(id) {
                this.$router.push('/profiles/' + this.user_id + '/activities/' + id);
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
    }

    .activity-card {
        cursor: pointer;
    }

    .activity-type-icon-collection {
        font-size: 0.75rem;
    }

    .create_button {
        cursor: pointer;
    }
</style>