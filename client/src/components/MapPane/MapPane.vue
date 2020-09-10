<template>
    <b-row v-bind:class="{ fill_space: maximise}">
        <b-col v-bind:class="{ fill_space: maximise}">
            <!-- Toggle Header -->
            <b-row style="cursor:pointer;" v-if="canHide">
                <b-col @click="()=>{showMap=!showMap; refreshMap()}">
                    <h4 class="mb-0">
                        <i v-if="showMap" class="fas fa-caret-down"></i>
                        <i v-else class="fas fa-caret-right"></i>
                        {{title}}
                    </h4>
                </b-col>
            </b-row>
            <!-- Map and slots -->
            <b-row v-bind:class="{ fill_space: maximise}">
                <b-col v-bind:class="{ fill_space: maximise}">
                    <b-collapse v-model="showMap" v-bind:class="{ fill_space: maximise}">
                        <!-- Header Slot -->
                        <b-row>
                            <b-col>

                                <slot name="header"></slot>
                            </b-col>
                        </b-row>
                        <!-- Map -->
                        <hr v-if="canHide">
                        <div style="height: 40em; width: 100%" v-bind:class="{ fill_space: maximise, 'mt-2': !maximise}">
                            <l-map
                                    ref="map"
                                    v-if="showMap"
                                    :zoom="zoom"
                                    :center="center"
                                    :options="mapOptions"
                                    style="height: 100%;"
                                    @click="(event) => {$emit('onMapClick', event)}"
                                    @update:center="centerUpdate"
                                    @update:zoom="zoomUpdate"
                            >
                                <l-tile-layer
                                        :url="url"
                                        :attribution="attribution"
                                />
                                <l-circle v-if="displayCircle"
                                        :lat-lng="circle.center"
                                        :radius="circle.radius"
                                        :color="circle.color"
                                />
                                <l-marker v-for="marker in markers"
                                          :key="marker.id"
                                          :visible="marker.visible"
                                          :lat-lng="marker.position"
                                          :icon="marker.icon"
                                          @click="markerSelected(marker)"
                                >
                                    <l-tooltip id="popUp"
                                               v-if="marker.title != null"
                                               :options='{ interactive: true, offset: [2, -36], direction: "top"}'
                                    >
                                        <b-container style="max-height: 6.5em; overflow: hidden">
                                            <b>
                                                {{marker.title}}
                                            </b>
                                            <hr style="margin: 0.25em">
                                            <span>{{marker.content.activityTypes}} <br></span>
                                            <span>{{marker.content.startTime}}</span>
                                        </b-container>
                                    </l-tooltip>
                                </l-marker>
                            </l-map>
                        </div>

                        <!-- Footer Slot -->
                        <b-row>
                            <b-col>
                                <slot name="footer"></slot>
                            </b-col>
                        </b-row>
                    </b-collapse>
                </b-col>
            </b-row>
        </b-col>
    </b-row>
</template>

