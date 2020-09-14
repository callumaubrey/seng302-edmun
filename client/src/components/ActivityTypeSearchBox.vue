<template>
  <b-row align-v="center">
    <b-col>
      <b-form-tags v-model="selectedOptions" no-outer-focus
                   size="lg">
        <template v-slot="{ tags, disabled, addTag, removeTag }">
          <ul v-if="tags.length > 0" class="list-inline d-inline-block mb-2">
            <li v-for="(tag, index) in tags" :key="tag" class="list-inline-item">
              <b-form-tag
                  :id="`activity-type-tag-${index}`"
                  @remove="onOptionRemove({tag, removeTag})"
                  @click="onOptionRemove({tag, removeTag})"
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
                :id="`activity-type-option-${index}`"
                v-for="(option, index) in availableOptions"
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
    <b-col style="flex: 0 0 50px;">
      <b-form inline>
        <b-form-radio-group id="activityTypesSearchMethods" v-model="method"
                            aria-describedby="activityTypesSearchMethodsHelp" @change=emitMethod>
          <b-form-radio id="activity-type-radio-and" class="searchByRadio" value="AND">And
          </b-form-radio>
          <b-form-radio id="activity-type-radio-or" class="searchByRadio" value="OR">Or
          </b-form-radio>
        </b-form-radio-group>
      </b-form>
    </b-col>
  </b-row>
</template>

<script>
  import api from '@/Api';

  export default {
    name: "ActivityList",
    components: {
      // Notification,
    },
    props: {
      selectedOptions: Array,
    },
    data() {
      return {
        selectedActivity: [],
        activityTypeOptions: [],
        searchQuery: "",
        method: "AND"
      }
    },

    computed: {
      /**
       * Computes the search criteria for searching for an activity type
       */
      criteria() {
        return this.searchQuery.trim().toLowerCase()
      },
      /**
       * Gets the available options for a user to select in activity type dropdown
       * Filters out already selected options then if there is an input to the search box, return
       * all activity types that adhere to the search requirements
       */
      availableOptions() {
        const criteria = this.criteria;
        // Filter out already selected options
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
    methods: {
      /**
       * Gets all activity types from the backend and sets the response to activityTypeOptions
       */
      getActivitiesTypesFromApi: function () {
        let currentObj = this;
        api.getActivityTypes()
        .then(function (response) {
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
      async onOptionClick({option, addTag}) {
        await addTag(option);
        this.searchQuery = ''
        this.emitActivityTypes();
      },
      /**
       * Called when a activity type tag is removed and the emits the new list of activity types
       * @param tag the activity type tag to remove
       * @param removeTag the function that removes the activity type tag
       */
      async onOptionRemove({tag, removeTag}) {
        await removeTag(tag)
        this.emitActivityTypes();
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
      emitMethod(method) {
        this.method = method;
        this.$emit('activityTypeMethod', this.method)
      },
    },
    /**
     * Called when the page loads
     */
    mounted() {
      this.getActivitiesTypesFromApi()
    }
  }
</script>