<template>
  <b-row class="mb-5">
    <b-col style="padding: 0em; max-width: 30%; background: whitesmoke; margin-top: 8px">
      <PathInfo ref="pathInfo" :path="path" :selected-keypoint="pathKeypointSelected" v-on:selected="selectedKeyPoint"></PathInfo>
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
        type: String,
        default: null
      },
      profileId: {
        type: Number,
        default: null
      },
      path: {
        type: Object,
        default: function () {
          return {}
        }
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
        this.$refs.path_editor.getPathFromActivity(this.profileId, this.activityId, true);
      },
      pathEdited: function() {
        this.path = this.$refs.path_editor.getUpdatedPathObject()
      },
      selectedKeyPoint: function(index) {
        this.pathKeypointSelected = index
        this.$refs.path_editor.setMapCenterFromIndex(index)
      },
      updateActivity: function(profileId, activityId) {
        return this.$refs.path_editor.updatePathInActivity(profileId, activityId)
      },
      refresh: function () {
          this.$refs.path_editor.refreshMap()
      }
    },
    mounted() {
      if (this.activityId != null) {
        this.loadActivityPath()
      }
    }


  }
</script>

<style scoped>

</style>