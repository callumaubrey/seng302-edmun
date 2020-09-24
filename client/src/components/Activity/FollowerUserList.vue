<template>
    <div>
        <!-- Grouped Tabs -->
        <!-- Commented out sections are for future roles that are in the api calls but i didn't realise we weren't using yet -->
        <b-card no-body>
            <b-tabs card>
                <b-tab key="Organisers" @click="currentGroup='Organisers'">
                    <template v-slot:title>
                        <b-row><b-col>Organisers</b-col><b-col><b-badge>{{organisers.length}}</b-badge></b-col></b-row>
                    </template>
                    <b-card style="margin-top:10px;" :key="user.PROFILE_ID" v-for="user in organisers">
                        <b-row class="text-center" align-v="center">
                            <b-col class="text-center">
                                {{ user.FULL_NAME }}
                            </b-col>
                            <b-col v-if="activityCreatorId==loggedInId" class="text-center">
                                <b-dropdown class="m-md-2" id="dropdown-1" text="Organiser">
                                    <b-dropdown-item @click="changeRole(user, 'participant')">Participant</b-dropdown-item>
                                </b-dropdown>
                            </b-col>
                        </b-row>
                    </b-card>
                </b-tab>
                <b-tab key="Participants" @click="currentGroup='Participants'">
                    <template v-slot:title>
                        <b-row><b-col>Participants</b-col><b-col><b-badge>{{participants.length}}</b-badge></b-col></b-row>
                    </template>
                <b-card style="margin-top:10px;" :key="user.PROFILE_ID" v-for="user in participants">
                    <b-row class="text-center" align-v="center">
                      <b-col cols="2">
                        <b-img center height="80px" rounded="circle"
                               :src="user.imageSrc"
                               onerror="this.onerror=null;this.src='https://www.signtech.co.nz/wp-content/uploads/2019/08/facebook-blank-face-blank-300x298.jpg'"
                               width="80px"></b-img>
                      </b-col>
                      <b-col class="text-lg-left">
                        {{ user.full_name }}
                      </b-col>
                      <b-col v-if="activityCreatorId==loggedInId" class="text-right">
                        <b-dropdown id="dropdown-1" class="m-md-2" text="Participant">
                          <b-dropdown-item @click="changeRole(user, 'organiser')">Organiser
                          </b-dropdown-item>
                          <b-dropdown-item @click="removeRole(user)">Remove</b-dropdown-item>

                        </b-dropdown>
                      </b-col>
                    </b-row>
                </b-card>
            </b-tab>
                <b-tab key="Organisers" title="Organisers" @click="currentGroup='Organisers'">
                  <b-card style="margin-top:10px;" :key="user.profile_id"
                          v-for="user in organisers">
                    <b-row align-v="center" class="text-center">
                      <b-col cols="2">
                        <b-img center height="80px" rounded="circle"
                               :src=user.imageSrc
                               onerror="this.onerror=null;this.src='https://www.signtech.co.nz/wp-content/uploads/2019/08/facebook-blank-face-blank-300x298.jpg'"
                               width="80px"></b-img>
                      </b-col>
                      <b-col class="text-lg-left">
                        {{ user.full_name }}
                      </b-col>
                      <b-col v-if="activityCreatorId==loggedInId" class="text-right">
                        <b-dropdown id="dropdown-1" class="m-md-2" text="Organiser">
                          <b-dropdown-item @click="changeRole(user, 'participant')">Participant
                          </b-dropdown-item>
                          <b-dropdown-item @click="removeRole(user)">Remove</b-dropdown-item>
                        </b-dropdown>
                      </b-col>
                    </b-row>
                  </b-card>
                </b-tab>
                <b-tab key="Accessors" title="Accessors" @click="currentGroup='Accessors'">
                  <b-card style="margin-top:10px;" :key="user.profile_id" v-for="user in accessors">
                    <b-row align-v="center" class="text-center">
                      <b-col cols="2">
                        <b-img center height="80px" rounded="circle"
                               :src=user.imageSrc
                               onerror="this.onerror=null;this.src='https://www.signtech.co.nz/wp-content/uploads/2019/08/facebook-blank-face-blank-300x298.jpg'"
                               width="80px"></b-img>
                      </b-col>
                      <b-col class="text-lg-left">
                        {{ user.full_name }}
                      </b-col>
                      <b-col v-if="activityCreatorId==loggedInId" class="text-right">
                        <b-dropdown id="dropdown-1" class="m-md-2" text="Participant">
                          <b-dropdown-item @click="removeRole(user)">Remove</b-dropdown-item>
                        </b-dropdown>
                      </b-col>
                    </b-row>
                  </b-card>
                </b-tab>
            </b-tabs>
        </b-card>

    </div>
</template>

<script>
import api from '@/Api';

