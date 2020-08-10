<template>
  <div>
    <div v-if="modal == true">
      <b-button id="activity-visibility-button" v-b-modal.modal-1 :variant="visibilityButtonColour">
        {{visibility}}
      </b-button>
      <b-modal size="xl" style="padding: 1em" id="modal-1" title="Share" hide-footer hide-header
               @show="updateSelectedValue" body-class="p-0" :static="true" ref="modal-1">
        <b-button-close style="padding: 10px" @click="$bvModal.hide('modal-1')"></b-button-close>
        <b-row>
          <b-col id="left-container">
            <b-row>
              <b-col>
                <h4 style="margin-top: 2%">Select Sharing Option</h4>
              </b-col>

            </b-row>

            <div style="margin-top: 30%; font-size: small">
              <p v-if="selected == 'Public'">Anyone will be able to view this activity.</p>
              <p v-if="selected == 'Private'">Only you will be able to view this activity.</p>
              <p v-if="selected == 'Restricted'">Only people you allow will be able to view this
                activity.</p>
            </div>
            <b-form-select style="margin-bottom: 10%;" v-model="selected" :options="options"
                           size="sm"></b-form-select>
            <b-alert
                :show="showWarning"
                dismissible
                variant="danger"
            >You have {{organiserCount}} organisers, {{followerCount}} followers and {{partCount}}
              participants. You are changing visibility type to be more restrictive, are you sure?
            </b-alert>
            <add-users v-if="selected == 'Restricted'" v-on:usersAdded="addUsers" :allUsersIds="allUsersIds" :activityId="activityId"></add-users>
            <p style="color: #cc9a9a">{{followerCount}} Followers {{partCount}} Participants
              {{organiserCount}} Organisers</p>
            <b-button style="margin-right: 2%; margin-bottom: 4%" @click="submit()">Save changes
            </b-button>
            <b-button style="margin-bottom: 4%" variant="danger" @click="closeModal()">Cancel
            </b-button>
          </b-col>

          <b-col v-if="selected == 'Restricted' && busy">
            <div class="d-flex justify-content-center mb-3" style="margin-top: 45%">
              <b-spinner label="Loading..."></b-spinner>
            </div>

          </b-col>
          <b-col style="size: auto" v-if="selected == 'Restricted' && !busy">
            <br>
            <restricted-users-tabs id="restricted-users-tabs"
                                   v-on:organisersChanged="updateOrganisers"
                                   v-on:participantsChanged="updateParticipants"
                                   v-on:followersChanged="updateFollowers"
                                   v-on:accessorsChanged="updateAccessors"
                                   v-on:selectAllOrganisers="selectAll(organisers.users)"
                                   v-on:selectAllParticipants="selectAll(participants.users)"
                                   v-on:selectAllFollowers="selectAll(followers.users)"
                                   v-on:selectAllAccessors="selectAll(accessors.users)"
                                   v-on:deselectAllOrganisers="deselectAll(organisers.users)"
                                   v-on:deselectAllParticipants="deselectAll(participants.users)"
                                   v-on:deselectAllFollowers="deselectAll(followers.users)"
                                   v-on:deselectAllAccessors="deselectAll(accessors.users)"
                                   :roles-data="roles_data = {organisers, followers, participants, accessors}"
                                   v-on:childToParent="updateUsers"
                                   :key="restrictedUsersTabsKey"></restricted-users-tabs>
          </b-col>
        </b-row>
        <b-row></b-row>

      </b-modal>
    </div>

  </div>
</template>

