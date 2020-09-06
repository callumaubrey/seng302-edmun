<template>
    <div>
        <map-pane ref="map" :can-hide="false" :displayCircle="true" @onMapClick="mapClicked"></map-pane>
    </div>
</template>

<script>
    import MapPane from "./MapPane";
    export default {
        name: "SearchLocationMapPane",
        components: {MapPane},
        props: {
            radius: {
                type: Number,
                default: null
            },
            center: {
                type: Array,
                default: null
            },
            activities: {
                type: Array,
                default: null
            }
        },
        data: function () {
            return {
            }
        },
        methods: {
            /**Adds markers to the map to be called whenever
             * activities on page are changed (after each API call)
             */
            addMarkers(){
                for (const activity of this.activities){
                    this.$refs.map.createMarker(activity.id, 2, activity.location.latitude, activity.location.longitude, activity.description, activity.name, true)
                }
            },
            /**Updates the circle overlay, center(lat, long) and radius
             * To be called whenever the radius slider changes
             */
            updateCircle(){
                this.$refs.map.updateCircle(this.center[0], this.center[1], this.radius)
            },
            /**
             * When the map is clicked the center of the circle is changed and the circle is updated
             * and then the new center is emitted for use in activity search query
             * @param event the map click event
             */
            mapClicked(event) {
                this.center = [event.latlng.lat, event.latlng.lng]
                this.updateCircle()
                this.$emit('locationChange', this.center)
            }
        },
        mounted() {
            this.updateCircle()
            this.addMarkers()
        }
    }
</script>

<style scoped>

</style>