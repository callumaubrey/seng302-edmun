<template>
    <b-row>
        <b-col>
            <!-- Map Pane -->
            <MapPane ref="map"
                     :title="title"
                     :can-hide="canHide"
                     @onMapClick="mapClicked">

                <!-- Location Autocomplete In Map Header -->
                <template v-slot:header>
                    <b-row>
                        <b-col cols="11">
                            <LocationAutocomplete :given-location="address" @emitLocation="textLocationSelected"></LocationAutocomplete>
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
</template>

<script>
    import MapPane from "./MapPane";
    import LocationAutocomplete from "../LocationAutocomplete";

    export default {
        name: "ModifyLocationMapPane",
        components: {
            MapPane,
            LocationAutocomplete
        },

        data: () => {
            return {
                markerOnMap: false
            }
        },

        props: {
            value: { // Value should be an object with members lat & lng
                type: Object,
                default: () => {return null}
            },
            title: {
                type: String,
                default: "Set Location"
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
            }
        },

        methods: {
            /**
             * When the map is clicked the event will change the v-model and move the pin on the map.
             * @param event click event with latitude and longitude
             */
            mapClicked: function(event) {
                let lat = event.latlng.lat;
                let lng = event.latlng.lng;

                // Update set location on v-model
                this.$emit('input', event.latlng);
                this.updateMarker(lat, lng);
            },

            /**
             * When an autocomplete text option is selected update the v-model and move the pin on the map
             * @param data object containing latitude and longitude
             */
            textLocationSelected: function(data) {
                // Update new location
                let latlng = {lat: data.lat, lng: data.lng};
                this.$emit('input', latlng);

                this.updateMarker(data.lat, data.lng);
                this.$refs.map.setMapCenter(data.lat, data.lng);
            },

            /**
             * Update the markers location on the map by removing the old one and adding a new one.
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
            }
        },

        mounted() {
            if (this.value !== null) {
                this.updateMarker(this.value.lat, this.value.lng);
            }
        }
    }
</script>

<style scoped>

</style>