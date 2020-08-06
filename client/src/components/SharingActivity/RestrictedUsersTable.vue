<template>
  <div>
    <b-table :items="data.users" :fields="fields">
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
        <b-form-select v-model="row.item.role" :options="activityRoles" v-on:change="emitToParent"></b-form-select>
      </template>
    </b-table>

    <b-button id="select-all-button" v-on:click="selectAll(data.users)" style="margin-right: 5px">
      Select All
    </b-button>

    <b-button id="deselect-all-button" v-on:click="deselectAll(data.users)">
      Deselect All
    </b-button>
  </div>
</template>

<script>
  export default {
    name: "RestrictedUsersTable",
    props: ['roleData', 'activityRoles'],
    data() {
      return {
        // fields: ['name', 'email', 'role', 'selected'],
        fields: [
          {
            key: 'full_name',
            label: 'Name',
            sortable: true
          },
          {
            key: 'primary_email',
            label: 'Primary Email',
            sortable: true
          },
          {
            key: 'role'
          },
          {
            key: 'selected'
          }
        ],
        data: this.roleData
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
        this.emitToParent();
      },
      /**
       * Checks the selected check box for all users
       * @param users all the users in the table
       */
      selectAll: function (users) {
        let i;
        let len = users.length
        for (i = 0; i < len; i++) {
          let user = users[i]
          user.selected = true;
          user._rowVariant = 'none';
        }
        this.emitToParent();
      },
      /**
       * Unchecks the selected check box for all users
       * @param users all the users in the table
       */
      deselectAll: function (users) {
        let i;
        let len = users.length
        for (i = 0; i < len; i++) {
          let user = users[i]
          user.selected = false;
          user._rowVariant = 'danger';
        }
        this.emitToParent();
      },
      /**
       * Emits changes to the parent whenever this function is called.
       * This function should be called whenever data is changed.
       */
      emitToParent () {
        this.$emit('childToParent', this.data)
      }
    }
  }
</script>

<style scoped>

</style>