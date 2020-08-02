<template>
    <div>
        <b-card no-body>
            <b-tabs card>

                <b-tab key="Organisers" title="Organisers" active class="tabs">
                    <b-table :items="organisers.users" :fields="organisers.fields">
                        <!-- This v-slot deals with deciding to keep the user or not-->
                        <template v-slot:cell(selected)="row">
                            <b-form-checkbox
                                    v-model="row.item.selected"
                                    v-on:change.native="rowColor(row)"
                            >
                            </b-form-checkbox>
                        </template>
                        <!-- This v-slot deals with selecting the role for the user-->
                        <template v-slot:cell(role)="row">
                            <b-form-select v-model="row.item.role" :options="activityRoles"></b-form-select>
                        </template>
                    </b-table>

                    <b-button v-on:click="selectAll(organisers.users)" style="margin-right: 5px">
                        Select All
                    </b-button>

                    <b-button v-on:click="deselectAll(organisers.users)">
                        Deselect All
                    </b-button>
                </b-tab>

                <b-tab key="Participants" title="Participants">
                    <b-table :items="participants.users" :fields="participants.fields">
                        <!-- This v-slot deals with deciding to keep the user or not-->
                        <template v-slot:cell(selected)="row">
                            <b-form-checkbox
                                    v-model="row.item.selected"
                                    v-on:change.native="rowColor(row)"
                            >
                            </b-form-checkbox>
                        </template>
                        <!-- This v-slot deals with selecting the role for the user-->
                        <template v-slot:cell(role)="row">
                            <b-form-select v-model="row.item.role" :options="activityRoles"></b-form-select>
                        </template>
                    </b-table>

                    <b-button v-on:click="selectAll(participants.users)" style="margin-right: 5px">
                        Select All
                    </b-button>

                    <b-button v-on:click="deselectAll(participants.users)">
                        Deselect All
                    </b-button>
                </b-tab>

                <b-tab key="Followers" title="Followers">
                    <b-table :items="followers.users" :fields="followers.fields">
                        <!-- This v-slot deals with deciding to keep the user or not-->
                        <template v-slot:cell(selected)="row">
                            <b-form-checkbox
                                    v-model="row.item.selected"
                                    v-on:change.native="rowColor(row)"
                            >
                            </b-form-checkbox>
                        </template>
                        <!-- This v-slot deals with selecting the role for the user-->
                        <template v-slot:cell(role)="row">
                            <b-form-select v-model="row.item.role" :options="activityRoles"></b-form-select>
                        </template>
                    </b-table>

                    <b-button v-on:click="selectAll(followers.users)" style="margin-right: 5px">
                        Select All
                    </b-button>

                    <b-button v-on:click="deselectAll(followers.users)">
                        Deselect All
                    </b-button>
                </b-tab>

                <b-tab key="Accessors" title="Accessors">
                    <b-table :items="accessors.users" :fields="accessors.fields">
                        <!-- This v-slot deals with deciding to keep the user or not-->
                        <template v-slot:cell(selected)="row">
                            <b-form-checkbox
                                    v-model="row.item.selected"
                                    v-on:change.native="rowColor(row)"
                            >
                            </b-form-checkbox>
                        </template>
                        <!-- This v-slot deals with selecting the role for the user-->
                        <template v-slot:cell(role)="row">
                            <b-form-select v-model="row.item.role" :options="activityRoles"></b-form-select>
                        </template>
                    </b-table>

                    <b-button v-on:click="selectAll(accessors.users)" style="margin-right: 5px">
                        Select All
                    </b-button>

                    <b-button v-on:click="deselectAll(accessors.users)">
                        Deselect All
                    </b-button>
                </b-tab>


            </b-tabs>
        </b-card>

    </div>
</template>

<script>
    export default {
        name: "ShareActivityUserList",
        data() {
            return {
                organisers: {
                    fields: ['name', 'email', 'role', 'selected'],
                    users: [
                        {
                            name: "John Doe",
                            email: "johndoe@doe.com",
                            role: "organiser",
                            selected: false,
                            _rowVariant: 'danger'
                        },
                        {
                            name: "James Smith",
                            email: "jamessmith@google.com",
                            role: "organiser",
                            selected: true,
                            _rowVariant: 'none'
                        },
                    ]
                },
                participants: [],
                accessors: [],
                followers: [],
                activityRoles: [
                    {value: "organiser", text: "Organiser"},
                    {value: "participant", text: "Participant"},
                    {value: "follower", text: "Follower"},
                    {value: "accessor", text: "Accessor"}
                ]
            }
        },
        methods: {
            /**
             * This function takes the given row and determines the colour of the row
             * @param row the row that is being changed
             */
            rowColor: function (row) {
                if (!row.item.selected) {
                    row.item._rowVariant = 'danger';
                } else {
                    row.item._rowVariant = 'none';
                }
            },
            selectAll: function (users) {
                let i;
                let len = users.length
                for (i = 0; i < len; i++) {
                    let user = users[i]
                    user.selected = true;
                    user._rowVariant = 'none';
                }
            },
            deselectAll: function (users) {
                let i;
                let len = users.length
                for (i = 0; i < len; i++) {
                    let user = users[i]
                    user.selected = false;
                    user._rowVariant = 'danger';
                }

            }
        }
    }
</script>

<style scoped>
    .tabs {
        min-height: 100%
    }


</style>