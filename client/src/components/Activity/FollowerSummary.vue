<template>
    <p>
        <span v-for="(group, group_index) in user_groups" :key="group.name">
            {{group.count}} {{group.name}}
            <span v-if="group_index !== user_groups.length - 1">
                |
            </span>
        </span>
    </p>
</template>

<script>
    export default {
        name: "FollowerSummary.vue",

        // Component Properties
        props: {
            activityId: {
                type: Number,
                default: null,
            },
        },

        // Component Members
        data() {
            return {
                user_groups: []
            }
        },

        mounted() {
            this.loadFollowerSummary()
        },

        // Component Methods
        methods: {
            loadFollowerSummary: function () {
                // TODO(Connor): Implement API call to get summary of followers
                // NOTE(Connor): This should have the limitation of not showing organisers for limited rights
                // This should be implemented in the backend since it is a security issue

                // Temporary replacement. Delete this function when replaced
                this.loadDummyData()
            },

            loadDummyData: function (){
                this.user_groups.push({
                    "name": "Participants",
                    "count": 200
                });
                this.user_groups.push({
                    "name": "Followers",
                    "count": 115
                });
                this.user_groups.push({
                    "name": "Organizers",
                    "count": 10
                });
            }
        }
    }
</script>

<style scoped>

</style>