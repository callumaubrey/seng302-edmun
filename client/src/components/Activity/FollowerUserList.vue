<template>
    <div>
        <!-- Header -->
        <FollowerSummary :activity-id="activityId"></FollowerSummary>

        <!-- Search Bar -->
        <b-input-group class="mt-2 mb-4">
            <template v-slot:prepend>
                <b-input-group-text>
                    <b-icon-search></b-icon-search>
                </b-input-group-text>
            </template>
            <b-input id="search" v-model="search_query" @change="runSearchQuery"></b-input>
        </b-input-group>

        <!-- Grouped Tabs -->
        <b-card no-body>
            <b-tabs card>
                <b-tab v-for="group in user_groups" :title="group.name" :key="group.name">
                    <b-table :items="group.users"></b-table>
                </b-tab>
            </b-tabs>
        </b-card>

    </div>
</template>

<script>
    import FollowerSummary from './FollowerSummary'

    export default {
        name: "FollowerUserList",
        components: {FollowerSummary},

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
                search_query: '',
                user_groups: []
            }
        },

        mounted() {
            this.loadData()
        },

        // Component Methods
        methods: {
            loadData: function() {
                // TODO(Connor): Replace with API queries

                this.user_groups = [];
                this.loadDummyData()
            },

            runSearchQuery: function() {
                if (this.search_query.length > 0) {
                    // TODO(Connor): Run axios query and fill user_groups
                    this.user_groups = [];
                    this.user_groups.push({name: 'Search Results', users: []});
                } else {
                    this.loadData();
                }
            },

            loadDummyData: function () {
                this.user_groups.push({
                    name: 'Participants',
                    users: [
                            {name: 'Connor Macdonald'},
                            {name: 'John Johnson'},
                            {name: 'Terry Tabla'},
                            {name: 'Mark Jeremy'},
                            {name: 'Tom Thompson'}
                        ]
                });
                this.user_groups.push({
                    name: 'Organisers',
                    users: [
                        {name: 'Jimmy Jeffery'}
                    ]
                });
            }
        }
    }
</script>

<style scoped>

</style>