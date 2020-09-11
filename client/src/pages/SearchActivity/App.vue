<template>
  <div>
    <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
    <b-container fluid class="content_container">
      <b-row class="content_row">

        <!-- Search Settings -->
        <b-col cols="4" class="activity_settings_column">
          <h1 class="mt-2">
            Search Activities
            <i v-if="loadingData" class="text-primary fas fa-circle-notch fa-spin"></i>
          </h1>
          <hr>

          <!-- Search Bar -->
          <b-row class="mb-3">
            <b-col class="pr-0">
              <ActivityNameSearch v-model="search_data.search_query"></ActivityNameSearch>
            </b-col>
            <b-col style="flex: 0 0 50px;">
              <b-button block size="lg" variant="primary" @click="search()">Search</b-button>
            </b-col>
          </b-row>

          <b-card body-class="p-2">
            <!-- Clickable Header -->
            <b-row class="clickable">
              <b-col @click="showAdvancedSettings=!showAdvancedSettings">
                <h5 class="mb-0">
                  <i v-if="showAdvancedSettings" class="fas fa-caret-down"></i>
                  <i v-else class="fas fa-caret-right"></i>
                  Advanced Settings
                </h5>
              </b-col>
            </b-row>

            <!-- Advanced Settings -->
            <b-collapse v-model="showAdvancedSettings">

              <!-- Activity Mode input -->
              <b-row class="my-3">
                <b-col>
                  <ActivityContinuousDurationSearchBox ref="ActivityModeInput"
                                                       :start-date-prop="search_data.duration_limit.start_date"
                                                       :end-date-prop="search_data.duration_limit.end_date"
                                                        v-on:selected="updateModeFilter"
                                                        v-on:dates="updateDurationDates"></ActivityContinuousDurationSearchBox>
                </b-col>
              </b-row>

              <!-- Hash tag input -->
              <b-row class="mb-3">
                <b-col>
                  <SearchActivityTag :max-entries="30" :title-label="'Hashtags'"
                                     :child-search-method="search_data.hashtags.method"
                                     :values="search_data.hashtags.values"
                                     :help-text="'Max 30 hashtags'"
                                     :input-character-limit="140"
                                     :input-placeholder="'Search by hashtag'"
                                     v-on:emitTags="updateHashTagValues"
                                     v-on:emitSearchMethod="updateHashTagMethod"></SearchActivityTag>
                </b-col>
              </b-row>

              <!-- Activity search input -->
              <b-row class="mb-3">
                <b-col>
                  <ActivityTypeSearchBox :selected-options="search_data.types.values"
                  v-on:selectedActivityTypes="updateTypeValues"
                  v-on:activityTypeMethod="updateTypeMethod"></ActivityTypeSearchBox>
                </b-col>
              </b-row>

              <!-- Activity Distance Slider -->
              <b-row>
                <b-col>
                  <b-card body-class="px-3 pt-2 pb-0">
                    <b-row class="pb-2">
                      <b-col>
                        <b-checkbox v-model="search_data.location.enabled">
                          Search Radius
                        </b-checkbox>
                      </b-col>
                    </b-row>
                    <ActivityDistanceSlider v-model="search_data.location.radius"
                            :disabled="!search_data.location.enabled"></ActivityDistanceSlider>
                  </b-card>
                </b-col>
              </b-row>
            </b-collapse>

          </b-card>
          <hr>

          <!-- Activity List -->
          <SearchActivityList v-bind:activity_data="activity_data"
                              @activityHover="activityHoverOver"
          ></SearchActivityList>

          <!-- No Results Message -->
          <b-row v-if="hasSearched && search_data.pagination.count === 0">
            <b-col class="text-center">
              <h2>No results</h2>
            </b-col>
          </b-row>

          <!-- Pagination -->
          <b-row>
            <b-col>
              <b-pagination
                      v-model="search_data.pagination.offset"
                      :total-rows="search_data.pagination.count"
                      :per-page="search_data.pagination.limit"
                      align="center"
                      @change="newPageSelected"
              ></b-pagination>
            </b-col>
          </b-row>
        </b-col>

        <b-col cols="8" class="p-0" style="background-color: red">
          <SearchLocationMapPane ref="map"
                                 :radius="search_data.location.radius*1000"
                                 :activities="activity_data"
                                 v-model="search_data.location.center"
                                 :display-circle="search_data.location.enabled">
          </SearchLocationMapPane>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>

