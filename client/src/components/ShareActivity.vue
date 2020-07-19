<template>
    <div>
        <div v-if="modal == true">
            <b-button v-b-modal.modal-1 v-if="visibility == 'Public'" variant="success">{{visibility}}</b-button>
            <b-button v-b-modal.modal-1 v-if="visibility == 'Private'" variant="danger">{{visibility}}</b-button>

            <b-modal id="modal-1" title="Share" @show="updateSelectedValue" @ok="changeVisibilityType" >
                <label>Select Sharing Option</label>
                <b-form-select v-model="selected" :options="options" size="sm"></b-form-select>
                <label v-if="visibility != selected">Are you sure you want to change visibility, press OK to continue</label>
                <div v-if="selected =='restricted1'">
                    <br>
                    <label>Add Email</label>
                    <b-form-group style="font-weight: bold" for="emailInput"
                                  description="Maximum of 5 emails allowed">
                        <b-input type="email"
                                 name="email"
                                 placeholder="john@example.com"
                                 v-model="email"></b-input>
                    </b-form-group>

                    <!--                    <b-card no-body>-->
<!--                        <b-tabs card>-->
<!--                            <b-tab key="Participants" title="Participants" @click="currentGroup='Participants'">-->
<!--                                <b-card style="margin-top:10px;" :key="user.profile_id" v-for="user in participants">-->
<!--                                    <b-row class="text-center" align-v="center">-->
<!--                                        <b-col class="text-center">-->
<!--                                            {{ user.full_name }}-->
<!--                                        </b-col>-->
<!--                                    </b-row>-->
<!--                                </b-card>-->
<!--                            </b-tab>-->
<!--                            <b-tab key="Organisers" title="Organisers" @click="currentGroup='Organisers'">-->
<!--                                <b-card style="margin-top:10px;" :key="user.profile_id" v-for="user in organisers">-->
<!--                                    <b-row class="text-center" align-v="center">-->
<!--                                        <b-col class="text-center">-->
<!--                                            {{ user.full_name }}-->
<!--                                        </b-col>-->
<!--                                    </b-row>-->
<!--                                </b-card>-->
<!--                            </b-tab>-->
<!--                            <b-tab key="Participants" title="Accessors" @click="currentGroup='Participants'">-->
<!--                                <b-card style="margin-top:10px;" :key="user.profile_id" v-for="user in accessors">-->
<!--                                    <b-row class="text-center" align-v="center">-->
<!--                                        <b-col class="text-center">-->
<!--                                            {{ user.full_name }}-->
<!--                                        </b-col>-->
<!--                                        <b-col class="text-center">-->
<!--                                            <b-button variant="danger" @click="deleteUser(user.profile_id)">Delete</b-button>-->

<!--                                        </b-col>-->
<!--                                    </b-row>-->
<!--                                </b-card>-->
<!--                            </b-tab>-->
<!--                        </b-tabs>-->
<!--                    </b-card>-->


                </div>

            </b-modal>
        </div>

        <div v-else>
            <label>Select sharing</label>
            <b-form-select v-model=selected :options="options" size="sm" @change="emitInputToParent"/>
        </div>
    </div>
</template>

<script>
    import api from '@/Api'
    export default {

        name: "ShareActivity",
        props: {
            profileId: String,
            activityId: String,
            modal: Boolean,
            visibility: String
        },
        data() {
            return {
                // selectedVisibility: null,
                selected: 'public',
                options: [
                    { value: 'Private', text: 'Private' },
                    // { value: 'restricted', text: 'Restricted' },
                    { value: 'Public', text: 'Public' }
                ],
                organisers: [],
                participants: [],
                accessors: [],
                followers: [],
            }
        },
        methods: {
            emitInputToParent() {
                this.$emit('emitInput', this.selected);
            },
            async updateSelectedValue() {
                this.selected = this.visibility
                await api.getActivityMembers(this.activityId)
                    .then((res) => {
                        this.organisers = res.data.Organiser;
                        this.participants = res.data.Participant;
                        this.accessors = res.data.Access;
                        this.followers = res.data.Follower;
                    })
                    .catch(err => {
                        console.log(err)
                    });
            },
            deleteUser(id) {
                alert(id)

            },

            changeVisibilityType() {

                let data = {
                    visibility: this.selected.toLowerCase(),
                }
                const currentObj = this;
                api.updateActivityVisibility(this.profileId, this.activityId, data)
                    .then(function () {
                        currentObj.visibility = currentObj.selected
                    })
                    .catch(function (error) {
                        alert(error)
                        // console.log(error);
                    });

            }
        },
        computed:{


        }
    }
</script>