<script>
    import L from "leaflet";
    import {LMap, LTileLayer, LMarker, LTooltip, LCircle} from "vue2-leaflet";

    export default {
        name: "MapPane",
        components: {
            LMap,
            LTileLayer,
            LMarker,
            LTooltip,
            LCircle
        },
        props: {
            canHide: {
                type: Boolean,
                default: true
            },
            title: {
                type: String,
                default: "Map"
            },
            displayCircle: {
                type: Boolean,
                default: false
            },
            maximise: {
                type: Boolean,
                default: false
            }
        },
        data() {
            //Sets up the icons
            const blueMarker = L.icon({
                iconUrl: require('leaflet/dist/images/marker-icon.png'),
                shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
                iconRetinaUrl: require('leaflet/dist/images/marker-icon.png'),
                iconAnchor: [10, 30],
            });
            const redMarker = L.icon({
                iconUrl: require('@/assets/red-marker-icon.png'),
                shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
                iconRetinaUrl: require('@/assets/red-marker-icon.png'),
                iconAnchor: [10, 30],
            });
            return {
                showMap: true,
                zoom: 13,
                center: L.latLng(-43.530629, 172.625955),
                url: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                attribution:
                    '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
                mapOptions: {
                    zoomSnap: 0.5
                },
                markers: [],
                blueMarker: blueMarker,
                redMarker: redMarker,

                userGeoLocation: null,
                circle: {
                    center: [-43.530629, 172.625955],
                    radius: 4500,
                    color: '#3388ff',
                }
            };
        },
        methods: {
            /**
             * Updates the zoom of the map
             **/
            zoomUpdate(zoom) {
                this.zoom = zoom
            },
            updateCircle(lat, lng, radius) {
                this.circle.center = [lat, lng];
                this.circle.radius = radius;
            },
            /**
             * Updates the center of the map
             **/
            centerUpdate(center) {
                this.center = center
            },
            /**
             * Updates the center of the map using lat and lng
             **/
            setMapCenter(lat, lng) {
                this.centerUpdate(L.latLng(lat, lng));
            },
            /**
             * Creates a marker on the map.
             * id: The way the marker is deleted
             * iconColour: used as a key for what icon to display. red = 1, blue = other
             * lat: latitude of the marker
             * lng: longitude of the marker
             * content: content of the tooltip
             * title: title of the tooltip
             *
             * e.g. createMarker(1, -43.630629, 172.625955) will be a red marker at those coordinates
             **/
            createMarker(id, iconColour, lat, lng, content, title) {
                //Check inputs and set position and icon
                let icon = null;
                let coordinates = [lat, lng];
                if (iconColour === 1) {
                    icon = this.redMarker
                } else {
                    icon = this.blueMarker
                }

                //Adds the marker to the markers list to be displayed
                this.markers.push({
                    id: id,
                    position: coordinates,
                    visible: true,
                    icon: icon,
                    content: content,
                    title: title
                })
            },
            /**
             * Removes a marker by id
             **/
            removeMarker(id) {
                if (this.markers.length < 1) {
                    return false;
                }
                let i = 0;
                let marker;
                //Loops over markers and removes a marker with the same id
                for (marker of this.markers) {
                    if (marker.id == id) {
                        this.markers.splice(i);
                        return true
                    }
                    i += 1
                }
                return false
            },
            /**
             * Updates the map's viewing size when map container size changes. For instance
             * in a tab.
             **/
            refreshMap() {
                let map = this.$refs.map;
                setTimeout(function () {
                    map.mapObject.invalidateSize();
                }, 100);
            },
            /**
             * If no markers are available, the map centers on the currently logged in users geo location
             **/
            updateMapToUserGeoLocation() {
                let _this = this;
                navigator.geolocation.getCurrentPosition((pos) => {
                    _this.userGeoLocation = {
                        lat: pos.coords.latitude,
                        lng: pos.coords.longitude
                    };
                    _this.$emit('userLocationUpdate', _this.userGeoLocation);
                });
            },
            /**
             * Focuses the map to the marker selected
             **/
            markerSelected(marker) {
                this.center = marker.position;
                this.$emit('markerSelected', marker.content.id);
            }
        },
        /**
         * Refresh the map everytime the map is rendered by vue.
         */
        updated: function () {
            this.$nextTick(function () {
                // Code that will run only after the
                // entire view has been re-rendered
                this.refreshMap();
            })
        },
        mounted() {
            this.updateMapToUserGeoLocation();
        }
    };
</script>
<style>
    .button {
        margin-left: 0.5em;
        margin-right: 0.5em
    }

    .leaflet-tooltip {
        padding-left: 0em;
        padding-right: 0em;
        min-width: 14em;
        max-width: 14em;
        white-space: normal;
        max-height: 8em;
        min-height: 8em;
        text-justify: distribute;
    }

    .fill_space {
        width: 100% !important;
        height: 100% !important;
        margin: 0 !important;
        padding: 0 !important;
    }
</style>