import NavBar from "../../components/NavBar";
import SearchActivityList from "../../components/Activity/SearchActivityList";
import Api from "../../Api";
import SearchActivityApi from "../../scripts/Activity/activitySearch";
import ActivityNameSearch from "../../components/ActivityNameSearch";
import SearchActivityTag from "../../components/Activity/SearchActivityTag";
import ActivityTypeSearchBox from "../../components/ActivityTypeSearchBox";
import ActivityContinuousDurationSearchBox
  from "../../components/ActivityContinuousDurationSearchBox";
import SearchLocationMapPane from "../../components/MapPane/SearchLocationMapPane";
import ActivityDistanceSlider from "../../components/ActivityDistanceSlider";

export default {
  name: "App.vue",
  components: {
    ActivityDistanceSlider,
    SearchLocationMapPane,
    ActivityContinuousDurationSearchBox,
    SearchActivityTag, ActivityNameSearch, NavBar, SearchActivityList, ActivityTypeSearchBox
  },
  data: function () {
    return {
      isLoggedIn: false,
      userName: "",
      hasSearched: false,
      loadingData: false,
      showAdvancedSettings: false,
      search_data: {
        search_query: '',
        search_mode_filter: 'all',
        duration_limit: {
          start_date: null,
          end_date: null
        },
        hashtags: {
            values: [],
            method: "AND"
        },
        types: {
          values: [],
          method: "AND"
        },
        pagination: {
          offset: 1,
          limit: 10,
          count: 0
        },
        location: {
          enabled: false,
          latitude: null,
          longitude: null,
          radius: 50
        }
      },
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
        this.hasSearched = true;

        this.activity_data = [];

        let row_offset = (this.search_data.pagination.offset - 1) * (this.search_data.pagination.limit) * 2;
        let row_limit = this.search_data.pagination.limit + 1;

        // Location Data
        let location_data = {
          latitude: this.search_data.location.enabled ? this.search_data.location.center[0] : null,
          longitude: this.search_data.location.enabled ? this.search_data.location.center[1] : null,
          radius: this.search_data.location.enabled ?  Math.round(this.search_data.location.radius): null
        };

        // Get Activities
        this.loadingData = true;
        Api.getActivitiesBySearch(this.search_data.search_query,
            this.search_data.types.values,
            this.search_data.types.method === "AND",
            this.search_data.hashtags.values,
            this.search_data.hashtags.method === "AND",
            this.search_data.search_mode_filter,
            this.search_data.duration_limit.start_date,
            this.search_data.duration_limit.end_date,
            row_offset,
            row_limit,
            location_data.longitude,
            location_data.latitude,
            location_data.radius).then((response) => {
          this.activity_data = response.data.results;
          this.loadingData = false;
        }).catch((error) => {
          console.log(error);
        });

        // Get Activity count
        Api.getActivityCountBySearch(this.search_data.search_query,
            this.search_data.types.values,
            this.search_data.types.method === "AND",
            this.search_data.hashtags.values,
            this.search_data.hashtags.method === "AND",
            this.search_data.search_mode_filter,
            this.search_data.duration_limit.start_date,
            this.search_data.duration_limit.end_date,
            location_data.longitude,
            location_data.latitude,
            location_data.radius).then((response) => {
          this.search_data.pagination.count = response.data / 2;
        }).catch((error) => {
          console.log(error);
        });

        // Set URL to the search query
        let query = SearchActivityApi.getSearchActivitiesQueryParams(this.search_data.search_query,
            this.search_data.types.values,
            this.search_data.types.method === "AND",
            this.search_data.hashtags.values,
            this.search_data.hashtags.method === "AND",
            this.search_data.search_mode_filter,
            this.search_data.duration_limit.start_date,
            this.search_data.duration_limit.end_date,
            this.search_data.pagination.offset,
            this.search_data.pagination.limit,
            location_data.longitude,
            location_data.latitude,
            location_data.radius);

        history.pushState(
                {},
                null,
                '/#' + this.$route.path + '?' + query
        );
      },

      search() {
        this.loadActivities();
      },

      activityHoverOver(activity) {
        if(activity.location != null) {
          this.$refs.map.setMapCenter(activity.location.latitude, activity.location.longitude);
        }
      },


      newPageSelected(page) {
        this.search_data.pagination.offset = page;
        this.loadActivities();
      },

      /**
       * Loads query into search data fields
       */
      loadQueryIntoFields() {
        let params = SearchActivityApi.getSearchActivtyParamsFromQueryURL(this.$route.query);

        if (params.search_query !== undefined) {
          this.search_data.search_query = params.search_query;
        }
        if (params.types !== undefined) {
          this.search_data.types.values = params.types;
        }
        if (params.types_method_and
            !== undefined) {
          this.search_data.types.method = params.types_method_and ? "AND" : "OR";
        }
        if (params.hashtags !== undefined) {
          this.search_data.hashtags.values = params.hashtags;
        }
        if (params.hashtags_method_and
            !== undefined) {
          this.search_data.hashtags.method = params.hashtags_method_and ? "AND"
              : "OR";
        }
        if (params.activity_mode_filter
            !== undefined) {
          this.search_data.search_mode_filter = params.activity_mode_filter;
        }
        if (params.start_date
            !== undefined) {
          this.search_data.duration_limit.start_date = params.start_date;
        }
        if (params.end_date
            !== undefined) {
          this.search_data.duration_limit.end_date = params.end_date;
        }
        if (params.pagination_offset
            !== undefined) {
          this.search_data.pagination.offset = params.pagination_offset;
        }
        if (params.pagination_limit
            !== undefined) {
          this.search_data.pagination.limit = params.pagination_limit;
        }
        if (params.lon !== undefined) {
          this.search_data.location.center[1] = params.longitude;
        }
        if (params.lat !== undefined) {
          this.search_data.location.center[0] = params.latitude;
        }
        if (params.radius !== undefined) {
          this.search_data.location.radius = params.radius
        }

        if (this.search_data.search_mode_filter
            === "continuous") {
          this.$refs.ActivityModeInput.toggleContinuousStateButton();
        }
        if (this.search_data.search_mode_filter
            === "duration") {
          this.$refs.ActivityModeInput.toggleDurationStateButton();
        }
      },

      updateHashTagValues(values) {
        this.search_data.hashtags.values = values;
      },

      updateHashTagMethod(method) {
        this.search_data.hashtags.method = method;
      },

      updateTypeValues(values) {
        this.search_data.types.values = values;
      },

      updateTypeMethod(method) {
        this.search_data.types.method = method;
      },

      updateModeFilter(mode) {
        this.search_data.search_mode_filter = mode;
      },

      updateDurationDates(duration) {
        if (duration.startDate.length === 0) {
          this.search_data.duration_limit.start_date = null;
        } else {
          this.search_data.duration_limit.start_date = duration.startDate;
        }

        if (duration.endDate.length === 0) {
          this.search_data.duration_limit.end_date = null;
        } else {
          this.search_data.duration_limit.end_date = duration.endDate;
        }
      }


    },

    mounted() {
      this.loadQueryIntoFields();
      this.getUser();
      this.loadActivities();

    }
  }
</script>

<style scoped>
  .clickable {
    cursor: pointer;
  }

  .content_container {
    margin-top: -50px;
    height: calc(100vh - 66px);
    max-height: calc(100vh - 66px);
    /** This is kinda of a dirty way to fill page height. It requires knowing the navbar height. If it was
    to change this page would break. However alternatives would be harder to understand and quite complicated**/
  }

  .content_row {
    height: inherit;
    max-height: inherit;
  }

  .activity_settings_column {
    overflow-y: scroll;
    max-height: inherit;
  }
</style>