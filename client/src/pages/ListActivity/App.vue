<template>
    <div id="app">
        <NavBar v-bind:isLoggedIn="isLoggedIn()"></NavBar>

        <b-container v-if="isLoggedIn()">
            <h3>My Activities</h3>
            <b-form-text class="mb-3">Click on an activity to view more information on the activity</b-form-text>
            <ActivityList :user_id="parseInt(this.$route.params.id)"></ActivityList>
        </b-container>

        <b-container v-else>
            <h1 class="text-center text-danger">You must be logged in to view this.</h1>
        </b-container>
    </div>

</template>

<script>
    import NavBar from "@/components/NavBar.vue"
    import ActivityList from "@/components/Activity/ActivityList.vue";
    import {mutations} from "../../store";


    const List = {
        name: 'List',
        components: {
            NavBar,
            ActivityList
        },
        methods: {
            isLoggedIn: function () {
                return mutations.isLoggedIn();
            }
        }
    };
    export default List
</script>

<style scoped>

</style>