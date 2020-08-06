<template>
  <div>
    <div v-if="modal == true">
      <b-button v-b-modal.modal-1 v-if="visibility == 'Public'" variant="success">{{visibility}}
      </b-button>
      <b-button v-b-modal.modal-1 v-if="visibility == 'Restricted'" variant="warning">
        {{visibility}}
      </b-button>
      <b-button v-b-modal.modal-1 v-if="visibility == 'Private'" variant="danger">{{visibility}}
      </b-button>

      <b-modal size="xl" style="padding: 1em" id="modal-1" title="Share" hide-footer hide-header
               @show="updateSelectedValue" body-class="p-0">
        <b-button-close style="padding: 10px" @click="$bvModal.hide('modal-1')"></b-button-close>
        <b-row>
          <b-col style="color: white; background: #4d4d4d; max-width: 40%; horiz-align: center">
            <h4>Select Sharing Option</h4>
            <div style="margin-top: 30%; font-size: small">
              <p v-if="selected == 'Public'">Anyone will be able to view this activity.</p>
              <p v-if="selected == 'Private'">Only you will be able to view this activity.</p>
              <p v-if="selected == 'Restricted'">Only people you allow will be able to view this
                activity.</p>
            </div>
            <b-form-select style="margin-bottom: 40%;" v-model="selected" :options="options"
                           size="sm"></b-form-select>
            <b-alert
                :show="showWarning"
                dismissible
                variant="danger"
            >You have {{organiserCount}} organisers, {{followerCount}} followers and {{partCount}}
              participants. You are changing visibility type to be more restrictive, are you sure?
            </b-alert>
            <b-form-group v-if="selected =='Restricted'" style="color: white" for="emailInput">
              <p style="font-size: small">Add multiple members by separating emails by space or
                semi-colon</p>
              <label>Input member emails</label>
              <b-input
                  name="email"
                  id="emailInput"
                  placeholder="john@example.com kevin@example.com"
                  v-model="emailInput"
              ></b-input>
              <p style="color: #cc9a9a">{{followerCount}} Followers {{partCount}} Participants
                {{organiserCount}} Organisers</p>
            </b-form-group>
            <b-button style="margin: 15px" @click="submit()">Save changes</b-button>
          </b-col>

          <b-col v-if="selected == 'Restricted' && busy">
            <div class="d-flex justify-content-center mb-3" style="margin-top: 45%">
              <b-spinner label="Loading..."></b-spinner>
            </div>

          </b-col>
          <b-col style="size: auto" v-if="selected == 'Restricted' && !busy">
            <br>
            <restricted-user-tabs v-on:organisersChanged="updateOrganisers"
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
                                  v-on:childToParent="updateUsers"></restricted-user-tabs>
          </b-col>
        </b-row>
      </b-modal>
    </div>

  </div>
</template>

<script>
  import api from '@/Api'
  import RestrictedUserTabs from "./RestrictedUsersTabs";

  export default {

    name: "ShareActivity",
    components: {RestrictedUserTabs},
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
        organisers: {},
        participants: {},
        accessors: {},
        followers: {},
        showWarning: false,
        partCount: null,
        organiserCount: null,
        followerCount: null,
        moreRestrictive: false,
        busy: true
      }
    },
    methods: {
      emitInputToParent() {
        this.$emit('emitInput', this.selected);
      },
      async updateSelectedValue() {
        this.emailInput = "";
        this.getCount();
        this.selected = this.visibility
        await api.getActivityMembers(this.activityId, 0, 10)
            .then((res) => {
              this.organisers["users"] = res.data.Organiser;
              this.participants["users"] = res.data.Participant;
              this.followers["users"] = res.data.Follower;
              this.accessors["users"] = res.data.Access;
              this.reformatUserData(this.organisers["users"], "organiser");
              this.reformatUserData(this.participants["users"], "participant");
              this.reformatUserData(this.followers["users"], "follower");
              this.reformatUserData(this.accessors["users"], "accessor");
              this.busy = false;
            })
            .catch(() => {
              alert("An error has occurred, please refresh the page")
            });
        this.showWarning = false
      },
      changeVisibilityType() {

        let data = {
          visibility: this.selected.toLowerCase(),
          accessors: []
        }
        this.addUserDataForUpdateVisibility(this.organisers.users, data);
        this.addUserDataForUpdateVisibility(this.participants.users, data);
        this.addUserDataForUpdateVisibility(this.followers.users, data);
        this.addUserDataForUpdateVisibility(this.accessors.users, data);
        let vueObj = this;
        api.updateActivityVisibility(this.profileId, this.activityId, data)
            .then(function () {
              vueObj.getCount()
              vueObj.visibility = vueObj.selected
              vueObj.$bvModal.hide('modal-1');

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
          user["selected"] = false;
          user["_rowVariant"] = "danger";
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
    }
  }
</script>
