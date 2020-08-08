<template>

  <div v-if="!result.isEditMode">
    <b-row>
      <b-col sm="4">
        <h6>{{ result.title }}</h6>
      </b-col>
      <b-col sm="5">
        <h6 v-if="result.type!=='StartFinish'">{{ result.result }}</h6>
        <div v-else>
          <h6> Start time: {{ result.resultStart }} </h6>
          <h6> End time: {{ result.resultEnd }} </h6>
        </div>
      </b-col>
      <b-col sm="1">
        <b-button-group>
          <b-button @click="result.isEditMode=true" class="button-group" variant="success">Edit
          </b-button>
          <b-button class="button-group" variant="danger">Delete</b-button>
        </b-button-group>
      </b-col>
    </b-row>
  </div>

  <div v-else>
    <b-row>
      <b-col sm="4">
        <b-form-select :options="Object.keys(metricTitleDict)" v-model="result.title"
                       v-on:change="updateInputGroup"></b-form-select>
      </b-col>
      <b-col sm="6">
        <b-form-input placeholder="Enter your result" v-if="result.type!=='StartFinish'"
                      v-model="result.result"></b-form-input>
        <b-input-group v-else>
          <b-row>
            <label>Start Time: </label>
            <b-form-input placeholder="Enter your start time" type="datetime-local"
                          v-model="result.resultStart"></b-form-input>
          </b-row>
          <b-row>
            <label style="padding-top: 5px">End Time: </label>
            <b-form-input placeholder="Enter your end time" type="datetime-local"
                          v-model="result.resultEnd"></b-form-input>
          </b-row>
        </b-input-group>
      </b-col>
      <b-col sm="1">
        <b-button v-if="isCreateResult">Create</b-button>
        <b-button @click="result.isEditMode=false" v-else>Save</b-button>
      </b-col>
    </b-row>
  </div>
</template>

<script>
export default {
  name: "RecordActivityResultForm",
  props: ['result', 'metricTitleDict', 'isCreateResult'],
  data() {
    return {}
  },
  methods: {
    updateInputGroup(val) {
      this.result.type = this.metricTitleDict[val]
    }
  }
}

</script>

<style>
.button-group {
  margin-right: 5px;
}
</style>