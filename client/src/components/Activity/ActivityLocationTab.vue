<template>
    <b-tab title="Activity Location">
        <b-row>
            <b-col>
                <!-- Map Pane -->
                <MapPane ref="map"
                         :title="title"
                         :can-hide="canHide"
                         @onMapClick="mapClicked"
                         @userLocationUpdate="userGeoLocationUpdate">
                    <!-- Location Autocomplete In Map Header -->
                    <template v-slot:header>
                        <b-row>
                            <b-col cols="11">
                                <LocationAutocomplete ref="location_input"
                                                      :priority-geo-location="priorityGeoLocation"
                                                      :given-location="address"
                                                      @emitLocation="textLocationSelected"></LocationAutocomplete>
                            </b-col>
                            <b-col cols="1">
                                <b-button block variant="primary" :disabled="!markerOnMap"
                                          @click="clearLocation">Clear</b-button>
                            </b-col>
                        </b-row>
                    </template>
                </MapPane>
            </b-col>
        </b-row>
    </b-tab>
</template>

<script>
    import MapPane from "../MapPane/MapPane";
    import LocationAutocomplete from "../LocationAutocomplete";

    export default {
        name: "ActivityLocationTab",
        components: {
            MapPane,
            LocationAutocomplete
        },

        data: () => {
            return {
                markerOnMap: false,
                priorityGeoLocation: null
            }
        },

        props: {
            value: { // Value should be an object with members lat & lng
                type: Object,
                default: () => {return null}
            },
            title: {
                type: String,
                default: "Set Activity Location"
            },
            iconColourId: {
                type: Number,
                default: 0
            },
            address: {
                type: String,
                default: null
            },
            canHide: {
                type: Boolean,
                default: true
            },
            userLat: {
                type: Number,
                default: null
            },
            userLong: {
                type: Number,
                default: null
            },
            activityLat: {
                type: Number,
                default: null
            },
            activityLong: {
                type: Number,
                default: null
            }
        },

        methods: {
            /**
             * When the map is clicked the event will change the v-model and move the activity pin on the map.
             * @param event click event with latitude and longitude
             */
            mapClicked: function(event) {
                let lat = event.latlng.lat;
                let lng = event.latlng.lng;

                // Update set location on v-model
                this.$emit('locationSelect', event.latlng);
                this.updateMarker(lat, lng);

                // Update Text in AutocompleteLocation component
                this.$refs.location_input.setLocationTextByCoords(lat, lng);
            },

            /**
             * When an autocomplete text option is selected update the v-model and move the activity pin on the map
             * @param data object containing latitude and longitude
             */
            textLocationSelected: function(data) {
                // Update new location
                let latlng = {lat: data.lat, lng: data.lng};
                this.$emit('locationSelect', latlng);

                this.updateMarker(data.lat, data.lng);
                this.$refs.map.setMapCenter(data.lat, data.lng);
            },

            /**
             * Update the activity marker on the map by removing the old one and adding a new one.
             * @param lat latitude
             * @param lng longitude
             */
            updateMarker: function(lat, lng) {
                this.markerOnMap = true;
                this.$refs.map.removeMarker(1);
                this.$refs.map.createMarker(1, this.iconColourId, lat, lng);
            },

            /**
             * Update the v-model and remove the marker
             */
            clearLocation: function () {
                this.markerOnMap = false;
                this.$refs.map.removeMarker(1);
                this.$emit('input', null);
                this.$refs.location_input.clearLocation();
            },

            /**
             * On Map getting users geo location update Location Autocomplete
             * for priority geo location searching.
             */
            userGeoLocationUpdate: function (coords) {
                this.priorityGeoLocation = coords;

                // Set users location if they are not already centered on a marker
                if(!this.markerOnMap) {
                    this.$refs.map.setMapCenter(coords.lat, coords.lng);
                }
            },

            refreshMap: function () {
                this.$refs.map.refreshMap()
            },
            /**
             * Sets the users location pin on the map if they have one using data from parent
             */
            setUsersLocationPin: function() {
                console.log(this.userLat);
                if (this.userLat == null && this.userLong == null) {
                    return;
                }
                this.$refs.map.createMarker(2, 1, this.userLat, this.userLong);
                // Centers map on user if activity has no current location
                if (this.value == null) {
                    this.$refs.map.setMapCenter(this.userLat, this.userLong);
                }
            }
        },
        mounted() {
            if (this.value !== null) {
                // Set Marker on map and center map on it
                this.updateMarker(this.value.lat, this.value.lng);
                this.$refs.map.setMapCenter(this.value.lat, this.value.lng);
            }
            setTimeout(this.setUsersLocationPin, 1000);
        }
    }
</script>

<style scoped>

</style>