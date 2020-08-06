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
        <b-form-select v-model="row.item.role" :options="activityRoles"
                       v-on:change="rowChanged(row)"></b-form-select>
      </template>
    </b-table>

    <b-button id="select-all-button" v-on:click="selectAll" style="margin-right: 5px">
      Select All
    </b-button>

    <b-button id="deselect-all-button" v-on:click="deselectAll">
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
        data: {
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

        }
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
        this.rowChanged(row)
      },
      selectAll: function () {
        this.$emit('selectAll')
      },
      deselectAll: function () {
        this.$emit('deselectAll')
      },
      rowChanged(row) {

        this.$emit('rowChanged', row)
      }
    },
    beforeMount() {
      this.data = this.roleData;
    }
  }
</script>

<style scoped>

</style>