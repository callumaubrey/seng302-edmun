<template>
    <div id="app" v-if="isLoggedIn">
        <NavBar v-bind:isLoggedIn="isLoggedIn" v-bind:userName="userName"></NavBar>
        <b-container>
            <ActivityList
                    :items="this.items"
                    :profileId="this.profileId"
                    :fields="this.fields"
                    :tableIsLoading="false"
            >
                <h3>Activities</h3>
                <p>Activity Results with the tag: {{ this.$route.params.hashtag }}</p>
            </ActivityList>
        </b-container>
    </div>
</template>

<script>
    import NavBar from '@/components/NavBar.vue';
    import ActivityList from '@/components/ActivityList.vue';
    import axios from "axios";

    export default {
        components: {
            NavBar,
            ActivityList
        },
        data() {
            return {
                isLoggedIn: false,
                userName: '',
                profileId: null,
                items: [],
                fields: ['activityName', 'description', 'location', 'startTime', 'endTime', 'activityTypes']
            }
        },
        methods: {
            getUserId: function () {
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/profiles/id')
                    .then((res) => {
                        this.profileId = res.data;
                        this.isLoggedIn = true;
                    })
                    .catch((err) => console.log(err));
            },
            getUserName: function () {
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/profiles/firstname')
                    .then((res) => {
                        this.userName = res.data;
                    })
                    .catch(err => console.log(err));
            },
            getActivities: function () {
                let hashTag = this.$route.params.hashtag;
                if (!hashTag) {
                    return;
                }

                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/hashtag/' + hashTag)
                    .then((res) => {
                        this.items = res.data;
                        for (let i = 0; i < res.data.length; i++) {
                            if (this.items[i].activityTypes !== null) {
                                this.items[i].activityTypes = this.items[i].activityTypes.join(", ");
                            }
                            if (this.items[i].location !== null) {
                                this.items[i].location = this.items[i].location.city + " " + this.items[i].location.state + ", " + this.items[i].location.country;
                            }
                        }
                    })
                    .catch(err => console.log(err));
            }
        },
        mounted() {
            this.getUserId();
            this.getUserName();
            this.getActivities();
        },
        watch: {
            '$route.params.hashtag' () {
                this.getActivities();
            }
        }
    }
</script>