<template>
    <div>
        <b-row>
            <b-button v-if="displayButton && !isSubscribed" @click="followActivity" variant="success" style="margin: 10px">Follow</b-button>
            <b-button v-if="displayButton && isSubscribed" @click="unfollowActivity" variant="danger" style="margin: 10px">Unfollow</b-button>
            <b-button v-if="!isGoing" @click="setGoing" variant="success" style="margin: 10px">Going</b-button>
            <b-button v-if="isGoing" @click="setNotGoing" variant="danger" style="margin: 10px">Not Going</b-button>
        </b-row>
    </div>

</template>

<script>
    import api from '@/Api';

    const FollowUnfollow = {
        name: "FollowUnfollow",
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
                displayButton: true,
                isSubscribed: false,
                isGoing: false
            }
        },
        async mounted() {
            await this.getCanFollow();
        },
        methods: {
            getCanFollow: async function() {
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
            getIsGoing: function() {
                // TODO add api call to see if logged in user is going already
            },
            followActivity: async function() {
                await api.subscribeToActivity(this.loggedInId, this.activityId)
                    .then(() => {
                        this.getCanFollow();
                    })
                    .catch((err) => {
                        alert(err.body)
                    });
            },
            unfollowActivity: async function() {
                await api.unsubscribeToActivity(this.loggedInId, this.activityId)
                    .then(() => {
                        this.getCanFollow();
                    })
                    .catch((err) => {
                        alert(err.body)
                    });
            },
            setGoing: function() {
                // TODO add api call to set user to going and also to follow if not already
            },
            setNotGoing: function() {
                // TODO add api call to set user not going
            }
        }
    };
    export default FollowUnfollow

</script>
