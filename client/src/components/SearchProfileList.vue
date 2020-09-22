<template>
  <div>
    <b-card v-for="profile in profile_data" :key="profile.profile_id"
            class="profile-card rounded mb-3">

      <b-card-body class="profile-card-body">
        <b-row>
          <b-col cols="1.0">
            <b-img alt="Center image" center height="80px" rounded="circle"
                   src="https://www.signtech.co.nz/wp-content/uploads/2019/08/facebook-blank-face-blank-300x298.jpg"
                   width="80px"></b-img>
          </b-col>
          <b-col class="title">
            <router-link :to="{name: 'Profile', params: { id: profile.profile_id}}">
              {{ profile.firstname }} {{ profile.lastname }}
            </router-link>
            <b-card-sub-title class="mb-2">{{ profile.primary_email }}</b-card-sub-title>
          </b-col>
          <b-col v-if="profile.activity_types !== ''"
                 class="activity-type-icon-collection float-right" cols="0.2">
            <ActivityTypeIcon v-for="type in profile.activity_types.replace(/ /g, '').split(',')"
                              :key="type"
                              :type_name="type" class="float-right"></ActivityTypeIcon>
          </b-col>
        </b-row>
        <b-row v-if="isAdmin" class="justify-content-md-end">
          <b-button class="actionBtn" size="sm" @click="emitEditEventToParent(profile)">
            Edit
          </b-button>
          <b-button id="btnDelete" class="actionBtn" danger size="sm"
                    variant="danger" @click="emitDeleteEvenToParent(profile)">
            Delete
          </b-button>
        </b-row>
      </b-card-body>
    </b-card>
  </div>
</template>

<script>
import ActivityTypeIcon from "@/components/Activity/ActivityType/ActivityTypeIcon";

export default {
  name: "SearchProfileList",
  components: {ActivityTypeIcon},
  data: function () {
    return {}
  },

  props: {
    profile_data: {
      type: Array
    },
    isAdmin: {
      type: Boolean
    }
  },
  methods: {
    goToProfile(profile_id) {
      this.$router.push('/profiles/' + profile_id);
    },
    emitEditEventToParent(profile) {
      this.$emit('edit-event', profile)
    },
    emitDeleteEvenToParent(profile) {
      this.$emit('delete-event', profile)
    }
  }

}
</script>

<style scoped>

.profile-card {
  cursor: pointer;
  padding: 0px;
  margin: 0px;
  min-height: 80px;
}

.title {
  font-size: 150%;
}

.actionBtn {
  margin: 2px 5px;
}

</style>