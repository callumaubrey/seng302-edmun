<template>
  <div>
    <b-row>
      <b-col cols="10">
        <b-form-text>{{ helpText }}</b-form-text>
        <b-form-tags v-model="values" no-outer-focus
                     style="margin: 0; padding: 0; background-color: initial; border: 0">
          <ul class="list-inline d-inline-block mb-2" style="min-height: 20px;">
            <li v-for="tag in values" :key="tag" class="list-inline-item" >
              <b-form-tag
                  @remove="removeTag(tag)"
                  :key="tag"
                  :title="tag"

                  class="mr-1"
              >{{ tag }}
              </b-form-tag>
            </li>
          </ul>
          <b-input-group>
            <b-form-input
                id="hashtag-input"
                v-model="value"
                :placeholder="inputPlaceholder"
                class="form-control"
                :state="inputState()"
                :disabled="maxEntriesReached"
                :maxlength="maxCharacters"
                list="autocomplete"
                v-on:input="autocompleteHashtagInput">
            </b-form-input>

            <b-input-group-append>
              <b-button id="add-hashtag-button" @click="addTag(value)" variant="primary" :disabled="maxEntriesReached || noInput || !checkInput()">Add</b-button>
            </b-input-group-append>
            <b-form-invalid-feedback>
              {{ inputErrorMessage }}
            </b-form-invalid-feedback>

          </b-input-group>
          <div v-if="!selected">
            <div v-for="option in hashtag.options" :key="option">
              <b-input
                  class="autocomplete-item"
                  :readonly=true
                  v-on:click="setInput(option)"
                  :value=option>
              </b-input>
            </div>
          </div>
        </b-form-tags>
      </b-col>
      <b-col class="method-col">
        <b-form-radio-group id="activityTagSearchMethods" v-model="childSearchMethod" @change="emitSearchMethod" >
          <b-form-radio id="activity-tag-radio-and" class="searchByRadio" value="AND"  >And
          </b-form-radio>
          <b-form-radio id="activity-tag-radio-or" class="searchByRadio" value="OR">Or
          </b-form-radio>
        </b-form-radio-group>
      </b-col>
    </b-row>
  </div>
</template>

<script>
  import api from "../../Api";

  export default {
    name: "SearchActivityTag",
    props: {
      maxEntries: Number,
      inputPlaceholder: String,
      helpText: String,
      values: Array,
      inputCharacterLimit: Number,
      childSearchMethod: {
        type: String,
        default: "AND"
      },
    },
    data() {
      return {
        value: "",
        selected: false,
        inputErrorMessage: "",
        hashtag: {
          options: [],
          searchMethod: 'AND'
        }
      }
    },
    methods: {
      /**
       * Sets the hashtag input to value when input field is clicked
       * @param value
       */
      setInput(value) {
        this.value = value;
        this.selected = true;
      },
      /**
       * adds hashtag to list of hashtags and calls emit tags function
       * @param value
       */
      addTag(value) {
        value = value.toLowerCase();
        if (value[0] !== "#") {
          value = "#" + value;
        }
        if (!this.values.includes(value)) {
          this.values.push(value);
          this.value = "";
          this.emitTagsToParent();
        }
      },
      /**
       * removes tag from list of tages and calls emit function
       * @param value
       */
      removeTag(value) {
        const index = this.values.indexOf(value);
        if (index > -1) {
          this.values.splice(index, 1);
          this.emitTagsToParent();
        }
      },
      /**
       * emits current tags to parent
       */
      emitTagsToParent() {
        this.$emit('emitTags', this.values);
      },
      /**
       * emits current input to parent, used for auto complete
       */
      autocompleteHashtagInput: function () {
        let value = this.value;
        let pattern = /^#?[a-zA-Z0-9_]*$/;
        if (!pattern.test(value)) {
          this.hashtag.options = [];
          return;
        }
        if (value[0] == "#") {
          value = value.substr(1);
        }
        if (value.length > 2) {
          let vue = this;
          api.getHashtagAutocomplete(value)
          .then(function (response) {
            let results = response.data.results;
            for (let i = 0; i < results.length; i++) {
              results[i] = "#" + results[i];
            }
            vue.hashtag.options = results;
            console.log(vue.hashtag.options)
          })
          .catch(function () {

          });
        } else {
          this.hashtag.options = [];
        }
      },
      /**
       * validation for hashtag input
       * @returns true if valid, false otherwise
       */
      valueValid() {
        let pattern = /^#?[a-zA-Z0-9_]*$/;
        if (!pattern.test(this.value)) {
          return false;
        } else {
          return true;
        }
      },
      /**
       * checks state of input is valid to be a hashtag
       * @returns null if valid, false otherwise
       */
      inputState() {
        if (this.checkInput()) {
          return null;
        } else {
          return false;
        }
      },
      /**
       * checks validity of input and if hashtag has already been used
       * @returns true if ok, false if otherwise
       */
      checkInput() {
        if (!this.valueValid()) {
          this.inputErrorMessage = "Hashtags may only contain alphabets, numbers and underscores.";
          return false;
        }
        let value = this.value.toLowerCase();
        if (this.values.includes(value) || this.values.includes("#" + value)) {
          this.inputErrorMessage = "This hashtag has already been added";
          return false;
        } else {
          return true;
        }
      },
      /**
       * emits search method (AND/OR) to parent
       * @param method
       */
      emitSearchMethod(method) {
        this.childSearchMethod = method;
        this.$emit('emitSearchMethod', method);
      },
    },
    computed: {
      /**
       * calculates how many hashtags
       * @returns true if 30 or more hashtags
       */
      maxEntriesReached() {
        return this.values.length >= this.maxEntries;
      },
      /**
       * calculates max character limit for hashtag input
       * @returns {number}
       */
      maxCharacters() {
        if (this.values[0] == "#") {
          return this.inputCharacterLimit + 1;
        } else {
          return this.inputCharacterLimit;
        }
      },
      /**
       * checks if input field is empty
       * @returns true if empty
       */
      noInput() {
        let tempValue = this.value;
        if (this.value[0] == "#") {
          tempValue = tempValue.substr(1);
        }
        if (tempValue.length == 0) {
          return true;
        } else {
          return false;
        }
      }
    }
  }
</script>

<style scoped>

  .method-col {
    padding-top: 30px;
  }

</style>