export default {
  name: "FollowerUserList",

  // Component Properties
  props: {
    activityId: {
      type: Number,
      default: null,
    },
    activityCreatorId: {
      type: Number,
      default: null,
    },
    loggedInId: {
      type: Number,
      default: null,
    }
  },

  // Component Members
  data() {
    return {
      organisers: [],
      participants: [],
      accessors: [],
      followers: [],
      currentGroup: "Participants",
      limit: 10,
      organiserOffset: 0,
      participantOffset: 0,
      accessorOffset: 0,
      followerOffset: 0,
      roleData: null
    }
  },
  async mounted() {
    this.scroll();
    await this.getMembers();
  },

  // Component Methods
  methods: {
    getMembers: async function () {
      await api.getActivityMembers(this.activityId)
      .then((res) => {
        this.organisers = res.data.Organiser;
        this.participants = res.data.Participant;
        this.accessors = res.data.Access;
        this.followers = res.data.Follower;
        this.getProfileImage(this.participants)
        this.getProfileImage(this.organisers);
        this.getProfileImage(this.accessors);
        this.getProfileImage(this.followers);
      })
      .catch(err => {
        console.log(err)
      });
    },
    async getProfileImage(users) {
      for (let i = 0; i < users.length; i++) {
        this.$set(users[i], 'imageSrc',
            process.env.VUE_APP_SERVER_ADD + "/profiles/" + users[i].profile_id + "/image")
      }
    },
    getMoreOrganisers: async function () {
      this.organiserOffset += this.limit;
      await api.getActivityOrganisers(this.activityId, this.organiserOffset, this.limit)
      .then((res) => {
        for (let i = 0; i < res.data.Organiser.length; i++) {
          if (!this.organisers.includes(res.data.Organiser[i])) {
            this.organisers.push(res.data.Organiser[i]);
          }
        }
        this.getProfileImage(this.organisers);
      })
      .catch(err => {
        console.log(err)
      });
    },
    getMoreParticipants: async function () {
      this.participantOffset += this.limit;
      await api.getActivityParticipants(this.activityId, this.participantOffset, this.limit)
      .then((res) => {
        for (let i = 0; i < res.data.Participant.length; i++) {
          if (!this.participants.includes(res.data.Participant[i])) {
            this.participants.push(res.data.Participant[i]);
          }
        }
        this.getProfileImage(this.participants);
      })
      .catch(err => {
        console.log(err)
      });
    },
    /* Uncomment for scroll loading for accessor and follower roles */
    getMoreAccessors: async function () {
      this.accessorOffset += this.limit;
      await api.getActivityAccessors(this.activityId, this.accessorOffset, this.limit)
      .then((res) => {
        for (let i = 0; i < res.data.Participant.length; i++) {
          if (!this.accessors.includes(res.data.Access[i])) {
            this.accessors.push(res.data.Access[i]);
          }
        }
        this.getProfileImage(this.accessors);
      })
      .catch(err => {
        console.log(err)
      });
    },
    changeRole: async function (user, role) {
      await api.getProfile(user.profile_id)
      .then((res) => {
        this.roleData = {
          subscriber: {
            email: res.data.primary_email.address,
            role: role
          }
        };
      }).catch(err => {
        console.log(err)

      });
      await api.updateRole(this.activityCreatorId, this.activityId, this.roleData)
      .then(() => {
        if (role == "organiser") {
          this.participantOffset -= 1;
          const index = this.participants.indexOf(user);
          this.participants.splice(index, 1);
          this.organisers.push(user);
        } else {
          this.organiserOffset -= 1;
          const index = this.participants.indexOf(user);
          this.organisers.splice(index, 1);
          this.participants.push(user);
        }
      })
      .catch(err => {
        console.log(err);

      });
    },
    removeRole: async function (user) {
      await api.getProfile(user.profile_id)
      .then((res) => {
        this.roleData = {
          email: res.data.primary_email.address
        };
      }).catch(err => {
        console.log(err)

      });
      await api.removeRole(this.activityCreatorId, this.activityId, this.roleData)
      .then(() => {
      })
      .catch(err => {
        console.log(err);
      });
      if (this.currentGroup == "Participants") {
        this.participantOffset -= 1;
        const index = this.participants.indexOf(user);
        this.participants.splice(index, 1);
      }
      if (this.currentGroup == "Organisers") {
        this.organiserOffset -= 1;
        const index = this.organisers.indexOf(user);
        this.organisers.splice(index, 1);
      }
      if (this.currentGroup == "Accessors") {
        this.accessorOffset -= 1;
        const index = this.accessors.indexOf(user);
        this.accessors.splice(index, 1);
      }
    },
    scroll() {
      window.onscroll = async () => {
        let bottomOfWindow = document.documentElement.scrollTop + window.innerHeight
            === document.documentElement.offsetHeight;
        if (bottomOfWindow) {
          if (this.currentGroup == "Organisers") {
            await this.getMoreOrganisers();
          }
          if (this.currentGroup == "Participants") {
            await this.getMoreParticipants();
          }
          if (this.currentGroup == "Accessors") {
            await this.getMoreAccessors();
          }
          if (this.currentGroup == "Followers") {
            await this.getMoreFollowers();
          }
        }
      }
    }
  }
}
</script>

<style scoped>

</style>