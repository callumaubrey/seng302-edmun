<template>
  <div>
    <b-card no-body>
      <b-tabs card>
        <b-tab key="Organisers" title="Organisers" active>
          <restricted-users-table v-on:rowChanged="organisersChanged"
                                  v-on:selectAll="selectAllOrganisers"
                                  v-on:deselectAll="deselectAllOrganisers"
                                  :role-data="rolesData.organisers"
                                  :activity-roles="activityRoles"
                                  :fields="organiserParticipantFields"></restricted-users-table>
        </b-tab>

        <b-tab key="Participants" title="Participants">
          <restricted-users-table v-on:rowChanged="participantsChanged"
                                  v-on:selectAll="selectAllParticipants"
                                  v-on:deselectAll="deselectAllParticipants"
                                  :role-data="rolesData.participants"
                                  :activity-roles="activityRoles"
                                  :fields="organiserParticipantFields"></restricted-users-table>
        </b-tab>

        <b-tab key="Followers" title="Followers">
          <restricted-users-table v-on:rowChanged="followersChanged"
                                  v-on:selectAll="selectAllFollowers"
                                  v-on:deselectAll="deselectAllFollowers"
                                  :role-data="rolesData.followers"
                                  :activity-roles="activityRoles"
                                  :fields="followerAccessorFields"></restricted-users-table>
        </b-tab>

        <b-tab key="Accessors" title="Accessors">
          <restricted-users-table v-on:rowChanged="accessorsChanged"
                                  v-on:selectAll="selectAllAccessors"
                                  v-on:deselectAll="deselectAllAccessors"
                                  :role-data="rolesData.accessors"
                                  :activity-roles="activityRoles"
                                  :fields="followerAccessorFields"></restricted-users-table>
        </b-tab>
      </b-tabs>
    </b-card>

  </div>
</template>

<script>
  import RestrictedUsersTable from "./RestrictedUsersTable";

  export default {
    name: "ShareActivityUserList",
    props: ["rolesData"],
    components: {
      RestrictedUsersTable
    },
    data() {
      return {
        activityRoles: [
          {value: "organiser", text: "Organiser"},
          {value: "participant", text: "Participant"}
        ],
        data: null,
        organiserParticipantFields: [
          {
            key: 'full_name',
            label: 'Name',
            sortable: true
          },
          {
            key: 'primary_email',
            label: 'Primary Email',
            sortable: true
          },
          {
            key: 'role'
          },
          {
            key: 'selected'
          }
        ],
        followerAccessorFields: [
          {
            key: 'full_name',
            label: 'Name',
            sortable: true
          },
          {
            key: 'primary_email',
            label: 'Primary Email',
            sortable: true
          },
          {
            key: 'selected'
          }
        ]
      }
    },
    methods: {
      organisersChanged(value) {
        this.$emit('organisersChanged', value);
      },
      participantsChanged(value) {
        this.$emit('participantsChanged', value);
      },
      followersChanged(value) {
        this.$emit('followersChanged', value);
      },
      accessorsChanged(value) {
        this.$emit('accessorsChanged', value);
      },
      selectAllOrganisers() {
        this.$emit('selectAllOrganisers');
      },
      selectAllParticipants() {
        this.$emit('selectAllParticipants');
      },
      selectAllFollowers() {
        this.$emit('selectAllFollowers');
      },
      selectAllAccessors() {
        this.$emit('selectAllAccessors');
      },
      deselectAllOrganisers() {
        this.$emit('deselectAllOrganisers');
      },
      deselectAllParticipants() {
        this.$emit('deselectAllParticipants');
      },
      deselectAllFollowers() {
        this.$emit('deselectAllFollowers');
      },
      deselectAllAccessors() {
        this.$emit('deselectAllAccessors');
      },

    },
    beforeMount() {
      this.data = this.rolesData
    }
  }
</script>

<style scoped>
</style>