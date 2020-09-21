<template>
  <b-row class="mb-5">
    <b-col style="padding: 0em; max-width: 30%; background: whitesmoke; margin-top: 8px">
      <PathInfo ref="pathInfo" :path="path" ></PathInfo>
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
        pathKeypointSelected: null
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
        this.path = this.$refs.path_editor.getPathObject()
      },

    },
    watch: {
      // pathKeypointSelected: function () {
      //   let keypoint = this.path.locations[this.pathKeypointSelected];
      //   this.$refs.pathMapPane.setMapCenter(keypoint.latitude, keypoint.longitude);
      // }
    },
    mounted() {
      // if(this.path) {
      //   this.$refs.pathMapPane.setMapCenter(this.path.locations[0].latitude, this.path.locations[0].longitude);
      //   this.$refs.pathMapPane.setPath(this.path, true, false);
      // }
      if (this.activityId != null) {
        this.loadActivityPath()

      }
    }


  }
</script>

<style scoped>

</style>