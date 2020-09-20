<template>
    <div>
        <b-tabs pills card align="center">
            <!-- Marker Info -->
            <b-tab active class="p-0 overflow-auto" style="max-height: 565px" id="pathInfoMarkerTab">
                <!-- Icon -->
                <template v-slot:title>
                    <i class="fas fa-map-marker-alt info-category-icon"></i>
                </template>

                <b-list-group>
                    <b-list-group-item  v-for="keypoint in keypointInfo" :key="keypoint.index"
                                        :class="{'selected_keypoint': selectedKeypoint === keypoint.index}"
                                        style="cursor: pointer" @click="selectKeypoint(keypoint.index)">
                        <b-row>
                            <!-- Marker Icon -->
                            <b-col style="flex: 0 0 35px; font-size: 0.75em" class="p-0">
                                <span v-if="keypoint.type==='start'" class="fa-stack fa-2x">
                                  <i class="fas fa-circle fa-stack-2x" style="color:#2af582"></i>
                                  <i class="fas fa-map-marker-alt fa-stack-1x fa-inverse"></i>
                                </span>
                                <span v-else-if="keypoint.type==='end'" class="fa-stack fa-2x">
                                  <i class="fas fa-circle fa-stack-2x" style="color:Tomato"></i>
                                  <i class="fas fa-flag-checkered fa-stack-1x fa-inverse"></i>
                                </span>
                                <span v-else class="fa-stack fa-2x">
                                  <i class="fas fa-circle fa-stack-2x" style="color:#1295ff"></i>
                                  <i class="fas fa-map-marker-alt fa-stack-1x fa-inverse"></i>
                                </span>
                            </b-col>

                            <!-- Marker Info -->
                            <b-col>
                                <span  v-if="keypoint.distance!==null"
                                        v-b-tooltip.hover title="To next point">{{keypoint.distance}} km</span>
                                <span v-else>Finish, {{totalDistance}} km Total</span>

                                <br>
                                <span class="text-secondary">{{keypoint.lat}}, {{keypoint.lng}}</span>
                            </b-col>
                        </b-row>
                    </b-list-group-item>
                </b-list-group>
            </b-tab>

            <!-- Directions -->
            <b-tab class="p-0 overflow-auto" style="max-height: 565px"
                   :disabled="path.type==='STRAIGHT'" >
                <!-- Icon -->
                <template v-slot:title>
                    <i class="fas fa-directions info-category-icon"></i>
                </template>


                <b-list-group  v-for="step in directionSteps" :key="step.index">
                    <b-list-group-item>
                        <b-row>
                            <!-- Marker Icon -->
                            <b-col style="flex: 0 0 35px; font-size: 0.75em" class="p-0">
                                <span class="fa-stack fa-2x">
                                  <i class="fas fa-square fa-stack-2x" style="color:#2af582"></i>
                                  <i :class="getDirectionStepIconClasses(step)"></i>
                                </span>
                            </b-col>

                            <!-- Marker Info -->
                            <b-col>
                                {{step.distance}}
                                {{step.instruction}}
                            </b-col>
                        </b-row>
                    </b-list-group-item>
                </b-list-group>
            </b-tab>
        </b-tabs>
<!--
        <b-list-group style="overflow: auto; max-height: 20em; max-width: 30em; margin: 0.5em" v-if="!directions">
            <b-list-group-item style="cursor: pointer" v-for="point in points" :key="point.id" @click="pointSelected(point)">
                <b>{{point.id + 1}}:</b>    {{point.name}}
            </b-list-group-item>
        </b-list-group>

        <b-list-group style="overflow: auto; max-height: 20em; max-width: 30em; margin: 0.5em" v-else>
            <b-list-group-item style="cursor: pointer" v-for="point in points" :key="point.id" @click="pointSelected(point)">
&lt;!&ndash;                <b>{{point.id + 1}}:</b>    &ndash;&gt;
                Test Location
            </b-list-group-item>
        </b-list-group>-->
    </div>
</template>

