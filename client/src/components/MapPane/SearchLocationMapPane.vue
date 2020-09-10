<template>
    <div class="h-100 w-100">
        <map-pane ref="map"
                  :maximise="true" :can-hide="false"
                  :displayCircle="displayCircle"
                  @userLocationUpdate="centerOnUserLocation"
                  @onMapClick="mapClicked"></map-pane>
    </div>
</template>

<script>
    import MapPane from "./MapPane";
    export default {
        name: "SearchLocationMapPane",
        components: {MapPane},
        props: {
            /**
             * The radius of the circle overlay measured in meters
             */
            radius: {
                type: Number,
                default: 50
            },
            /**
             * An array of [lat, lng] used for where to position and center the circle overlay
             */
            center: {
                type: Array,
                default: () => { return [-43.530629, 172.625955] }
            },
            /**
             * The activities to be displayed on the map
             */
            activities: {
                type: Array,
                default: null
            },
            /**
             * Show Circle on map
             **/
            displayCircle: {
                type: Boolean,
                default: false
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
                //TODO: change the content of the marker below to display the activities Start time and activity type instead of the description (Once u42-t3 is merged)
                this.$refs.map.clearMarkers();
                for (const activity of this.activities){
                    let content = {
                        activityTypes: activity.activityTypes,
                        startTime: activity.startTime
                    };
                    this.$refs.map.createMarker(activity.id, 2, activity.location.latitude, activity.location.longitude, content, activity.activityName)
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
                if(this.displayCircle) {
                    this.center = [event.latlng.lat, event.latlng.lng];
                    this.updateCircle();
                    this.$emit('locationChange', this.center);
                }
            },

            /**
             * When the map has location access this method is called to center the circle
             * on the users location.
             * @param event latlng object
             */
            centerOnUserLocation(event) {
                this.center = [event.lat, event.lng];
                this.$emit('locationChange', this.center);
            }
        },
        watch: {
            radius: function() {
                this.updateCircle();
            },
            activities: function () {
                this.addMarkers();
            }
        },
        model: {
            prop: 'center',
            event: 'locationChange'
        },
        mounted() {
            this.updateCircle();
            setTimeout(() => {
                this.$refs.map.refreshMap();
                console.log("Refreshed");
            }, 100)
        },
    }
</script>

<style scoped>

</style>