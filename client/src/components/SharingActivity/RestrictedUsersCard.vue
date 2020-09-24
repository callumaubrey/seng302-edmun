<template>
  <div>
    <b-card v-for="(profile, index) in data.users" v-bind:key="index"
            v-bind:class="{'border-variant-danger': profile.selected === false}">
      <b-card-body>
        <b-row>
          <b-col cols="1.0">
            <b-img :src=profile.imageSrc alt="Center image" center height="80px"
                   onerror="this.onerror=null;this.src='https://www.signtech.co.nz/wp-content/uploads/2019/08/facebook-blank-face-blank-300x298.jpg'"
                   rounded="circle"
                   width="80px"></b-img>
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
              v-on:change="rowChanged(profile, index)"
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
        data: {},
        defaultImg: "https://www.signtech.co.nz/wp-content/uploads/2019/08/facebook-blank-face-blank-300x298.jpg"
      }
    },
    props: ['roleData', 'activityRoles'],
    methods: {
      rowChanged(profile, index) {
        let obj = {}
        obj.index = index
        obj.item = profile
        this.$emit('rowChanged', obj)
      },
      selectAll: function () {
        this.$emit('selectAll')
      },
      deselectAll: function () {
        this.$emit('deselectAll')
      }
    },
    async beforeMount() {
      this.data = this.roleData;
      this.selectAll();
    }
  }
</script>

<style scoped>

  .border-variant-danger {
    background-color: pink;
  }

</style>
