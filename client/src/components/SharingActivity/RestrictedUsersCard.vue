<template>
  <div>
    <b-card v-for="(profile, index) in roleData.users" v-bind:key="index"
            v-bind:class="{'border-variant-danger': profile.selected === false}">
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
                           v-on:change.native="rowChanged(profile, index)"></b-form-select>
          </b-col>
          <b-form-checkbox
              v-model="profile.selected"
              v-on:change="rowColor(profile, index)"
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
    props: ['roleData', 'activityRoles'],
    methods: {
      rowColor(profile, index) {
        this.rowChanged(profile, index)
      },
      rowChanged(profile, index) {
        console.log("TEST")
        console.log(this.roleData)
        let obj = {}
        obj.index = index
        obj.item = profile
        this.$emit('rowChanged', obj)
      },
      selectAll: function () {
        console.log("SELECT FLAG")
        this.$emit('selectAll')
      },
      deselectAll: function () {
        console.log("DESELECT FLAG")
        this.$emit('deselectAll')
      },
    },
    beforeMount() {
      console.log(this.roleData)
      this.data = this.roleData;
    },
    watch: {
      roleData: {
        handler: function (roleData) {
          console.log("CARD")
          console.log(roleData)
          // check someData and eventually call
          this.$forceUpdate()
        },
        immediate: true
      }
    }
  }
</script>

<style scoped>

  .border-variant-danger {
    background-color: pink;
  }

</style>
