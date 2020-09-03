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
                        <div style="height: 40em; width: 100%" class="mt-2">
                            <l-map
                                    v-if="showMap"
                                    :zoom="zoom"
                                    :center="center"
                                    :options="mapOptions"
                                    style="height: 100%; display: block"
                                    @click="(event) => {$emit('onMapClick', event)}"
                                    @update:center="centerUpdate"
                                    @update:zoom="zoomUpdate"
                            >
                                <l-tile-layer
                                        :url="url"
                                        :attribution="attribution"
                                />
                                <l-marker v-for="marker in markers"
                                          :key="marker.id"
                                          :visible="marker.visible"
                                          :lat-lng="marker.position"
                                          :icon="marker.icon"
                                >
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

            <hr v-if="canHide">
        </b-col>
    </b-row>
</template>

<script>
    import L from "leaflet";
    import {LMap, LTileLayer, LMarker} from "vue2-leaflet";

    export default {
        name: "MapPane",
        components: {
            LMap,
            LTileLayer,
            LMarker,
        },
        props: {
            canHide: {
                type: Boolean,
                default: true
            },
            title: {
                type: String,
                default: "Map"
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
                iconUrl: require('@/assets/red_man.png'),
                shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
                iconRetinaUrl: require('@/assets/red_man.png'),
                iconSize: [65, 65],
                iconAnchor: [10, 0],
                shadowAnchor: [-10,-15]
            });
            return {
                showMap: true,
                zoom: 13,
                center: L.latLng(-43.530629, 172.625955),
                url: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                attribution:
                    '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
                currentZoom: 11.5,
                currentCenter: L.latLng(-43.530629, 172.625955),
                mapOptions: {
                    zoomSnap: 0.5
                },
                markers: [],
                blueMarker: blueMarker,
                redMarker: redMarker
            };
        },
        methods: {
            /**
             * Updates the zoom of the map
             **/
            zoomUpdate(zoom) {
                this.currentZoom = zoom;
                this.zoom = zoom
            },
            /**
             * Updates the center of the map
             **/
            centerUpdate(center) {
                this.currentCenter = center;
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
             * e.g. createMarker(1, -43.630629, 172.625955) will be a red marker at those coordinates
             **/
            createMarker(id, iconColour, lat, lng) {
                //Check inputs and set position and icon
                let icon = null
                let coordinates = [lat, lng]
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
                })
            },
            /**
             * Removes a marker by id
             **/
            removeMarker(id) {
                if (this.markers.length < 1) {
                    return false;
                }
                var i = 0;
                var marker;
                //Loops over markers and removes a marker with the same id
                for (marker of this.markers) {
                    if (marker.id == id) {
                        this.markers.splice(i)
                        return true
                    }
                    i += 1
                }
                return false
            },
            /**
             * Updates the map tiles when the map is visible
             * This is a pretty gross fix to the map tiles not loading bug but it works -Will
             **/
            refreshMap() {
                setTimeout(function () {
                    window.dispatchEvent(new Event('resize'))
                }, 100);
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
        }
    };
</script>
<style>
    .button {
        margin-left: 0.5em;
        margin-right: 0.5em
    }
</style>