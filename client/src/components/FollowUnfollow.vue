<template>
    <div>
        <b-row style="margin-top:0.5rem;">
            <b-button v-if="displayFollowButton && !isSubscribed" @click="followActivity" variant="success"
                      style="margin: 10px">Follow
            </b-button>
            <b-button v-if="displayFollowButton && isSubscribed" @click="unfollowActivity" variant="danger"
                      style="margin: 10px">Unfollow
            </b-button>
            <b-button v-if="displayGoingButton && !isGoing" @click="setGoing" variant="success" style="margin: 10px">
                Going
            </b-button>
            <b-button v-if="displayGoingButton && isGoing" @click="setNotGoing" variant="danger" style="margin: 10px">
                Not Going
            </b-button>
        </b-row>
    </div>

</template>

<script>
    import api from '@/Api';
    import {store} from "../store";

    const FollowUnfollow = {
        name: "FollowUnfollow",
        components: {},
        props: {
            activityId: {
                type: Number,
                default: null
            },
            activityOwnerId: {
                type: Number,
                default: null
            },
            loggedInId: {
                type: Number,
                default: null
            },
        },
        data: function () {
            return {
                displayFollowButton: Boolean,
                displayGoingButton: Boolean,
                isSubscribed: Boolean,
                isGoing: Boolean
            }
        },
        async beforeMount() {
            this.displayGoingButton = false
            this.displayFollowButton = false
            this.getIsGoing()
            this.getCanFollow();
        },
        methods: {
            getCanFollow: async function () {
                if (this.loggedInId != this.activityOwnerId) {
                    await api.getIsSubscribed(this.loggedInId, this.activityId)
                        .then((res) => {
                            this.isSubscribed = res.data.subscribed;
                        })
                        .catch(() => {
                            console.log('500 error')
                        });
                } else {
                    this.displayButton = false;
                }
            },
            /**
             * Calls getIsParticipating endpoint which should return a boolean if successful. The boolean
             * is if the user is participating in the activity. This is used for displaying the going button
             * and following status
             * @returns {Promise<void>}
             */
            getIsGoing: async function () {
                this.update
                await api.getIsParticipating(this.loggedInId, this.activityId)
                    .then((res) => {
                        if (res.data.participant == "null") {
                            this.displayFollowButton = false
                            this.displayGoingButton = false
                        }
                        if (res.data.participant == "false") {
                            this.displayFollowButton = true
                            this.displayGoingButton = true
                            this.isGoing = false
                            this.isSubscribed = true
                        }
                        if (res.data.participant == "true") {
                            this.displayFollowButton = false
                            this.displayGoingButton = true
                            this.isGoing = true
                            this.isSubscribed = false
                        }

                    })
                    .catch(() => {
                        console.log('500 error')
                    });
            },
            followActivity: async function () {
                await api.subscribeToActivity(this.loggedInId, this.activityId)
                    .then(() => {
                        this.getCanFollow();
                        this.$emit("activityFollowed");
                        store.newNotification("Activity Followed", "success", 2)
                    })
                    .catch((err) => {
                        alert(err.body)
                    });
            },

            unfollowActivity: async function () {
                await api.unsubscribeToActivity(this.loggedInId, this.activityId)
                    .then(() => {
                        store.newNotification("Activity Unfollowed", "success", 2)
                        this.getCanFollow();
                        this.$emit("activityFollowed");
                    })
                    .catch((err) => {
                        alert(err.body)
                    });
            },

            /**
             * Sets a user to participant if they are able to be. Updates displaying
             * and function of the follow/unFollow and going/notGoing buttons. If error
             * then logs in console
             */
            setGoing: async function () {
                await api.setActivityRoleParticipant(this.loggedInId, this.activityId)
                    .then(() => {
                        this.displayFollowButton = false
                        this.displayGoingButton = true
                        this.isGoing = true
                        this.isSubscribed = true
                        store.newNotification("Activity Participation Updated", "success", 2)
                        this.$emit("activityFollowed");
                    })
                    .catch((err) => {
                        console.log(err)
                    });
            },

            /**
             * Demotes a user from participant to follower and sets variables for the displaying
             * and function of the follow/unFollow and going/notGoing buttons
             * @returns {Promise<void>}
             */
            setNotGoing: async function () {
                await api.deleteActivityRoleParticipant(this.loggedInId, this.activityId)
                    .then(() => {
                        store.newNotification("Activity Participation Updated", "success", 2)
                        this.displayFollowButton = true
                        this.displayGoingButton = true
                        this.isGoing = false
                        this.$emit("activityFollowed");
                    })
                    .catch((err) => {
                        alert(err.body)
                    });
            },
        },
    };
    export default FollowUnfollow

</script>
