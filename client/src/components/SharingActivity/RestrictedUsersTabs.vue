<template>
    <div>
        <b-card no-body>
            <b-tabs card>
                <b-tab key="Organisers" title="Organisers" active>
                    <restricted-users-table v-on:childToParent="updateOrganisers" :role-data="organisers" :activity-roles="activityRoles"></restricted-users-table>
                </b-tab>

                <b-tab key="Participants" title="Participants">
                    <restricted-users-table v-on:childToParent="updateParticipants" :role-data="participants" :activity-roles="activityRoles"></restricted-users-table>
                </b-tab>

                <b-tab key="Followers" title="Followers">
                    <restricted-users-table v-on:childToParent="updateFollowers" :role-data="followers" :activity-roles="activityRoles"></restricted-users-table>
                </b-tab>

                <b-tab key="Accessors" title="Accessors">
                    <restricted-users-table v-on:childToParent="updateAccessors" :role-data="accessors" :activity-roles="activityRoles"></restricted-users-table>
                </b-tab>
            </b-tabs>
        </b-card>

    </div>
</template>

<script>
    import RestrictedUsersTable from "./RestrictedUsersTable";
    export default {
        name: "ShareActivityUserList",
        components: {
            RestrictedUsersTable
        },
        data() {
            return {
                organisers: {
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
             * Updates the organisers from changes in the child component
             * @param value the updated data from the child component
             */
            updateOrganisers (value) {
                this.organisers = value
            },
            /**
             * Updates the participants from changes in the child component
             * @param value the updated data from the child component
             */
            updateParticipants (value) {
                this.participants = value
            },
            /**
             * Updates the followers from changes in the child component
             * @param value the updated data from the child component
             */
            updateFollowers (value) {
                this.followers = value
            },
            /**
             * Updates the accessors from changes in the child component
             * @param value the updated data from the child component
             */
            updateAccessors (value) {
                this.accessors = value
            }
        }
    }
</script>

<style scoped>
</style>