<script>
  import api from '@/Api'
  import RestrictedUsersTabs from "./RestrictedUsersTabs";
  import AddUsers from "./AddUsers";

  export default {

    name: "ShareActivity",
    components: {RestrictedUsersTabs, AddUsers},
    props: {
      profileId: String,
      activityId: String,
      modal: Boolean,
      visibility: String,
    },
    data() {
      return {
        // selectedVisibility: null,
        selected: 'Public',
        description: 'Reeee',
        emailInput: '',
        options: [
          {value: 'Private', text: 'Private'},
          {value: 'Restricted', text: 'Restricted'},
          {value: 'Public', text: 'Public'}
        ],
        organisers: [],
        participants: [],
        accessors: [],
        followers: [],
        showWarning: false,
        partCount: null,
        organiserCount: null,
        followerCount: null,
        moreRestrictive: false,
        busy: true,
        restrictedUsersTabsKey: 0,
        allUsersIds: []
      }
    },
    methods: {
      emitInputToParent() {
        this.$emit('emitInput', this.selected);
      },
      async updateSelectedValue() {
        this.busy = true;
        this.emailInput = "";
        this.getCount();
        this.selected = this.visibility
        await api.getActivityMembers(this.activityId, 0, 10000)
            .then((res) => {
              if (res.status == 200) {
                this.organisers["users"] = res.data.Organiser;
                this.participants["users"] = res.data.Participant;
                this.followers["users"] = res.data.Follower;
                this.accessors["users"] = res.data.Access;
                this.reformatUserData(this.organisers["users"], "organiser");
                this.reformatUserData(this.participants["users"], "participant");
                this.reformatUserData(this.followers["users"], "follower");
                this.reformatUserData(this.accessors["users"], "access");
                this.busy = false;
              }
            })
            .catch(() => {
              alert("An error has occurred, please refresh the page")
            });
        this.showWarning = false
        this.computeUserIds();
      },
      /**
       * Gets all the user ids from all the different roles and place them into the array allUsersIds
       * This array is used by the AddUsers.vue component to check to make sure the creator doesn't
       * add users that are already in the activity to the activity.
       */
      computeUserIds() {
        for (let user of this.organisers.users) {
          this.allUsersIds.push(user.profile_id)
        }
        for (let user of this.participants.users) {
          this.allUsersIds.push(user.profile_id)
        }
        for (let user of this.followers.users) {
          this.allUsersIds.push(user.profile_id)
        }
        for (let user of this.accessors.users) {
          this.allUsersIds.push(user.profile_id)
        }
      },
      changeVisibilityType() {

        let data = {
          visibility: this.selected.toLowerCase(),
          accessors: []
        }
        switch (data.visibility) {
          case("public"):
            break;
          case("restricted"):
            this.addUserDataForUpdateVisibility(this.organisers.users, data);
            this.addUserDataForUpdateVisibility(this.participants.users, data);
            this.addUserDataForUpdateVisibility(this.followers.users, data);
            this.addUserDataForUpdateVisibility(this.accessors.users, data);
            break;
          case("private"):
            break;
        }

        let vueObj = this;
        api.updateActivityVisibility(this.profileId, this.activityId, data)
            .then(function () {
              vueObj.getCount()
              vueObj.visibility = vueObj.selected
              vueObj.$bvModal.hide('modal-1');
              vueObj.notifyParent();
            })
            .catch(function () {
              alert("An error has occurred, please refresh the page")
            });
      },
      submit() {
        this.parseEmailInput();
        this.changeVisibilityType();
      },
      parseEmailInput() {
        var entryArray
        let data = this.emailInput
        if (data.includes(";")) {
          entryArray = data.split(';')
        } else {
          entryArray = data.split(' ')
        }
        this.email = entryArray
      },
      beforeMount() {
        this.updateSelectedValue()
      },
      getCount() {
        const currentObj = this;
        api.getActivityMemberCounts(this.activityId)
            .then(function (response) {
              currentObj.partCount = response.data.participants
              currentObj.followerCount = response.data.followers
              currentObj.organiserCount = response.data.organisers
            })
            .catch(function () {
              alert("An error has occurred, please refresh the page")
            });
      },
      reformatUserData(roleUsers, roleName) {
        let i;
        let n = roleUsers.length;
        for (i = 0; i < n; i++) {
          let user = roleUsers[i];
          user["selected"] = true;
          user["_rowVariant"] = "none";
          user["role"] = roleName;
        }
      },
      addUserDataForUpdateVisibility(roleUsers, data) {
        let i;
        let n = roleUsers.length;
        for (i = 0; i < n; i++) {
          let user = roleUsers[i];
          if (user.selected == true) {
            let emailRolePair = {
              "email": user.primary_email,
              "role": user.role
            }
            data.accessors.push(emailRolePair)
          }

        }
      },
      updateUsers(value) {
        this.organisers = value.organisers;
        this.participants = value.participants;
        this.followers = value.followers;
        this.accessors = value.accessors;

      },
      updateOrganisers(value) {
        this.organisers.users[value.index] = value.item;
      },
      updateParticipants(value) {
        this.participants.users[value.index] = value.item;
      },
      updateFollowers(value) {
        this.followers.users[value.index] = value.item;
      },
      updateAccessors(value) {
        this.accessors.users[value.index] = value.item;
      },
      /**
       * Checks the selected check box for all users
       * @param users all the users in the table
       */
      selectAll: function (users) {
        let i;
        let len = users.length
        for (i = 0; i < len; i++) {
          let user = users[i]
          user.selected = true;
          user._rowVariant = 'none';
        }
      },
      /**
       * Unchecks the selected check box for all users
       * @param users all the users in the table
       */
      deselectAll: function (users) {
        let i;
        let len = users.length
        for (i = 0; i < len; i++) {
          let user = users[i]
          user.selected = false;
          user._rowVariant = 'danger';
        }
      },
      notifyParent() {
        this.$emit("componentUpdate", this.visibility);
      },
      addUsers(users) {
        for (let user of users) {
          user.role = "access"
          user.selected = true;
          user._rowVariant = 'none';
          this.accessors.users.push(user)
        }
        this.computeUserIds();
        this.forceRerender();
      },
      forceRerender() {
        this.restrictedUsersTabsKey += 1;
      },
      closeModal() {
        this.$refs['modal-1'].hide()
      }

    },
    watch: {
      selected: function () {
        if (this.organiserCount === 0 && this.partCount === 0 && this.followerCount === 0) {
          this.showWarning = false
          return
        }
        if (this.visibility === "Restricted" && this.selected === "Private" || (this.visibility
            === "Public" && this.selected !== "Public")) {
          this.showWarning = true
        } else {
          this.showWarning = false
        }
      }
    },
    computed: {
      visibilityButtonColour: function () {
        switch (this.visibility.toLowerCase()) {
          case "public":
            return "success";
          case "restricted":
            return "warning";
          case "private":
            return "danger";
          default:
            return "primary";
        }
      }
    }
  }
</script>

<style scoped>
  #left-container {
    color: white;
    background: #4d4d4d;
    max-width: 40%;
    horiz-align: center;
    border-radius: 3px 0px 0px 3px;
  }

</style>
