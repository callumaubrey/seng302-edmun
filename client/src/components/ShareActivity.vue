<template>
    <div>
        <div v-if="modal == true">
            <b-button v-b-modal.modal-1 v-if="visibility == 'Public'" variant="success">{{visibility}}</b-button>
            <b-button v-b-modal.modal-1 v-if="visibility == 'Restricted'" variant="warning">{{visibility}}</b-button>
            <b-button v-b-modal.modal-1 v-if="visibility == 'Private'" variant="danger">{{visibility}}</b-button>

            <b-modal size="xl" style="padding: 1em" id="modal-1" title="Share" hide-footer hide-header @show="updateSelectedValue" @ok="changeVisibilityType" body-class="p-0" >
                <b-button-close style="padding: 10px" @click="$bvModal.hide('modal-1')"></b-button-close>
                <b-row>
                    <b-col style="color: white; background: #4d4d4d; max-width: 40%; horiz-align: center">
                        <h4>Select Sharing Option</h4>
                        <div style="margin-top: 30%; font-size: small">
                            <p v-if="selected == 'Public'">Anyone will be able to view this activity.</p>
                            <p v-if="selected == 'Private'">Only you will be able to view this activity.</p>
                            <p v-if="selected == 'Restricted'">Only people you allow will be able to view this activity.</p>
                        </div>
                        <b-form-select style="margin-bottom: 40%;" v-model="selected" :options="options" size="sm"></b-form-select>
                        <b-form-group v-if="selected =='Restricted'" style="color: white" for="emailInput">
                            <p style="font-size: small">Maximum of 5 emails allowed. Add multiple members by separating emails by space or semi-colon</p>
                            <label>Input member emails</label>
                            <b-input type="email"
                                     name="email"
                                     id="emailInput"
                                     placeholder="john@example.com kevin@example.com"
                                     v-model="emailInput"
                                     ></b-input>
                            <label v-if="visibility != selected">Are you sure you want to change visibility, press OK to continue
                                This will effect:</label>
                            <p style="color: #cc9a9a">{{followers.length}} Followers {{participants.length}} Participants {{organisers.length}} Organisers</p>
                        </b-form-group>
                        <b-button style="margin: 15px" @click="submit(), $bvModal.hide('modal-1')">Save</b-button>
                    </b-col>

                    <b-col style="size: auto">
                        <br>
                        <follower-user-list :activity-id=activityId :logged-in-id=profileId :activity-creator-id="profileId" style="size: auto"></follower-user-list>
                    </b-col>
                </b-row>






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




            </b-modal>
        </div>
    </div>
</template>

<script>
    import api from '@/Api'
    import FollowerUserList from "./Activity/FollowerUserList";
    export default {

        name: "ShareActivity",
        components: {FollowerUserList},
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
                description: 'Reeee',
                emailInput: '',
                options: [
                    { value: 'Private', text: 'Private' },
                     { value: 'Restricted', text: 'Restricted' },
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
                await api.getActivityMembers(this.activityId, 0, 10)
                    .then((res) => {
                        this.organisers = res.data.Organiser;
                        this.participants = res.data.Participant;
                        this.accessors = res.data.Access;
                        this.followers = res.data.Follower;
                        console.log(this.organisers.length)
                    })
                    .catch(err => {
                        console.log(err)
                    });
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

            },
            submit() {
                this.parseEmailInput();
                this.changeVisibilityType()
            },
            parseEmailInput() {
                var entryArray
                let data = this.emailInput
                if (data.includes(";")){
                    entryArray = data.split(';')
                } else {
                    entryArray = data.split(' ')
                }
                this.email = entryArray
                // for (const address of this.email){
                //     api.ShareActivity()
                // }
            },
            beforeMount() {
                this.updateSelectedValue()
            }
        },
        computed:{

        }
    }
</script>
