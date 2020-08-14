<template>
  <b-row align-v="center">
    <b-col cols="9">
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
    <b-col cols="3">
      <b-form inline>
        <label style="margin-right:10px;">Search Method: </label>
        <b-form-radio-group id="activityTypesSearchMethods" v-model="method"
                            aria-describedby="activityTypesSearchMethodsHelp" @change=emitMethod>
          <b-form-radio id="radio-and" class="searchByRadio" value="AND">And
          </b-form-radio>
          <b-form-radio id="radio-or" class="searchByRadio" value="OR">Or
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
      criteria() {
        // Compute the search criteria
        return this.searchQuery.trim().toLowerCase()
      },
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
      searchDesc() {
        if (this.criteria && this.availableOptions.length === 0) {
          return 'There are no tags matching your search criteria'
        }
        return ''
      }

    },
    methods: {
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
      emitActivityTypes() {
        this.$emit('selectedActivityTypes', this.selectedOptions)
      },
      emitMethod(method) {
        this.method = method;
        this.$emit('activityTypeMethod', this.method)
      },
    },
    mounted() {
      this.getActivitiesTypesFromApi()
    }
  }
</script>