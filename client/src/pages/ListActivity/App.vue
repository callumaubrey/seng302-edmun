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
            <b-table striped hover :items="items" :fields="fields"></b-table>
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
                userName: '',
                items: [],
                fields: ['activityName', 'description', 'activityTypes']
            }
        },
        methods: {
            getUserIdAndActivities: function () {
                let currentObj = this;
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/id')
                    .then(function (response) {
                        currentObj.profileId = response.data;
                    })
                    .catch(function (err) {
                        alert(err);
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
            getActivities: function (id) {
                this.axios.defaults.withCredentials = true;
                this.axios.get('http://localhost:9499/profiles/' + id + '/activities')
                    .then((res) => {
                        this.items = res.data;
                        for (let i = 0; i < res.data.length; i++) {
                            this.items[i].activityTypes = this.items[i].activityTypes.join(", ");
                        }
                    })
                    .catch(err => console.log(err))
            }
        },
        mounted() {
            this.getUserIdAndActivities();
            this.getName();
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

</style>