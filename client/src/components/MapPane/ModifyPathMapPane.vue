<template>
    <map-pane @onMapClick="mapClicked" ref="map" ></map-pane>
</template>

<script>
    import MapPane from "./MapPane";
    export default {
        name: "RecordActivityResultModal",
        components: {
            MapPane
        },
        props: ['profileId', 'activityId', 'loggedInId'],
        data() {
            return {}
        },
        methods: {
            mapClicked(event) {
                this.$emit('waypointClicked', this.center)
                this.$refs.map.routePoints.push([event.latlng.lat, event.latlng.lng])
            }
        }
    }

</script>




<style scoped>

</style>