<template>
  <div>
    <b-card v-for="profile in roleData.users" :key="profile.profile_id"
            v-bind:class="{'border-variant-danger': !profile.selected}">
      <b-card-body>
        <b-row>
          <b-col cols="1.0">
            <b-img alt="Center image" center height="50px" rounded="circle"
                   src="https://www.signtech.co.nz/wp-content/uploads/2019/08/facebook-blank-face-blank-300x298.jpg"
                   width="50px"></b-img>
          </b-col>
          <b-col class="text-lg-left">
            {{ profile.full_name }}
            <b-card-sub-title class="mb-2" style="padding-top: 5px">{{
                profile.primary_email
              }}
            </b-card-sub-title>
          </b-col>
          <b-col>
            <b-form-select v-model="profile.role" :options="activityRoles"
                           v-on:change.native="rowColor(profile)"></b-form-select>
          </b-col>
          <b-form-checkbox
              v-model="profile.selected"
              v-on:change.native="rowColor(profile)"
          >
          </b-form-checkbox>
        </b-row>
      </b-card-body>
    </b-card>
    <b-button id="select-all-button" style="margin-right: 5px; margin-top:5px"
              v-on:click="selectAll">
      Select All
    </b-button>

    <b-button id="deselect-all-button" style="margin-top: 5px" v-on:click="deselectAll">
      Deselect All
    </b-button>
  </div>
</template>

<script>

export default {
  name: "RestrictedUsersCard",
  data: function () {
    return {
      data: {}
    }
  },
  props: {
    roleData: {
      type: Array
    },
    activityRoles: {
      type: Array
    }
  },
  methods: {
    rowColor(profile) {
      console.log(profile.selected)
      this.roleChanged(profile)
    },
    roleChanged(profile) {
      this.$emit('rowChanged', profile)
    },
    selectAll: function () {
      this.$emit('selectAll')
    },
    deselectAll: function () {
      this.$emit('deselectAll')
    },
  },
  beforeMount() {
    console.log(this.roleData)
    this.data = this.roleData;
  }
}
</script>

<style scoped>

.border-variant-danger {
  background-color: pink;
}

</style>
