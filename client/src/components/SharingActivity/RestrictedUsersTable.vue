<template>
  <div>
    {{ data.users }}
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
        {{ row }}
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
    props: ['roleData', 'activityRoles', 'fields'],
    data() {
      return {
        data: {
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
      console.log("TABLE")
      console.log(this.roleData)
      this.data = this.roleData;
    }
  }
</script>

<style scoped>

</style>