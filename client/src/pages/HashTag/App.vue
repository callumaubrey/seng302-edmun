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
                items: [
                    { name: 'Test', description: 'a description', location: 'NZ', start_date: '2020-10-10', end_date: '2020-10-10', activity_types: 'Run'}
                ],
                fields: ['name', 'description', 'location', 'start_date', 'end_date', 'activity_types']
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
        },
        mounted() {
            this.getUserId();
            this.getUserName();
        }
    }
</script>