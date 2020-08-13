<template>
  <b-row align-v="center">
    <b-col cols="9">
      <b-form-tags v-model="selectedOptions" no-outer-focus
                   size="lg">
        <template v-slot="{ tags, disabled, addTag, removeTag }">
          <ul v-if="tags.length > 0" class="list-inline d-inline-block mb-2">
            <li v-for="tag in tags" :key="tag" class="list-inline-item">
              <b-form-tag
                  @remove="removeTag(tag) && emitActivityTypes()"
                  :title="tag"
                  :disabled="disabled"
                  variant="primary"
              >{{ tag }}
              </b-form-tag>
            </li>
          </ul>
          <b-dropdown size="sm" variant="outline-secondary" block menu-class="w-100">
            <template v-slot:button-content>
              <b-icon icon="tag-fill"></b-icon>
              Choose activity types to search by
            </template>
            <b-dropdown-form @submit.stop.prevent="() => {}">
              <b-form-group
                  label-for="tag-search-input"
                  label="Search activity types"
                  label-cols-md="auto"
                  class="mb-0"
                  label-size="sm"
                  :description="searchDesc"
                  :disabled="disabled"
              >
                <b-form-input
                    v-model="searchQuery"
                    id="tag-search-input"
                    type="search"
                    size="sm"
                    autocomplete="off"
                ></b-form-input>
              </b-form-group>
            </b-dropdown-form>
            <b-dropdown-divider></b-dropdown-divider>
            <b-dropdown-item-button
                id="activity-type-dropdown"
                v-for="option in availableOptions"
                :key="option"
                @click="onOptionClick({ option, addTag })"
            >
              {{ option }}
            </b-dropdown-item-button>
            <b-dropdown-text v-if="availableOptions.length === 0">
              There are no activity types available to select
            </b-dropdown-text>
          </b-dropdown>
        </template>

      </b-form-tags>
    </b-col>
    <b-col cols="3">
      <b-form inline>
        <label style="margin-right:10px;">Search Method: </label>
        <b-form-radio-group id="activityTypesSearchMethods" v-model="method"
                            aria-describedby="activityTypesSearchMethodsHelp">
          <b-form-radio class="searchByRadio" value="AND">And
          </b-form-radio>
          <b-form-radio class="searchByRadio" value="OR">Or
          </b-form-radio>
        </b-form-radio-group>
      </b-form>
    </b-col>
  </b-row>
</template>

<script>
  import api from '@/Api'

  export default {
    name: "ActivityList",
    components: {
      // Notification,
    },
    props: {
      items: Array,
      fields: Array,
      profileId: Number,
      tableIsLoading: Boolean,
    },
    data() {
      return {
        selectedActivity: [],
        activityTypeOptions: [],
        selectedOptions: [],
        searchQuery: "",
        method: "AND"
      }
    },
    computed: {
      criteria() {
        /**
         * Computes the search criteria for searching for an activity type
         */
        return this.searchQuery.trim().toLowerCase()
      },
      /**
       * Gets the available options for a user to select in activity type dropdown
       * Filters out already selected options then if there is an input to the search box, return
       * all activity types that adhere to the search requirements
       */
      availableOptions() {
        const criteria = this.criteria;
        const options = this.activityTypeOptions.filter(
            opt => this.selectedOptions.indexOf(opt) === -1);
        if (criteria) {
          // Show only options that match criteria
          return options.filter(opt => opt.toLowerCase().indexOf(criteria) > -1);
        }
        // Show all options available
        return options
      },
      /**
       * If the search returned zero responses return a message to let user know
       */
      searchDesc() {
        if (this.criteria && this.availableOptions.length === 0) {
          return 'There are no tags matching your search criteria'
        }
        return ''
      }

    },
    watch: {
      /**
       * Whenever the method data value is changed call the emitMethod function
       */
      method: function () {
        this.emitMethod();
      }
    },
    methods: {
      /**
       * Gets all activity types from the backend and sets the response to activityTypeOptions
       */
      getActivitiesTypesFromApi: function () {
        let currentObj = this;
        api.getActivityTypes()
        .then(function (response) {
          console.log(response.data);
          currentObj.activityTypeOptions = response.data;
        })
        .catch(function (error) {
          console.log(error.response.data);
        });
      },
      /**
       * Call when the user selects an activity type, the updated selected activity types are then
       * emitted to the parent component
       * @param option the activity type the user has currently selected
       * @param addTag updates the selected activity types
       */
      onOptionClick({option, addTag}) {
        addTag(option);
        this.searchQuery = ''
        this.emitActivityTypes()
      },
      /**
       * Emits the updated activity type list to the parent component
       */
      emitActivityTypes() {
        this.$emit('selectedActivityTypes', this.selectedOptions)
      },
      /**
       * Emits the updated activity type method (either 'AND' or 'OR') to the parent component
       */
      emitMethod() {
        this.$emit('activityTypeMethod', this.method)
      },
    },
    mounted() {
      /**
       * Called when the page loads to get the available activity types from the backend
       */
      this.getActivitiesTypesFromApi()
    }
  }
</script>