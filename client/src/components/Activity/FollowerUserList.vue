<template>
    <div>
        <!-- Search Bar -->
        <b-input-group class="mt-2 mb-4">
            <template v-slot:prepend>
                <b-input-group-text>
                    <b-icon-search></b-icon-search>
                </b-input-group-text>
            </template>
            <b-input @change="runSearchQuery" id="search" v-model="searchQuery"></b-input>
        </b-input-group>


        <!-- Grouped Tabs -->
        <b-card no-body>
            <b-tabs card>
                <b-tab :key="group.name" :title="group.name" v-for="group in userGroups">
                    <b-table :fields="fields" :items="group.users">
                        <template v-slot:cell(name)="row">
                            {{ row.item.name }}
                        </template>

                        <template v-slot:cell(action)="">
                            <b-dropdown class="m-md-2" id="dropdown-1" text="Participant">
                                <b-dropdown-item>Organiser</b-dropdown-item>
                            </b-dropdown>
                        </template>
                    </b-table>
                </b-tab>
            </b-tabs>
        </b-card>

    </div>
</template>

<script>

    export default {
        name: "FollowerUserList",

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
                fields: ["Name", "Action"],
                searchQuery: '',
                userGroups: [],
                activityRoles: ["Participant", "Organizer"]
            }
        },
        mounted() {
            this.loadData()
        },

        // Component Methods
        methods: {
            loadData: function() {
                // TODO(Connor): Replace with API queries

                this.userGroups = [];
                this.loadDummyData()
            },

            runSearchQuery: function() {
                if (this.searchQuery.length > 0) {
                    // TODO(Connor): Run axios query and fill user_groups
                    this.userGroups = [];
                    this.userGroups.push({name: 'Search Results', users: []});
                } else {
                    this.loadData();
                }
            },

            loadDummyData: function () {
                this.userGroups.push({
                    name: 'Participants',
                    users: [
                        {name: 'Connor Macdonald'},
                        {name: 'John Johnson'},
                        {name: 'Terry Tabla'},
                        {name: 'Mark Jeremy'},
                        {name: 'Tom Thompson'}
                    ]
                });
                this.userGroups.push({
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