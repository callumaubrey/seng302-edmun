<template>
  <b-row class="mb-5">
    <b-col style="padding: 0em; max-width: 30%; background: whitesmoke; margin-top: 8px">
      <PathInfo ref="pathInfo" :path="newPath" ></PathInfo>
    </b-col>
    <b-col style="padding: 0em">
      <ModifyPathMapPane ref="path_editor" v-on:pathEdited="pathEdited" ></ModifyPathMapPane>
    </b-col>
  </b-row>

</template>

<script>
  import ModifyPathMapPane from "./ModifyPathMapPane";
  import PathInfo from "./PathInfo";
  export default {
    name: "PathInfoMapCreateEdit",
    components: {PathInfo, ModifyPathMapPane},
    props: {
      activityId: {
        type: Number,
        default: null
      },
      profileId: {
        type: Number,
        default: null
      },
      path: {
        type: Object,
        default: null
      },
    },

      data: function () {
      return {
        pathKeypointSelected: null,
        newPath: null
      }
    },
    methods: {
      markerClicked: function (id) {
        this.pathKeypointSelected = id;
      },
      updatePath: function() {
        return this.$refs.path_editor.updatePathInActivity(this.profileId, this.activityId);
      },
      loadActivityPath: function() {
        this.$refs.path_editor.getPathFromActivity(this.profileId, this.activityId);
      },
      pathEdited: function() {
        this.newPath = this.$refs.path_editor.getUpdatedPathObject()
      },

    },
    mounted() {
      if (this.activityId != null) {
        this.loadActivityPath()
      }
      this.newPath = this.path
    }


  }
</script>

<style scoped>

</style>