<script>
    import L from 'leaflet';
    import axios from "axios";


    const directionIconTypes = [
        'fa-long-arrow-alt-left', // Left
        'fa-long-arrow-alt-right', // Right
        'fa-long-arrow-alt-left', // Sharp left
        'fa-long-arrow-alt-right', // Sharp right
        'fa-long-arrow-alt-left', // Slight left
        'fa-long-arrow-alt-right', // Slight right
        'fa-long-arrow-alt-up', // Straight
        'fa-sync-alt', // Enter roundabout
        'fa-sync-alt', // Exit roundabout
        'fa-undo', // U-turn
        'fa-flag-checkered', // Goal
        'external-link-square-alt', // Depart
        'fa-long-arrow-alt-left', // Keep left
        'fa-long-arrow-alt-right', // Kepp right

    ];

    export default {
        name: "PathInfo",
        components: {
        },
        /** The points of the path of which information are displayed for*/
        props: {
            path: Object,
            selectedKeypoint: Number
        },
        model: {
            prop: 'selectedKeypoint',
            event: 'selected'
        },
        data() {
            return {
                keypointInfo: [],
                totalDistance: 0,

                directionSegments: [],
                directionSteps: []
            }
        },
        methods: {

            /**
             * Generates keypointInfo with keypoint information such as
             * distance, lat, lng.
             **/
            generateKeypointInfoFromPath() {
                this.keypointInfo = [];
                this.totalDistance = 0;
                for(let i = 0; i < this.path.locations.length; i++) {
                    let keypoint = this.path.locations[i];


                    // Calculate distance
                    let distance = null;
                    if(i < this.path.locations.length-1) {
                        if(this.path.type === "STRAIGHT") {
                            let next_keypoint = this.path.locations[i+1];
                            distance = this.getStraightLineDistanceBetweenKeypoints(keypoint, next_keypoint);
                        } else {
                            distance = this.directionSegments[i].distance / 1000;
                        }

                       this.totalDistance += distance;
                       distance = distance.toFixed(2);
                    }


                    // Calculate type
                    let type = "keypoint";
                    if(i === 0) type = "start";
                    if(i === this.path.locations.length-1) type = "end";

                    this.keypointInfo.push({
                        index: i,
                        lat: keypoint.latitude.toFixed(6),
                        lng: keypoint.longitude.toFixed(6),
                        distance: distance,
                        type: type
                    });
                }

                this.totalDistance = this.totalDistance.toFixed(2);
            },

            /**
             *  Get distance between two locations in kilometers.
             **/
            getStraightLineDistanceBetweenKeypoints(keypointA, keypointB) {
                let a = L.latLng(keypointA.latitude, keypointA.longitude);
                let b = L.latLng(keypointB.latitude, keypointB.longitude);
                return a.distanceTo(b) / 1000.0
            },

            /**
             * Selects a keypoint to show to user
             * @param index keypoint index
             */
            selectKeypoint(index) {
                this.$emit('selected', index);
            },


            /**
             * Calls openrouteservice api to get direction info.
             */
            getDirectionInfo: async function() {
                // Create api keypoints from path locations
                let keypoints = [];
                for(const keypoint of this.path.locations) {
                    keypoints.push([keypoint.longitude, keypoint.latitude]);
                }

                // Get direction route using keypoints
                let res = await axios.post("https://api.openrouteservice.org/v2/directions/foot-hiking/geojson",
                    {
                        coordinates: keypoints,
                    },
                    {headers: {Authorization: "5b3ce3597851110001cf6248183abbef295f42049b13e7a011f98247"}})

                this.directionSegments = res.data.features[0].properties.segments;
            },

            generateDirectionInfo() {
                let i = 0;
                this.directionSteps = [];
                for(const segment of this.directionSegments) {
                    for(let step of segment.steps) {
                        step.index = i;
                        this.directionSteps.push(step);
                        i++;
                    }
                }
            },

            getDirectionStepIconClasses(step) {
                let classes = {
                    'fas':true,
                    'fa-stack-1x':true,
                    'fa-inverse':true
                };
                classes[directionIconTypes[step.type]] = true;
                return classes;
            }
        },
        async mounted() {
            await this.getDirectionInfo();
            this.generateKeypointInfoFromPath();
            this.generateDirectionInfo();
        },
        watch: {
            path: function() {
                this.generateKeypointInfoFromPath();
            },
            selectedKeypoint: function () {
                let tab = document.getElementById("pathInfoMarkerTab");
                let offset = 74 * this.selectedKeypoint;
                tab.scrollTo({top:offset, behavior:'smooth'});
            }
        }
    }
</script>

<style scoped>
    .info-category-icon {
        font-size: 2em;
    }
    .selected_keypoint {
        background-color: #cff0ff;
    }
</style>