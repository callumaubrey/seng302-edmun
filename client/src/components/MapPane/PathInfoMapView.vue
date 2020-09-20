<template>
    <b-row class="mb-5">
        <b-col style="padding: 0em; max-width: 30%; background: whitesmoke; margin-top: 8px">
            <PathInfo ref="pathInfo" :path="path" v-model="pathKeypointSelected"></PathInfo>
        </b-col>
        <b-col style="padding: 0em">
            <map-pane ref="pathMapPane" :can-hide="false" @markerSelected="markerClicked"></map-pane>
        </b-col>
    </b-row>
</template>

<script>
    import PathInfo from "./PathInfo";
    import MapPane from "./MapPane";

    export default {
        name: "PathInfoMapView",
        components: {MapPane, PathInfo},
        props: {
            path: Object
        },
        data: function () {
            return {
                pathKeypointSelected: null
            }
        },
        methods: {
            markerClicked: function (id) {
                this.pathKeypointSelected = id;
            }
        },
        watch: {
            pathKeypointSelected: function () {
                let keypoint = this.path.locations[this.pathKeypointSelected];
                this.$refs.pathMapPane.setMapCenter(keypoint.latitude, keypoint.longitude);
            }
        },
        mounted() {
            if(this.path) {
                this.$refs.pathMapPane.setMapCenter(this.path.locations[0].latitude, this.path.locations[0].longitude);
                this.$refs.pathMapPane.setPath(this.path, true, false);
            }
        }
    }
</script>

<style scoped>

</style>