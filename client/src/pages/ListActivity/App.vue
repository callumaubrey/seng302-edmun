<template>
    <div id="app">
        <NavBar isLoggedIn=true v-bind:userName="userName"></NavBar>
        <b-container>
            <b-row class="my-1">
                <b-col sm="11">
                    <h3> List of Activitys</h3>
                </b-col>
                <b-col sm="1">
                    <b-button align="right" to="/activity/new"> Create</b-button>
                </b-col>
            </b-row>
            <b-table striped hover :items="items"></b-table>
        </b-container>
    </div>

</template>

<script>
    import NavBar from "@/components/NavBar.vue"
    // import axios from 'axios'

    const List = {
        name: 'List',
        components: {
            NavBar
        },
        data: function () {
            return {
                profileId: null,
                isLoggedIn: true,
                userName: 'Richard Lobb',
                items: [
                    { Name: "Triathalon", Description: 'Had a run round', ActivityTypes: 'Hike, Bike', StartTime: "12/12/20 5:00am", EndTime:"12/12/20 10:00am" },
                    { Name: "Surfing", Description: 'Had a surf round', ActivityTypes: 'Swim, Surf', StartTime: "16/11/20 8:00am", EndTime:"16/11/20 10:00am" },
                ]
            }
        }, methods: {
            getUserId: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/id')
                    .then(function (response) {
                        currentObj.profile_id = response.data;
                    })
                    .catch(function () {
                    });
            },
            getName: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/user')
                    .then(function (response) {
                        currentObj.userName = response.data;
                    })
                    .catch(function () {
                    });
            },
        }, mounted: function() {
            this.getUserId();
            this.getName();

        }
    }
    export default List
</script>

<style scoped>

</style>