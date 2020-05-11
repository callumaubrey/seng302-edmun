<template>
    <div id="app" v-if="isLoggedIn">
        <NavBar v-bind:isLoggedIn="isLoggedIn"></NavBar>
        <b-container>
            <b-row class="my-1">
                <b-col sm="11">
                    <h3>{{profileName}}'s Activities</h3>
                    <b-form-text>Click on an activity to view more information on the activity</b-form-text>
                </b-col>
                <b-col sm="1">
                    <b-button v-if="profileId === loggedinId" align="right" @click="goToCreateActivity"> Create</b-button>
                </b-col>
            </b-row>
            <b-table hover :items="items" :fields="fields" class="clickable" @row-clicked="goToActivity" :busy="tableIsLoading">
                <template v-slot:cell(selectedActivity)></template>
                <template v-slot:table-busy>
                    <div class="text-center text-primary my-2">
                        <b-spinner class="align-middle"></b-spinner>
                        <strong> Loading...</strong>
                    </div>
                </template>
            </b-table>
        </b-container>
    </div>

</template>

<script>
    import NavBar from "@/components/NavBar.vue"
    import axios from 'axios'

    const List = {
        name: 'List',
        components: {
            NavBar
        },
        data: function () {
            return {
                tableIsLoading: true,
                profileId: null,
                isLoggedIn: false,
                loggedinId: null,
                profileName: '',
                userName: '',
                items: [],
                fields: ['activityName', 'description', 'activityTypes'],
                selectedActivity: []
            }
        },
        methods: {
            getLoggedInId: function () {
                let currentObj = this;
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/profiles/id')
                    .then(function (response) {
                        currentObj.loggedinId = response.data;
                    })
                    .catch (function (err) {
                        alert(err);
                    });
            },
            getProfileIdAndName: function () {
                let currentObj = this;
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/profiles/' + currentObj.$route.params.id)
                    .then(function (response) {
                        currentObj.profileId = response.data.id;
                        currentObj.profileName = response.data.firstname;
                    })
                    .catch(function (err) {
                        alert(err);
                    });
            },
            getLoggedInName: function () {
                let currentObj = this;
                axios.defaults.withCredentials = true;
                axios.get('http://localhost:9499/profiles/firstname')
                    .then(function (response) {
                        currentObj.userName = response.data;
                        currentObj.isLoggedIn = true;
                    })
                    .catch(function () {
                    });
            },
            getActivities: function (id) {
                axios.defaults.withCredentials = true;
                let vueObject = this;
                axios.get('http://localhost:9499/profiles/' + id + '/activities')
                    .then((res) => {
                        this.items = res.data;
                        for (let i = 0; i < res.data.length; i++) {
                            this.items[i].activityTypes = this.items[i].activityTypes.join(", ");
                        }
                        vueObject.tableIsLoading = false;
                    })
                    .catch(err => {
                        console.log(err);
                        vueObject.tableIsLoading = false;
                    })
            },
            goToCreateActivity: function() {
                // double check, if user somehow clicks create button when hidden.
                if (parseInt(this.profileId) === parseInt(this.loggedinId)) {
                    this.$router.push('/profiles/' + this.profileId + '/activities/create');
                }
            },
            goToActivity: function (items) {
                this.selectedActivity = items;
                const profileId = this.$route.params.id;
                let activityId = this.selectedActivity.id;
                this.$router.push('/profiles/' + profileId + '/activities/' + activityId);
            }
        },
        beforeMount() {
            this.getProfileIdAndName();
            this.getLoggedInId();
            this.getLoggedInName();
        },
        mounted() {
        },
        watch: {
            profileId: function (val) {
                if (val) {
                    this.getActivities(val);
                }
            }
        }
    }
    export default List
</script>

<style scoped>
    .clickable {
        cursor: pointer;
    }
</style>