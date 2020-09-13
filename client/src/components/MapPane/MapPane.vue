<template>
    <b-row>
        <b-col>
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
            <b-row>
                <b-col>
                    <b-collapse v-model="showMap">
                        <!-- Header Slot -->
                        <b-row>
                            <b-col>

                                <slot name="header"></slot>
                            </b-col>
                        </b-row>
                        <!-- Map -->
                        <hr v-if="canHide">
                        <div style="height: 40em; width: 100%" class="mt-2">
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
                                <LControl v-if="pathOverlay" class="control-overlay">
                                    <b-col>
                                        <b-row>
                                            <b-button style="margin: 0.5em" @click="$parent.prevPoint()">Back</b-button>
                                            <b-button style="margin: 0.5em" @click="$parent.resetMarkerAndPoint()">Reset</b-button>
                                        </b-row>
                                        <b-row>
                                            <b-form-radio-group id="checkbox-group-2" v-model="$parent.autoRoute" name="flavour-2"
                                                                   v-if="$parent.canChangeSelection">
                                                <b-form-radio value="false">Selection</b-form-radio>
                                                <b-form-radio value="true">Auto route</b-form-radio>
                                            </b-form-radio-group>
                                        </b-row>
                                    </b-col>


                                </LControl>
                                <l-circle v-if="displayCircle"
                                        :lat-lng="circle.center"
                                        :radius="circle.radius"
                                        :color="circle.color"
                                />

                                <!--Routing-->
                                <l-polyline :lat-lngs="routePoints">
                                </l-polyline>

                                <l-marker v-for="marker in markers"
                                          :key="marker.id"
                                          :visible="marker.visible"
                                          :lat-lng="marker.position"
                                          :icon="marker.icon"
                                          @click="markerSelected(marker)"
                                >
                                    <l-tooltip id="popUp"
                                               :options='{ interactive: true, offset: [2, -36], direction: "top"}'
                                               v-if="tooltip"
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
    import {LMap, LTileLayer, LMarker, LTooltip, LCircle, LPolyline, LControl} from "vue2-leaflet";

    export default {
        name: "MapPane",
        components: {
            LMap,
            LTileLayer,
            LMarker,
            LTooltip,
            LCircle,
            LPolyline,
            LControl
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
            pathOverlay: {
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
            const pathMarker = L.icon({
                iconUrl: require('@/assets/path-marker.png'),
                iconRetinaUrl: require('@/assets/path-marker.png'),
                iconAnchor: [8, 8],
            });
            const pathStartMarker = L.icon({
                iconUrl: require('@/assets/path-start-marker.png'),
                iconRetinaUrl: require('@/assets/path-start-marker.png'),
                iconAnchor: [10, 30],
            });
            const pathEndMarker = L.icon({
                iconUrl: require('@/assets/path-end-marker.png'),
                iconRetinaUrl: require('@/assets/path-end-marker.png'),
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
                pathMarker: pathMarker,
                pathStartMarker: pathStartMarker,
                pathEndMarker: pathEndMarker,

                userGeoLocation: null,
                circle: {
                    center: [-43.530629, 172.625955],
                    radius: 4500,
                    color: '#3388ff',
                },
                routePoints: [],
                markerObjects: {},
                tooltip: true
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
                this.displayCircle = true;
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
             * iconColour: used as a key for what icon to display. red = 1, pathMarker = 3, blue = other
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
                }
                if (iconColour === 3) {
                    icon = this.pathStartMarker
                }
                else {
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
             * Gets latest marker that the user has inputted
             *
             **/
            getLatestMarker() {
                return this.markers[this.markers.length - 1]
            },

            /**
             * Sets latest markers icon color to blue
             *
             **/
            updateStartFinishMarkers() {
                if (this.markers.length == 2) {
                    this.markers[0].icon = this.pathStartMarker
                    this.markers[1].icon = this.pathEndMarker
                }
                if (this.markers.length > 2) {
                    this.markers[this.markers.length - 2].icon = this.pathMarker
                    this.markers[this.markers.length - 1].icon = this.pathEndMarker
                }
            },

            /**
             * Returns all markers longitude and latitude in a list
             *
             **/
            getAllMarkersCoords() {
                let coords = []
                for (let marker of this.markers) {
                    coords.push([marker.position[1], marker.position[0]])
                }
                return coords
            },
            /**
             * Sets route points with given parameter
             *
             **/
            setRoutePoints(routePoints) {
              this.routePoints = routePoints
            },
            /**
             * Sets markers with given parameter
             *
             **/
            setMarkers(markers) {
                this.markers = markers
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
            },
            // editRoutePoint(point) {
            //     let i = 0;
            //     let marker;
            //     //Loops over markers and removes a marker with the same id
            //     for (marker of this.routePoints) {
            //         if (marker !== point) {
            //             this.routePoints[i] = point
            //             return true
            //         }
            //         i += 1
            //     }
            // }
            // getMarkers() {
            //     this.$nextTick(() => {
            //         this.markerObjects = this.$refs.markersRef.map(ref => ref.mapObject._latlng)
            //         alert(this.markerObjects)
            //         alert(this.markerObjects[0]._latlng.lat)
            //     })
            // },
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

    .control-overlay {
        background: #fff;
        padding: 0 0.5em;
        border: 1px solid #aaa;
        border-radius: 0.1em;
        opacity: 80%;
        padding: 0.5em;
        margin: 0.5em;
    }
</style>